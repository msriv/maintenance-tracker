# Android CI — Reference

## Gradle Signing Configuration

### Sign from environment variables (CI-friendly)
```kotlin
// app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore/release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### Encode keystore for CI
```bash
# Encode
base64 -i release.keystore | pbcopy  # macOS — copies to clipboard
base64 -w 0 release.keystore         # Linux — prints to stdout

# Decode in CI (GitHub Actions step)
echo "$KEYSTORE_BASE64" | base64 --decode > app/release.keystore
```

---

## Google Play Service Account Setup

1. Go to Google Play Console > Setup > API access.
2. Link to a Google Cloud project (or create one).
3. Create a service account with "Service account" role.
4. Grant the service account "Release manager" role in Play Console.
5. Download the JSON key file.
6. Store JSON contents as a CI secret: `PLAY_STORE_SERVICE_ACCOUNT_JSON`.

### Fastlane supply (gradle-play-publisher alternative)
```ruby
# Alternative: gradle-play-publisher Gradle plugin
# build.gradle.kts
plugins {
    id("com.github.triplet.play") version "3.10.1"
}

play {
    serviceAccountCredentials.set(file(System.getenv("PLAY_STORE_JSON_KEY_PATH") ?: "service-account.json"))
    track.set("internal")
    defaultToAppBundles.set(true)
}
```

```bash
# Deploy via Gradle task
./gradlew publishProductionReleaseBundle
```

---

## GitHub Actions Full Build Matrix

```yaml
# Run debug and release builds in parallel
jobs:
  build:
    strategy:
      matrix:
        build_type: [debug, release]
        flavor: [dev, staging, production]
        exclude:
          - build_type: release
            flavor: dev
          - build_type: release
            flavor: staging
    
    steps:
      - name: Build ${{ matrix.flavor }} ${{ matrix.build_type }}
        run: ./gradlew assemble${{ matrix.flavor | capitalize }}${{ matrix.build_type | capitalize }}
```

## Firebase App Distribution

```yaml
- name: Upload to Firebase App Distribution
  uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    token: ${{ secrets.FIREBASE_TOKEN }}
    groups: internal-testers
    file: app/build/outputs/apk/staging/debug/app-staging-debug.apk
    releaseNotes: "Build ${{ github.run_number }} from ${{ github.ref }}"
```

Or via Fastlane:
```ruby
lane :firebase do
  gradle(task: "assemble", flavor: "staging", build_type: "Debug")
  firebase_app_distribution(
    app: ENV["FIREBASE_APP_ID"],
    firebase_cli_token: ENV["FIREBASE_TOKEN"],
    groups: "internal-testers",
    release_notes: "Build #{ENV['BUILD_NUMBER']}"
  )
end
```

---

## Gradle Caching Strategy

### Cache keys (ordered by specificity)
```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
      .gradle/configuration-cache
    key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle.kts', '**/gradle-wrapper.properties', '**/libs.versions.toml') }}
    restore-keys: |
      gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle.kts') }}
      gradle-${{ runner.os }}-
```

### Gradle configuration cache (Gradle 8+)
```kotlin
// gradle.properties
org.gradle.configuration-cache=true
org.gradle.configuration-cache.problems=warn
```

### Build speed optimizations
```kotlin
// gradle.properties for CI
org.gradle.jvmargs=-Xmx4g -XX:+UseParallelGC
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=false  # Don't use daemon in CI
kotlin.incremental=true
```

---

## Version Code Automation

### Auto-increment with GitHub run number
```kotlin
// app/build.gradle.kts
val buildNumber = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1

android {
    defaultConfig {
        versionCode = buildNumber
        versionName = "1.0.$buildNumber"
    }
}
```

```yaml
# GitHub Actions step
- name: Set build number
  run: echo "BUILD_NUMBER=${{ github.run_number }}" >> $GITHUB_ENV
```

### Read versionCode from gradle
```bash
./gradlew -q :app:printVersionCode
```

```kotlin
// app/build.gradle.kts
tasks.register("printVersionCode") {
    doLast { println(android.defaultConfig.versionCode) }
}
```

---

## What's New (changelogs) for Play Store

Directory structure for per-language changelogs:
```
fastlane/
  metadata/
    android/
      en-US/
        changelogs/
          default.txt           — fallback changelog
          1234.txt              — version-specific (versionCode)
      de-DE/
        changelogs/
          default.txt
```

```ruby
# Fastlane reads these automatically when you call upload_to_play_store
upload_to_play_store(
  track: "internal",
  whats_new_files: Dir["fastlane/metadata/android/*/changelogs/default.txt"]
)
```

---

## Bitrise Android Configuration

```yaml
# bitrise.yml (relevant sections)
workflows:
  primary:
    steps:
      - activate-ssh-key: {}
      - git-clone: {}
      - cache-pull: {}
      - install-missing-android-tools:
          inputs:
            - gradlew_path: "$GRADLEW_PATH"
      - android-lint:
          inputs:
            - project_location: "$PROJECT_LOCATION"
      - android-unit-test:
          inputs:
            - project_location: "$PROJECT_LOCATION"
            - variant: "debug"
      - android-build:
          inputs:
            - project_location: "$PROJECT_LOCATION"
            - variant: "release"
            - build_type: "aab"
      - sign-apk:
          inputs:
            - android_app: "$BITRISE_AAB_PATH"
            - keystore_url: "$BITRISEIO_ANDROID_KEYSTORE_URL"
            - keystore_password: "$ANDROID_KEYSTORE_PASSWORD"
            - keystore_alias: "$ANDROID_KEY_ALIAS"
            - private_key_password: "$ANDROID_KEY_PASSWORD"
      - google-play-deploy:
          inputs:
            - package_name: "com.example.app"
            - track: "internal"
            - service_account_json_key_path: "$BITRISEIO_GOOGLE_PLAY_API_JSON"
      - cache-push: {}
```

---

## GitLab CI Android Configuration

```yaml
# .gitlab-ci.yml
image: reactnativecommunity/react-native-android:latest  # or custom Android image

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"

cache:
  key: "${CI_COMMIT_REF_SLUG}"
  paths:
    - .gradle/
    - build/

stages:
  - test
  - build
  - deploy

test:
  stage: test
  script:
    - ./gradlew test lint
  artifacts:
    reports:
      junit: "**/build/test-results/test/*.xml"

build:
  stage: build
  only:
    - main
  script:
    - echo $KEYSTORE_BASE64 | base64 -d > app/release.keystore
    - ./gradlew bundleProductionRelease
  artifacts:
    paths:
      - app/build/outputs/bundle/

deploy:
  stage: deploy
  only:
    - main
  script:
    - bundle exec fastlane android internal
  environment:
    name: production
```
