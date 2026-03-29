"""
Scraper for bikez.com — extracts motorcycle makes, models, and year ranges.
"""
import logging
import re
from datetime import datetime
from typing import Optional
from urllib.parse import urljoin

from bs4 import BeautifulSoup
from slugify import slugify

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from models import RegistryMake, RegistryModel
from scrapers.base_scraper import BaseScraper

logger = logging.getLogger(__name__)

BASE_URL = "https://www.bikez.com"
BRANDS_URL = f"{BASE_URL}/brands/"

# Map bikez category keywords to internal VehicleType strings
_CATEGORY_MAP = {
    "scooter": "SCOOTER",
    "moped": "SCOOTER",
    "maxi scooter": "SCOOTER",
    "motorcycle": "BIKE",
    "naked": "BIKE",
    "sport": "BIKE",
    "touring": "BIKE",
    "adventure": "BIKE",
    "enduro": "BIKE",
    "motocross": "BIKE",
    "off-road": "BIKE",
    "trial": "BIKE",
    "supermoto": "BIKE",
    "cruiser": "BIKE",
    "chopper": "BIKE",
    "custom": "BIKE",
    "classic": "BIKE",
    "roadster": "BIKE",
    "streetfighter": "BIKE",
    "cafe racer": "BIKE",
    "minibike": "BIKE",
    "atv": "OTHER",
    "quad": "OTHER",
    "snowmobile": "OTHER",
    "car": "CAR",
    "truck": "TRUCK",
}

# Known country of origin for major brands (best-effort)
_BRAND_COUNTRIES = {
    "honda": "Japan",
    "yamaha": "Japan",
    "suzuki": "Japan",
    "kawasaki": "Japan",
    "ducati": "Italy",
    "bmw": "Germany",
    "ktm": "Austria",
    "triumph": "United Kingdom",
    "royal enfield": "India",
    "bajaj": "India",
    "tvs": "India",
    "hero": "India",
    "harley-davidson": "United States",
    "indian": "United States",
    "aprilia": "Italy",
    "moto guzzi": "Italy",
    "mv agusta": "Italy",
    "benelli": "Italy",
    "husqvarna": "Sweden",
    "gas gas": "Spain",
    "cf moto": "China",
    "sym": "Taiwan",
    "kymco": "Taiwan",
    "peugeot": "France",
    "piaggio": "Italy",
    "vespa": "Italy",
    "derbi": "Spain",
    "beta": "Italy",
    "sherco": "France",
    "ural": "Russia",
    "jawa": "Czech Republic",
}


def _resolve_vehicle_type(category_text: str) -> str:
    """Map a raw bikez category string to a VehicleType enum value."""
    lowered = category_text.lower().strip()
    for keyword, vehicle_type in _CATEGORY_MAP.items():
        if keyword in lowered:
            return vehicle_type
    return "BIKE"  # Default: most bikez entries are bikes


def _parse_year_range(year_text: str) -> tuple[Optional[int], Optional[int]]:
    """
    Parse year range strings like "2003-2007", "2019-", "2021", "2005 - Present".
    Returns (year_from, year_to) where year_to=None means still in production.
    """
    text = year_text.strip().replace("Present", "").replace("present", "")
    # Match "YYYY-YYYY" or "YYYY - YYYY"
    match = re.search(r"(\d{4})\s*[-–]\s*(\d{4})", text)
    if match:
        return int(match.group(1)), int(match.group(2))
    # Match "YYYY-" or "YYYY -" (ongoing)
    match = re.search(r"(\d{4})\s*[-–]", text)
    if match:
        return int(match.group(1)), None
    # Single year
    match = re.search(r"(\d{4})", text)
    if match:
        year = int(match.group(1))
        return year, year
    return 0, None


def _parse_displacement(text: str) -> Optional[int]:
    """Extract displacement in cc from strings like '599 cc', '1000cc'."""
    match = re.search(r"(\d+)\s*cc", text, re.IGNORECASE)
    if match:
        return int(match.group(1))
    return None


class BikeScraper(BaseScraper):
    """Scrape make and model data from bikez.com."""

    def scrape_makes(self) -> list[RegistryMake]:
        """Fetch the brands listing page and return all RegistryMake objects."""
        self.logger.info("Fetching brands list from %s", BRANDS_URL)
        response = self.get(BRANDS_URL)
        if response is None:
            self.logger.error("Failed to fetch brands page")
            return []

        soup = BeautifulSoup(response.text, "html.parser")
        makes: list[RegistryMake] = []

        # bikez.com brands page lists brands as anchor tags in a table/div
        brand_links = soup.select("a[href*='/brands/']")
        seen_ids: set[str] = set()

        for link in brand_links:
            href = link.get("href", "")
            # Only process links like /brands/honda.php or /brands/honda/
            if not re.search(r"/brands/[^/]+", href):
                continue
            name = link.get_text(strip=True)
            if not name or name.lower() in ("brands", "all brands", ""):
                continue

            make_id = slugify(name)
            if not make_id or make_id in seen_ids:
                continue
            seen_ids.add(make_id)

            country = _BRAND_COUNTRIES.get(name.lower())
            makes.append(
                RegistryMake(
                    id=make_id,
                    name=name,
                    country=country,
                    logo_url=None,
                    updated_at=datetime.utcnow(),
                )
            )

        self.logger.info("Found %d makes", len(makes))
        return makes

    def scrape_models(self, make_id: str) -> list[RegistryModel]:
        """
        Fetch all models for a given make from bikez.com.
        Returns an empty list if the brand page returns 404.
        """
        # bikez brand pages follow the pattern /brands/{make}.php
        brand_url = f"{BASE_URL}/brands/{make_id}.php"
        self.logger.info("Fetching models for '%s' from %s", make_id, brand_url)

        response = self.get(brand_url)
        if response is None:
            self.logger.warning("Skipping make '%s' — page not found or error", make_id)
            return []

        soup = BeautifulSoup(response.text, "html.parser")
        models: list[RegistryModel] = []

        # Each model row is typically a table row with model name and year range
        rows = soup.select("table tr")
        for row in rows:
            cells = row.find_all("td")
            if len(cells) < 2:
                continue

            # First cell: model link/name; Second cell: year range; optional third: category
            model_link = cells[0].find("a")
            if model_link is None:
                continue

            model_name = model_link.get_text(strip=True)
            if not model_name:
                continue

            year_text = cells[1].get_text(strip=True) if len(cells) > 1 else ""
            category_text = cells[2].get_text(strip=True) if len(cells) > 2 else ""
            displacement_text = cells[3].get_text(strip=True) if len(cells) > 3 else ""

            year_from, year_to = _parse_year_range(year_text)
            if year_from == 0:
                continue  # Skip rows with no parseable year

            vehicle_type = _resolve_vehicle_type(category_text)
            displacement_cc = _parse_displacement(displacement_text)
            model_slug = slugify(model_name)
            model_id = f"{make_id}_{model_slug}_{year_from}"

            # Fetch variants by visiting the model detail page
            variants = self._fetch_variants(model_link, make_id, model_slug)

            models.append(
                RegistryModel(
                    id=model_id,
                    make=make_id,
                    model=model_name,
                    year_from=year_from,
                    year_to=year_to,
                    variants=variants,
                    vehicle_type=vehicle_type,
                    displacement_cc=displacement_cc,
                    updated_at=datetime.utcnow(),
                )
            )

        self.logger.info("Found %d models for make '%s'", len(models), make_id)
        return models

    def _fetch_variants(
        self,
        model_link,
        make_id: str,
        model_slug: str,
    ) -> list[str]:
        """
        Attempt to find variant names for a model. bikez.com lists variants as
        sub-links from the model listing page. Returns a list of variant name strings.
        """
        href = model_link.get("href", "")
        if not href:
            return []

        model_url = urljoin(BASE_URL, href)
        response = self.get(model_url)
        if response is None:
            return []

        soup = BeautifulSoup(response.text, "html.parser")
        variants: list[str] = []

        # Look for variant table or list — bikez groups variants under the model page
        variant_links = soup.select("a[href*='specifications']")
        seen: set[str] = set()
        for link in variant_links:
            variant_name = link.get_text(strip=True)
            # Filter out navigation links and generic labels
            if (
                variant_name
                and len(variant_name) > 1
                and variant_name not in seen
                and not variant_name.lower().startswith(("next", "prev", "back", "home"))
            ):
                seen.add(variant_name)
                variants.append(variant_name)

        return variants[:10]  # Cap to avoid noise from navigation links

    def scrape_all(self) -> tuple[list[RegistryMake], list[RegistryModel]]:
        """
        Scrape all makes and all models from bikez.com.
        Returns (makes, models) tuple.
        """
        self.logger.info("Starting full bikez.com scrape")
        makes = self.scrape_makes()
        all_models: list[RegistryModel] = []

        for i, make in enumerate(makes):
            self.logger.info(
                "Scraping models for %s (%d/%d)", make.name, i + 1, len(makes)
            )
            models = self.scrape_models(make.id)
            all_models.extend(models)

        self.logger.info(
            "Scrape complete: %d makes, %d models total", len(makes), len(all_models)
        )
        return makes, all_models
