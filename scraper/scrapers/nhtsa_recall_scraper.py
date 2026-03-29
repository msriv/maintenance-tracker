"""
Scraper for NHTSA recall data using the free public API (no auth required).
API: https://api.nhtsa.gov/recalls/recallsByVehicle
"""
import logging
import re
import threading
import time
from datetime import datetime
from typing import Optional

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
from models import RecallAlert, RegistryModel
from scrapers.base_scraper import BaseScraper


class _RateLimiter:
    """
    Thread-safe token bucket rate limiter.
    Ensures at most `rate` requests per second globally across all threads.
    """

    def __init__(self, rate: float):
        self._min_interval = 1.0 / rate
        self._lock = threading.Lock()
        self._last = 0.0

    def acquire(self) -> None:
        with self._lock:
            now = time.monotonic()
            wait = self._min_interval - (now - self._last)
            if wait > 0:
                time.sleep(wait)
            self._last = time.monotonic()

logger = logging.getLogger(__name__)

NHTSA_BASE_URL = "https://api.nhtsa.gov/recalls/recallsByVehicle"
NHTSA_CAMPAIGN_URL = "https://www.nhtsa.gov/vehicle-safety/recalls#recall--"

# Keywords for severity classification
_CRITICAL_PATTERN = re.compile(
    r"\b(death|fatal|fatality|fire|explosion|rollover|ejection|loss of control)\b",
    re.IGNORECASE,
)
_MODERATE_PATTERN = re.compile(
    r"\b(injury|injur|crash|accident|collision|fracture|laceration)\b",
    re.IGNORECASE,
)


def _classify_severity(consequence: str) -> str:
    """
    Classify recall severity based on consequence description keywords.
    CRITICAL  — mentions death, fire, rollover, etc.
    MODERATE  — mentions injury, crash, accident
    MINOR     — everything else
    """
    if _CRITICAL_PATTERN.search(consequence):
        return "CRITICAL"
    if _MODERATE_PATTERN.search(consequence):
        return "MODERATE"
    return "MINOR"


def _parse_date(raw: Optional[str]) -> str:
    """
    Normalise NHTSA date strings to ISO format (YYYY-MM-DD).
    NHTSA returns dates as "YYYYMMDD" or already formatted strings.
    Falls back to empty string on parse failure.
    """
    if not raw:
        return ""
    # "20231015" -> "2023-10-15"
    match = re.match(r"^(\d{4})(\d{2})(\d{2})$", raw.strip())
    if match:
        return f"{match.group(1)}-{match.group(2)}-{match.group(3)}"
    # Already ISO-ish
    if re.match(r"^\d{4}-\d{2}-\d{2}", raw):
        return raw[:10]
    return raw


def _model_query_years(model: RegistryModel) -> list[int]:
    """
    Return the minimal set of years to query for a model.
    Querying year_from catches recalls on early units; querying year_to (or
    current year) catches recalls on the latest units. NHTSA campaign records
    include their full year_from/year_to range, so two queries are enough to
    discover all campaigns that overlap with a model's production window.
    """
    current_year = datetime.utcnow().year
    start = max(model.year_from, 1980)
    end = min(model.year_to if model.year_to is not None else current_year, current_year)
    if start == end:
        return [start]
    return [start, end]


class NHTSARecallScraper(BaseScraper):
    """Fetch recall data from the NHTSA public recalls API."""

    # Shared across all threads in this scraper instance
    # Default: 1 request/second — conservative, unlikely to be flagged
    _NHTSA_RATE = float(os.getenv("NHTSA_RATE_LIMIT", "1.0"))
    _BATCH_SIZE = int(os.getenv("NHTSA_BATCH_SIZE", "100"))
    _BATCH_PAUSE = float(os.getenv("NHTSA_BATCH_PAUSE_SECONDS", "10.0"))
    _WORKERS = int(os.getenv("NHTSA_WORKERS", "5"))

    def __init__(self):
        super().__init__()
        # One shared rate limiter for all threads — enforces global req/s ceiling
        self._nhtsa_limiter = _RateLimiter(rate=self._NHTSA_RATE)

    def get(self, url, params=None, timeout=30, skip_rate_limit=False):
        """Override to treat HTTP 400 as a valid empty-result response.

        NHTSA returns 400 (with a valid JSON body) for makes/models it does
        not recognise — motorcycles and niche brands in particular. Passing
        the response through lets the caller inspect the body rather than
        silently dropping it.

        Uses a shared thread-safe rate limiter instead of BaseScraper's
        per-instance limiter, so parallel workers don't bypass the ceiling.
        """
        if not skip_rate_limit:
            self._nhtsa_limiter.acquire()
        import requests as _requests  # noqa: PLC0415 — lazy to avoid circular import
        try:
            response = self.session.get(url, params=params, timeout=timeout)
            if response.status_code in (200, 400):
                return response
            if response.status_code == 404:
                self.logger.debug("404 for %s — skipping", url)
                return None
            response.raise_for_status()
            return response
        except _requests.exceptions.HTTPError as exc:
            self.logger.warning("HTTP error fetching %s: %s", url, exc)
            return None
        except _requests.exceptions.ConnectionError as exc:
            self.logger.warning("Connection error fetching %s: %s", url, exc)
            return None
        except _requests.exceptions.Timeout:
            self.logger.warning("Timeout fetching %s", url)
            return None
        except _requests.exceptions.RequestException as exc:
            self.logger.error("Unexpected error fetching %s: %s", url, exc)
            return None

    def fetch_recalls_for_vehicle(
        self, make: str, model: str, year: int
    ) -> list[RecallAlert] | None:
        """
        Query the NHTSA API for a single make/model/year combination.
        Returns a list of RecallAlert objects (empty = no recalls found),
        or None if a network/parse error occurred.
        """
        params = {"make": make, "model": model, "modelYear": str(year)}
        self.logger.debug("Querying NHTSA for %s %s %d", make, model, year)

        response = self.get(NHTSA_BASE_URL, params=params)
        if response is None:
            self.logger.warning(
                "No response from NHTSA for %s %s %d", make, model, year
            )
            return None

        try:
            data = response.json()
        except ValueError:
            self.logger.warning(
                "Invalid JSON from NHTSA for %s %s %d", make, model, year
            )
            return None

        results = data.get("results", [])
        if not results:
            return []

        alerts: list[RecallAlert] = []
        for item in results:
            campaign_number = item.get("NHTSACampaignNumber", "").strip()
            if not campaign_number:
                continue

            consequence = item.get("Consequence", "") or ""
            severity = _classify_severity(consequence)

            # NHTSA year range for this campaign
            start_my = item.get("ModelYear", year)
            try:
                year_from = int(start_my)
            except (TypeError, ValueError):
                year_from = year
            year_to_raw = item.get("EndModelYear", start_my)
            try:
                year_to = int(year_to_raw)
            except (TypeError, ValueError):
                year_to = year_from

            alerts.append(
                RecallAlert(
                    id=campaign_number,
                    make=item.get("Make", make).title(),
                    model=item.get("Model", model).title(),
                    year_from=year_from,
                    year_to=year_to,
                    component=item.get("Component", "").strip(),
                    consequence=consequence.strip(),
                    remedy=(item.get("Remedy", "") or "").strip(),
                    description=(item.get("Summary", "") or "").strip(),
                    severity=severity,
                    issued_date=_parse_date(item.get("ReportReceivedDate")),
                    nhtsa_url=f"{NHTSA_CAMPAIGN_URL}{campaign_number}",
                    updated_at=datetime.utcnow(),
                )
            )

        return alerts

    def fetch_all_recalls(
        self,
        models: list[RegistryModel],
        max_consecutive_errors: int = config.MAX_RETRIES,
    ) -> list[RecallAlert]:
        """
        Fetch recalls for every model using a thread pool with rate limiting and
        batch pausing.

        Strategy:
        - Each model is queried for year_from and year_to only (2 queries max),
          cutting total requests from ~20k to ~3k for a full model list.
        - A shared RateLimiter caps the global request rate (NHTSA_RATE_LIMIT/s).
        - Queries are processed in batches (NHTSA_BATCH_SIZE), with a pause of
          NHTSA_BATCH_PAUSE_SECONDS between batches so NHTSA doesn't see a
          sustained burst.
        - Aborts early if error count reaches max_consecutive_errors.

        Env vars (all optional):
          NHTSA_RATE_LIMIT           requests/sec   default 1.0
          NHTSA_BATCH_SIZE           queries/batch  default 100
          NHTSA_BATCH_PAUSE_SECONDS  seconds        default 10.0
          NHTSA_WORKERS              thread count   default 5
        """
        from concurrent.futures import ThreadPoolExecutor, as_completed

        tasks: list[tuple[str, str, int]] = []
        for m in models:
            for year in _model_query_years(m):
                tasks.append((m.make, m.model, year))

        total = len(tasks)
        self.logger.info(
            "Fetching recalls: %d models → %d queries | "
            "rate=%.1f/s  batch=%d  pause=%.0fs  workers=%d",
            len(models), total,
            self._NHTSA_RATE, self._BATCH_SIZE, self._BATCH_PAUSE, self._WORKERS,
        )

        seen_campaign_ids: set[str] = set()
        all_recalls: list[RecallAlert] = []
        error_count = 0
        completed = 0

        # Slice tasks into batches
        batches = [tasks[i:i + self._BATCH_SIZE]
                   for i in range(0, total, self._BATCH_SIZE)]

        for batch_num, batch in enumerate(batches, start=1):
            self.logger.info(
                "Recall batch %d/%d (%d queries) — %d found so far",
                batch_num, len(batches), len(batch), len(all_recalls),
            )

            with ThreadPoolExecutor(max_workers=self._WORKERS) as pool:
                futures = {
                    pool.submit(
                        self.fetch_recalls_for_vehicle,
                        make=make, model=model, year=year,
                    ): (make, model, year)
                    for make, model, year in batch
                }
                for future in as_completed(futures):
                    completed += 1
                    recalls = future.result()

                    if recalls is None:
                        error_count += 1
                        if error_count >= max_consecutive_errors:
                            self.logger.error(
                                "Aborting recall fetch: %d consecutive errors "
                                "after %d/%d queries",
                                error_count, completed, total,
                            )
                            return all_recalls
                    else:
                        error_count = 0
                        for recall in recalls:
                            if recall.id not in seen_campaign_ids:
                                seen_campaign_ids.add(recall.id)
                                all_recalls.append(recall)

            # Pause between batches — let NHTSA breathe
            if batch_num < len(batches):
                self.logger.info(
                    "Batch %d done — pausing %.0fs before next batch",
                    batch_num, self._BATCH_PAUSE,
                )
                time.sleep(self._BATCH_PAUSE)

        self.logger.info("Total unique recalls fetched: %d", len(all_recalls))
        return all_recalls
