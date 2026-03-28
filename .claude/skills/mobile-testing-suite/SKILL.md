---
name: mobile-testing-suite
description: >
  Generate comprehensive test suites for mobile app features, screens, components, repositories,
  use cases, and end-to-end flows. Use this skill whenever the user asks to: write unit tests for
  a ViewModel, test a Repository, write Compose UI tests, write XCUITest tests, test a use case,
  test coroutine flows, test async Swift code, add MockK mocks, create XCTest mocks, set up
  Turbine for Flow testing, write JUnit 5 tests, write Robolectric tests, add accessibility
  assertions to UI tests, generate a test plan, or check test coverage. Also triggers for:
  "write tests for my feature", "how do I test this ViewModel", "mock this dependency",
  "test this repository", "write a UI test for this screen", "I need 80% coverage",
  "test the loading/error/empty states", "snapshot test iOS". Do NOT use for generating
  production code, CI/CD pipelines (use mobile-ci-pipeline), or debugging crashes
  (use mobile-debug-doctor).
---

# Mobile Testing Suite Skill

## Step 0 — Gather Context

Ask if not specified:

1. **What to test**: Screen, Composable component, ViewModel, Repository, UseCase, or end-to-end flow?
2. **Platform**: Android, iOS, or both?
3. **Testing libraries** (or use defaults):
   - Android defaults: JUnit 5, MockK, Turbine, Compose Testing, Robolectric
   - iOS defaults: XCTest, async/await test patterns, XCUITest
4. **What dependencies need to be mocked**: List injected dependencies for the class under test.
5. **What states/scenarios to cover**: Happy path, error, empty, loading, edge cases?

---

## Step 1 — Read Reference Files

- For **Android**: read `references/android-testing.md`
- For **iOS**: read `references/ios-testing.md`

---

## Step 2 — Android Test Generation

### File placement
```
app/src/test/kotlin/...          — Unit tests (JVM, no Android)
app/src/androidTest/kotlin/...  — Instrumented tests (requires device/emulator)
```

Use unit tests (`src/test/`) for: ViewModels, UseCases, Repositories, mappers, utilities.
Use instrumented tests (`src/androidTest/`) for: Compose UI tests, database (Room), end-to-end.

---

### ViewModel Unit Tests (JUnit 5 + MockK + Turbine)

```kotlin
@ExtendWith(InstantExecutorExtension::class)
class ProfileViewModelTest {

    @RelaxedMockK
    private lateinit var getUserUseCase: GetUserUseCase

    private lateinit var viewModel: ProfileViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(getUserUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit Success state when use case returns user`() = runTest {
        // Arrange
        val expectedUser = User(id = "1", displayName = "Jane", email = "jane@example.com")
        coEvery { getUserUseCase(any()) } returns ApiResult.Success(expectedUser)

        // Act & Assert (Turbine for Flow)
        viewModel.uiState.test {
            viewModel.loadUser("1")
            assertThat(awaitItem()).isEqualTo(ProfileUiState.Loading)
            val successState = awaitItem()
            assertThat(successState).isInstanceOf(ProfileUiState.Success::class.java)
            assertThat((successState as ProfileUiState.Success).user).isEqualTo(expectedUser)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit Error state when use case fails`() = runTest {
        // Arrange
        coEvery { getUserUseCase(any()) } returns ApiResult.Error(message = "Network failure")

        // Act & Assert
        viewModel.uiState.test {
            viewModel.loadUser("1")
            skipItems(1)  // skip Loading
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(ProfileUiState.Error::class.java)
            assertThat((errorState as ProfileUiState.Error).message).isEqualTo("Network failure")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit Empty state when user list is empty`() = runTest {
        coEvery { getUserUseCase(any()) } returns ApiResult.Success(null)

        viewModel.uiState.test {
            viewModel.loadUser("1")
            skipItems(1)
            assertThat(awaitItem()).isEqualTo(ProfileUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry should reload data after error`() = runTest {
        val user = User.fixture()
        coEvery { getUserUseCase(any()) } returnsMany listOf(
            ApiResult.Error(message = "First call fails"),
            ApiResult.Success(user)
        )

        viewModel.uiState.test {
            viewModel.loadUser("1")
            skipItems(2)  // Loading, Error
            viewModel.retry()
            skipItems(1)  // Loading again
            assertThat(awaitItem()).isInstanceOf(ProfileUiState.Success::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

### Repository Unit Tests

```kotlin
class UserRepositoryTest {

    @RelaxedMockK private lateinit var apiService: UserApiService
    @RelaxedMockK private lateinit var userDao: UserDao

    private lateinit var repository: UserRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = UserRepositoryImpl(apiService, userDao, TestCoroutineDispatchers(testDispatcher))
    }

    @Test
    fun `getUser should return mapped domain model on success`() = runTest {
        val dto = UserDto(id = "1", fullName = "Jane", email = "jane@example.com")
        coEvery { apiService.getUser("1") } returns Response.success(dto)

        val result = repository.getUser("1")

        assertThat(result).isInstanceOf(ApiResult.Success::class.java)
        val user = (result as ApiResult.Success).data
        assertThat(user.id).isEqualTo("1")
        assertThat(user.displayName).isEqualTo("Jane")
    }

    @Test
    fun `getUser should return NetworkError on IOException`() = runTest {
        coEvery { apiService.getUser(any()) } throws IOException("No network")

        val result = repository.getUser("1")

        assertThat(result).isEqualTo(ApiResult.NetworkError)
    }

    @Test
    fun `getUser should return Error on non-2xx response`() = runTest {
        coEvery { apiService.getUser(any()) } returns Response.error(404, "".toResponseBody())

        val result = repository.getUser("1")

        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        assertThat((result as ApiResult.Error).code).isEqualTo(404)
    }
}
```

---

### Compose UI Tests

```kotlin
@HiltAndroidTest
class ProfileScreenTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    @BindValue
    @JvmField
    val fakeRepository: UserRepository = FakeUserRepository()

    @Before
    fun setUp() { hiltRule.inject() }

    @Test
    fun profileScreen_displaysUserName_whenDataLoaded() {
        (fakeRepository as FakeUserRepository).stubbedUser =
            ApiResult.Success(User.fixture(displayName = "Jane Doe"))

        composeTestRule.setContent {
            AppTheme { ProfileScreen(onNavigateUp = {}) }
        }

        composeTestRule.onNodeWithText("Jane Doe").assertIsDisplayed()
    }

    @Test
    fun profileScreen_showsRetryButton_onError() {
        (fakeRepository as FakeUserRepository).stubbedUser =
            ApiResult.Error(message = "Server error")

        composeTestRule.setContent {
            AppTheme { ProfileScreen(onNavigateUp = {}) }
        }

        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertHasClickAction()
    }

    @Test
    fun profileScreen_showsLoadingIndicator_initially() {
        (fakeRepository as FakeUserRepository).delay = 1000L

        composeTestRule.setContent {
            AppTheme { ProfileScreen(onNavigateUp = {}) }
        }

        composeTestRule.onNodeWithContentDescription("Loading").assertIsDisplayed()
    }

    @Test
    fun profileScreen_toggleDarkMode_changesState() {
        composeTestRule.setContent {
            AppTheme { SettingsScreen(onNavigateUp = {}) }
        }

        composeTestRule
            .onNodeWithContentDescription("Dark mode toggle")
            .assertIsOff()
            .performClick()
            .assertIsOn()
    }
}
```

---

### Use Case Tests

```kotlin
class GetUserUseCaseTest {

    @RelaxedMockK private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetUserUseCase(repository, TestCoroutineDispatchers())
    }

    @Test
    fun `invoke should return user from repository`() = runTest {
        val user = User.fixture()
        coEvery { repository.getUser("1") } returns ApiResult.Success(user)

        val result = useCase(GetUserUseCase.Params("1"))

        assertThat(result).isEqualTo(ApiResult.Success(user))
        coVerify(exactly = 1) { repository.getUser("1") }
    }
}
```

---

## Step 3 — iOS Test Generation

### File placement
```
Tests/Unit/          — XCTest unit tests (no UI, fast)
Tests/Integration/   — Tests with real/fake infrastructure
UITests/             — XCUITest end-to-end tests (require app target)
```

### ViewModel Unit Tests (async/await)

```swift
@MainActor
final class ProfileViewModelTests: XCTestCase {
    
    var sut: ProfileViewModel!
    var mockRepository: MockUserRepository!
    
    override func setUp() {
        super.setUp()
        mockRepository = MockUserRepository()
        sut = ProfileViewModel(getUserUseCase: GetUserUseCase(repository: mockRepository))
    }
    
    override func tearDown() {
        sut = nil
        mockRepository = nil
        super.tearDown()
    }
    
    // MARK: - Happy Path
    
    func test_load_setsSuccessState_whenRepositoryReturnsUser() async {
        // Arrange
        let expectedUser = User.fixture(displayName: "Jane Doe")
        mockRepository.getUserResult = .success(expectedUser)
        
        // Act
        await sut.load(userId: "1")
        
        // Assert
        guard case .success(let user) = sut.state else {
            XCTFail("Expected success state, got \(sut.state)")
            return
        }
        XCTAssertEqual(user.displayName, "Jane Doe")
    }
    
    // MARK: - Error Path
    
    func test_load_setsErrorState_whenRepositoryThrows() async {
        // Arrange
        mockRepository.getUserResult = .failure(APIError.serverError(statusCode: 500, message: nil))
        
        // Act
        await sut.load(userId: "1")
        
        // Assert
        guard case .error = sut.state else {
            XCTFail("Expected error state, got \(sut.state)")
            return
        }
    }
    
    // MARK: - Loading State
    
    func test_load_setsLoadingState_initially() async {
        // Arrange — simulate slow network
        mockRepository.delay = 0.5
        
        // Act
        let task = Task { await self.sut.load(userId: "1") }
        
        // Assert loading state is set before task completes
        XCTAssertEqual(sut.state, .loading)
        await task.value
    }
    
    // MARK: - Retry
    
    func test_retry_reloadsData() async {
        // Arrange
        mockRepository.getUserResults = [
            .failure(APIError.networkError(URLError(.notConnectedToInternet))),
            .success(User.fixture())
        ]
        
        await sut.load(userId: "1")
        XCTAssertTrue(sut.state == .error(""))  // first load fails
        
        // Act
        await sut.load(userId: "1")  // retry
        
        // Assert
        if case .success = sut.state { } else {
            XCTFail("Expected success after retry")
        }
    }
}
```

### Mock Protocol

```swift
final class MockUserRepository: UserRepositoryProtocol {
    var getUserResult: Result<User, Error> = .success(User.fixture())
    var getUserResults: [Result<User, Error>] = []
    var getUserCallCount = 0
    var delay: TimeInterval = 0
    
    func getUser(id: String) async throws -> User {
        getUserCallCount += 1
        if delay > 0 { try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000)) }
        
        if !getUserResults.isEmpty {
            return try getUserResults.removeFirst().get()
        }
        return try getUserResult.get()
    }
}
```

### XCUITest

```swift
final class ProfileUITests: XCTestCase {
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments = ["--uitesting"]
        app.launchEnvironment = ["MOCK_USER_ID": "test-123"]
        app.launch()
    }
    
    func test_profileScreen_displaysUserName() {
        app.tabBars.buttons["Profile"].tap()
        
        XCTAssertTrue(app.staticTexts["Jane Doe"].waitForExistence(timeout: 5))
    }
    
    func test_settingsScreen_toggleDarkMode() {
        app.tabBars.buttons["Settings"].tap()
        
        let toggle = app.switches["Dark Mode"]
        XCTAssertTrue(toggle.exists)
        XCTAssertEqual(toggle.value as? String, "0")  // off
        toggle.tap()
        XCTAssertEqual(toggle.value as? String, "1")  // on
    }
    
    func test_errorState_showsRetryButton() {
        app.launchEnvironment["SIMULATE_ERROR"] = "true"
        app.launch()
        app.tabBars.buttons["Profile"].tap()
        
        let retryButton = app.buttons["Retry"]
        XCTAssertTrue(retryButton.waitForExistence(timeout: 5))
        retryButton.tap()
        // Assert loading indicator appears
        XCTAssertTrue(app.activityIndicators.firstMatch.waitForExistence(timeout: 2))
    }
    
    // Accessibility test
    func test_profileScreen_accessibilityLabels() {
        app.tabBars.buttons["Profile"].tap()
        
        // Verify avatar has accessibility label
        let avatar = app.images["Profile photo"]
        XCTAssertTrue(avatar.waitForExistence(timeout: 5))
        
        // Verify interactive elements are accessible
        let editButton = app.buttons["Edit profile"]
        XCTAssertTrue(editButton.isHittable)
    }
}
```

---

## Step 4 — Test Plan Document

Generate a `TestPlan.md` after generating the tests:

```markdown
# Test Plan — [Feature Name]

## Coverage Summary
| Layer | Tests | Coverage Goal |
|---|---|---|
| ViewModel | 8 tests | All UiState transitions |
| Repository | 5 tests | Success, error, network error, cache hit/miss |
| UseCase | 3 tests | Happy path, error propagation |
| UI (Compose/SwiftUI) | 6 tests | All states visible on screen |

## What's Covered
- ✅ Loading → Success transition
- ✅ Loading → Error transition
- ✅ Empty state when data is empty
- ✅ Retry behavior
- ✅ User interaction: form submission, toggle, navigation
- ✅ Accessibility labels present

## What Requires Manual Testing
- ⚠️ Dark mode visual appearance
- ⚠️ Dynamic Type (very large font sizes)
- ⚠️ Real network conditions (slow 3G, offline)
- ⚠️ Background/foreground lifecycle (OS killing the app)
- ⚠️ Localization (different languages and RTL)

## Test Execution
- Unit tests: `./gradlew test` or `Cmd+U` in Xcode
- UI tests: `./gradlew connectedAndroidTest` or Xcode Test Plan
- Coverage report: `./gradlew jacocoTestReport` or Xcode Coverage report
```

---

## Example

**Input:**
> "Write tests for my SettingsViewModel on Android. It has toggleDarkMode(), updateNotifications(), and signOut() methods."

**Expected output:**
- `SettingsViewModelTest.kt` with JUnit 5 + MockK + Turbine:
  - `toggleDarkMode_should_update_isDarkMode_in_UiState`
  - `updateNotifications_should_persist_preference`
  - `signOut_should_emit_NavigateToLogin_event`
  - `signOut_should_clear_user_session`
  - `loading_state_emitted_before_signOut_completes`
  - `error_state_emitted_when_signOut_throws`
- `FakeUserRepository.kt` and `FakePreferencesRepository.kt` with controllable return values
- Brief `TestPlan.md`

---

## Checklist Before Responding

- [ ] Tests follow Arrange-Act-Assert structure
- [ ] Test names use `should_X_when_Y` or `test_method_expectedBehavior_condition` pattern
- [ ] All three states tested: success, error, empty/loading
- [ ] Each test is isolated (no shared mutable state between tests)
- [ ] Coroutine dispatcher / MainActor setup included
- [ ] Mock/fake for each injected dependency provided
- [ ] Edge cases covered: null/empty input, network failure, timeout
- [ ] Accessibility assertions included in UI tests
- [ ] `TestPlan.md` generated listing coverage and manual testing gaps
