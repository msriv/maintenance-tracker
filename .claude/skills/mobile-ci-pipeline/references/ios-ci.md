# iOS CI/CD Reference

## GitHub Actions: Full iOS Pipeline

### Complete Workflow (`.github/workflows/ios.yml`)

```yaml
name: iOS CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]
  workflow_dispatch:
    inputs:
      lane:
        description: 'Fastlane lane to run'
        required: true
        default: 'beta'
        type: choice
        options: [test, beta, release]

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true

env:
  XCODE_VERSION: '16.0'
  RUBY_VERSION: '3.3'
  SCHEME: 'MyApp'
  BUNDLE_EXEC: 'bundle exec'

jobs:
  test:
    name: Test
    runs-on: macos-15
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Select Xcode version
        run: sudo xcode-select -s /Applications/Xcode_${{ env.XCODE_VERSION }}.app

      - name: Setup Ruby
        uses: ruby/setup-ruby@v1
        with:
          ruby-version: ${{ env.RUBY_VERSION }}
          bundler-cache: true          # runs `bundle install` and caches gems

      - name: Cache SPM packages
        uses: actions/cache@v4
        with:
          path: |
            ~/Library/Developer/Xcode/DerivedData
            .build
          key: ${{ runner.os }}-spm-${{ hashFiles('**/Package.resolved') }}
          restore-keys: |
            ${{ runner.os }}-spm-

      - name: Run tests
        run: ${{ env.BUNDLE_EXEC }} fastlane test

      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: fastlane/test_output/

  build-and-deploy:
    name: Build & Deploy (${{ github.event.inputs.lane || 'beta' }})
    runs-on: macos-15
    needs: test
    if: github.ref == 'refs/heads/main' || github.event_name == 'workflow_dispatch'
    environment: ${{ github.event.inputs.lane == 'release' && 'production' || 'staging' }}

    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0    # needed for build number from git history

      - name: Select Xcode version
        run: sudo xcode-select -s /Applications/Xcode_${{ env.XCODE_VERSION }}.app

      - name: Setup Ruby
        uses: ruby/setup-ruby@v1
        with:
          ruby-version: ${{ env.RUBY_VERSION }}
          bundler-cache: true

      - name: Cache SPM packages
        uses: actions/cache@v4
        with:
          path: |
            ~/Library/Developer/Xcode/DerivedData
            .build
          key: ${{ runner.os }}-spm-${{ hashFiles('**/Package.resolved') }}
          restore-keys: |
            ${{ runner.os }}-spm-

      - name: Install Apple certificate and provisioning profile
        # Only needed if NOT using Fastlane Match — skip this block if using Match
        if: false
        env:
          BUILD_CERTIFICATE_BASE64: ${{ secrets.BUILD_CERTIFICATE_BASE64 }}
          P12_PASSWORD: ${{ secrets.P12_PASSWORD }}
          BUILD_PROVISION_PROFILE_BASE64: ${{ secrets.BUILD_PROVISION_PROFILE_BASE64 }}
          KEYCHAIN_PASSWORD: ${{ secrets.KEYCHAIN_PASSWORD }}
        run: |
          CERTIFICATE_PATH=$RUNNER_TEMP/build_certificate.p12
          PP_PATH=$RUNNER_TEMP/build_pp.mobileprovision
          KEYCHAIN_PATH=$RUNNER_TEMP/app-signing.keychain-db

          echo -n "$BUILD_CERTIFICATE_BASE64" | base64 --decode -o $CERTIFICATE_PATH
          echo -n "$BUILD_PROVISION_PROFILE_BASE64" | base64 --decode -o $PP_PATH

          security create-keychain -p "$KEYCHAIN_PASSWORD" $KEYCHAIN_PATH
          security set-keychain-settings -lut 21600 $KEYCHAIN_PATH
          security unlock-keychain -p "$KEYCHAIN_PASSWORD" $KEYCHAIN_PATH
          security import $CERTIFICATE_PATH -P "$P12_PASSWORD" -A \
            -t cert -f pkcs12 -k $KEYCHAIN_PATH
          security list-keychain -d user -s $KEYCHAIN_PATH

          mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles
          cp $PP_PATH ~/Library/MobileDevice/Provisioning\ Profiles

      - name: Run Fastlane lane
        env:
          MATCH_PASSWORD: ${{ secrets.MATCH_PASSWORD }}
          MATCH_GIT_BASIC_AUTHORIZATION: ${{ secrets.MATCH_GIT_BASIC_AUTHORIZATION }}
          APP_STORE_CONNECT_API_KEY_ID: ${{ secrets.APP_STORE_CONNECT_API_KEY_ID }}
          APP_STORE_CONNECT_API_ISSUER_ID: ${{ secrets.APP_STORE_CONNECT_API_ISSUER_ID }}
          APP_STORE_CONNECT_API_KEY_CONTENT: ${{ secrets.APP_STORE_CONNECT_API_KEY_CONTENT }}
          FASTLANE_APPLE_APPLICATION_SPECIFIC_PASSWORD: ${{ secrets.FASTLANE_APPLE_APPLICATION_SPECIFIC_PASSWORD }}
        run: ${{ env.BUNDLE_EXEC }} fastlane ${{ github.event.inputs.lane || 'beta' }}

      - name: Upload IPA artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-ipa
          path: fastlane/builds/*.ipa

      - name: Notify Slack
        if: always()
        uses: slackapi/slack-github-action@v1
        with:
          payload: |
            {
              "text": "iOS ${{ github.event.inputs.lane || 'beta' }}: ${{ job.status }} on `${{ github.ref_name }}` by ${{ github.actor }}"
            }
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

---

## Gemfile

Always version-pin Fastlane and plugins in a `Gemfile` at the project root:

```ruby
# Gemfile
source 'https://rubygems.org'

gem 'fastlane', '~> 2.222'
gem 'cocoapods', '~> 1.15'    # only if using CocoaPods

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
```

```ruby
# fastlane/Pluginfile
gem 'fastlane-plugin-firebase_app_distribution'   # if using Firebase
gem 'fastlane-plugin-increment_build_number_in_plist'
```

Run `bundle install` locally. Commit `Gemfile.lock`. The `bundler-cache: true` option in the workflow handles caching automatically.

---

## Fastlane Match Setup

Match stores certificates and profiles in a Git repo (or S3/Google Cloud Storage). It is the recommended approach for team signing.

### 1. Initial Setup

```bash
# One-time setup
bundle exec fastlane match init
# → choose storage mode: git
# → enter the URL of your private certificates repo
```

### 2. Generate Certificates and Profiles

```bash
# Development certificates + profiles
bundle exec fastlane match development

# Distribution certificate + App Store profile
bundle exec fastlane match appstore

# Ad Hoc profile (for Firebase App Distribution)
bundle exec fastlane match adhoc
```

### 3. Matchfile

```ruby
# fastlane/Matchfile
git_url("https://github.com/your-org/ios-certificates")
git_branch("main")
storage_mode("git")

type("development")             # default — overridden per lane

app_identifier(["com.example.myapp"])
username("ci@example.com")     # Apple ID used to create certificates

# In CI, authenticate via Git token rather than SSH:
# Set MATCH_GIT_BASIC_AUTHORIZATION = base64("username:token")
```

### 4. Appfile

```ruby
# fastlane/Appfile
app_identifier("com.example.myapp")
apple_id("ci@example.com")
itc_team_id("123456789")        # App Store Connect team ID
team_id("ABCDE12345")           # Developer Portal team ID
```

---

## Fastfile (Full)

```ruby
# fastlane/Fastfile
default_platform(:ios)

APP_IDENTIFIER = "com.example.myapp"
SCHEME         = "MyApp"
WORKSPACE      = "MyApp.xcworkspace"  # use .xcodeproj if no workspace
OUTPUT_DIR     = "fastlane/builds"

platform :ios do

  before_all do
    # Load App Store Connect API key once — used by upload_to_testflight,
    # match, and other actions that need ASC access.
    app_store_connect_api_key(
      key_id:        ENV["APP_STORE_CONNECT_API_KEY_ID"],
      issuer_id:     ENV["APP_STORE_CONNECT_API_ISSUER_ID"],
      key_content:   ENV["APP_STORE_CONNECT_API_KEY_CONTENT"],
      in_house:      false
    )
  end

  # ── Test ─────────────────────────────────────────────────────────────────
  lane :test do
    run_tests(
      workspace:       WORKSPACE,
      scheme:          SCHEME,
      devices:         ["iPhone 16"],
      clean:           true,
      output_directory: "fastlane/test_output",
      output_types:    "html,junit",
      code_coverage:   true,
      derived_data_path: "DerivedData"
    )
  end

  # ── Beta (TestFlight) ─────────────────────────────────────────────────────
  lane :beta do
    increment_version
    sync_code_signing(type: "appstore")   # match appstore

    build_app(
      workspace:             WORKSPACE,
      scheme:                SCHEME,
      configuration:         "Release",
      export_method:         "app-store",
      output_directory:      OUTPUT_DIR,
      output_name:           "MyApp.ipa",
      derived_data_path:     "DerivedData",
      xcargs:                "MARKETING_VERSION=$(cat version.txt)"
    )

    upload_to_testflight(
      ipa:                    "#{OUTPUT_DIR}/MyApp.ipa",
      skip_waiting_for_build_processing: true,
      changelog:              read_changelog
    )
  end

  # ── Release (App Store) ───────────────────────────────────────────────────
  lane :release do
    increment_version
    sync_code_signing(type: "appstore")

    build_app(
      workspace:         WORKSPACE,
      scheme:            SCHEME,
      configuration:     "Release",
      export_method:     "app-store",
      output_directory:  OUTPUT_DIR,
      output_name:       "MyApp.ipa",
      derived_data_path: "DerivedData"
    )

    upload_to_app_store(
      ipa:                    "#{OUTPUT_DIR}/MyApp.ipa",
      skip_metadata:          true,
      skip_screenshots:       true,
      submit_for_review:      false,   # set true to auto-submit
      automatic_release:      false
    )
  end

  # ── Firebase App Distribution ─────────────────────────────────────────────
  lane :firebase_distribute do
    sync_code_signing(type: "adhoc")

    build_app(
      workspace:        WORKSPACE,
      scheme:           SCHEME,
      configuration:    "Release",
      export_method:    "ad-hoc",
      output_directory: OUTPUT_DIR,
      output_name:      "MyApp-AdHoc.ipa"
    )

    firebase_app_distribution(
      app:              "1:123456789:ios:abcdef",   # Firebase App ID
      groups:           "internal-testers",
      release_notes:    read_changelog,
      firebase_cli_token: ENV["FIREBASE_TOKEN"]
    )
  end

  # ── Helpers ───────────────────────────────────────────────────────────────
  private_lane :increment_version do
    # Build number = total git commit count (monotonically increasing)
    build_number = sh("git rev-list --count HEAD").strip
    increment_build_number(
      build_number: build_number,
      xcodeproj:    "MyApp.xcodeproj"
    )
  end

  private_lane :read_changelog do
    changelog_path = "CHANGELOG.md"
    File.exist?(changelog_path) ? File.read(changelog_path).lines.first(10).join : "No changelog"
  end

end
```

---

## App Store Connect API Key

The API key eliminates the need for Apple ID + password + 2FA in CI. It authenticates with App Store Connect for TestFlight uploads, metadata, and provisioning.

### Create the Key

1. Go to **App Store Connect → Users and Access → Integrations → App Store Connect API**
2. Click **+** → give it a name (e.g., "CI Key") → Role: **App Manager** (or Developer for TestFlight only)
3. Download the `.p8` file — **you can only download it once**
4. Note the **Key ID** and **Issuer ID** from the page

### Store in GitHub Secrets

| Secret name | Value |
|---|---|
| `APP_STORE_CONNECT_API_KEY_ID` | Key ID (e.g., `ABC123DEFG`) |
| `APP_STORE_CONNECT_API_ISSUER_ID` | Issuer ID (UUID) |
| `APP_STORE_CONNECT_API_KEY_CONTENT` | Full content of `.p8` file (include header/footer lines) |

### Usage in Fastfile

```ruby
app_store_connect_api_key(
  key_id:      ENV["APP_STORE_CONNECT_API_KEY_ID"],
  issuer_id:   ENV["APP_STORE_CONNECT_API_ISSUER_ID"],
  key_content: ENV["APP_STORE_CONNECT_API_KEY_CONTENT"],
  in_house:    false   # true for Enterprise distribution
)
```

When called in `before_all`, this key is automatically used by `upload_to_testflight`, `upload_to_app_store`, `match`, and `pilot` without further configuration.

---

## TestFlight Upload Details

### `upload_to_testflight` Key Parameters

```ruby
upload_to_testflight(
  ipa:                              "path/to/MyApp.ipa",
  app_identifier:                   "com.example.myapp",
  skip_waiting_for_build_processing: true,   # don't block CI waiting for Apple
  changelog:                        "Bug fixes and performance improvements",
  distribute_external:              false,   # true to push to external testers
  groups:                           ["Internal Testers"],
  notify_external_testers:          false,
  expire_previous_builds:           true     # auto-expire old builds in same group
)
```

### Getting the Build Number to TestFlight

TestFlight requires each upload to have a unique build number (`CFBundleVersion`). The git commit count approach (`git rev-list --count HEAD`) is reliable in CI because:
- It's monotonically increasing with every commit
- It doesn't require writing back to the repo
- It works correctly in shallow clones if you set `fetch-depth: 0`

---

## Provisioning Profile Management

### With Match (Recommended)

Match downloads and installs the right certificate and profile automatically:

```ruby
sync_code_signing(
  type:           "appstore",        # appstore | adhoc | development | enterprise
  app_identifier: "com.example.myapp",
  readonly:       true               # CI should never create new certs — readonly!
)
```

**Important**: Always use `readonly: true` in CI. Certificate creation requires human interaction with Apple. Certificates should be created locally by a team lead and pushed to the Match repo, then CI only reads them.

### Without Match (Manual Profiles)

If not using Match, install certificate + profile directly in the workflow (see the commented-out step in the GitHub Actions YAML above). Store these in secrets:

| Secret | How to create |
|---|---|
| `BUILD_CERTIFICATE_BASE64` | `base64 -i Certificates.p12` |
| `P12_PASSWORD` | Password used when exporting .p12 from Keychain |
| `BUILD_PROVISION_PROFILE_BASE64` | `base64 -i MyApp_AppStore.mobileprovision` |
| `KEYCHAIN_PASSWORD` | Any random string — used just for CI keychain |

### Exporting .p12 from Keychain Access

1. Open **Keychain Access** → **My Certificates**
2. Right-click the **Apple Distribution** certificate (including private key)
3. Export → **Personal Information Exchange (.p12)**
4. Set a password → base64 encode the file

---

## Xcode Version Selection

### Available Xcode Versions on GitHub-Hosted Runners

GitHub's macOS runners come with multiple Xcode versions pre-installed. Check the current list at:
`https://github.com/actions/runner-images/blob/main/images/macos/macos-15-Readme.md`

### Selecting a Specific Version

```yaml
- name: Select Xcode
  run: sudo xcode-select -s /Applications/Xcode_16.0.app

# Or use the action:
- uses: maxim-lobanov/setup-xcode@v1
  with:
    xcode-version: '16.0'
```

### Version Strategy

- **Pin to a specific minor version** (e.g., `16.0`) in CI for reproducibility
- **Test on new Xcode** in a separate branch/workflow before upgrading
- Use `xcodebuild -version` to log the actual version at the start of your workflow for debugging

---

## SPM Caching Strategy

```yaml
- name: Cache SPM
  uses: actions/cache@v4
  with:
    path: |
      ~/Library/Developer/Xcode/DerivedData/**/SourcePackages/checkouts
      ~/Library/Developer/Xcode/DerivedData/**/SourcePackages/repositories
    key: ${{ runner.os }}-spm-${{ hashFiles('**/Package.resolved') }}
    restore-keys: |
      ${{ runner.os }}-spm-
```

`Package.resolved` is the lock file — cache invalidates automatically when any dependency version changes. Commit `Package.resolved` to your repo.

---

## CocoaPods Caching (if applicable)

```yaml
- name: Cache CocoaPods
  uses: actions/cache@v4
  with:
    path: Pods
    key: ${{ runner.os }}-pods-${{ hashFiles('**/Podfile.lock') }}
    restore-keys: |
      ${{ runner.os }}-pods-

- name: Install pods
  run: bundle exec pod install --repo-update
```

Commit `Podfile.lock`. The cache key is derived from it so updates invalidate the cache automatically.

---

## Build Environments (xcconfig)

Use `.xcconfig` files to manage per-environment configuration without build scheme duplication:

### File Structure

```
MyApp/
├── Config/
│   ├── Base.xcconfig
│   ├── Debug.xcconfig
│   ├── Staging.xcconfig
│   └── Release.xcconfig
```

### Base.xcconfig

```
// Base.xcconfig
PRODUCT_BUNDLE_IDENTIFIER = com.example.myapp
MARKETING_VERSION = 1.0.0
CURRENT_PROJECT_VERSION = 1

API_BASE_URL = https://api.example.com
FEATURE_FLAGS_ENABLED = NO
```

### Staging.xcconfig

```
// Staging.xcconfig — overrides base values
#include "Base.xcconfig"

PRODUCT_BUNDLE_IDENTIFIER = com.example.myapp.staging
API_BASE_URL = https://staging-api.example.com
FEATURE_FLAGS_ENABLED = YES
```

### Reading xcconfig Values in Swift

```swift
// Config.swift — type-safe access to Info.plist values backed by xcconfig
enum Config {
    static var apiBaseURL: URL {
        guard let urlString = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String,
              let url = URL(string: urlString) else {
            fatalError("API_BASE_URL not configured")
        }
        return url
    }
}
```

Add `API_BASE_URL = $(API_BASE_URL)` to `Info.plist` to bridge xcconfig → Bundle.

---

## Secrets Reference

### GitHub Secrets to Configure

| Secret | Used for | How to obtain |
|---|---|---|
| `APP_STORE_CONNECT_API_KEY_ID` | ASC API auth | App Store Connect → Integrations |
| `APP_STORE_CONNECT_API_ISSUER_ID` | ASC API auth | Same page as Key ID |
| `APP_STORE_CONNECT_API_KEY_CONTENT` | ASC API auth | Contents of downloaded `.p8` file |
| `MATCH_PASSWORD` | Decrypt Match repo | Set when running `fastlane match init` |
| `MATCH_GIT_BASIC_AUTHORIZATION` | Clone Match repo | `base64("github-username:personal-access-token")` |
| `SLACK_WEBHOOK_URL` | Notifications | Slack app incoming webhook URL |
| `FIREBASE_TOKEN` | Firebase distribution | `firebase login:ci` output |

### What Goes in the Repo (Not Secrets)

- `Gemfile` + `Gemfile.lock` — committed, version-pinned
- `fastlane/Fastfile`, `Matchfile`, `Appfile` — committed (no sensitive values)
- `Package.resolved` / `Podfile.lock` — committed lock files
- `.xcconfig` files — committed (no secrets; use environment variables for secrets)

### What Never Goes in the Repo

- `.p12` certificate files
- `.mobileprovision` files
- `.p8` App Store Connect API key file
- Any `.env` file containing real credentials
- `AuthKey_*.p8` files

---

## Bitrise iOS Configuration

For teams using Bitrise instead of GitHub Actions:

```yaml
# bitrise.yml
format_version: '13'
default_step_lib_source: https://github.com/bitrise-io/bitrise-steplib.git

app:
  envs:
    - BITRISE_PROJECT_PATH: MyApp.xcworkspace
    - BITRISE_SCHEME: MyApp
    - BITRISE_EXPORT_METHOD: app-store

workflows:
  test:
    steps:
      - activate-ssh-key: {}
      - git-clone: {}
      - certificate-and-profile-installer: {}
      - cocoapods-install:
          inputs:
            - is_cache_disabled: 'false'
      - xcode-test:
          inputs:
            - project_path: $BITRISE_PROJECT_PATH
            - scheme: $BITRISE_SCHEME
            - simulator_device: iPhone 16
      - cache-push: {}

  beta:
    before_run: [test]
    steps:
      - activate-ssh-key: {}
      - git-clone: {}
      - fastlane:
          inputs:
            - lane: beta
      - deploy-to-bitrise-io: {}
```

---

## Debugging CI Failures

### Common Issues and Fixes

| Symptom | Root Cause | Fix |
|---|---|---|
| `No provisioning profile found` | Match readonly mode, wrong type | Check `type:` in `sync_code_signing`, ensure match repo has appstore profile |
| `Code signing error: ambiguous` | Multiple matching certs in keychain | Use `MATCH_KEYCHAIN_NAME` env to isolate |
| `Build number already exists` | Duplicate CFBundleVersion on TestFlight | Ensure `git rev-list --count HEAD` is unique; check `fetch-depth: 0` |
| `xcodebuild: error: workspace not found` | Wrong path in `WORKSPACE` | Confirm `.xcworkspace` path relative to repo root |
| `error: No such module 'X'` | SPM cache stale after Package.resolved change | Clear SPM cache key or add `--disableAutomaticPackageResolution` |
| `fastlane: command not found` | Missing `bundle exec` prefix | Always prefix with `bundle exec fastlane` |
| `401 Unauthorized` on ASC upload | Expired or wrong API key | Regenerate key in App Store Connect, update all three secrets |
| Test timeout on macOS runner | Simulator boot delay | Add `- name: Boot simulator` step before tests; increase `timeout` in `run_tests` |

### Enabling Verbose Logging

```yaml
- name: Run Fastlane (verbose)
  run: bundle exec fastlane beta --verbose
  env:
    FASTLANE_HIDE_CHANGELOG: true
    CI: true
```

### Running Locally with Same Environment

```bash
# Simulate CI locally
export APP_STORE_CONNECT_API_KEY_ID="..."
export APP_STORE_CONNECT_API_ISSUER_ID="..."
export APP_STORE_CONNECT_API_KEY_CONTENT=$(cat AuthKey_ABC123.p8)
export MATCH_PASSWORD="..."

bundle exec fastlane beta
```
