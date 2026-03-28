# iOS Debugging — Reference

## Xcode Crash Report Format

Crash reports (`.ips` or `.crash`) have this structure:

```
Incident Identifier: ABC-123-...
Hardware Model:      iPhone16,2
Process:             MyApp [1234]
Path:                /private/var/containers/Bundle/.../MyApp.app/MyApp
Identifier:          com.example.myapp
Version:             2.1.0 (42)
AppStoreTools:       16C5032a
OS Version:          iPhone OS 18.2 (22C150)

Exception Type:  EXC_CRASH (SIGABRT)
Exception Codes: 0x0000000000000000, 0x0000000000000000
Termination Reason: SIGNAL 6

Application Specific Information:
*** Terminating app due to uncaught exception 'NSInvalidArgumentException',
reason: '*** -[__NSPlaceholderArray initWithObjects:count:]: attempt to insert nil object'

Thread 0 Crashed:
0   libswiftCore.dylib              0x00000001a1234567 swift_willThrow + 12
1   MyApp                          0x000000010012abcd ProfileViewModel.load() + 124
2   MyApp                          0x000000010012bcde closure in ProfileView.body + 88
...

Thread 1:
0   libsystem_kernel.dylib          0x... __psynch_cvwait + 8
...

Binary Images:
0x100108000 - 0x10025ffff MyApp arm64 <UUID> /var/containers/.../MyApp.app/MyApp
```

### Key fields to read
- **Exception Type**: `EXC_CRASH`, `EXC_BAD_ACCESS`, `EXC_BAD_INSTRUCTION`
- **Exception Subtype**: `KERN_INVALID_ADDRESS` (bad pointer), `KERN_PROTECTION_FAILURE` (read-only memory)
- **Termination Reason**: `SIGNAL 6` (abort), `SIGNAL 11` (SIGSEGV), `SIGNAL 10` (SIGBUS)
- **Thread X Crashed**: The thread where the crash occurred. Almost always Thread 0 for Swift crashes.
- **Application Specific Information**: For Objective-C exceptions, this shows the actual error message.

---

## Symbolication

Unsymbolicated crash reports show hex addresses instead of function names. To symbolicate:

### Automatically in Xcode
1. Open Xcode > Window > Organizer > Crashes.
2. Drag the `.ips` file into the crash list.
3. Xcode auto-symbolicates if the `.dSYM` is in the derived data or uploaded to App Store.

### Manually with `atos`
```bash
# Get the load address from "Binary Images" section of the crash report
# Format: atos -arch arm64 -o MyApp.app.dSYM/Contents/Resources/DWARF/MyApp -l <load_address> <crash_address>
atos -arch arm64 -o MyApp.app.dSYM/Contents/Resources/DWARF/MyApp -l 0x100108000 0x10012abcd
# Output: ProfileViewModel.load() (in MyApp) (ProfileViewModel.swift:47)
```

### Find dSYM for a specific build
```bash
# Search by UUID from crash report
mdfind "com_apple_xcode_dsym_uuids == <UUID-FROM-CRASH>"
```

---

## Signing and Provisioning Troubleshooting

### Certificates
```bash
# List all signing identities
security find-identity -v -p codesigning

# Check certificate expiry
security find-certificate -a -p | openssl x509 -noout -enddate
```

### Provisioning profiles
```bash
# List all installed profiles
ls ~/Library/MobileDevice/Provisioning\ Profiles/

# Inspect a profile
security cms -D -i ~/Library/MobileDevice/Provisioning\ Profiles/UUID.mobileprovision
```

### Common signing errors and fixes

| Error | Cause | Fix |
|---|---|---|
| `No signing certificate found` | Cert not in keychain | Download from developer.apple.com > Certificates |
| `Provisioning profile doesn't include the app ID` | Bundle ID mismatch | Match `PRODUCT_BUNDLE_IDENTIFIER` to App ID in portal |
| `Device not included in profile` | UDID not registered | Add device UDID in developer portal, regenerate profile |
| `Provisioning profile has expired` | Profile > 1 year old | Regenerate in developer portal |
| `Certificate has been revoked` | Cert revoked | Create new cert, update profile to use it |
| `entitlements do not match` | Added capability without updating profile | Add capability in developer portal, regenerate profile |

### Fastlane Match (recommended for teams)
```ruby
# Matchfile
git_url("https://github.com/your-org/certificates")
storage_mode("git")
type("development")
app_identifier(["com.example.app"])
username("dev@example.com")
```
```bash
fastlane match development   # Install dev certs
fastlane match appstore      # Install distribution certs
fastlane match nuke appstore # Nuclear option: revoke all and start fresh
```

---

## SPM Resolution Errors

### Checksum mismatch
```
error: the checksum '...' does not match previously recorded checksum for '...'
```
**Fix**: Delete the cached package and re-resolve:
```bash
rm -rf ~/Library/Caches/org.swift.swiftpm
rm Package.resolved
# Then File > Packages > Resolve Package Versions in Xcode
```

### Version conflict
```
error: package resolution failed: Dependencies could not be resolved because root depends on...
```
**Fix**: Open `Package.resolved` and look for the conflicting package. Align version requirements across all packages that depend on it:
```json
// Package.resolved — change from `.upToNextMajor(from: "1.0.0")` to `.exact("1.2.3")`
```

### Network error during resolution
```
error: failed to fetch 'https://github.com/...' which is required by package
```
**Fix**: Check network, try `File > Packages > Reset Package Caches`, or use a mirror if behind a corporate proxy.

---

## Common Xcode Build Errors

### Undefined symbols for architecture arm64
```
Undefined symbols for architecture arm64:
  "_OBJC_CLASS_$_AVAudioSession", referenced from:...
```
**Fix**: Add the framework to the target:
- `AVFoundation.framework` for AVAudioSession
- `StoreKit.framework` for SKPayment
- `CoreLocation.framework` for CLLocationManager
- `UserNotifications.framework` for UNNotificationRequest

Go to: Target > General > Frameworks, Libraries, and Embedded Content > `+`

### File not found (header)
```
error: 'SomeHeader.h' file not found
```
**Fix**: If using CocoaPods, run `pod install`. If using Xcode directly, add the framework's header search path in Build Settings > Header Search Paths.

### Ambiguous type in expressions
```
error: type of expression is ambiguous without more context
```
**Root cause**: Complex Swift expression the compiler can't infer. Usually occurs in closures, chained calls, or large dictionary literals.
**Fix**: Add explicit type annotations to break down the expression:
```swift
// Before
let result = items.filter { $0.isActive }.map { transform($0) }.sorted()

// After — annotate to help the compiler
let active: [Item] = items.filter { $0.isActive }
let transformed: [TransformedItem] = active.map { transform($0) }
let result = transformed.sorted()
```

### Build succeeds but crashes on launch (missing framework)
```
dyld: Library not loaded: @rpath/SomeFramework.framework/SomeFramework
Reason: image not found
```
**Fix**: In Target > General > Frameworks: change the framework from "Do Not Embed" to "Embed & Sign".

---

## Instruments Profiling

### Memory leaks
1. Product > Profile > Leaks template.
2. Run the app, navigate between screens.
3. Look for red `X` marks in the leak track.
4. Click a leak to see the allocation backtrace.

### CPU profiling (Time Profiler)
1. Product > Profile > Time Profiler template.
2. Record while reproducing the performance issue.
3. In the call tree, enable "Hide System Libraries" to focus on app code.
4. Sort by "Self Time" to find the heaviest function calls.

### Main thread checker
Enable in Scheme > Run > Diagnostics > Main Thread Checker. Catches UIKit/SwiftUI access off the main thread at runtime with a console message.
