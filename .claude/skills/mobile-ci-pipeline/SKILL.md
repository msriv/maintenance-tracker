---
name: mobile-ci-pipeline
description: >
  Generate CI/CD pipeline configurations for Android and iOS mobile apps. Use this skill whenever
  the user asks to: set up GitHub Actions for a mobile app, configure Bitrise, set up GitLab CI
  for Android/iOS, write a Fastfile for mobile, configure Fastlane for iOS or Android, set up
  automated builds, automate TestFlight uploads, automate Play Store uploads, set up code signing
  in CI, configure Fastlane Match, set up Firebase App Distribution, automate APK/AAB signing,
  configure build variants in CI, set up Gradle caching in CI, handle iOS provisioning in CI,
  or write a README for a mobile CI pipeline. Also triggers for: "automate my mobile releases",
  "how do I deploy to TestFlight automatically", "set up CD for my Android app", "configure
  signing for CI", "generate a Fastlane setup", "set up build matrix for Android". Do NOT use
  for generating app code, debugging, or testing setup (use mobile-testing-suite for tests).
---

# Mobile CI Pipeline Skill

## Step 0 — Gather Configuration

Ask for the following if not specified:

1. **CI provider**: GitHub Actions, Bitrise, GitLab CI, or Fastlane standalone?
2. **Platform**: Android, iOS, or both?
3. **Deployment target(s)**:
   - Android: Play Console (internal/alpha/beta/production), Firebase App Distribution, both?
   - iOS: TestFlight, App Store, Firebase App Distribution?
4. **Build variants / flavors**: dev, staging, production? Or just one?
5. **Signing setup**: iOS — Fastlane Match or manual provisioning profiles? Android — keystore from CI secrets?
6. **Notifications**: Slack, email, or none?

Default to GitHub Actions if provider is unspecified.

---

## Step 1 — Read Reference Files

- For **Android** CI: read `references/android-ci.md`
- For **iOS** CI: read `references/ios-ci.md`

---

## Step 2 — Android GitHub Actions Workflow

File: `.github/workflows/android.yml`

```yaml
name: Android CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]
  workflow_dispatch:
    inputs:
      deploy_target:
        description: 'Deploy to (internal|alpha|beta|production)'
        required: false
        default: 'internal'

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true

jobs:
  test:
    name: Run Tests
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Cache Gradle dependencies
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
            .gradle/configuration-cache
          key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle.kts', '**/gradle-wrapper.properties', '**/libs.versions.toml') }}
          restore-keys: gradle-${{ runner.os }}-
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Run lint
        run: ./gradlew lint
      
      - name: Run unit tests
        run: ./gradlew test
      
      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: '**/build/reports/tests/'
      
      - name: Upload lint results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: lint-results
          path: '**/build/reports/lint-results-*.html'

  build:
    name: Build Release AAB
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main' || github.event_name == 'workflow_dispatch'
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Restore Gradle cache
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle.kts', '**/gradle-wrapper.properties') }}
      
      - name: Increment build number
        run: |
          BUILD_NUMBER=${{ github.run_number }}
          sed -i "s/versionCode = .*/versionCode = $BUILD_NUMBER/" app/build.gradle.kts
      
      - name: Decode keystore
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 --decode > app/release.keystore
      
      - name: Build release AAB
        run: ./gradlew bundleProductionRelease
        env:
          KEYSTORE_PATH: release.keystore
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      
      - name: Upload AAB artifact
        uses: actions/upload-artifact@v4
        with:
          name: release-aab
          path: app/build/outputs/bundle/productionRelease/app-production-release.aab
          retention-days: 30

  deploy:
    name: Deploy to Play Store
    needs: build
    runs-on: ubuntu-latest
    environment: production
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Download AAB
        uses: actions/download-artifact@v4
        with:
          name: release-aab
          path: ./artifacts
      
      - name: Upload to Play Store (Internal)
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT_JSON }}
          packageName: com.example.app
          releaseFiles: ./artifacts/*.aab
          track: ${{ github.event.inputs.deploy_target || 'internal' }}
          status: completed
          whatsNewDirectory: ./whatsnew

  notify:
    name: Slack Notification
    needs: [test, build, deploy]
    runs-on: ubuntu-latest
    if: always()
    steps:
      - name: Notify Slack
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          text: "Android build ${{ github.run_number }} — ${{ job.status }}"
          webhook_url: ${{ secrets.SLACK_WEBHOOK_URL }}
```

---

## Step 3 — iOS GitHub Actions Workflow

File: `.github/workflows/ios.yml`

```yaml
name: iOS CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]
  workflow_dispatch:

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true

env:
  XCODE_VERSION: '16.2'
  SCHEME: 'ExampleApp'
  BUNDLE_ID: 'com.example.app'

jobs:
  test:
    name: Run Tests
    runs-on: macos-15
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Select Xcode version
        run: sudo xcode-select -s /Applications/Xcode_${{ env.XCODE_VERSION }}.app
      
      - name: Cache SPM packages
        uses: actions/cache@v4
        with:
          path: |
            ~/Library/Developer/Xcode/DerivedData/**/SourcePackages/checkouts
            .build
          key: spm-${{ runner.os }}-${{ hashFiles('**/Package.resolved') }}
          restore-keys: spm-${{ runner.os }}-
      
      - name: Run unit tests
        run: |
          xcodebuild test \
            -scheme ${{ env.SCHEME }} \
            -destination "platform=iOS Simulator,name=iPhone 16,OS=latest" \
            -enableCodeCoverage YES \
            -resultBundlePath TestResults.xcresult \
            | xcpretty
      
      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: TestResults.xcresult

  build-and-deploy:
    name: Build & Upload to TestFlight
    needs: test
    runs-on: macos-15
    if: github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Select Xcode version
        run: sudo xcode-select -s /Applications/Xcode_${{ env.XCODE_VERSION }}.app
      
      - name: Cache SPM packages
        uses: actions/cache@v4
        with:
          path: ~/Library/Developer/Xcode/DerivedData/**/SourcePackages/checkouts
          key: spm-${{ runner.os }}-${{ hashFiles('**/Package.resolved') }}
      
      - name: Set up Ruby and Fastlane
        uses: ruby/setup-ruby@v1
        with:
          ruby-version: '3.3'
          bundler-cache: true
      
      - name: Install certificates and profiles (Fastlane Match)
        run: bundle exec fastlane match appstore --readonly
        env:
          MATCH_PASSWORD: ${{ secrets.MATCH_PASSWORD }}
          MATCH_GIT_BASIC_AUTHORIZATION: ${{ secrets.MATCH_GIT_TOKEN }}
          APP_STORE_CONNECT_API_KEY_ID: ${{ secrets.ASC_KEY_ID }}
          APP_STORE_CONNECT_API_ISSUER_ID: ${{ secrets.ASC_ISSUER_ID }}
          APP_STORE_CONNECT_API_KEY_CONTENT: ${{ secrets.ASC_KEY_CONTENT }}
      
      - name: Increment build number
        run: |
          agvtool new-version -all ${{ github.run_number }}
      
      - name: Build and upload to TestFlight
        run: bundle exec fastlane beta
        env:
          APP_STORE_CONNECT_API_KEY_ID: ${{ secrets.ASC_KEY_ID }}
          APP_STORE_CONNECT_API_ISSUER_ID: ${{ secrets.ASC_ISSUER_ID }}
          APP_STORE_CONNECT_API_KEY_CONTENT: ${{ secrets.ASC_KEY_CONTENT }}
          MATCH_PASSWORD: ${{ secrets.MATCH_PASSWORD }}
          MATCH_GIT_BASIC_AUTHORIZATION: ${{ secrets.MATCH_GIT_TOKEN }}
      
      - name: Notify Slack
        if: always()
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          text: "iOS build ${{ github.run_number }} uploaded to TestFlight"
          webhook_url: ${{ secrets.SLACK_WEBHOOK_URL }}
```

---

## Step 4 — Fastlane Configuration

### Android Fastfile
```ruby
# fastlane/Fastfile (Android)
default_platform(:android)

platform :android do

  desc "Run all tests"
  lane :test do
    gradle(task: "test")
  end

  desc "Build and deploy to Play Store internal track"
  lane :internal do
    increment_version_code
    gradle(
      task: "bundle",
      flavor: "production",
      build_type: "Release",
      properties: {
        "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
        "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
        "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
        "android.injected.signing.key.password" => ENV["KEY_PASSWORD"],
      }
    )
    upload_to_play_store(track: "internal")
  end

  desc "Promote internal to beta"
  lane :promote_to_beta do
    upload_to_play_store(
      track: "internal",
      track_promote_to: "beta",
      skip_upload_apk: true,
      skip_upload_aab: true
    )
  end

  desc "Promote beta to production"
  lane :release do
    upload_to_play_store(
      track: "beta",
      track_promote_to: "production",
      skip_upload_apk: true,
      skip_upload_aab: true,
      rollout: "0.1"  # 10% staged rollout
    )
  end

  private_lane :increment_version_code do
    path = "../app/build.gradle.kts"
    re = /versionCode\s*=\s*(\d+)/
    s = File.read(path)
    versionCode = s[re, 1].to_i + 1
    s[re] = "versionCode = #{versionCode}"
    File.write(path, s)
    versionCode
  end
end
```

### Android Appfile
```ruby
# fastlane/Appfile (Android)
json_key_file(ENV["PLAY_STORE_JSON_KEY_PATH"] || "fastlane/service-account.json")
package_name("com.example.app")
```

### iOS Fastfile
```ruby
# fastlane/Fastfile (iOS)
default_platform(:ios)

platform :ios do

  desc "Run all tests"
  lane :test do
    run_tests(
      scheme: "ExampleApp",
      devices: ["iPhone 16"],
      clean: true,
      code_coverage: true
    )
  end

  desc "Build and upload to TestFlight"
  lane :beta do
    setup_ci if ENV["CI"]

    api_key = app_store_connect_api_key(
      key_id: ENV["APP_STORE_CONNECT_API_KEY_ID"],
      issuer_id: ENV["APP_STORE_CONNECT_API_ISSUER_ID"],
      key_content: ENV["APP_STORE_CONNECT_API_KEY_CONTENT"],
      is_key_content_base64: true
    )

    match(type: "appstore", readonly: is_ci, api_key: api_key)

    increment_build_number(
      build_number: ENV["BUILD_NUMBER"] || latest_testflight_build_number + 1,
      xcodeproj: "ExampleApp.xcodeproj"
    )

    build_app(
      scheme: "ExampleApp",
      configuration: "Release",
      export_method: "app-store",
      export_options: {
        provisioningProfiles: {
          "com.example.app" => "match AppStore com.example.app"
        }
      }
    )

    upload_to_testflight(
      api_key: api_key,
      skip_waiting_for_build_processing: true,
      changelog: File.read("fastlane/changelog.txt") rescue "Bug fixes and improvements"
    )
  end

  desc "Submit to App Store"
  lane :release do
    api_key = app_store_connect_api_key(
      key_id: ENV["APP_STORE_CONNECT_API_KEY_ID"],
      issuer_id: ENV["APP_STORE_CONNECT_API_ISSUER_ID"],
      key_content: ENV["APP_STORE_CONNECT_API_KEY_CONTENT"],
      is_key_content_base64: true
    )

    deliver(
      api_key: api_key,
      submit_for_review: true,
      automatic_release: false,
      force: true,
      skip_screenshots: true,
      skip_metadata: false
    )
  end
end
```

### iOS Matchfile
```ruby
# fastlane/Matchfile
git_url("https://github.com/your-org/certificates-repo")
storage_mode("git")
app_identifier(["com.example.app", "com.example.app.widget"])
username("dev@example.com")
```

### iOS Appfile
```ruby
# fastlane/Appfile
app_identifier("com.example.app")
apple_id("dev@example.com")
itc_team_id("123456789")
team_id("ABCDEF1234")
```

---

## Step 5 — Build Variants (Environment Configs)

### Android build flavors
```kotlin
// app/build.gradle.kts
android {
    flavorDimensions += "environment"
    
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_BASE_URL", "\"https://api-dev.example.com\"")
            resValue("string", "app_name", "ExampleApp Dev")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "API_BASE_URL", "\"https://api-staging.example.com\"")
            resValue("string", "app_name", "ExampleApp Staging")
        }
        create("production") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
            resValue("string", "app_name", "ExampleApp")
        }
    }
}
```

### iOS Xcode Configurations
```
ExampleApp.xcodeproj/
  xcconfig/
    Base.xcconfig          — shared settings
    Debug.xcconfig         — includes Base, overrides for debug
    Staging.xcconfig       — includes Base, staging API URL
    Release.xcconfig       — includes Base, production settings
```

```
// Staging.xcconfig
#include "Base.xcconfig"
API_BASE_URL = https://api-staging.example.com
BUNDLE_ID_SUFFIX = .staging
```

---

## Step 6 — Secrets Management

### What goes in CI secrets (NEVER in the repo)

| Secret Name | Description |
|---|---|
| `KEYSTORE_BASE64` | Android keystore, base64 encoded: `base64 -i release.keystore` |
| `KEYSTORE_PASSWORD` | Android keystore password |
| `KEY_ALIAS` | Android key alias |
| `KEY_PASSWORD` | Android key password |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Google Play service account JSON (full contents) |
| `ASC_KEY_ID` | App Store Connect API key ID |
| `ASC_ISSUER_ID` | App Store Connect API issuer ID |
| `ASC_KEY_CONTENT` | App Store Connect `.p8` key content, base64 encoded |
| `MATCH_PASSWORD` | Fastlane Match encryption password |
| `MATCH_GIT_TOKEN` | GitHub token for Match certificates repo (base64: `echo -n "user:token" | base64`) |
| `SLACK_WEBHOOK_URL` | Slack incoming webhook URL |
| `FIREBASE_APP_ID` | Firebase App ID for App Distribution |
| `FIREBASE_TOKEN` | Firebase CI token: `firebase login:ci` |

### What's safe to commit
- `.github/workflows/*.yml` — workflow files (no secrets, uses `${{ secrets.NAME }}`)
- `fastlane/Fastfile`, `Matchfile`, `Appfile` — use `ENV["VAR"]` for sensitive values
- `fastlane/Gemfile` — Ruby dependencies

---

## Step 7 — Setup README

Generate a `CI_SETUP.md`:

```markdown
# CI/CD Pipeline Setup

## Prerequisites
- GitHub repository with Actions enabled
- [Android] Google Play Console project with API access set up
- [iOS] Apple Developer account, App Store Connect access, certificates repo

## First-Time Setup

### Android
1. Generate a release keystore: `keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias mykey`
2. Base64 encode it: `base64 -i release.keystore | pbcopy`
3. Add GitHub secrets: KEYSTORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
4. Create a Google Play service account (Play Console > Setup > API access)
5. Add PLAY_STORE_SERVICE_ACCOUNT_JSON secret

### iOS
1. Create App Store Connect API key (Users > Integrations > App Store Connect API)
2. Add secrets: ASC_KEY_ID, ASC_ISSUER_ID, ASC_KEY_CONTENT (base64 of .p8 file)
3. Set up Fastlane Match: `bundle exec fastlane match init`
4. Run `bundle exec fastlane match development` and `bundle exec fastlane match appstore`
5. Add MATCH_PASSWORD and MATCH_GIT_TOKEN secrets

## Running Locally

### Android
bundle exec fastlane android test
bundle exec fastlane android internal

### iOS
bundle exec fastlane ios test
bundle exec fastlane ios beta

## Build Variants
| Workflow | Branch | Variant | Target |
|---|---|---|---|
| PR check | any | debug | unit tests only |
| CI | develop | staging debug | tests + Firebase |
| Release | main | production release | Play Store / TestFlight |
```

---

## Example

**Input:**
> "Set up GitHub Actions for my Android app. I want it to run tests on every PR and deploy to Play Store internal track on merges to main. We use Kotlin + Gradle."

**Expected output:**
- `.github/workflows/android.yml` — test job on PR, build+deploy job on main merge
- `fastlane/Fastfile` — `test`, `internal`, and `release` lanes
- `fastlane/Appfile` — package name and service account
- `CI_SETUP.md` — keystore encoding steps, secrets list, first-time setup guide
- `fastlane/whatsnew/` folder structure for Play Store changelogs

---

## Checklist Before Responding

- [ ] Provider confirmed (GitHub Actions, Bitrise, GitLab CI, Fastlane)
- [ ] Platform(s) confirmed
- [ ] Deployment target(s) confirmed
- [ ] Build variants / flavors handled
- [ ] Signing setup included (keystore for Android, Match for iOS)
- [ ] Gradle / SPM caching configured
- [ ] Secrets listed with setup instructions — none hardcoded
- [ ] Build number auto-increment included
- [ ] Slack/notification step included if requested
- [ ] `CI_SETUP.md` README generated
- [ ] Fastlane Gemfile included
