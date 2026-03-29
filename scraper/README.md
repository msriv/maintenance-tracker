# MotoTracker Scraper

A Python Cloud Run Job that populates Firestore with motorcycle registry data, OEM maintenance schedules, best practices, and NHTSA recall alerts.

---

## Prerequisites

- [gcloud CLI](https://cloud.google.com/sdk/docs/install) — authenticated with `gcloud auth login`
- [Docker](https://docs.docker.com/get-docker/) — for building the container image locally
- Python 3.11+ — for local development and testing

---

## Local Development Setup

```bash
cd scraper/

# Create and activate a virtual environment
python3 -m venv .venv
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### Firebase credentials for local runs

The scraper uses Application Default Credentials (ADC). For local development you need either:

**Option A — Service account key file:**
```bash
# Download a key for the scraper service account from the GCP console, then:
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/mototracker-scraper-key.json"
```

**Option B — gcloud ADC (if your gcloud account has Firestore access):**
```bash
gcloud auth application-default login
```

---

## Running Scrapers Locally

```bash
# Run all scrapers (default)
python main.py --all

# Run individual components
python main.py --makes              # Bikez.com makes only
python main.py --models             # Bikez.com models only
python main.py --schedules          # Curated OEM schedules only
python main.py --best-practices     # Hardcoded best practices only
python main.py --recalls            # NHTSA recall data only

# Combine flags
python main.py --schedules --best-practices --recalls
```

Environment variables that control behaviour:

| Variable | Default | Description |
|---|---|---|
| `SCRAPER_PROJECT_ID` | `mototracker-491619` | GCP project ID |
| `RATE_LIMIT_SECONDS` | `1.0` | Minimum seconds between HTTP requests |
| `MAX_RETRIES` | `3` | Max retries on 429/5xx responses |
| `LOG_LEVEL` | `INFO` | Python log level (`DEBUG`, `INFO`, `WARNING`, `ERROR`) |

---

## Deployment

Deployment is fully automated via `deploy.sh`. Run it once from the `scraper/` directory:

```bash
cd scraper/
chmod +x deploy.sh
./deploy.sh
```

What the script does:

1. Sets the active GCP project to `mototracker-491619`
2. Enables required APIs: Cloud Run, Cloud Scheduler, Firestore, Container Registry, Cloud Build
3. Creates the `mototracker-scraper` service account (idempotent)
4. Grants IAM roles: `roles/datastore.user` and `roles/run.invoker`
5. Builds the Docker image with Cloud Build and pushes to `gcr.io/mototracker-491619/mototracker-scraper:latest`
6. Creates (or updates) the Cloud Run Job `mototracker-scraper`
7. Creates (or updates) the Cloud Scheduler job to run every Sunday at 02:00 UTC

To deploy **and** trigger an immediate run:

```bash
./deploy.sh --run-now
```

---

## Firebase Project Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/) and open project **MotoTracker** (ID: `mototracker-491619`).
2. Navigate to **Firestore Database** → **Rules** and paste the contents of `firestore.rules`, then publish.
3. To use the Android app with anonymous auth: enable **Anonymous** sign-in under **Authentication → Sign-in method**.

For the scraper service account key (needed for local dev):
1. GCP Console → IAM & Admin → Service Accounts
2. Select `mototracker-scraper@mototracker-491619.iam.gserviceaccount.com`
3. Keys → Add Key → JSON → Download
4. Set `GOOGLE_APPLICATION_CREDENTIALS` to the downloaded file path

---

## Cloud Scheduler Cadence

The scheduler job runs **every Sunday at 02:00 UTC** (`0 2 * * 0`).

This weekly cadence is chosen because:
- Bikez.com model data changes infrequently (new models are announced seasonally)
- NHTSA recalls are issued on a rolling basis but don't require hourly freshness
- OEM schedules and best practices are static (curated) and only change when the scraper code is updated

To change the schedule, edit the `--schedule` flag in `deploy.sh` and re-run it.

---

## Firestore Collection Structure

```
Firestore (default)
├── registry/
│   ├── makes/
│   │   └── {make_id}          # e.g. "honda"
│   │       ├── id: "honda"
│   │       ├── name: "Honda"
│   │       ├── country: "Japan"
│   │       └── updated_at: <timestamp>
│   ├── models/
│   │   └── {model_id}         # e.g. "honda_cbr600rr_2003"
│   │       ├── id
│   │       ├── make
│   │       ├── model
│   │       ├── year_from / year_to
│   │       ├── variants: [...]
│   │       ├── vehicle_type: "BIKE" | "SCOOTER" | "CAR" | "TRUCK" | "OTHER"
│   │       ├── displacement_cc
│   │       └── updated_at
│   ├── schedules/
│   │   └── {schedule_id}      # e.g. "honda_cb_shine_2006_engine_oil"
│   │       ├── make / model / year_from / year_to
│   │       ├── task_type      # matches Android MaintenanceTaskType enum
│   │       ├── task_label
│   │       ├── interval_days / interval_km
│   │       ├── notes
│   │       ├── source         # URL or "curated"
│   │       └── updated_at
│   └── best_practices/
│       └── {practice_id}
│           ├── category       # "ROUTINE" | "POST_SERVICE" | "MANUFACTURER"
│           ├── task_type
│           ├── title / description
│           ├── applicable_to: ["BIKE", "SCOOTER"] or ["ALL"]
│           ├── is_template    # true = can be added as a task from the app
│           ├── template_interval_days / template_interval_km
│           └── updated_at
├── recalls/
│   └── {campaign_number}      # NHTSA campaign number e.g. "23V123000"
│       ├── make / model
│       ├── year_from / year_to
│       ├── component / consequence / remedy / description
│       ├── severity           # "MINOR" | "MODERATE" | "CRITICAL"
│       ├── issued_date        # ISO 8601 string
│       ├── nhtsa_url
│       └── updated_at
└── sync_metadata/
    ├── latest                 # Always reflects the most recent run
    └── {run_id}               # Immutable archive of each run
        ├── run_id
        ├── completed_at
        ├── makes_count / models_count / schedules_count / ...
        └── errors: [...]
```

---

## Triggering a Manual Run

Via gcloud:
```bash
gcloud run jobs execute mototracker-scraper \
  --region=us-central1 \
  --project=mototracker-491619 \
  --wait
```

With specific flags (override CMD):
```bash
gcloud run jobs execute mototracker-scraper \
  --region=us-central1 \
  --project=mototracker-491619 \
  --args="--schedules,--best-practices" \
  --wait
```

View logs after a run:
```bash
gcloud logging read \
  'resource.type=cloud_run_job AND resource.labels.job_name=mototracker-scraper' \
  --limit=100 \
  --format=json \
  --project=mototracker-491619
```
