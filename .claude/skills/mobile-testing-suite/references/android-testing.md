# Android Testing — Reference

## Test Dependencies (libs.versions.toml)

```toml
[versions]
junit5 = "5.10.2"
mockk = "1.13.11"
turbine = "1.1.0"
robolectric = "4.12.2"
compose-testing = "1.6.7"
hilt-testing = "2.51.1"
truth = "1.4.2"
coroutines-test = "1.8.0"

[libraries]
junit5-api = { group = "org.junit.jupiter", name = "junit-jupiter-api", version.ref = "junit5" }
junit5-engine = { group = "org.junit.jupiter", name = "junit-jupiter-engine", version.ref = "junit5" }
junit5-params = { group = "org.junit.jupiter", name = "junit-jupiter-params", version.ref = "junit5" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
mockk-android = { group = "io.mockk", name = "mockk-android", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
robolectric = { group = "org.robolectric", name = "robolectric", version.ref = "robolectric" }
compose-ui-test = { group = "androidx.compose.ui", name = "ui-test-junit4" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
hilt-testing = { group = "com.google.dagger", name = "hilt-android-testing", version.ref = "hilt-testing" }
truth = { group = "com.google.truth", name = "truth", version.ref = "truth" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines-test" }
```

### build.gradle.kts (app)
```kotlin
android {
    testOptions {
        unitTests.isReturnDefaultValues = true  // Prevent unmocked Android classes from throwing
        unitTests.isIncludeAndroidResources = true  // For Robolectric
        animationsDisabled = true  // For Compose UI tests — disable animations
    }
}

dependencies {
    // Unit tests
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.params)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.robolectric)

    // Instrumented tests
    androidTestImplementation(libs.compose.ui.test)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.mockk.android)
    debugImplementation(libs.compose.ui.test.manifest)
    kspAndroidTest(libs.hilt.compiler)
}

// JUnit 5 task configuration
tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## JUnit 5 Patterns

### Basic test class structure
```kotlin
@ExtendWith(MockKExtension::class)
class MyClassTest {

    @MockK
    private lateinit var dependency: SomeDependency

    @RelaxedMockK  // auto-stubs all methods; use when you don't care about all interactions
    private lateinit var relaxedDep: AnotherDependency

    private lateinit var sut: MyClass  // sut = System Under Test

    @BeforeEach
    fun setUp() {
        sut = MyClass(dependency, relaxedDep)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should return expected value when input is valid`() {
        // Arrange
        every { dependency.getData() } returns "test data"

        // Act
        val result = sut.processData()

        // Assert
        assertThat(result).isEqualTo("expected")
        verify(exactly = 1) { dependency.getData() }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "  ", "\n"])
    fun `should throw when input is blank`(input: String) {
        assertThrows<IllegalArgumentException> { sut.processData(input) }
    }
}
```

### Coroutine test setup
```kotlin
@ExtendWith(MockKExtension::class)
class ViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUpDispatchers() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDownDispatchers() {
        Dispatchers.resetMain()
    }

    @Test
    fun `coroutine test example`() = runTest(testDispatcher) {
        // Standard pattern — runTest handles coroutine scope + virtual time
        val result = sut.fetchData()
        assertThat(result).isNotNull()
    }
}

// Test coroutine dispatchers (injectable)
class TestCoroutineDispatchers(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : CoroutineDispatchers {
    override val io: CoroutineDispatcher = dispatcher
    override val main: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}
```

---

## MockK Patterns

```kotlin
// Basic mock
val mockRepo = mockk<UserRepository>()
every { mockRepo.getUser("1") } returns ApiResult.Success(User.fixture())

// Suspend function mock
coEvery { mockRepo.getUser("1") } returns ApiResult.Success(User.fixture())
coVerify { mockRepo.getUser("1") }

// ArgumentCaptor equivalent
val slot = slot<String>()
coEvery { mockRepo.getUser(capture(slot)) } returns ApiResult.Success(User.fixture())
// After call: slot.captured == the actual argument

// Multiple return values
coEvery { mockRepo.getUser(any()) } returnsMany listOf(
    ApiResult.Error(message = "First call fails"),
    ApiResult.Success(User.fixture())
)

// Throw exception
coEvery { mockRepo.getUser(any()) } throws IOException("Network error")

// Verify call count
verify(exactly = 1) { mockRepo.getUser("1") }
verify(atLeast = 2) { mockRepo.getUser(any()) }
confirmVerified(mockRepo)  // Ensures no unexpected interactions
```

---

## Turbine (Flow Testing)

```kotlin
// Basic Flow test
@Test
fun `flow emits expected sequence`() = runTest {
    val flow = flowOf(1, 2, 3)

    flow.test {
        assertThat(awaitItem()).isEqualTo(1)
        assertThat(awaitItem()).isEqualTo(2)
        assertThat(awaitItem()).isEqualTo(3)
        awaitComplete()
    }
}

// StateFlow from ViewModel
@Test
fun `ViewModel uiState transitions correctly`() = runTest {
    coEvery { mockUseCase(any()) } returns ApiResult.Success(User.fixture())

    viewModel.uiState.test {
        assertThat(awaitItem()).isEqualTo(ProfileUiState.Loading)
        viewModel.loadUser("1")
        assertThat(awaitItem()).isInstanceOf(ProfileUiState.Success::class.java)
        cancelAndIgnoreRemainingEvents()
    }
}

// SharedFlow / Channel (one-shot events)
@Test
fun `should emit navigation event on sign out`() = runTest {
    viewModel.events.test {
        viewModel.signOut()
        val event = awaitItem()
        assertThat(event).isInstanceOf(ProfileEvent.NavigateToLogin::class.java)
        cancelAndIgnoreRemainingEvents()
    }
}
```

---

## Compose Testing

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

// Basic rendering
@Test
fun settingsScreen_showsAllSections() {
    composeTestRule.setContent {
        AppTheme { SettingsScreen(uiState = SettingsUiState.Success(UserSettings())) }
    }

    composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
    composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
    composeTestRule.onNodeWithText("Account").assertIsDisplayed()
}

// Interaction
@Test
fun darkModeToggle_updatesState_whenClicked() {
    var isDark = false
    composeTestRule.setContent {
        AppTheme {
            Switch(
                checked = isDark,
                onCheckedChange = { isDark = it }
            )
        }
    }

    composeTestRule.onNodeWithRole(Role.Switch).performClick()
    assertThat(isDark).isTrue()
}

// Wait for async operations
@Test
fun screen_showsData_afterLoading() {
    composeTestRule.setContent { ProfileScreen(viewModel = fakeViewModel) }

    composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule.onAllNodesWithText("Jane Doe").fetchSemanticsNodes().isNotEmpty()
    }

    composeTestRule.onNodeWithText("Jane Doe").assertIsDisplayed()
}

// Semantic queries
composeTestRule.onNode(
    hasText("Submit") and hasClickAction()
).performClick()

composeTestRule.onNodeWithContentDescription("Profile photo").assertIsDisplayed()
composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
```

---

## Hilt Testing

```kotlin
// HiltAndroidTest setup
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserRepositoryIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserRepository

    @Before
    fun setUp() { hiltRule.inject() }
}

// Replace module in tests
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
@Module
object FakeNetworkModule {
    @Provides @Singleton
    fun provideUserApiService(): UserApiService = mockk<UserApiService>().apply {
        coEvery { getUser(any()) } returns Response.success(UserDto.fixture())
    }
}

// @BindValue for quick replacement
@HiltAndroidTest
class MyTest {
    @BindValue @JvmField
    val fakeRepository: UserRepository = FakeUserRepository()
}
```

---

## Room Testing (in-memory database)

```kotlin
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()  // allowMainThread only in tests
        dao = db.userDao()
    }

    @After
    fun closeDb() { db.close() }

    @Test
    fun insertAndRetrieveUser() = runTest {
        val entity = UserEntity(id = "1", displayName = "Jane", email = "jane@example.com", isPremium = false)
        dao.insertAll(listOf(entity))

        val retrieved = dao.getById("1")
        assertThat(retrieved).isEqualTo(entity)
    }

    @Test
    fun observeAll_emitsUpdatesOnInsert() = runTest {
        dao.observeAll().test {
            assertThat(awaitItem()).isEmpty()
            dao.insertAll(listOf(UserEntity.fixture()))
            assertThat(awaitItem()).hasSize(1)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## Fake Implementations (preferred over mocks for repositories)

```kotlin
class FakeUserRepository : UserRepository {
    var stubbedUser: ApiResult<User> = ApiResult.Success(User.fixture())
    var stubbedUsers: ApiResult<List<User>> = ApiResult.Success(listOf(User.fixture()))
    var delay: Long = 0L

    private val _usersFlow = MutableStateFlow<List<User>>(emptyList())

    override suspend fun getUser(id: String): ApiResult<User> {
        if (delay > 0) delay(delay)
        return stubbedUser
    }

    override fun observeUsers(): Flow<List<User>> = _usersFlow

    fun emit(users: List<User>) { _usersFlow.value = users }
}

// Fixture extensions
fun User.Companion.fixture(
    id: String = "test-id",
    displayName: String = "Test User",
    email: String = "test@example.com"
) = User(id = id, displayName = displayName, email = email)

fun UserDto.Companion.fixture(
    id: String = "test-id",
    fullName: String = "Test User",
    email: String = "test@example.com"
) = UserDto(id = id, fullName = fullName, email = email)
```
