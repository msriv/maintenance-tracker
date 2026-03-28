---
name: mobile-debug-doctor
description: >
  Diagnose and fix mobile app crashes, errors, and build failures. Use this skill whenever the
  user pastes or describes: a logcat output, an Android crash log, an ANR trace, a Firebase
  Crashlytics stack trace, an Xcode crash report (.ips file), a Sentry mobile error, a Gradle
  build error, an Xcode build error, a CocoaPods error, an SPM resolution failure, a signing or
  provisioning profile error, a ProGuard/R8 obfuscation issue, or any stack trace from an Android
  or iOS app. Also triggers for: "my app keeps crashing", "why is my app getting ANRs", "how do
  I fix this Gradle error", "my build is failing", "Xcode won't archive", "my app crashes on
  launch", "what does this stack trace mean", "NullPointerException in my Android app",
  "force unwrap crash iOS", "OOM error", "StrictMode violation". Do NOT use for UI generation or
  API layer setup.
---

# Mobile Debug Doctor Skill

## Step 0 — Accept Input

The user will paste one or more of:
- Raw logcat output (Android)
- Xcode crash report / `.ips` file (iOS)
- ANR trace (`/data/anr/traces.txt`)
- Firebase Crashlytics stack trace
- Gradle build error output
- Xcode build error output
- CocoaPods or SPM error
- ProGuard/R8 mapping issue (obfuscated stack trace)

If the user only describes the crash in words (no trace), ask them to share the actual error output before diagnosing. A description alone is rarely enough to pinpoint the root cause.

---

## Step 1 — Identify Error Type

Classify the input as one of:

| Type | Key signals |
|---|---|
| Runtime crash (Android) | `FATAL EXCEPTION`, `AndroidRuntime`, exception class + `at com.example...` |
| ANR | `ANR in`, `Reason: Input dispatching timed out`, `main` thread blocked |
| Build failure (Gradle) | `BUILD FAILED`, `error: `, `Execution failed for task` |
| Runtime crash (iOS) | `Exception Type: EXC_CRASH`, `Thread 0 Crashed`, `SIGSEGV`, `SIGABRT` |
| Build failure (Xcode) | `error:`, `Build input file cannot be found`, `Undefined symbols` |
| CocoaPods error | `[!]`, `Unable to find a specification`, `pod install` |
| SPM error | `error: package resolution failed`, `checksum mismatch` |
| Signing / provisioning | `code signing error`, `No profile for team`, `expired certificate` |
| Obfuscated trace | Method names look like `a.b.c()` or `com.example.a.a()` |

State the classification explicitly before proceeding.

---

## Step 2 — Extract the Key Signal

### For Android runtime crashes:
1. Find the exception type (e.g., `java.lang.NullPointerException`, `java.lang.IllegalStateException`).
2. Find the first non-system line in the stack trace — this is the direct crash site in app code.
3. Find the root cause line (the deepest `Caused by:` block).

### For ANR traces:
1. Find the `main` thread section.
2. Look for `waiting to lock` or `sleeping` or a long chain of `nativePoll`.
3. Identify what the main thread was doing (database query, network call, SharedPreferences, etc.).

### For Gradle build errors:
1. Find lines starting with `error:` (compilation errors) or `Execution failed for task` (task failures).
2. Note the task name (e.g., `:app:compileDebugKotlin`, `:app:mergeDebugResources`).
3. Find the first error after `BUILD FAILED`.

### For iOS crashes:
1. Find `Exception Type` and `Exception Subtype`.
2. Find `Thread X Crashed` — the crashing thread.
3. Extract the first 3–5 frames of the crashing thread, especially any frames in the app's own code (not system frameworks).
4. Check `Application Specific Information` for additional context.

---

## Step 3 — Explain Root Cause in Plain Language

Explain what actually went wrong in 2–5 sentences. Avoid jargon where possible. Structure:
- **What happened**: The app crashed because [X].
- **Why it happened**: This occurred because [Y — the underlying cause].
- **When it happens**: This type of crash typically occurs when [Z — common trigger].

---

## Step 4 — Provide the Fix

Give a concrete, copy-pasteable fix. Include:
- The specific file/location to change.
- Before and after code snippets where applicable.
- If it's a build error, the exact Gradle/Xcode config change.
- If it's a dependency conflict, the exact version to pin.

---

## Step 5 — Preventive Measures

Suggest 1–3 ways to prevent this class of issue in the future.

---

## Common Crash Patterns Reference

### Android

#### NullPointerException
```
java.lang.NullPointerException: Attempt to invoke virtual method '...' on a null object reference
    at com.example.app.ProfileFragment.updateUI(ProfileFragment.kt:42)
```
**Root cause**: `binding`, `view`, or injected dependency is null — usually because it's accessed before `onViewCreated` or after `onDestroyView`.
**Fix**: Use `binding?.let { }` safe calls, or check `isAdded && view != null` before accessing views. Never store `binding` in a field without nulling it in `onDestroyView`.
```kotlin
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null  // Prevent memory leaks AND null access
}
```

#### IllegalStateException — Fragment not attached
```
java.lang.IllegalStateException: Fragment ProfileFragment not attached to a context.
```
**Root cause**: Callback or coroutine fires after `onDetach()`.
**Fix**: Use `viewLifecycleOwner.lifecycleScope` instead of `lifecycleScope` for UI-related coroutines in Fragments. Check `isAdded` before `requireContext()`.

#### MainThread NetworkException
```
android.os.NetworkOnMainThreadException
    at android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork
```
**Root cause**: Network call on main thread.
**Fix**: Wrap in `withContext(Dispatchers.IO) { }` or use Retrofit's `suspend` functions which run off the main thread automatically.

#### OOM — Out of Memory
```
java.lang.OutOfMemoryError: Failed to allocate a X byte allocation
```
**Root causes**: Bitmap loaded without sampling, large list held in memory, memory leak (activity reference in static context).
**Fix**: Use Coil/Glide for image loading (handles bitmap pooling). Use `LeakCanary` to detect leaks. In `RecyclerView`, avoid holding Activity context in Adapter.

#### ANR — Main thread blocked
```
ANR in com.example.app (com.example.app/.MainActivity)
Reason: Input dispatching timed out
```
**Root cause**: Main thread blocked for >5s. Common causes: `SharedPreferences.commit()`, database read, `Thread.sleep()`, synchronous network call.
**Fix**: Use `SharedPreferences.apply()` (async), Room with suspend functions, `Dispatchers.IO` for any I/O.
```kotlin
// Wrong
val value = prefs.getString("key", null)  // Synchronous read — usually fine
prefs.edit().putString("key", value).commit()  // commit() blocks main thread

// Right
prefs.edit().putString("key", value).apply()  // apply() is async
```

#### Compose — Recomposition crash
```
java.lang.IllegalStateException: Reading a state that was created after the snapshot was taken
```
**Root cause**: State modified from a non-Compose context or read during composition from wrong scope.
**Fix**: Ensure state mutations happen in `LaunchedEffect`, event handlers, or `rememberCoroutineScope().launch { }` — never directly in the composable body.

#### Fragment back stack crash
```
java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
```
**Root cause**: Fragment transaction committed after `onSaveInstanceState` is called.
**Fix**: Use `commitAllowingStateLoss()` only for non-critical UI changes. For navigation, use the result listener pattern or Shared ViewModel instead of direct fragment transactions in async callbacks.

---

### iOS

#### Force unwrap crash (EXC_CRASH / SIGABRT)
```
Fatal error: Unexpectedly found nil while unwrapping an Optional value
Thread 0 Crashed: ... Swift runtime failure: force unwrapped a nil value
```
**Root cause**: `!` force unwrap on a nil Optional.
**Fix**: Replace with `guard let`, `if let`, or `??` default value.
```swift
// Wrong
let user = viewModel.user!

// Right
guard let user = viewModel.user else { return }
// or
let user = viewModel.user ?? User.placeholder()
```

#### Main thread violation (SwiftUI / UIKit)
```
[ThreadSanitizer] Swift access race in ...
Publishing changes from background threads is not allowed
```
**Root cause**: `@Published` property updated on background thread.
**Fix**: Annotate ViewModel with `@MainActor` (iOS 17+) or wrap updates in `await MainActor.run { }`.
```swift
@MainActor
final class FeatureViewModel: ObservableObject { ... }
```

#### Retain cycle / memory leak
Signal: app memory grows unboundedly; `deinit` never called.
**Fix**: Use `[weak self]` in closures that capture self. Use Instruments > Leaks to confirm.
```swift
// Wrong
viewModel.onComplete = { self.handleCompletion() }

// Right
viewModel.onComplete = { [weak self] in self?.handleCompletion() }
```

#### SwiftUI state mutation off main actor
```
Modifying state during view update, this will cause undefined behavior
```
**Root cause**: State modified synchronously inside `body`.
**Fix**: Use `.task`, `.onAppear`, or an explicit button action — never modify state during the view's `body` evaluation.

---

## Gradle Build Errors

### Dependency conflict
```
Execution failed for task ':app:checkDebugDuplicateClasses'.
> 1 exception was raised by workers:
  com.example.Foo is defined multiple times
```
**Fix**: Use `./gradlew :app:dependencies --configuration debugRuntimeClasspath` to find the conflict. Force a specific version:
```kotlin
configurations.all {
    resolutionStrategy {
        force("com.example:library:1.2.3")
    }
}
```

### Kotlin version mismatch
```
error: module was compiled with an incompatible version of Kotlin. 
The binary version of its metadata is X.Y.Z, expected version is A.B.C.
```
**Fix**: Align `kotlin_version` in `libs.versions.toml` with the version used by all plugins.

### R8 / ProGuard — class not found at runtime
```
java.lang.ClassNotFoundException: com.example.dto.UserDto
```
**Fix**: Add keep rule for the affected class:
```proguard
-keep class com.example.dto.** { *; }
```

---

## Xcode Build Errors

### Missing file / module not found
```
error: Build input file cannot be found: '/Users/.../Pods/Headers/...'
```
**Fix**: Run `pod install` (or `pod update` for version issues). Then clean build folder (`Cmd+Shift+K`).

### Undefined symbols
```
Undefined symbols for architecture arm64:
  "_OBJC_CLASS_$_SKPaymentQueue", referenced from:...
```
**Fix**: Add the missing framework to the target's `Frameworks, Libraries, and Embedded Content` section (in this case, `StoreKit.framework`).

### Provisioning profile expired or mismatch
```
No signing certificate "iOS Development" found
error: No profile for team 'XXXXXXXXXX' matching '...'
```
**Fix steps**:
1. Xcode > Preferences > Accounts — refresh certificates.
2. In target settings, set Signing to "Automatically manage signing."
3. If that fails: `security delete-certificate` the expired cert, then re-download from Apple Developer portal.
4. Check that the device UDID is registered in the provisioning profile.

---

## Checklist Before Responding

- [ ] Error type classified
- [ ] Key signal (exception class, faulting frame, failing task) extracted and quoted
- [ ] Root cause explained in plain language
- [ ] Concrete fix provided with code/config snippet
- [ ] Preventive measure(s) suggested
- [ ] If trace is obfuscated, explain how to symbolicate / deobfuscate
