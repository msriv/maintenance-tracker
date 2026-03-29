import os

GCP_PROJECT_ID = os.getenv("SCRAPER_PROJECT_ID", "mototracker-491619")
FIRESTORE_DATABASE = "(default)"
SCRAPER_RATE_LIMIT_SECONDS = float(os.getenv("RATE_LIMIT_SECONDS", "1.0"))
MAX_RETRIES = int(os.getenv("MAX_RETRIES", "3"))
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
