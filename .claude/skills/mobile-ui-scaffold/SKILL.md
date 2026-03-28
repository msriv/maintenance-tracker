---
name: mobile-ui-scaffold
description: >
  Generate screen/component boilerplate and UI scaffolds for Android (Jetpack Compose + Kotlin)
  and iOS (SwiftUI + Swift). Use this skill whenever the user asks to create a screen, page, view,
  component, widget, form, or UI element for a mobile app — even if they just say "build me a login
  screen", "add a settings page", "create a profile view", "make a dashboard", or "scaffold a new
  feature screen". Also triggers for: navigation setup between screens, screen state management
  (loading/error/empty), accessibility on mobile UI, ViewModel wiring, Material 3 theming on
  Android, HIG compliance on iOS, Compose previews, SwiftUI previews, UiState modeling, or any
  request to "stub out", "scaffold", "bootstrap", or "template" a mobile UI. Do NOT use for web/
  React components, Flutter, backend code, or API layer generation (use mobile-api-integration for
  that).
---

# Mobile UI Scaffold Skill

## Step 0 — Gather Context

Before generating anything, collect the following if not already specified:

1. **Platform**: Android, iOS, or both?
2. **Screen type / name**: What is this screen called and what does it do?
3. **Data & state**: What data does it display? Where does it come from (API, local DB, user input)?
4. **Navigation context**: What navigates to this screen? What does this screen navigate to?
5. **Design description**: Any specific sections, components, or layout (e.g., "profile header + list of posts + floating action button")?

If the user hasn't provided all of the above, ask them concisely before generating. You can make reasonable assumptions for minor details (padding, exact color tokens) but always confirm platform and screen purpose.

---

## Step 1 — Read Reference Files

- If generating **Android** output: read `references/android-compose.md` before writing any code.
- If generating **iOS** output: read `references/ios-swiftui.md` before writing any code.
- If generating **both**: read both reference files.

---

## Step 2 — Android Output (Jetpack Compose)

Generate the following files for every Android screen:

### File Structure
```
feature/
  <FeatureName>Screen.kt       — Composable screen function
  <FeatureName>ViewModel.kt    — ViewModel with StateFlow + UiState
  <FeatureName>UiState.kt      — Sealed class for UI states
```

### ScreenName.kt — Composable Screen
- Annotate with `@Composable`, accept `viewModel: FeatureViewModel = hiltViewModel()`.
- Collect state: `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`.
- Use a `when (uiState)` block to render `LoadingContent`, `ErrorContent`, `EmptyContent`, and the actual content.
- Use `Scaffold` with `TopAppBar` (Material 3) as the outer wrapper.
- Apply `Modifier.fillMaxSize()`, `verticalScroll`, proper padding from `WindowInsets`.
- Add `contentDescription` to all images. Add `semantics { }` blocks to interactive elements.
- Include a `@Preview` composable at the bottom with sample data.

### FeatureViewModel.kt
- Extend `ViewModel()`.
- Inject repository via constructor with `@HiltViewModel` + `@Inject constructor`.
- Expose `private val _uiState = MutableStateFlow<FeatureUiState>(FeatureUiState.Loading)`.
- Expose public `val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()`.
- Launch data fetching in `init { viewModelScope.launch { ... } }`.
- Map results to UiState sealed class variants.

### FeatureUiState.kt
```kotlin
sealed class FeatureUiState {
    object Loading : FeatureUiState()
    data class Error(val message: String) : FeatureUiState()
    object Empty : FeatureUiState()
    data class Success(val data: FeatureData) : FeatureUiState()
}
```

### Architecture comment block
Add a comment at the top of each file:
```kotlin
/**
 * [ScreenName] — [one-line purpose]
 *
 * Architecture: MVVM + UiState sealed class
 * State: Collected from ViewModel via StateFlow, lifecycle-aware
 * Navigation: Receives [NavController], emits events via ViewModel SharedFlow
 */
```

### Navigation Graph Entry
Show the caller how to register this screen in the nav graph:
```kotlin
composable(route = AppDestinations.FEATURE) {
    FeatureScreen(onNavigateUp = navController::navigateUp)
}
```

---

## Step 3 — iOS Output (SwiftUI)

Generate the following files:

### File Structure
```
Feature/
  FeatureView.swift             — SwiftUI View
  FeatureViewModel.swift        — ObservableObject ViewModel
```

### FeatureView.swift
- Declare as `struct FeatureView: View`.
- Inject ViewModel: `@StateObject private var viewModel = FeatureViewModel()`.
- Use a `Group` or `ZStack` to switch between loading, error, empty, and success states.
- Use `NavigationStack` entry point or accept a `@Binding` for navigation as appropriate.
- Apply `.accessibilityLabel()`, `.accessibilityHint()` to interactive elements.
- Use `.task { await viewModel.load() }` for data fetching on appear.
- Include `#Preview { FeatureView() }` at the bottom.

### FeatureViewModel.swift
```swift
@MainActor
final class FeatureViewModel: ObservableObject {
    @Published var state: ViewState = .loading
    
    enum ViewState {
        case loading
        case error(String)
        case empty
        case success(FeatureData)
    }
    
    func load() async {
        // fetch and map to state
    }
}
```

### HIG compliance
- Use `List` with `.listStyle(.insetGrouped)` for settings-style screens.
- Use `ScrollView` + `LazyVStack` for feeds.
- Use `.navigationTitle()` and `.navigationBarTitleDisplayMode(.large)` for top-level screens.
- Respect safe area insets; never hardcode padding for notch/home indicator areas.

---

## Step 4 — Translating Design Descriptions to Components

When the user describes a layout (e.g., "settings page with dark mode toggle and account section"), map their description to concrete components:

| User says | Android (Compose) | iOS (SwiftUI) |
|---|---|---|
| Toggle / switch | `Switch` composable | `Toggle` view |
| List of items | `LazyColumn` | `List` |
| Profile picture | `AsyncImage` (Coil) | `AsyncImage` (Kingfisher) or `.task` |
| Tab bar | `TabRow` / `NavigationBar` | `TabView` |
| Bottom sheet | `ModalBottomSheet` | `.sheet()` modifier |
| Search | `SearchBar` / `DockedSearchBar` | `.searchable()` modifier |
| Pull to refresh | `PullRefreshIndicator` | `.refreshable()` modifier |

Do not generate an empty skeleton if the user described a real screen. Populate it with the actual components they described.

---

## Step 5 — Loading / Error / Empty States

Always scaffold these three states, even if the user doesn't ask:

**Loading**: centered `CircularProgressIndicator` (Android) or `ProgressView()` (iOS).
**Error**: centered icon + message + retry button. Wire the retry button to `viewModel.retry()`.
**Empty**: centered illustration placeholder (comment) + descriptive message + optional CTA button.

---

## Example

**Input:**
> "Create a settings screen for Android with dark mode toggle, notification preferences, and account section"

**Expected output:**

`SettingsScreen.kt` — Scaffold with TopAppBar "Settings", three `ListSection` composables:
- "Appearance" section: `SwitchPreference` for Dark Mode wired to `viewModel.toggleDarkMode()`
- "Notifications" section: switches for Push and Email notifications
- "Account" section: `ListItem` rows for Profile, Change Password, Sign Out (with confirmation dialog)

`SettingsViewModel.kt` — HiltViewModel, exposes `SettingsUiState.Success(settings: UserSettings)`, handles `toggleDarkMode()`, `updateNotificationPref()`, `signOut()` with `viewModelScope.launch`.

`SettingsUiState.kt` — sealed class with Loading, Error, Success(UserSettings) variants.

`UserSettings.kt` — data class: `isDarkMode: Boolean, pushEnabled: Boolean, emailEnabled: Boolean`.

Each section includes `contentDescription` on the switches and `semantics { heading() }` on section titles.

---

## Checklist Before Responding

- [ ] Platform confirmed (Android / iOS / both)
- [ ] Screen purpose is clear
- [ ] All three states (loading/error/empty) are scaffolded
- [ ] Accessibility attributes added
- [ ] Preview composable / #Preview included
- [ ] Navigation wiring shown
- [ ] Architecture comment block included
- [ ] If user gave a design description, actual components are used (not empty skeleton)
