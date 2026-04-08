#!/usr/bin/env bash
# MotoTracker Scraper — GCP deployment script
#
# Usage:
#   ./deploy.sh              — build image + deploy both jobs + set up scheduler
#   ./deploy.sh --run-now    — same, then immediately trigger the full-scrape job
#
set -euo pipefail

PROJECT_ID="mototracker-491619"
REGION="us-central1"
SERVICE_ACCOUNT="mototracker-scraper"
SERVICE_ACCOUNT_EMAIL="${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com"
IMAGE="gcr.io/${PROJECT_ID}/mototracker-scraper:latest"

# Two Cloud Run Jobs sharing the same image
FULL_JOB="mototracker-scraper-full"
INCREMENTAL_JOB="mototracker-scraper-incremental"

# Weekly scheduler triggers the incremental job
SCHEDULER_JOB_NAME="mototracker-scraper-weekly"

RUN_NOW=false
for arg in "$@"; do
  case $arg in
    --run-now) RUN_NOW=true ;;
  esac
done

STORAGE_BUCKET="${PROJECT_ID}.firebasestorage.app"
ENV_VARS="SCRAPER_PROJECT_ID=${PROJECT_ID},STORAGE_BUCKET=${STORAGE_BUCKET},LOG_LEVEL=INFO,RATE_LIMIT_SECONDS=1.0,MAX_RETRIES=3,NOTIFY_WEBHOOK_URL=${NOTIFY_WEBHOOK_URL:-},NHTSA_RATE_LIMIT=1.0,NHTSA_BATCH_SIZE=100,NHTSA_BATCH_PAUSE_SECONDS=10,NHTSA_WORKERS=5"

echo "=== MotoTracker Scraper Deployment ==="
echo "Project:         ${PROJECT_ID}"
echo "Region:          ${REGION}"
echo "Image:           ${IMAGE}"
echo "Service account: ${SERVICE_ACCOUNT_EMAIL}"
echo ""

# ------------------------------------------------------------------
# 1. Set active project
# ------------------------------------------------------------------
echo "[1/7] Setting active GCP project..."
gcloud config set project "${PROJECT_ID}"

# ------------------------------------------------------------------
# 2. Enable required APIs
# ------------------------------------------------------------------
echo "[2/7] Enabling required GCP APIs..."
gcloud services enable \
  run.googleapis.com \
  cloudscheduler.googleapis.com \
  firestore.googleapis.com \
  containerregistry.googleapis.com \
  cloudbuild.googleapis.com \
  --project="${PROJECT_ID}"
echo "      APIs enabled."

# ------------------------------------------------------------------
# 3. Create service account (idempotent)
# ------------------------------------------------------------------
echo "[3/7] Creating service account '${SERVICE_ACCOUNT}'..."
if gcloud iam service-accounts describe "${SERVICE_ACCOUNT_EMAIL}" \
    --project="${PROJECT_ID}" &>/dev/null; then
  echo "      Service account already exists — skipping."
else
  gcloud iam service-accounts create "${SERVICE_ACCOUNT}" \
    --display-name="MotoTracker Scraper" \
    --project="${PROJECT_ID}"
  echo "      Service account created."
fi

# ------------------------------------------------------------------
# 4. Grant IAM roles
# ------------------------------------------------------------------
echo "[4/7] Granting IAM roles..."
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/datastore.user" \
  --condition=None --quiet

gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/run.invoker" \
  --condition=None --quiet

# Needed to upload make logos and model images to Firebase Storage
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/storage.objectAdmin" \
  --condition=None --quiet
echo "      IAM roles granted."

# ------------------------------------------------------------------
# 5. Build and push Docker image
# ------------------------------------------------------------------
echo "[5/7] Building and pushing Docker image..."
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"
gcloud builds submit --tag "${IMAGE}" --project="${PROJECT_ID}" .
echo "      Image pushed: ${IMAGE}"

# ------------------------------------------------------------------
# 6. Deploy Cloud Run Jobs
# ------------------------------------------------------------------
echo "[6/7] Deploying Cloud Run Jobs..."

_upsert_job() {
  local job_name="$1"
  local args="$2"
  local timeout="$3"
  local label="$4"

  echo "      ${label}..."
  if gcloud run jobs describe "${job_name}" \
      --region="${REGION}" --project="${PROJECT_ID}" &>/dev/null; then
    gcloud run jobs update "${job_name}" \
      --image="${IMAGE}" \
      --region="${REGION}" \
      --project="${PROJECT_ID}" \
      --service-account="${SERVICE_ACCOUNT_EMAIL}" \
      --set-env-vars="${ENV_VARS}" \
      --args="${args}" \
      --max-retries=1 \
      --task-timeout="${timeout}"
  else
    gcloud run jobs create "${job_name}" \
      --image="${IMAGE}" \
      --region="${REGION}" \
      --project="${PROJECT_ID}" \
      --service-account="${SERVICE_ACCOUNT_EMAIL}" \
      --set-env-vars="${ENV_VARS}" \
      --args="${args}" \
      --max-retries=1 \
      --task-timeout="${timeout}"
  fi
}

# Full scrape: all sources, up to 2 hours
_upsert_job "${FULL_JOB}"        "--all"         7200 \
  "Full-scrape job '${FULL_JOB}' (run manually)"

# Incremental: new makes + recalls only, 30 min should be plenty
_upsert_job "${INCREMENTAL_JOB}" "--incremental" 1800 \
  "Incremental job '${INCREMENTAL_JOB}' (weekly scheduler)"

echo "      Both jobs deployed."

# ------------------------------------------------------------------
# 7. Schedule the incremental job (weekly, Sunday 02:00 UTC)
# ------------------------------------------------------------------
echo "[7/7] Setting up Cloud Scheduler for incremental job..."

INCREMENTAL_JOB_URI="https://run.googleapis.com/v2/projects/${PROJECT_ID}/locations/${REGION}/jobs/${INCREMENTAL_JOB}:run"

if gcloud scheduler jobs describe "${SCHEDULER_JOB_NAME}" \
    --location="${REGION}" --project="${PROJECT_ID}" &>/dev/null; then
  echo "      Scheduler job exists — updating..."
  gcloud scheduler jobs update http "${SCHEDULER_JOB_NAME}" \
    --location="${REGION}" \
    --project="${PROJECT_ID}" \
    --schedule="0 2 * * 0" \
    --time-zone="UTC" \
    --uri="${INCREMENTAL_JOB_URI}" \
    --http-method=POST \
    --oauth-service-account-email="${SERVICE_ACCOUNT_EMAIL}"
else
  echo "      Creating scheduler job..."
  gcloud scheduler jobs create http "${SCHEDULER_JOB_NAME}" \
    --location="${REGION}" \
    --project="${PROJECT_ID}" \
    --schedule="0 2 * * 0" \
    --time-zone="UTC" \
    --uri="${INCREMENTAL_JOB_URI}" \
    --http-method=POST \
    --oauth-service-account-email="${SERVICE_ACCOUNT_EMAIL}"
fi

echo "      Scheduler configured (incremental runs every Sunday at 02:00 UTC)."

# ------------------------------------------------------------------
# Optional: trigger full scrape immediately
# ------------------------------------------------------------------
if [ "${RUN_NOW}" = true ]; then
  echo ""
  echo "=== Triggering full scrape now ==="
  gcloud run jobs execute "${FULL_JOB}" \
    --region="${REGION}" \
    --project="${PROJECT_ID}" \
    --wait
  echo "=== Full scrape completed ==="
fi

echo ""
echo "=== Deployment complete! ==="
echo ""
echo "Jobs:"
echo "  Full scrape (manual):  gcloud run jobs execute ${FULL_JOB} --region=${REGION}"
echo "  Incremental (manual):  gcloud run jobs execute ${INCREMENTAL_JOB} --region=${REGION}"
echo ""
echo "Logs:"
echo "  Full:        gcloud logging read 'resource.labels.job_name=${FULL_JOB}' --limit=50"
echo "  Incremental: gcloud logging read 'resource.labels.job_name=${INCREMENTAL_JOB}' --limit=50"
echo ""
echo "Scheduler:     gcloud scheduler jobs describe ${SCHEDULER_JOB_NAME} --location=${REGION}"
