import os

GCP_PROJECT_ID = os.getenv("SCRAPER_PROJECT_ID", "mototracker-491619")
FIRESTORE_DATABASE = "(default)"
SCRAPER_RATE_LIMIT_SECONDS = float(os.getenv("RATE_LIMIT_SECONDS", "1.0"))
MAX_RETRIES = int(os.getenv("MAX_RETRIES", "3"))
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
# Optional: Slack webhook URL (or any service accepting {"text": "..."} JSON POST)
NOTIFY_WEBHOOK_URL = os.getenv("NOTIFY_WEBHOOK_URL", "")

# NHTSA recall scraper tuning
NHTSA_RATE_LIMIT = float(os.getenv("NHTSA_RATE_LIMIT", "1.0"))   # requests/sec
NHTSA_BATCH_SIZE = int(os.getenv("NHTSA_BATCH_SIZE", "100"))      # queries per batch
NHTSA_BATCH_PAUSE = float(os.getenv("NHTSA_BATCH_PAUSE_SECONDS", "10.0"))  # sec between batches
NHTSA_WORKERS = int(os.getenv("NHTSA_WORKERS", "5"))              # parallel threads
