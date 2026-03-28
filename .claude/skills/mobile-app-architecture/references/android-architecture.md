# Android Architecture — Reference

## MVVM with Jetpack Compose

### Canonical data flow
```
UI (Composable) → collects StateFlow → ViewModel → calls UseCase → Repository → Data source
UI → user event → ViewModel.onEvent() → updates StateFlow → UI recomposes
```

### MVI variant (for complex state)
```kotlin
// MVI uses a single sealed UiIntent + reducer
sealed class FeedIntent {
    object LoadFeed : FeedIntent()
    data class LikePost(val id: String) : FeedIntent()
    data class Search(val query: String) : FeedIntent()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    fun onIntent(intent: FeedIntent) {
        viewModelScope.launch {
            _state.update { currentState ->
                reduce(currentState, intent)
            }
        }
    }

    private suspend fun reduce(state: FeedUiState, intent: FeedIntent): FeedUiState =
        when (intent) {
            is FeedIntent.LoadFeed -> {
                val result = getFeedUseCase()
                state.copy(isLoading = false, posts = result.getOrNull() ?: emptyList())
            }
            is FeedIntent.LikePost -> state.copy(
                posts = state.posts.map { if (it.id == intent.id) it.copy(isLiked = true) else it }
            )
            is FeedIntent.Search -> state.copy(searchQuery = intent.query)
        }
}
```

## Multi-Module Gradle Setup

### settings.gradle.kts
```kotlin
rootProject.name = "ExampleApp"

include(":app")
include(":core:core-network")
include(":core:core-database")
include(":core:core-ui")
include(":core:core-domain")
include(":core:core-testing")
include(":feature:feature-auth")
include(":feature:feature-profile")
include(":feature:feature-feed")
```

### Convention plugin for feature modules (build-logic/convention/src/main/kotlin/FeaturePlugin.kt)
```kotlin
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("com.google.dagger.hilt.android")
            
            dependencies {
                add("implementation", project(":core:core-ui"))
                add("implementation", project(":core:core-domain"))
                add("implementation", libs.findLibrary("hilt.android").get())
                add("ksp", libs.findLibrary("hilt.compiler").get())
                add("testImplementation", project(":core:core-testing"))
            }
        }
    }
}
```

### Feature module build.gradle.kts
```kotlin
plugins {
    id("example.android.feature")  // convention plugin
}

android {
    namespace = "com.example.feature.auth"
}

dependencies {
    implementation(project(":core:core-network"))
}
```

## Navigation: Type-Safe with Navigation Compose 2.8+

```kotlin
// Destination definitions (serializable)
@Serializable object AuthGraph
@Serializable object HomeGraph
@Serializable data class DetailDestination(val itemId: String)
@Serializable object ProfileDestination

// NavHost
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = AuthGraph) {
        navigation<AuthGraph>(startDestination = LoginDestination) {
            composable<LoginDestination> {
                LoginScreen(onLoginSuccess = { navController.navigate(HomeGraph) {
                    popUpTo(AuthGraph) { inclusive = true }
                }})
            }
        }
        
        navigation<HomeGraph>(startDestination = FeedDestination) {
            composable<FeedDestination> {
                FeedScreen(onItemClick = { id -> navController.navigate(DetailDestination(id)) })
            }
            composable<DetailDestination> { backStackEntry ->
                val dest: DetailDestination = backStackEntry.toRoute()
                DetailScreen(itemId = dest.itemId, onNavigateUp = navController::navigateUp)
            }
        }
    }
}
```

## Clean Architecture Layers

### Dependency rule
```
UI Layer → depends on → Domain Layer → has NO dependency on → Data Layer
Data Layer → implements → Domain interfaces
```

### Domain layer — pure Kotlin, no Android imports
```kotlin
// domain/model/User.kt — no Android imports
data class User(val id: String, val name: String, val email: String)

// domain/repository/UserRepository.kt — interface only
interface UserRepository {
    suspend fun getUser(id: String): ApiResult<User>
    fun observeUsers(): Flow<List<User>>
}

// domain/usecase/GetUserUseCase.kt
class GetUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(userId: String): ApiResult<User> =
        withContext(dispatchers.io) { repository.getUser(userId) }
}
```

### Data layer — implements domain interfaces
```kotlin
// data/repository/UserRepositoryImpl.kt
class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUser(id: String): ApiResult<User> {
        return safeApiCall { apiService.getUser(id) }.map { it.toDomain() }
    }

    override fun observeUsers(): Flow<List<User>> =
        userDao.observeAll().map { it.map(UserEntity::toDomain) }
}
```

## Hilt Scopes Reference

| Scope | Component | Lifetime |
|---|---|---|
| `@Singleton` | `SingletonComponent` | App lifetime |
| `@ActivityRetainedScoped` | `ActivityRetainedComponent` | ViewModel lifetime |
| `@ViewModelScoped` | `ViewModelComponent` | ViewModel lifetime |
| `@ActivityScoped` | `ActivityComponent` | Activity lifetime |
| `@FragmentScoped` | `FragmentComponent` | Fragment lifetime |

Use `@Singleton` for repositories, network clients, database.
Use `@ViewModelScoped` for dependencies unique to one ViewModel.

## Testing Architecture Hooks

```kotlin
// Provide test doubles via Hilt test modules
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
@Module
abstract class FakeRepositoryModule {
    @Binds
    abstract fun bindUserRepository(fake: FakeUserRepository): UserRepository
}

class FakeUserRepository : UserRepository {
    var stubbedUser: ApiResult<User> = ApiResult.Success(testUser)
    override suspend fun getUser(id: String) = stubbedUser
    override fun observeUsers(): Flow<List<User>> = flowOf(listOf(testUser))
}
```
