import logging
import random
import time
from typing import Optional

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config


class BaseScraper:
    """Base class for all scrapers providing shared HTTP session, rate limiting, and retry logic."""

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.session = self._build_session()
        self.rate_limit_seconds = config.SCRAPER_RATE_LIMIT_SECONDS
        self._last_request_time: float = 0.0

    def _build_session(self) -> requests.Session:
        session = requests.Session()
        retry_strategy = Retry(
            total=config.MAX_RETRIES,
            status_forcelist=[429, 500, 502, 503, 504],
            backoff_factor=2,
            allowed_methods=["GET"],
            raise_on_status=False,
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("https://", adapter)
        session.mount("http://", adapter)
        session.headers.update(
            {
                "User-Agent": (
                    "Mozilla/5.0 (compatible; MotoTrackerBot/1.0; "
                    "+https://github.com/mototracker)"
                ),
                "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Accept-Language": "en-US,en;q=0.5",
            }
        )
        return session

    def _rate_limit(self, jitter: bool = True) -> None:
        """Enforce rate limiting with optional random jitter between requests."""
        now = time.monotonic()
        elapsed = now - self._last_request_time
        wait = self.rate_limit_seconds - elapsed
        if jitter:
            wait += random.uniform(0.5, 1.5)
        if wait > 0:
            time.sleep(wait)
        self._last_request_time = time.monotonic()

    def get(
        self,
        url: str,
        params: Optional[dict] = None,
        timeout: int = 30,
        skip_rate_limit: bool = False,
    ) -> Optional[requests.Response]:
        """Perform a rate-limited GET request with retry and error handling."""
        if not skip_rate_limit:
            self._rate_limit()

        try:
            response = self.session.get(url, params=params, timeout=timeout)
            if response.status_code == 404:
                self.logger.debug("404 for %s — skipping", url)
                return None
            response.raise_for_status()
            return response
        except requests.exceptions.HTTPError as exc:
            self.logger.warning("HTTP error fetching %s: %s", url, exc)
            return None
        except requests.exceptions.ConnectionError as exc:
            self.logger.warning("Connection error fetching %s: %s", url, exc)
            return None
        except requests.exceptions.Timeout:
            self.logger.warning("Timeout fetching %s", url)
            return None
        except requests.exceptions.RequestException as exc:
            self.logger.error("Unexpected error fetching %s: %s", url, exc)
            return None
