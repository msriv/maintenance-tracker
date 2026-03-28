# iOS Testing — Reference

## XCTest Setup

```swift
// No extra dependencies for unit tests — XCTest is built in
// For snapshot tests: add SnapshotTesting via SPM
// .package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.15.0")
```

---

## XCTest Patterns

### Async test methods (Swift 5.5+)
```swift
func test_load_returnsUser() async throws {
    // async throws — preferred over XCTestExpectation for async code
    let user = try await sut.load(userId: "1")
    XCTAssertEqual(user.id, "1")
}

// With timeout
func test_load_completesWithinTimeout() async throws {
    let user = try await withThrowingTaskGroup(of: User.self) { group in
        group.addTask { try await self.sut.load(userId: "1") }
        group.addTask {
            try await Task.sleep(nanoseconds: 5_000_000_000)  // 5s timeout
            throw XCTestError(.timeoutWhileWaiting)
        }
        let result = try await group.next()!
        group.cancelAll()
        return result
    }
    XCTAssertNotNil(user)
}
```

### XCTestExpectation (for Combine / callback-based code)
```swift
func test_publishedProperty_updatesOnLoad() {
    let expectation = expectation(description: "State updated to success")
    var cancellables = Set<AnyCancellable>()
    
    sut.$state
        .dropFirst()  // Skip initial value
        .sink { state in
            if case .success = state { expectation.fulfill() }
        }
        .store(in: &cancellables)
    
    Task { await self.sut.load(userId: "1") }
    
    waitForExpectations(timeout: 5)
}
```

### Measure performance
```swift
func test_sortAlgorithmPerformance() {
    let items = (0..<10000).map { _ in Item.fixture() }
    
    measure {
        _ = items.sorted { $0.name < $1.name }
    }
}
```

---

## Mock Protocol Pattern

```swift
// Protocol-first design makes mocking trivial
protocol UserRepositoryProtocol {
    func getUser(id: String) async throws -> User
    func saveUser(_ user: User) async throws
    func deleteUser(id: String) async throws
}

// Mock — one per protocol, reusable across all tests
final class MockUserRepository: UserRepositoryProtocol {
    // Stubbable return values
    var getUserResult: Result<User, Error> = .success(.fixture())
    var saveUserResult: Result<Void, Error> = .success(())
    var deleteUserResult: Result<Void, Error> = .success(())
    
    // Call tracking
    var getUserCallCount = 0
    var getUserLastId: String?
    var saveUserCallCount = 0
    var deleteUserCallCount = 0
    var deletedUserIds: [String] = []
    
    // Configurable delay for testing loading states
    var delay: TimeInterval = 0
    
    func getUser(id: String) async throws -> User {
        getUserCallCount += 1
        getUserLastId = id
        if delay > 0 { try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000)) }
        return try getUserResult.get()
    }
    
    func saveUser(_ user: User) async throws {
        saveUserCallCount += 1
        try saveUserResult.get()
    }
    
    func deleteUser(id: String) async throws {
        deleteUserCallCount += 1
        deletedUserIds.append(id)
        try deleteUserResult.get()
    }
    
    // Convenience for multiple sequential results
    var getUserResults: [Result<User, Error>] = []
    func getUser_nextResult(id: String) async throws -> User {
        guard !getUserResults.isEmpty else { return try getUserResult.get() }
        return try getUserResults.removeFirst().get()
    }
}
```

---

## Testing @Observable ViewModels

```swift
@MainActor
final class ProfileViewModelTests: XCTestCase {
    var sut: ProfileViewModel!
    var mockRepo: MockUserRepository!
    
    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockUserRepository()
        sut = ProfileViewModel(repository: mockRepo)
    }
    
    func test_initialState_isLoading() {
        XCTAssertEqual(sut.state, .loading)
    }
    
    func test_load_withValidId_setsSuccessState() async {
        // Arrange
        mockRepo.getUserResult = .success(User.fixture(id: "1", displayName: "Test"))
        
        // Act
        await sut.load(userId: "1")
        
        // Assert
        switch sut.state {
        case .success(let user):
            XCTAssertEqual(user.id, "1")
            XCTAssertEqual(user.displayName, "Test")
        default:
            XCTFail("Expected .success, got \(sut.state)")
        }
    }
    
    func test_load_withNetworkError_setsErrorState() async {
        // Arrange
        mockRepo.getUserResult = .failure(URLError(.notConnectedToInternet))
        
        // Act
        await sut.load(userId: "1")
        
        // Assert
        switch sut.state {
        case .error(let message):
            XCTAssertFalse(message.isEmpty)
        default:
            XCTFail("Expected .error, got \(sut.state)")
        }
    }
    
    func test_calledRepositoryOnce() async {
        await sut.load(userId: "42")
        XCTAssertEqual(mockRepo.getUserCallCount, 1)
        XCTAssertEqual(mockRepo.getUserLastId, "42")
    }
}
```

---

## XCUITest Patterns

### Launch with test configuration
```swift
// In test target
let app = XCUIApplication()
app.launchArguments = ["--uitesting", "--disable-animations"]
app.launchEnvironment = ["API_BASE_URL": "http://localhost:8080"]
app.launch()

// In app target — read launch args
#if DEBUG
if CommandLine.arguments.contains("--uitesting") {
    // Inject mock server or test data
}
#endif
```

### Element queries
```swift
// By accessibility identifier (most reliable)
app.buttons["submit_button"].tap()
app.textFields["email_input"].typeText("user@example.com")

// By label
app.buttons["Submit"].tap()
app.staticTexts["Welcome, Jane"].waitForExistence(timeout: 5)

// By type + index
app.cells.element(boundBy: 0).tap()

// By predicate
let pred = NSPredicate(format: "label CONTAINS 'Jane'")
app.staticTexts.matching(pred).firstMatch.tap()
```

### Page Object Pattern (for maintainable UI tests)
```swift
struct ProfileScreen {
    let app: XCUIApplication
    
    var nameLabel: XCUIElement { app.staticTexts["profile_name"] }
    var editButton: XCUIElement { app.buttons["Edit profile"] }
    var avatarImage: XCUIElement { app.images["Profile photo"] }
    
    @discardableResult
    func tapEdit() -> EditProfileScreen {
        editButton.tap()
        return EditProfileScreen(app: app)
    }
    
    func assertDisplayed(name: String) {
        XCTAssertTrue(nameLabel.waitForExistence(timeout: 5))
        XCTAssertEqual(nameLabel.label, name)
    }
}

// Usage
func test_editProfile_updatesName() {
    let profileScreen = ProfileScreen(app: app)
    profileScreen.assertDisplayed(name: "Jane Doe")
    
    let editScreen = profileScreen.tapEdit()
    editScreen.clearAndType(newName: "Jane Smith")
    editScreen.tapSave()
    
    profileScreen.assertDisplayed(name: "Jane Smith")
}
```

---

## Snapshot Testing (swift-snapshot-testing)

```swift
import SnapshotTesting
import SwiftUI

final class ProfileViewSnapshotTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Set to true once to update reference images, then set back to false
        isRecording = false
    }
    
    func test_profileView_snapshot_lightMode() {
        let view = ProfileView(viewModel: .fixture(state: .success(.fixture())))
        assertSnapshot(of: view, as: .image(layout: .device(config: .iPhone13)))
    }
    
    func test_profileView_snapshot_darkMode() {
        let view = ProfileView(viewModel: .fixture(state: .success(.fixture())))
            .preferredColorScheme(.dark)
        assertSnapshot(of: view, as: .image(layout: .device(config: .iPhone13)))
    }
    
    func test_profileView_snapshot_errorState() {
        let view = ProfileView(viewModel: .fixture(state: .error("Failed to load")))
        assertSnapshot(of: view, as: .image(layout: .device(config: .iPhone13)))
    }
}
```

---

## Test Fixtures and Factory

```swift
// Extension-based fixtures on domain models
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

extension UserDTO {
    static func fixture(
        id: String = "test-id",
        fullName: String = "Test User",
        email: String = "test@example.com"
    ) -> UserDTO {
        UserDTO(id: id, fullName: fullName, email: email)
    }
}

// ViewModel fixture for previews and tests
extension ProfileViewModel {
    static func fixture(state: ViewState = .loading) -> ProfileViewModel {
        let vm = ProfileViewModel(repository: MockUserRepository())
        vm.state = state
        return vm
    }
}
```

---

## Test Plans in Xcode

Xcode test plans (`.xctestplan`) let you configure multiple test runs with different environments:

1. Product > Test Plan > New Test Plan
2. Add test targets
3. Create configurations:
   - `Unit Tests` — fast, no simulator
   - `UI Tests` — full simulator run with mock server
   - `CI` — unit + UI, no flaky tests, parallel execution

```json
// .xctestplan excerpt
{
  "configurations": [
    {
      "id": "unit",
      "name": "Unit Tests Only",
      "options": {
        "environmentVariableEntries": [
          { "key": "MOCK_NETWORKING", "value": "true" }
        ]
      }
    }
  ]
}
```
