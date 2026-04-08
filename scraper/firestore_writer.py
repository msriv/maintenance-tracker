"""
Firestore writer using firebase-admin SDK with Application Default Credentials.
Supports batched writes (500 docs per batch — Firestore hard limit).
"""
import logging
import uuid
from datetime import datetime
from typing import Any

import firebase_admin
from firebase_admin import credentials, firestore, storage

from models import (
    BestPractice,
    RecallAlert,
    RegistryMake,
    RegistryModel,
    RegistrySchedule,
)

logger = logging.getLogger(__name__)

_FIRESTORE_BATCH_LIMIT = 500


def _to_dict(obj: Any) -> dict:
    """Convert a Pydantic model to a plain dict suitable for Firestore."""
    return obj.model_dump()


class FirestoreWriter:
    """Write scraped data to Firestore using batched writes."""

    def __init__(self, project_id: str, storage_bucket: str = ""):
        self.project_id = project_id
        self.storage_bucket = storage_bucket
        self._init_firebase()
        self.db = firestore.client()
        self.image_uploader = None
        if storage_bucket:
            from image_uploader import ImageUploader
            self.image_uploader = ImageUploader()

    def _init_firebase(self) -> None:
        """Initialise the Firebase Admin SDK if not already initialised.
        Uses Application Default Credentials (ADC) — works automatically
        in Cloud Run via the attached service account."""
        if not firebase_admin._apps:
            app_options = {"projectId": self.project_id}
            if self.storage_bucket:
                app_options["storageBucket"] = self.storage_bucket
            firebase_admin.initialize_app(options=app_options)
            logger.info("Firebase Admin SDK initialised for project '%s'", self.project_id)
        else:
            logger.debug("Firebase Admin SDK already initialised")

    def _batch_write(self, collection_path: str, items: list, id_getter) -> int:
        """
        Write a list of Pydantic model instances to a Firestore collection
        using batched writes. Returns the number of documents written.

        Args:
            collection_path: Firestore collection path (e.g. "registry_makes")
            items: List of Pydantic model instances
            id_getter: Callable that returns the document ID from an item
        """
        if not items:
            return 0

        collection_ref = self.db.collection(collection_path)
        total_written = 0
        batch_start = 0

        while batch_start < len(items):
            batch_items = items[batch_start : batch_start + _FIRESTORE_BATCH_LIMIT]
            batch = self.db.batch()

            for item in batch_items:
                doc_id = id_getter(item)
                doc_ref = collection_ref.document(doc_id)
                batch.set(doc_ref, _to_dict(item), merge=True)

            batch.commit()
            total_written += len(batch_items)
            logger.debug(
                "Committed batch of %d documents to '%s' (total so far: %d)",
                len(batch_items),
                collection_path,
                total_written,
            )
            batch_start += _FIRESTORE_BATCH_LIMIT

        return total_written

    def upsert_makes(self, makes: list[RegistryMake]) -> int:
        """Upload logos to Storage then upsert makes into Firestore."""
        logger.info("Upserting %d makes", len(makes))
        if self.image_uploader:
            for make in makes:
                if make.logo_url and not make.logo_url.startswith("https://storage.googleapis.com"):
                    stored = self.image_uploader.upload_make_logo(make.id, make.logo_url)
                    if stored:
                        make.logo_url = stored
        count = self._batch_write("registry_makes", makes, lambda m: m.id)
        logger.info("Upserted %d makes", count)
        return count

    def upsert_models(self, models: list[RegistryModel]) -> int:
        """Upload hero images to Storage then upsert models into Firestore."""
        logger.info("Upserting %d models", len(models))
        if self.image_uploader:
            for model in models:
                if model.image_url and not model.image_url.startswith("https://storage.googleapis.com"):
                    stored = self.image_uploader.upload_model_image(model.id, model.image_url)
                    if stored:
                        model.image_url = stored
        count = self._batch_write("registry_models", models, lambda m: m.id)
        logger.info("Upserted %d models", count)
        return count

    def upsert_schedules(self, schedules: list[RegistrySchedule]) -> int:
        """Upsert all schedules into registry_schedules/{schedule_id}. Returns doc count."""
        logger.info("Upserting %d schedules", len(schedules))
        count = self._batch_write("registry_schedules", schedules, lambda s: s.id)
        logger.info("Upserted %d schedules", count)
        return count

    def upsert_best_practices(self, practices: list[BestPractice]) -> int:
        """Upsert all best practices into best_practices/{id}. Returns doc count."""
        logger.info("Upserting %d best practices", len(practices))
        count = self._batch_write("best_practices", practices, lambda p: p.id)
        logger.info("Upserted %d best practices", count)
        return count

    def upsert_recalls(self, recalls: list[RecallAlert]) -> int:
        """Upsert all recalls into recalls/{recall_id}. Returns doc count."""
        logger.info("Upserting %d recalls", len(recalls))
        count = self._batch_write("recalls", recalls, lambda r: r.id)
        logger.info("Upserted %d recalls", count)
        return count

    def get_existing_make_ids(self) -> set[str]:
        """Return the set of make IDs already present in registry_makes."""
        docs = self.db.collection("registry_makes").stream()
        ids = {doc.id for doc in docs}
        logger.info("Found %d existing makes in Firestore", len(ids))
        return ids

    def update_sync_metadata(self, run_id: str, stats: dict) -> None:
        """
        Write run metadata to sync_metadata/latest and an archival timestamped doc.
        stats dict should include keys like: makes_count, models_count, etc.
        """
        now = datetime.utcnow()
        payload = {
            "run_id": run_id,
            "completed_at": now,
            "project_id": self.project_id,
            **stats,
        }

        # Overwrite the "latest" sentinel document
        latest_ref = self.db.collection("sync_metadata").document("latest")
        latest_ref.set(payload)

        # Also write an immutable archive entry under the run_id
        archive_ref = self.db.collection("sync_metadata").document(run_id)
        archive_ref.set(payload)

        logger.info(
            "Sync metadata updated: run_id=%s, stats=%s", run_id, stats
        )
