# iOS Architecture — Reference

## MVVM with async/await (Recommended)

### Canonical data flow
```
View → .task { await viewModel.load() } → ViewModel → calls UseCase → Repository → Data source
View → user action → viewModel.onTapButton() → @Published var state updates → View redraws
```

### @Observable macro (iOS 17+) — preferred
```swift
import Observation

@Observable
final class ProfileViewModel {
    var state: ViewState = .loading
    var isEditing = false
    
    private let getUserUseCase: GetUserUseCase
    
    init(getUserUseCase: GetUserUseCase) {
        self.getUserUseCase = getUserUseCase
    }
    
    func load(userId: String) async {
        state = .loading
        do {
            let user = try await getUserUseCase.execute(userId)
            state = .success(user)
        } catch {
            state = .error(error.localizedDescription)
        }
    }
    
    enum ViewState {
        case loading, error(String), empty, success(User)
    }
}

// View — no @StateObject needed with @Observable
struct ProfileView: View {
    @State private var viewModel: ProfileViewModel
    
    init(deps: AppDependencies) {
        _viewModel = State(wrappedValue: ProfileViewModel(getUserUseCase: deps.getUserUseCase))
    }
}
```

## Coordinator Pattern (Navigation)

```swift
// For complex navigation flows where NavigationStack alone is insufficient
@Observable
final class AppCoordinator {
    var path = NavigationPath()
    var sheet: SheetDestination?
    var fullScreenCover: FullScreenDestination?
    
    func navigate(to destination: AppDestination) {
        path.append(destination)
    }
    
    func present(sheet: SheetDestination) {
        self.sheet = sheet
    }
    
    func popToRoot() {
        path.removeLast(path.count)
    }
    
    func dismiss() {
        _ = path.removeLast()
    }
}

// Root view
struct AppRootView: View {
    @State private var coordinator = AppCoordinator()
    
    var body: some View {
        NavigationStack(path: $coordinator.path) {
            HomeView()
                .navigationDestination(for: AppDestination.self) { destination in
                    switch destination {
                    case .profile(let id): ProfileView(userId: id)
                    case .detail(let id): DetailView(itemId: id)
                    }
                }
        }
        .sheet(item: $coordinator.sheet) { sheet in
            // sheet content
        }
        .environment(coordinator)
    }
}
```

## Clean Architecture in Swift

### Layer separation
```
Features/
  Profile/
    UI/           — SwiftUI views, ViewModels
    Domain/       — Protocols (Repository, UseCase), Domain models
    Data/         — Repository implementations, DTOs, mappers
```

### Domain layer — pure Swift, no UIKit/SwiftUI imports
```swift
// Domain/Models/User.swift — no framework imports
struct User: Identifiable, Equatable {
    let id: String
    let displayName: String
    let email: String
    let avatarURL: URL?
}

// Domain/Repositories/UserRepositoryProtocol.swift
protocol UserRepositoryProtocol {
    func getUser(id: String) async throws -> User
    func getUsers() async throws -> [User]
    func updateUser(_ user: User) async throws -> User
}

// Domain/UseCases/GetUserUseCase.swift
struct GetUserUseCase {
    let repository: UserRepositoryProtocol
    
    func execute(_ userId: String) async throws -> User {
        try await repository.getUser(id: userId)
    }
}
```

### Data layer — implements domain protocols
```swift
// Data/Repositories/UserRepository.swift
final class UserRepository: UserRepositoryProtocol {
    private let apiClient: APIClient
    private let cache: UserCacheProtocol?
    
    init(apiClient: APIClient, cache: UserCacheProtocol? = nil) {
        self.apiClient = apiClient
        self.cache = cache
    }
    
    func getUser(id: String) async throws -> User {
        if let cached = try? await cache?.getUser(id: id) { return cached }
        let dto: UserDTO = try await apiClient.request(.getUser(id: id))
        let user = dto.toDomain()
        try? await cache?.store(user: user)
        return user
    }
}
```

## SPM Multi-Target Setup

### Package.swift
```swift
// swift-tools-version: 5.10
import PackageDescription

let package = Package(
    name: "ExampleApp",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "NetworkKit", targets: ["NetworkKit"]),
        .library(name: "DesignSystem", targets: ["DesignSystem"]),
        .library(name: "AuthFeature", targets: ["AuthFeature"]),
        .library(name: "ProfileFeature", targets: ["ProfileFeature"]),
    ],
    dependencies: [
        .package(url: "https://github.com/onevcat/Kingfisher.git", from: "7.0.0"),
    ],
    targets: [
        .target(
            name: "NetworkKit",
            path: "Sources/NetworkKit"
        ),
        .target(
            name: "DesignSystem",
            path: "Sources/DesignSystem",
            dependencies: []
        ),
        .target(
            name: "AuthFeature",
            path: "Sources/Features/Auth",
            dependencies: ["NetworkKit", "DesignSystem"]
        ),
        .target(
            name: "ProfileFeature",
            path: "Sources/Features/Profile",
            dependencies: ["NetworkKit", "DesignSystem", "Kingfisher"]
        ),
        .testTarget(
            name: "ProfileFeatureTests",
            dependencies: ["ProfileFeature"]
        ),
    ]
)
```

## Composition Root (Manual DI)

```swift
// AppDependencies.swift — all object creation in one place
@MainActor
final class AppDependencies: ObservableObject {
    // Core
    let tokenStorage: TokenStorage
    let apiClient: APIClient
    
    // Repositories
    let userRepository: UserRepositoryProtocol
    let postRepository: PostRepositoryProtocol
    
    // Use cases — instantiated fresh or shared based on need
    var getUserUseCase: GetUserUseCase { GetUserUseCase(repository: userRepository) }
    var getPostsUseCase: GetPostsUseCase { GetPostsUseCase(repository: postRepository) }
    
    // ViewModels — created per-feature
    func makeProfileViewModel(userId: String) -> ProfileViewModel {
        ProfileViewModel(userId: userId, getUserUseCase: getUserUseCase)
    }
    
    init() {
        tokenStorage = TokenStorage()
        apiClient = APIClient(
            baseURL: URL(string: AppConfig.baseURL)!,
            tokenStorage: tokenStorage
        )
        userRepository = UserRepository(apiClient: apiClient)
        postRepository = PostRepository(apiClient: apiClient)
    }
    
    // Test factory
    static func makeForTesting() -> AppDependencies {
        let deps = AppDependencies()
        // Override with fakes if needed (better done via protocol injection at ViewModel level)
        return deps
    }
}
```

## Environment-Based Configuration

```swift
// AppConfig.swift
enum AppConfig {
    static var baseURL: String {
        switch environment {
        case .debug:   return "https://api-dev.example.com"
        case .staging: return "https://api-staging.example.com"
        case .release: return "https://api.example.com"
        }
    }
    
    static var environment: Environment {
        #if DEBUG
        return .debug
        #elseif STAGING
        return .staging
        #else
        return .release
        #endif
    }
    
    enum Environment { case debug, staging, release }
}
```

## Testing Architecture Support

```swift
// Protocol-based mocking
final class MockUserRepository: UserRepositoryProtocol {
    var getUserResult: Result<User, Error> = .success(User.fixture())
    var getUserCallCount = 0
    
    func getUser(id: String) async throws -> User {
        getUserCallCount += 1
        return try getUserResult.get()
    }
}

// Test extension for fixtures
extension User {
    static func fixture(
        id: String = "test-id",
        displayName: String = "Test User",
        email: String = "test@example.com",
        avatarURL: URL? = nil
    ) -> User {
        User(id: id, displayName: displayName, email: email, avatarURL: avatarURL)
    }
}
```
