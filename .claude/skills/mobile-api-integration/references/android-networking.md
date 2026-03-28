# Android Networking — Reference

## Gradle Dependencies (libs.versions.toml)
```toml
[versions]
retrofit = "2.11.0"
okhttp = "4.12.0"
kotlinx-serialization = "1.6.3"
kotlinx-datetime = "0.6.0"
room = "2.6.1"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.jakewharton.retrofit", name = "retrofit2-kotlinx-serialization-converter", version = "1.0.0" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlinx-datetime" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
```

## Retrofit + kotlinx.serialization vs Gson

Prefer **kotlinx.serialization** for new projects:
- Works with Kotlin value classes and sealed classes natively
- No reflection at runtime (better for R8/ProGuard)
- Multiplatform compatible

```kotlin
// build.gradle.kts plugin
plugins { kotlin("plugin.serialization") }

// Converter setup in NetworkModule
val json = Json {
    ignoreUnknownKeys = true   // Tolerant of extra API fields
    coerceInputValues = true   // Null for missing optional fields
    isLenient = true
}
val converterFactory = json.asConverterFactory("application/json; charset=UTF8".toMediaType())
```

## OkHttp Interceptors

### Token refresh interceptor (Authenticator pattern)
```kotlin
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiService: AuthApiService  // separate, no-auth Retrofit instance
) : Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
        // Called when 401 received
        val refreshToken = tokenStorage.getRefreshToken() ?: return null  // Can't refresh, give up
        
        val newTokens = runBlocking {
            authApiService.refresh(RefreshRequest(refreshToken))
        }.getOrNull() ?: return null
        
        tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
        
        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .build()
    }
}
```

### Network caching interceptor
```kotlin
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(10, TimeUnit.MINUTES)
            .build()
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}

// OkHttp client with cache
OkHttpClient.Builder()
    .cache(Cache(context.cacheDir, 10 * 1024 * 1024))  // 10MB cache
    .addNetworkInterceptor(CacheInterceptor())
    .build()
```

## Room Setup

### Entity
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "is_premium") val isPremium: Boolean,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)

fun UserEntity.toDomain() = User(id = id, displayName = displayName, email = email, isPremium = isPremium)
fun User.toEntity() = UserEntity(id = id, displayName = displayName, email = email, isPremium = isPremium)
```

### DAO
```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY display_name ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE cached_at < :threshold")
    suspend fun evictStale(threshold: Long)
}
```

### Database
```kotlin
@Database(entities = [UserEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT")
    }
}
```

### Database Hilt Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}
```

## Coroutine Dispatchers (Injectable)

```kotlin
interface CoroutineDispatchers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DefaultDispatchers : CoroutineDispatchers {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
}

// In tests, inject TestCoroutineDispatchers instead
```

## Pagination with Paging 3

```kotlin
// Paging source
class UsersPagingSource(private val api: UserApiService) : PagingSource<Int, UserDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserDto> {
        val page = params.key ?: 1
        return try {
            val response = api.getUsers(page = page, limit = params.loadSize)
            LoadResult.Page(
                data = response.body()?.items ?: emptyList(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.body()?.hasMore == true) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, UserDto>) = state.anchorPosition
}

// In repository
fun getUsersPaged(): Flow<PagingData<User>> = Pager(
    config = PagingConfig(pageSize = 20, enablePlaceholders = false),
    pagingSourceFactory = { UsersPagingSource(apiService) }
).flow.map { pagingData -> pagingData.map { it.toDomain() } }

// In ViewModel
val pagedUsers = repository.getUsersPaged().cachedIn(viewModelScope)
```

## ProGuard Rules for Networking

```proguard
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.example.**$$serializer { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Your DTO classes (important!)
-keep class com.example.data.network.dto.** { *; }
```
