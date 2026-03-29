"""
Scraper for BikeWale (bikewale.com) — Indian motorcycle market data.
Supplements bikez.com with Indian-specific makes and models (Hero, Bajaj, TVS, etc.)
that bikez may lack or have incomplete data for.
"""
import logging
import re
from datetime import datetime
from typing import Optional

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from models import RegistryMake, RegistryModel
from scrapers.base_scraper import BaseScraper

logger = logging.getLogger(__name__)

BASE_URL = "https://www.bikewale.com"
BRANDS_URL = f"{BASE_URL}/new-bikes-in-india/"

# BikeWale model status codes
_STATUS_ACTIVE = 2
_STATUS_DISCONTINUED = 3

# Known country of origin for BikeWale brands
_BRAND_COUNTRIES = {
    "hero": "India",
    "bajaj": "India",
    "tvs": "India",
    "royal-enfield": "India",
    "royalenfield": "India",
    "ola": "India",
    "ather": "India",
    "revolt": "India",
    "ultraviolette": "India",
    "oben": "India",
    "pure-ev": "India",
    "matter": "India",
    "hop-electric": "India",
    "honda": "Japan",
    "yamaha": "Japan",
    "suzuki": "Japan",
    "kawasaki": "Japan",
    "ducati": "Italy",
    "bmw": "Germany",
    "ktm": "Austria",
    "triumph": "United Kingdom",
    "aprilia": "Italy",
    "vespa": "Italy",
    "husqvarna": "Sweden",
    "cfmoto": "China",
    "benelli": "Italy",
    "harley-davidson": "United States",
    "indian": "United States",
    "jawa": "Czech Republic",
    "yezdi": "Czech Republic",
}

# Map BikeWale segment/type signals to VehicleType
_ELECTRIC_VEHICLE_TYPE = "ELECTRIC"


def _parse_year_from_launched(launched_on: str) -> Optional[int]:
    """Parse year from BikeWale launchedOn string like '11/14/2019 00:00:00'."""
    if not launched_on:
        return None
    match = re.search(r"(\d{4})", launched_on)
    if match:
        return int(match.group(1))
    return None


def _parse_year_range_from_name(model_name: str) -> tuple[Optional[int], Optional[int]]:
    """
    Extract year range from model names like 'CB350RS [2021-2022]' or 'Activa [2000-2015]'.
    Returns (year_from, year_to) or (None, None) if no bracket found.
    """
    match = re.search(r"\[(\d{4})-(\d{4})\]", model_name)
    if match:
        return int(match.group(1)), int(match.group(2))
    match = re.search(r"\[(\d{4})\]", model_name)
    if match:
        year = int(match.group(1))
        return year, year
    return None, None


def _clean_model_name(model_name: str) -> str:
    """Strip year bracket suffixes from model names."""
    return re.sub(r"\s*\[\d{4}(?:-\d{4})?\]$", "", model_name).strip()


class BikeWaleScraper(BaseScraper):
    """Scrape make and model data from bikewale.com (Indian market focus)."""

    def scrape_makes(self) -> list[RegistryMake]:
        """Fetch all brands from the BikeWale new bikes listing page."""
        self.logger.info("Fetching brands list from %s", BRANDS_URL)
        response = self.get(BRANDS_URL)
        if response is None:
            self.logger.error("Failed to fetch BikeWale brands page")
            return []

        # Brand pages follow /{slug}-bikes/ pattern
        hrefs = re.findall(r'href="/([a-z0-9-]+)-bikes/"', response.text)
        seen: set[str] = set()
        makes: list[RegistryMake] = []

        for slug in hrefs:
            if slug in seen or slug in ("new", "used", "upcoming", "electric"):
                continue
            seen.add(slug)

            # Derive display name: royalenfield → Royal Enfield, ktm → KTM
            name = slug.replace("-", " ").title()
            # Fix known casing
            name_overrides = {
                "Royalenfield": "Royal Enfield",
                "Tvs": "TVS",
                "Ktm": "KTM",
                "Bmw": "BMW",
                "Cfmoto": "CFMoto",
                "Bgauss": "BGauss",
                "Ola": "OLA",
                "Qj Motor": "QJ Motor",
                "Bsa": "BSA",
                "Mv Agusta": "MV Agusta",
            }
            name = name_overrides.get(name, name)

            country = _BRAND_COUNTRIES.get(slug)
            makes.append(
                RegistryMake(
                    id=f"bw_{slug}",
                    name=name,
                    country=country,
                    logo_url=None,
                    updated_at=datetime.utcnow(),
                )
            )

        self.logger.info("Found %d makes on BikeWale", len(makes))
        return makes

    def scrape_models(self, make_slug: str) -> list[RegistryModel]:
        """
        Fetch all models for a given make from BikeWale.
        make_slug should be the raw slug (e.g. 'honda', 'royalenfield') without 'bw_' prefix.
        """
        brand_url = f"{BASE_URL}/{make_slug}-bikes/"
        self.logger.info("Fetching BikeWale models for '%s'", make_slug)

        response = self.get(brand_url)
        if response is None:
            self.logger.warning("Skipping BikeWale make '%s' — page unavailable", make_slug)
            return []

        html = response.text
        current_year = datetime.utcnow().year

        # Core pattern: fields that always appear consecutively
        # makeMaskingName → modelId → modelName → modelMaskingName → ... → status
        pattern = (
            r'"makeMaskingName":"([^"]+)"'
            r',"modelId":(\d+)'
            r',"modelName":"([^"]+)"'
            r',"modelMaskingName":"([^"]+)"'
            r'[^}]{0,300}'
            r'"status":(\d+)'
        )
        matches = re.findall(pattern, html, re.DOTALL)

        # Build a lookup of launchedOn per modelMaskingName from a separate pass
        launched_lookup: dict[str, str] = {}
        for lo, ms in re.findall(r'"launchedOn":"([^"]*)"[^}]{0,600}"modelMaskingName":"([^"]+)"', html):
            launched_lookup.setdefault(ms, lo)

        # Electric vehicle detection per modelMaskingName
        ev_lookup: dict[str, bool] = {}
        for ms, ev in re.findall(r'"modelMaskingName":"([^"]+)"[^}]{0,200}"isElectricVehicle":(true|false)', html):
            ev_lookup[ms] = ev == "true"

        seen_slugs: set[str] = set()
        models: list[RegistryModel] = []

        # Known EV-only brands — fallback when isElectricVehicle field is absent
        _ev_brands = {"ather", "ola", "revolt", "ultraviolette", "oben", "pure-ev",
                      "matter", "hop-electric", "river", "raptee-hv", "bgauss",
                      "lectrix", "ivoomi", "okinawa", "ampere", "gemopai",
                      "evolet", "simple-energy", "bounce", "vida", "kinetic-green"}

        for make_mask, model_id_str, model_name, model_slug, status_str in matches:
            # Only keep models belonging to this make's page
            if make_mask != make_slug:
                continue
            if model_slug in seen_slugs:
                continue
            seen_slugs.add(model_slug)

            is_electric = ev_lookup.get(model_slug, make_slug in _ev_brands)
            status = int(status_str)
            launched_on = launched_lookup.get(model_slug, "")

            # Determine year range
            bracket_from, bracket_to = _parse_year_range_from_name(model_name)
            clean_name = _clean_model_name(model_name)

            if bracket_from:
                year_from = bracket_from
                year_to = bracket_to
            else:
                year_from = _parse_year_from_launched(launched_on) or current_year
                year_to = None if status == _STATUS_ACTIVE else current_year

            vehicle_type = _ELECTRIC_VEHICLE_TYPE if is_electric else "BIKE"

            models.append(
                RegistryModel(
                    id=f"bw_{make_mask}_{model_slug}_{year_from}",
                    make=f"bw_{make_mask}",
                    model=clean_name,
                    year_from=year_from,
                    year_to=year_to,
                    variants=[],
                    vehicle_type=vehicle_type,
                    displacement_cc=None,
                    updated_at=datetime.utcnow(),
                )
            )

        self.logger.info("Found %d models for BikeWale make '%s'", len(models), make_slug)
        return models

    def scrape_all(self) -> tuple[list[RegistryMake], list[RegistryModel]]:
        """Scrape all makes and models from BikeWale."""
        self.logger.info("Starting full BikeWale scrape")
        makes = self.scrape_makes()
        all_models: list[RegistryModel] = []

        for i, make in enumerate(makes):
            self.logger.info(
                "Scraping BikeWale models for %s (%d/%d)", make.name, i + 1, len(makes)
            )
            # make.id is 'bw_{slug}' — strip prefix for URL
            slug = make.id.removeprefix("bw_")
            models = self.scrape_models(slug)
            all_models.extend(models)

        self.logger.info(
            "BikeWale scrape complete: %d makes, %d models", len(makes), len(all_models)
        )
        return makes, all_models
