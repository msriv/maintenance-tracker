"""
ImageUploader — downloads images from scrape sources and re-hosts them in
Firebase Storage so the app always reads from stable, owned URLs.

Storage layout:
  registry/makes/{make_id}/logo.{ext}
  registry/models/{model_id}/hero.{ext}
"""
import logging
from typing import Optional

import requests
from firebase_admin import storage

import config

logger = logging.getLogger(__name__)

_MAX_IMAGE_BYTES = 4 * 1024 * 1024  # 4 MB — skip anything larger
_CONTENT_TYPE_TO_EXT = {
    "image/jpeg": "jpg",
    "image/jpg": "jpg",
    "image/png": "png",
    "image/webp": "webp",
    "image/gif": "gif",
    "image/svg+xml": "svg",
}


class ImageUploader:
    """Download images and upload them to Firebase Storage."""

    def __init__(self) -> None:
        self.bucket = storage.bucket(config.STORAGE_BUCKET)
        self._session = requests.Session()
        self._session.headers.update({
            "User-Agent": (
                "Mozilla/5.0 (compatible; MotoTrackerBot/1.0; "
                "+https://mototracker-491619.web.app)"
            )
        })

    def upload_make_logo(self, make_id: str, source_url: str) -> Optional[str]:
        """Download and store a brand logo. Returns the public Storage URL."""
        return self._upload(source_url, dest_prefix=f"registry/makes/{make_id}/logo")

    def upload_model_image(self, model_id: str, source_url: str) -> Optional[str]:
        """Download and store a model hero image. Returns the public Storage URL."""
        return self._upload(source_url, dest_prefix=f"registry/models/{model_id}/hero")

    def _upload(self, source_url: str, dest_prefix: str) -> Optional[str]:
        if not source_url:
            return None
        try:
            resp = self._session.get(source_url, timeout=20, stream=True)
            resp.raise_for_status()

            # Enforce size cap before reading into memory
            content_length = int(resp.headers.get("Content-Length", 0))
            if content_length > _MAX_IMAGE_BYTES:
                logger.warning("Skipping oversized image (%d bytes): %s", content_length, source_url)
                return None

            data = resp.content
            if len(data) > _MAX_IMAGE_BYTES:
                logger.warning("Skipping oversized image (%d bytes): %s", len(data), source_url)
                return None

            content_type = resp.headers.get("Content-Type", "image/jpeg").split(";")[0].strip()
            ext = _CONTENT_TYPE_TO_EXT.get(content_type, "jpg")
            dest_path = f"{dest_prefix}.{ext}"

            blob = self.bucket.blob(dest_path)

            # Skip re-upload if already stored
            if blob.exists():
                blob.make_public()
                return blob.public_url

            blob.upload_from_string(data, content_type=content_type)
            blob.make_public()
            logger.info("Uploaded %s → gs://%s/%s", source_url, config.STORAGE_BUCKET, dest_path)
            return blob.public_url

        except Exception as exc:
            logger.warning("Image upload failed for %s: %s", source_url, exc)
            return None
