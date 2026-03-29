#!/usr/bin/env bash
# MotoTracker Scraper — GCP deployment script
# Usage: ./deploy.sh [--run-now]
# Pass --run-now to trigger the Cloud Run Job immediately after deployment.
set -euo pipefail

PROJECT_ID="mototracker-491619"
REGION="us-central1"
SERVICE_ACCOUNT="mototracker-scraper"
SERVICE_ACCOUNT_EMAIL="${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com"
IMAGE="gcr.io/${PROJECT_ID}/mototracker-scraper:latest"
JOB_NAME="mototracker-scraper"
SCHEDULER_JOB_NAME="mototracker-scraper-weekly"
RUN_NOW=false

# Parse optional --run-now flag
for arg in "$@"; do
  case $arg in
    --run-now)
      RUN_NOW=true
      ;;
  esac
done

echo "=== MotoTracker Scraper Deployment ==="
echo "Project:        ${PROJECT_ID}"
echo "Region:         ${REGION}"
echo "Image:          ${IMAGE}"
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
  echo "      Service account already exists — skipping creation."
else
  gcloud iam service-accounts create "${SERVICE_ACCOUNT}" \
    --display-name="MotoTracker Scraper" \
    --project="${PROJECT_ID}"
  echo "      Service account created."
fi

# ------------------------------------------------------------------
# 4. Grant IAM roles
# ------------------------------------------------------------------
echo "[4/7] Granting IAM roles to service account..."

# Datastore User — read/write access to Firestore
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/datastore.user" \
  --condition=None \
  --quiet

# Cloud Run Job Invoker — allows Cloud Scheduler to trigger the job
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/run.invoker" \
  --condition=None \
  --quiet

echo "      IAM roles granted."

# ------------------------------------------------------------------
# 5. Build and push Docker image to GCR
# ------------------------------------------------------------------
echo "[5/7] Building and pushing Docker image to GCR..."
# Ensure we're in the scraper directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

gcloud builds submit \
  --tag "${IMAGE}" \
  --project="${PROJECT_ID}" \
  .

echo "      Image pushed: ${IMAGE}"

# ------------------------------------------------------------------
# 6. Create or update Cloud Run Job
# ------------------------------------------------------------------
echo "[6/7] Deploying Cloud Run Job '${JOB_NAME}'..."

if gcloud run jobs describe "${JOB_NAME}" \
    --region="${REGION}" \
    --project="${PROJECT_ID}" &>/dev/null; then
  echo "      Job exists — updating..."
  gcloud run jobs update "${JOB_NAME}" \
    --image="${IMAGE}" \
    --region="${REGION}" \
    --project="${PROJECT_ID}" \
    --service-account="${SERVICE_ACCOUNT_EMAIL}" \
    --set-env-vars="SCRAPER_PROJECT_ID=${PROJECT_ID},LOG_LEVEL=INFO,RATE_LIMIT_SECONDS=1.0,MAX_RETRIES=3" \
    --max-retries=1 \
    --task-timeout=3600
else
  echo "      Creating new job..."
  gcloud run jobs create "${JOB_NAME}" \
    --image="${IMAGE}" \
    --region="${REGION}" \
    --project="${PROJECT_ID}" \
    --service-account="${SERVICE_ACCOUNT_EMAIL}" \
    --set-env-vars="SCRAPER_PROJECT_ID=${PROJECT_ID},LOG_LEVEL=INFO,RATE_LIMIT_SECONDS=1.0,MAX_RETRIES=3" \
    --max-retries=1 \
    --task-timeout=3600
fi

echo "      Cloud Run Job deployed."

# ------------------------------------------------------------------
# 7. Create or update Cloud Scheduler job
# ------------------------------------------------------------------
echo "[7/7] Setting up Cloud Scheduler job '${SCHEDULER_JOB_NAME}'..."

JOB_URI="https://run.googleapis.com/v1/namespaces/${PROJECT_ID}/jobs/${JOB_NAME}:run"

if gcloud scheduler jobs describe "${SCHEDULER_JOB_NAME}" \
    --location="${REGION}" \
    --project="${PROJECT_ID}" &>/dev/null; then
  echo "      Scheduler job exists — updating..."
  gcloud scheduler jobs update http "${SCHEDULER_JOB_NAME}" \
    --location="${REGION}" \
    --project="${PROJECT_ID}" \
    --schedule="0 2 * * 0" \
    --time-zone="UTC" \
    --uri="${JOB_URI}" \
    --http-method=POST \
    --oauth-service-account-email="${SERVICE_ACCOUNT_EMAIL}"
else
  echo "      Creating new scheduler job..."
  gcloud scheduler jobs create http "${SCHEDULER_JOB_NAME}" \
    --location="${REGION}" \
    --project="${PROJECT_ID}" \
    --schedule="0 2 * * 0" \
    --time-zone="UTC" \
    --uri="${JOB_URI}" \
    --http-method=POST \
    --oauth-service-account-email="${SERVICE_ACCOUNT_EMAIL}"
fi

echo "      Cloud Scheduler job configured (runs every Sunday at 02:00 UTC)."

# ------------------------------------------------------------------
# Optional: trigger an immediate run
# ------------------------------------------------------------------
if [ "${RUN_NOW}" = true ]; then
  echo ""
  echo "=== Triggering immediate execution of Cloud Run Job ==="
  gcloud run jobs execute "${JOB_NAME}" \
    --region="${REGION}" \
    --project="${PROJECT_ID}" \
    --wait
  echo "=== Job execution completed ==="
fi

echo ""
echo "=== Deployment complete! ==="
echo ""
echo "Useful commands:"
echo "  Manual run:    gcloud run jobs execute ${JOB_NAME} --region=${REGION}"
echo "  View logs:     gcloud logging read 'resource.type=cloud_run_job AND resource.labels.job_name=${JOB_NAME}' --limit=50 --format=json"
echo "  Job status:    gcloud run jobs describe ${JOB_NAME} --region=${REGION}"
echo "  Scheduler:     gcloud scheduler jobs describe ${SCHEDULER_JOB_NAME} --location=${REGION}"
