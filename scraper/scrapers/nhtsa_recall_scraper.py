"""
Scraper for NHTSA recall data using the free public API (no auth required).
API: https://api.nhtsa.gov/recalls/recallsByVehicle
"""
import logging
import re
from datetime import datetime
from typing import Optional

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from models import RecallAlert, RegistryModel
from scrapers.base_scraper import BaseScraper

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


def _model_year_range(model: RegistryModel) -> range:
    """Generate the range of model years to query for a RegistryModel."""
    current_year = datetime.utcnow().year
    year_to = model.year_to if model.year_to is not None else current_year
    # Clamp range to a sensible window; NHTSA covers back to ~1970s
    return range(max(model.year_from, 1980), min(year_to, current_year) + 1)


class NHTSARecallScraper(BaseScraper):
    """Fetch recall data from the NHTSA public recalls API."""

    def fetch_recalls_for_vehicle(
        self, make: str, model: str, year: int
    ) -> list[RecallAlert]:
        """
        Query the NHTSA API for a single make/model/year combination.
        Returns a (possibly empty) list of RecallAlert objects.
        """
        params = {"make": make, "model": model, "modelYear": str(year)}
        self.logger.debug("Querying NHTSA for %s %s %d", make, model, year)

        response = self.get(NHTSA_BASE_URL, params=params)
        if response is None:
            self.logger.warning(
                "No response from NHTSA for %s %s %d", make, model, year
            )
            return []

        try:
            data = response.json()
        except ValueError:
            self.logger.warning(
                "Invalid JSON from NHTSA for %s %s %d", make, model, year
            )
            return []

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

    def fetch_all_recalls(self, models: list[RegistryModel]) -> list[RecallAlert]:
        """
        Fetch recalls for every model in the provided list, across all model years.
        De-duplicates by campaign number so the same recall isn't returned multiple times.
        """
        self.logger.info("Fetching recalls for %d models", len(models))
        seen_campaign_ids: set[str] = set()
        all_recalls: list[RecallAlert] = []

        for registry_model in models:
            for year in _model_year_range(registry_model):
                recalls = self.fetch_recalls_for_vehicle(
                    make=registry_model.make,
                    model=registry_model.model,
                    year=year,
                )
                for recall in recalls:
                    if recall.id not in seen_campaign_ids:
                        seen_campaign_ids.add(recall.id)
                        all_recalls.append(recall)

        self.logger.info("Total unique recalls fetched: %d", len(all_recalls))
        return all_recalls
