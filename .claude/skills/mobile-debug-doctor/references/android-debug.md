# Android Debugging — Reference

## Logcat Format

```
DATE     TIME     PID-TID/PACKAGE PRIORITY/TAG: MESSAGE
03-28 14:22:01.123  1234-1234/com.example.app E/AndroidRuntime: FATAL EXCEPTION: main
```

### Priority levels
- `V` Verbose, `D` Debug, `I` Info, `W` Warning, `E` Error, `F` Fatal, `S` Silent

### Filter logcat for your app only
```bash
adb logcat --pid=$(adb shell pidof com.example.app)
# or by tag
adb logcat -s AndroidRuntime:E MyTag:D
```

### Capture crash log to file
```bash
adb logcat -d > crash.txt  # Dump current buffer
adb logcat > ongoing.txt   # Stream until Ctrl+C
```

---

## ANR Trace Structure

ANR traces are written to `/data/anr/traces.txt` on device.

### Pull ANR trace
```bash
adb pull /data/anr/traces.txt
```

### ANR trace anatomy
```
----- pid 1234 at 2024-03-28 14:22:01 -----
Cmd line: com.example.app

DALVIK THREADS (23):
"main" prio=5 tid=1 Native
  | group="main" sCount=1 dsCount=0 flags=1 obj=0x... self=0x...
  | sysTid=1234 nice=-10 cgrp=default sched=0/0 handle=0x...
  | state=S schedstat=( 0 0 0 ) utm=10 stm=5 core=0 HZ=100
  | stack=0x7f... stackSize=8192KB
  | held mutexes=
  native: #00 pc 000... /system/lib64/libc.so (__epoll_pwait+8)
  native: #01 pc 000... /system/lib64/liblooper.so (Looper::pollOnce+...)
  ...
  at android.database.sqlite.SQLiteDatabase.query(SQLiteDatabase.java:1234)
  at com.example.app.data.local.UserDao.getUser(UserDao.java:56)
  at com.example.app.ui.MainActivity.onCreate(MainActivity.kt:42)   ← BLOCKING CALL HERE
```

### Reading ANR for deadlock
Look for `waiting to lock <0x...>` on the main thread, then find which thread holds that lock:
```
"main" prio=5 tid=1 Blocked
  | waiting to lock <0x12345678> held by thread 12

"background-thread" prio=5 tid=12 Blocked
  | waiting to lock <0x87654321> held by thread 1  ← DEADLOCK
```

---

## Gradle Dependency Resolution

### Print full dependency tree
```bash
./gradlew :app:dependencies --configuration debugRuntimeClasspath > deps.txt
```

### Find who's pulling in a specific library
```bash
./gradlew :app:dependencyInsight --configuration debugRuntimeClasspath --dependency okhttp
```

### Force resolution of a version conflict
```kotlin
// build.gradle.kts (app)
configurations.all {
    resolutionStrategy {
        force("com.squareup.okhttp3:okhttp:4.12.0")
        eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion("1.9.22")
                because("Align all Kotlin stdlib versions")
            }
        }
    }
}
```

### Exclude transitive dependency
```kotlin
implementation("com.library:name:1.0.0") {
    exclude(group = "com.conflicting", module = "library")
}
```

---

## ProGuard / R8

### Generate and keep mapping file (release builds)
```kotlin
// build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### Symbolicate an obfuscated stack trace
```bash
# mapping.txt is in app/build/outputs/mapping/release/
java -jar retrace.jar mapping.txt obfuscated-stacktrace.txt
```

### Essential ProGuard rules for common libraries
```proguard
# Keep serializable DTOs (Gson/kotlinx.serialization)
-keep class com.example.app.data.network.dto.** { *; }
-keepclassmembers class com.example.app.data.network.dto.** { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.example.app.**$$serializer { *; }
-keepclassmembers class com.example.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Enum values (often stripped incorrectly)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
```

---

## Common Gradle Task Failures

### `mergeDebugResources` — duplicate resource
```
AAPT: error: duplicate value for resource 'attr/color' with config ''
```
**Fix**: Check if multiple modules define the same resource name. Rename one, or set `resourcePrefix` in each module's `build.gradle.kts`:
```kotlin
android { resourcePrefix = "feature_auth_" }
```

### `kaptGenerateStubsDebugKotlin` — Hilt processing error
```
error: [Hilt] @HiltViewModel is only supported on classes that directly extend ViewModel
```
**Fix**: Your ViewModel must extend `ViewModel()` directly. Ensure you're not extending a custom base class that Hilt can't inspect. If you need a base class, use `@HiltViewModel` on the concrete class only.

### `compileDebugKotlin` — unresolved reference
```
error: unresolved reference: SomeClass
```
**Fix**: Check module dependency ordering. If `:feature-auth` needs a class from `:core-domain`, add `implementation(project(":core:core-domain"))` to `:feature-auth`'s `build.gradle.kts`.

### Gradle sync fails after updating AGP
1. Update `gradle-wrapper.properties` to the compatible Gradle version (check AGP release notes).
2. Update `compileSdk` and `targetSdk` if required by the new AGP.
3. Run `./gradlew wrapper --gradle-version X.Y.Z` to update wrapper.

---

## StrictMode Setup (Development Only)

```kotlin
// Application.onCreate() — debug builds only
if (BuildConfig.DEBUG) {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()       // Log violations (use penaltyDeath() to catch them in tests)
            .build()
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .detectActivityLeaks()
            .penaltyLog()
            .build()
    )
}
```

## LeakCanary Setup

```kotlin
// build.gradle.kts (app)
dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}
// No code needed — LeakCanary auto-installs via ContentProvider
```
