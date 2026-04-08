"""
MotoTracker Scraper — Cloud Run Job entry point.

Usage:
    python main.py [--makes] [--models] [--schedules] [--recalls]
                   [--best-practices] [--all]

Default behaviour with no flags is equivalent to --all.
"""
import argparse
import json
import logging
import re
import sys
import urllib.request
import uuid
from datetime import datetime

import config
from firestore_writer import FirestoreWriter
from scrapers import BikeScraper, BikeWaleScraper, NHTSARecallScraper, OEMScheduleScraper, get_best_practices


def _norm(s: str) -> str:
    """Normalise a make/model name for dedup comparison: lowercase, alphanumeric only.
    Strips source prefixes (e.g. 'bw_') before comparing."""
    s = re.sub(r"^[a-z]{2}_", "", s)  # strip 2-char source prefix like 'bw_'
    return re.sub(r"[^a-z0-9]", "", s.lower())


def _dedup_makes(makes):
    """Return makes deduplicated by normalised name, first entry wins."""
    seen: dict[str, bool] = {}
    out = []
    for m in makes:
        key = _norm(m.name)
        if key not in seen:
            seen[key] = True
            out.append(m)
        else:
            logging.getLogger("main").debug(
                "Dedup makes: dropping duplicate '%s' (id=%s)", m.name, m.id
            )
    return out


def _dedup_models(models):
    """
    Return models deduplicated by (normalised make, normalised model, year_from).
    First entry wins — bikez runs before BikeWale so bikez data takes priority
    for bikes covered by both sources.
    """
    seen: dict[tuple, bool] = {}
    out = []
    dropped = 0
    for m in models:
        key = (_norm(m.make), _norm(m.model), m.year_from)
        if key not in seen:
            seen[key] = True
            out.append(m)
        else:
            dropped += 1
    if dropped:
        logging.getLogger("main").info(
            "Dedup models: dropped %d cross-source duplicates", dropped
        )
    return out


def _notify_completion(run_id: str, stats: dict, success: bool) -> None:
    """POST a run summary to NOTIFY_WEBHOOK_URL if configured.
    Compatible with Slack incoming webhooks and any service that accepts
    a JSON body with a 'text' key.
    """
    url = config.NOTIFY_WEBHOOK_URL
    if not url:
        return
    status = "SUCCESS" if success else "FAILED"
    lines = [
        f"[{status}] MotoTracker scraper run finished",
        f"Run ID: {run_id}",
        f"Makes: {stats['makes_count']}  Models: {stats['models_count']}  "
        f"Schedules: {stats['schedules_count']}  Recalls: {stats['recalls_count']}  "
        f"Best practices: {stats['best_practices_count']}",
    ]
    if stats["errors"]:
        lines.append(f"Errors ({len(stats['errors'])}): " + " | ".join(stats["errors"][:5]))
    payload = json.dumps({"text": "\n".join(lines)}).encode()
    try:
        req = urllib.request.Request(
            url,
            data=payload,
            headers={"Content-Type": "application/json"},
            method="POST",
        )
        urllib.request.urlopen(req, timeout=10)
        logging.getLogger("main").info("Completion notification sent to webhook")
    except Exception as exc:
        logging.getLogger("main").warning("Failed to send completion notification: %s", exc)


def _configure_logging() -> None:
    level = getattr(logging, config.LOG_LEVEL.upper(), logging.INFO)
    logging.basicConfig(
        level=level,
        format="%(asctime)s [%(levelname)s] %(name)s — %(message)s",
        datefmt="%Y-%m-%dT%H:%M:%SZ",
        stream=sys.stdout,
    )


def _parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="MotoTracker data scraper for Firestore population."
    )
    parser.add_argument(
        "--makes",
        action="store_true",
        help="Scrape and upload motorcycle makes",
    )
    parser.add_argument(
        "--models",
        action="store_true",
        help="Scrape and upload motorcycle models",
    )
    parser.add_argument(
        "--schedules",
        action="store_true",
        help="Upload curated OEM maintenance schedules",
    )
    parser.add_argument(
        "--recalls",
        action="store_true",
        help="Fetch and upload NHTSA recall data",
    )
    parser.add_argument(
        "--best-practices",
        action="store_true",
        dest="best_practices",
        help="Upload hardcoded best practice templates",
    )
    parser.add_argument(
        "--all",
        action="store_true",
        dest="run_all",
        help="Run all scrapers (default behaviour when no flag is given)",
    )
    parser.add_argument(
        "--incremental",
        action="store_true",
        help=(
            "Incremental mode: skip makes already in Firestore, only scrape new makes "
            "and their models. Always includes recalls. Ideal for the weekly scheduled run."
        ),
    )
    return parser.parse_args()


def main() -> None:
    _configure_logging()
    logger = logging.getLogger("main")
    args = _parse_args()

    # Incremental mode overrides individual flags: only new makes/models + recalls
    if args.incremental:
        run_makes = True
        run_models = True
        run_schedules = False
        run_recalls = True
        run_best_practices = False
    else:
        # If no specific flag is given, run everything
        run_all = args.run_all or not any(
            [args.makes, args.models, args.schedules, args.recalls, args.best_practices]
        )
        run_makes = run_all or args.makes
        run_models = run_all or args.models
        run_schedules = run_all or args.schedules
        run_recalls = run_all or args.recalls
        run_best_practices = run_all or args.best_practices

    run_id = f"run_{datetime.utcnow().strftime('%Y%m%d_%H%M%S')}_{uuid.uuid4().hex[:8]}"
    logger.info("=== MotoTracker scraper starting | run_id=%s ===", run_id)
    logger.info("Project: %s", config.GCP_PROJECT_ID)
    logger.info(
        "Tasks: makes=%s models=%s schedules=%s recalls=%s best_practices=%s",
        run_makes,
        run_models,
        run_schedules,
        run_recalls,
        run_best_practices,
    )

    writer = FirestoreWriter(project_id=config.GCP_PROJECT_ID, storage_bucket=config.STORAGE_BUCKET)
    stats: dict = {
        "makes_count": 0,
        "models_count": 0,
        "schedules_count": 0,
        "recalls_count": 0,
        "best_practices_count": 0,
        "errors": [],
    }
    success = True

    # ------------------------------------------------------------------
    # Makes & Models — bikez.com
    # ------------------------------------------------------------------
    makes = []
    models = []

    if run_makes or run_models:
        bike_scraper = BikeScraper()

        if args.incremental:
            # Incremental: only scrape makes not already in Firestore
            try:
                all_makes = bike_scraper.scrape_makes()
                existing_ids = writer.get_existing_make_ids()
                makes = [m for m in all_makes if m.id not in existing_ids]
                logger.info(
                    "Incremental mode: %d new makes (of %d total), skipping %d already stored",
                    len(makes),
                    len(all_makes),
                    len(existing_ids),
                )
                for make in makes:
                    try:
                        make_models = bike_scraper.scrape_models(make.id)
                        models.extend(make_models)
                    except Exception as exc:
                        logger.error(
                            "Bikez scrape_models failed for new make '%s': %s",
                            make.id, exc, exc_info=True,
                        )
                        stats["errors"].append(f"bikez_scrape_models_{make.id}: {exc}")
            except Exception as exc:
                logger.error("Incremental makes scrape failed: %s", exc, exc_info=True)
                stats["errors"].append(f"bikez_incremental: {exc}")
                success = False
        elif run_makes and run_models:
            # Single pass: scrape both together for efficiency
            try:
                makes, models = bike_scraper.scrape_all()
            except Exception as exc:
                logger.error("Bikez scrape_all failed: %s", exc, exc_info=True)
                stats["errors"].append(f"bikez_scrape_all: {exc}")
                success = False
        else:
            if run_makes:
                try:
                    makes = bike_scraper.scrape_makes()
                except Exception as exc:
                    logger.error("Bikez scrape_makes failed: %s", exc, exc_info=True)
                    stats["errors"].append(f"bikez_scrape_makes: {exc}")
                    success = False
            if run_models:
                # Need makes list to iterate; fall back to scraping makes first
                if not makes:
                    try:
                        makes = bike_scraper.scrape_makes()
                    except Exception as exc:
                        logger.error(
                            "Bikez scrape_makes (for models) failed: %s", exc, exc_info=True
                        )
                        stats["errors"].append(f"bikez_scrape_makes_for_models: {exc}")
                        success = False
                if makes:
                    for make in makes:
                        try:
                            make_models = bike_scraper.scrape_models(make.id)
                            models.extend(make_models)
                        except Exception as exc:
                            logger.error(
                                "Bikez scrape_models failed for '%s': %s",
                                make.id,
                                exc,
                                exc_info=True,
                            )
                            stats["errors"].append(f"bikez_scrape_models_{make.id}: {exc}")

        # BikeWale — Indian market supplement
        try:
            bw_scraper = BikeWaleScraper()
            if args.incremental:
                existing_ids = existing_ids if "existing_ids" in dir() else writer.get_existing_make_ids()
                bw_all_makes, bw_all_models = bw_scraper.scrape_all()
                bw_new_makes = [m for m in bw_all_makes if m.id not in existing_ids]
                bw_new_models = [
                    m for m in bw_all_models
                    if any(m.make == nm.id for nm in bw_new_makes)
                ]
                logger.info(
                    "BikeWale incremental: %d new makes, %d new models",
                    len(bw_new_makes), len(bw_new_models),
                )
                makes.extend(bw_new_makes)
                models.extend(bw_new_models)
            else:
                bw_makes, bw_models = bw_scraper.scrape_all()
                makes.extend(bw_makes)
                models.extend(bw_models)
        except Exception as exc:
            logger.error("BikeWale scrape failed: %s", exc, exc_info=True)
            stats["errors"].append(f"bikewale_scrape: {exc}")

        makes = _dedup_makes(makes)
        models = _dedup_models(models)
        logger.info("After dedup: %d makes, %d models", len(makes), len(models))

        if run_makes and makes:
            try:
                stats["makes_count"] = writer.upsert_makes(makes)
            except Exception as exc:
                logger.error("Firestore upsert_makes failed: %s", exc, exc_info=True)
                stats["errors"].append(f"firestore_upsert_makes: {exc}")
                success = False

        if run_models and models:
            try:
                stats["models_count"] = writer.upsert_models(models)
            except Exception as exc:
                logger.error("Firestore upsert_models failed: %s", exc, exc_info=True)
                stats["errors"].append(f"firestore_upsert_models: {exc}")
                success = False

    # ------------------------------------------------------------------
    # OEM Maintenance Schedules (curated — no HTTP)
    # ------------------------------------------------------------------
    if run_schedules:
        try:
            oem_scraper = OEMScheduleScraper()
            schedules = oem_scraper.get_all_schedules()
            logger.info("Loaded %d curated OEM schedules", len(schedules))
            stats["schedules_count"] = writer.upsert_schedules(schedules)
        except Exception as exc:
            logger.error("OEM schedules failed: %s", exc, exc_info=True)
            stats["errors"].append(f"oem_schedules: {exc}")
            success = False

    # ------------------------------------------------------------------
    # Best Practices (hardcoded — no HTTP)
    # ------------------------------------------------------------------
    if run_best_practices:
        try:
            practices = get_best_practices()
            logger.info("Loaded %d best practices", len(practices))
            stats["best_practices_count"] = writer.upsert_best_practices(practices)
        except Exception as exc:
            logger.error("Best practices failed: %s", exc, exc_info=True)
            stats["errors"].append(f"best_practices: {exc}")
            success = False

    # ------------------------------------------------------------------
    # NHTSA Recalls
    # ------------------------------------------------------------------
    if run_recalls:
        try:
            recall_scraper = NHTSARecallScraper()
            # Use scraped models if available; otherwise fetch recalls for
            # a representative set of models from the OEM schedules
            if not models:
                logger.info(
                    "No models scraped in this run; fetching NHTSA recalls for "
                    "a curated set of popular models"
                )
                from scrapers.oem_schedule_scraper import OEMScheduleScraper as _OEM
                from models import RegistryModel
                from datetime import datetime as _dt

                # Build a minimal model list from OEM schedule data
                oem = _OEM()
                seen_keys: set[tuple] = set()
                fallback_models: list[RegistryModel] = []
                for sched in oem.get_all_schedules():
                    key = (sched.make, sched.model, sched.year_from)
                    if key not in seen_keys:
                        seen_keys.add(key)
                        fallback_models.append(
                            RegistryModel(
                                id=f"{sched.make}_{sched.model}_{sched.year_from}",
                                make=sched.make,
                                model=sched.model,
                                year_from=sched.year_from,
                                year_to=sched.year_to,
                                variants=[],
                                vehicle_type="BIKE",
                                displacement_cc=None,
                                updated_at=_dt.utcnow(),
                            )
                        )
                models_for_recalls = fallback_models
            else:
                models_for_recalls = models

            recalls = recall_scraper.fetch_all_recalls(models_for_recalls)
            if recalls:
                stats["recalls_count"] = writer.upsert_recalls(recalls)
            else:
                logger.info("No recalls found for provided models")
        except Exception as exc:
            logger.error("NHTSA recalls failed: %s", exc, exc_info=True)
            stats["errors"].append(f"nhtsa_recalls: {exc}")
            success = False

    # ------------------------------------------------------------------
    # Write run metadata
    # ------------------------------------------------------------------
    try:
        writer.update_sync_metadata(run_id=run_id, stats=stats)
    except Exception as exc:
        logger.error("Failed to write sync metadata: %s", exc, exc_info=True)
        # Non-fatal — don't flip success flag

    # ------------------------------------------------------------------
    # Summary
    # ------------------------------------------------------------------
    logger.info("=== Run summary | run_id=%s ===", run_id)
    logger.info("  Makes written:          %d", stats["makes_count"])
    logger.info("  Models written:         %d", stats["models_count"])
    logger.info("  Schedules written:      %d", stats["schedules_count"])
    logger.info("  Best practices written: %d", stats["best_practices_count"])
    logger.info("  Recalls written:        %d", stats["recalls_count"])
    if stats["errors"]:
        logger.warning("  Errors encountered (%d):", len(stats["errors"]))
        for err in stats["errors"]:
            logger.warning("    - %s", err)

    _notify_completion(run_id=run_id, stats=stats, success=success)

    if success:
        logger.info("=== Run completed successfully ===")
        sys.exit(0)
    else:
        logger.error("=== Run completed with errors — see log above ===")
        sys.exit(1)


if __name__ == "__main__":
    main()
