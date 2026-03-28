---
name: mobile-app-architecture
description: >
  Help choose, design, and scaffold a full mobile app architecture. Use this skill whenever the
  user asks about: "what architecture should I use for my app", "how should I structure my Android/
  iOS project", "set up Clean Architecture", "scaffold MVVM", "set up MVI for Android", "how do
  I organize my Kotlin/Swift modules", "multi-module Android project", "SPM targets iOS",
  "dependency injection setup", "Hilt setup from scratch", "navigation architecture", or any
  question about folder structure, layering, module boundaries, DI containers, or architectural
  patterns for a mobile app. Also triggers for: "how should I separate concerns in my app",
  "generate a folder structure for my feature", "create an architecture decision record", or
  "how do I scale my mobile codebase". Do NOT use for individual screen generation (use
  mobile-ui-scaffold) or API layer alone (use mobile-api-integration).
---

# Mobile App Architecture Skill

## Step 0 — Gather Context

Ask the following before recommending an architecture:

1. **Platform**: Android, iOS, or both?
2. **App size**: Small (1–10 screens), Medium (10–30 screens), Large (30+ screens / multiple features/teams)?
3. **Team size**: Solo, small team (2–5), or large team (5+)?
4. **Testing requirements**: Unit tests only, full test pyramid (unit + integration + UI)?
5. **Offline support**: None, read-only cache, full offline-first?
6. **Existing codebase constraints**: Greenfield, or retrofitting an existing app?

If the user is in a hurry, assume Medium app, small team, unit + UI tests, no offline unless specified.

---

## Step 2 — Read Reference Files

- For **Android**: read `references/android-architecture.md`
- For **iOS**: read `references/ios-architecture.md`

---

## Step 3 — Recommend Architecture

### Decision matrix

| Scenario | Recommended |
|---|---|
| Small app, solo dev | MVVM (simple), no DI framework, single module |
| Medium app, small team | MVVM + Hilt (Android) / manual DI (iOS), single-module with feature packages |
| Large app, multiple teams | Clean Architecture + MVVM/MVI, multi-module (Android) / multi-target SPM (iOS) |
| Complex UI state, real-time updates | MVI (Android) / TCA-inspired (iOS) |
| Strict offline-first | Clean Architecture + Repository pattern + local-first strategy |

Explain your recommendation in 2–3 sentences. Don't just say "use Clean Architecture" — explain the tradeoff.

---

## Step 4 — Generate Folder Structure

### Android — MVVM, Single Module
```
app/src/main/kotlin/com/example/app/
├── MainActivity.kt
├── AppNavHost.kt
├── di/
│   ├── AppModule.kt
│   └── NetworkModule.kt
├── data/
│   ├── network/
│   │   ├── ApiService.kt
│   │   └── dto/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   └── dao/
│   └── repository/
│       └── UserRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   └── User.kt
│   ├── repository/
│   │   └── UserRepository.kt        ← interface
│   └── usecase/
│       └── GetUserUseCase.kt
└── ui/
    ├── theme/
    │   ├── Theme.kt
    │   ├── Color.kt
    │   └── Type.kt
    └── feature/
        └── profile/
            ├── ProfileScreen.kt
            ├── ProfileViewModel.kt
            └── ProfileUiState.kt
```

### Android — Clean Architecture, Multi-Module
```
root/
├── app/                              — Application + DI graph root + NavHost
├── core/
│   ├── core-network/                 — Retrofit, OkHttp, auth interceptor
│   ├── core-database/                — Room, AppDatabase, base DAOs
│   ├── core-ui/                      — Shared composables, theme, tokens
│   ├── core-domain/                  — Base UseCase, Result wrapper
│   └── core-testing/                 — Shared test fakes, test dispatchers
└── feature/
    ├── feature-auth/
    │   ├── data/                     — AuthRepository, AuthApiService, AuthDto
    │   ├── domain/                   — LoginUseCase, LogoutUseCase
    │   └── ui/                       — LoginScreen, LoginViewModel, LoginUiState
    ├── feature-profile/
    └── feature-feed/
```

### iOS — MVVM, Single Target
```
Sources/
├── App/
│   ├── ExampleApp.swift
│   └── AppRootView.swift
├── Core/
│   ├── Network/
│   │   ├── APIClient.swift
│   │   └── Endpoint.swift
│   ├── Storage/
│   │   └── TokenStorage.swift
│   └── Extensions/
├── Domain/
│   ├── Models/
│   │   └── User.swift
│   ├── Repositories/
│   │   └── UserRepositoryProtocol.swift
│   └── UseCases/
│       └── GetUserUseCase.swift
└── Features/
    └── Profile/
        ├── ProfileView.swift
        ├── ProfileViewModel.swift
        └── ProfileRepository.swift
```

### iOS — Multi-Target (SPM)
```
Sources/
├── App/                              — Main target, entry point
├── AppCore/                          — Shared utilities, extensions, tokens
├── NetworkKit/                       — APIClient, Endpoint, base service
├── DesignSystem/                     — Reusable SwiftUI components, colors, fonts
└── Features/
    ├── Auth/                         — Login, registration flows
    ├── Profile/                      — Profile view and edit
    └── Feed/                         — Content feed
```

---

## Step 5 — Base Classes and Interfaces

### Android Domain Layer

```kotlin
// Base UseCase pattern
abstract class UseCase<in Params, out Result> {
    abstract suspend operator fun invoke(params: Params): Result
}

// Parameterless variant
abstract class NoParamsUseCase<out Result> {
    abstract suspend operator fun invoke(): Result
}

// Example implementation
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<GetUserUseCase.Params, ApiResult<User>>() {

    data class Params(val userId: String)

    override suspend operator fun invoke(params: Params): ApiResult<User> =
        userRepository.getUser(params.userId)
}
```

### Android Result wrapper
```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String, val exception: Throwable? = null) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()

    fun <R> map(transform: (T) -> R): ApiResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is NetworkError -> this
    }
    
    fun getOrNull(): T? = (this as? Success)?.data
}
```

### iOS Domain Layer
```swift
// Use case protocol
protocol UseCase {
    associatedtype Input
    associatedtype Output
    func execute(_ input: Input) async throws -> Output
}

// Example
struct GetUserUseCase: UseCase {
    let repository: UserRepositoryProtocol
    
    func execute(_ input: String) async throws -> User {
        try await repository.getUser(id: input)
    }
}

// Result-returning use case (no throws)
struct GetUserSafeUseCase {
    let repository: UserRepositoryProtocol
    
    func execute(id: String) async -> Result<User, APIError> {
        do {
            return .success(try await repository.getUser(id: id))
        } catch let error as APIError {
            return .failure(error)
        } catch {
            return .failure(.networkError(error))
        }
    }
}
```

---

## Step 6 — DI Setup

### Android — Hilt from scratch
```kotlin
// 1. Add to build.gradle.kts (app)
plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}
dependencies {
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}

// 2. Application class
@HiltAndroidApp
class ExampleApplication : Application()

// 3. Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }

// 4. ViewModel (auto-injected)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel()

// 5. Module binding interface implementations
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

### iOS — Manual DI (Composition Root)
```swift
// AppDependencies.swift — composition root, created once in App entry point
@MainActor
final class AppDependencies {
    let tokenStorage: TokenStorage
    let apiClient: APIClient
    let userRepository: UserRepositoryProtocol
    let getUserUseCase: GetUserUseCase
    
    init() {
        tokenStorage = TokenStorage()
        apiClient = APIClient(
            baseURL: URL(string: AppConfig.baseURL)!,
            tokenStorage: tokenStorage
        )
        userRepository = UserRepository(apiClient: apiClient)
        getUserUseCase = GetUserUseCase(repository: userRepository)
    }
}

// Injected through view hierarchy
@main
struct ExampleApp: App {
    @StateObject private var deps = AppDependencies()
    
    var body: some Scene {
        WindowGroup {
            AppRootView()
                .environmentObject(deps)
        }
    }
}

// Feature view receives what it needs
struct ProfileView: View {
    @EnvironmentObject var deps: AppDependencies
    // pass deps.getUserUseCase to ViewModel init
}
```

---

## Step 7 — Architecture Decision Record (ADR)

Generate an `architecture-decision-record.md` file with this structure:

```markdown
# ADR-001: Mobile App Architecture

## Status
Accepted

## Context
[App name, team size, key requirements that drove the decision]

## Decision
We will use [MVVM / Clean Architecture + MVVM / MVI] with [Hilt / manual DI] for dependency injection.

## Rationale
- [Reason 1: e.g., MVVM is the officially supported pattern for Jetpack Compose]
- [Reason 2: e.g., single module chosen to reduce complexity for a 3-person team]
- [Reason 3: e.g., Clean Architecture deferred until team grows beyond 5]

## Consequences
Positive:
- [e.g., Easy to test ViewModels in isolation]

Negative / Tradeoffs:
- [e.g., More boilerplate in ViewModel layer compared to MVC]

## Alternatives Considered
- **Clean Architecture**: Rejected for initial phase due to overhead for current team size
- **MVI**: Considered for complex state screens; will revisit if state complexity grows

## Review Date
[6 months from today]
```

---

## Step 8 — Dependency Graph (ASCII)

For multi-module setups, generate a dependency diagram:

```
:app
  └──► :feature-auth
  └──► :feature-profile
  └──► :feature-feed

:feature-auth
  └──► :core-domain
  └──► :core-network
  └──► :core-ui

:feature-profile
  └──► :core-domain
  └──► :core-database
  └──► :core-ui

:core-domain        (no dependencies on other modules)
:core-network       └──► :core-domain
:core-database      └──► :core-domain
:core-ui            └──► :core-domain (for models used in previews)
```

Rule: features depend on core modules. Features NEVER depend on other features. `:app` depends on all features.

---

## Example

**Input:**
> "I'm building a news reader app for Android, medium size, team of 3, needs offline support."

**Expected output:**
- Recommends MVVM + Clean Architecture (medium app, offline = repository pattern needed)
- Single module (team of 3, not worth multi-module overhead yet)
- Folder structure with `data/`, `domain/`, `ui/feature/`
- Hilt DI setup (3 modules: NetworkModule, DatabaseModule, RepositoryModule)
- Room for caching with offline-first repository pattern (fetch from DB, refresh from network)
- ADR explaining why single-module Clean Architecture beats full multi-module

---

## Checklist Before Responding

- [ ] App context gathered (size, team, offline needs)
- [ ] Architecture recommendation includes a brief rationale
- [ ] Full folder structure generated
- [ ] Base classes / interfaces scaffolded
- [ ] DI setup shown end-to-end
- [ ] ADR markdown generated
- [ ] Dependency graph included for multi-module setups
- [ ] Navigation skeleton shown
