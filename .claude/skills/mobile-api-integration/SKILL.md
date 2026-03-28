---
name: mobile-api-integration
description: >
  Generate the complete network/data layer for a mobile app from an API specification, endpoint
  descriptions, or JSON response samples. Use this skill whenever the user asks to: integrate an
  API, call an endpoint, set up Retrofit, configure URLSession, create a Repository class, model
  API responses, handle auth tokens or bearer headers, add network error handling, generate DTO/
  Codable model classes, wire up coroutines or async/await for networking, add offline caching
  with Room or CoreData/SwiftData, configure OkHttp interceptors, set up Alamofire, or connect
  a mobile app to a backend. Also triggers for: "how do I call this API in Android/iOS", "generate
  a network layer", "parse this JSON response", "set up dependency injection for networking", or
  pasting a raw JSON blob and asking to model it. Do NOT use for UI generation (use
  mobile-ui-scaffold) or CI/CD setup (use mobile-ci-pipeline).
---

# Mobile API Integration Skill

## Step 0 — Gather Input

Ask the user for the following if not already provided:

1. **Platform**: Android, iOS, or both?
2. **API specification**: OpenAPI/Swagger JSON or YAML, plain-text endpoint list, Postman collection JSON, or a raw JSON response sample?
3. **Auth type**: None, API key (header/query), Bearer token (stored where?), OAuth 2.0?
4. **Caching requirement**: None, in-memory, persistent (Room/CoreData/SwiftData)?
5. **Existing networking library preference**: Retrofit, Ktor (Android); URLSession, Alamofire (iOS)? Default to Retrofit for Android and URLSession async/await for iOS if unspecified.

If the user pastes raw JSON, infer DTO structure from it without asking further.

---

## Step 1 — Read Reference Files

- For **Android** output: read `references/android-networking.md` before writing code.
- For **iOS** output: read `references/ios-networking.md` before writing code.

---

## Step 2 — Parse the Input Specification

### If given an OpenAPI/Swagger spec:
- Extract endpoints: method, path, request body schema, response schema, error codes.
- Map each schema object to a DTO class.
- Group endpoints by resource tag → one Retrofit service interface or one URLSession service struct per resource.

### If given a plain-text endpoint list:
```
GET /users/{id}             → returns User object
POST /auth/login            → body: {email, password} → returns {token, user}
GET /posts?page=1&limit=20  → returns paginated list
```
Infer DTO fields from the endpoint names and parameter names. Generate sensible field names; comment `// TODO: verify field names against actual API response`.

### If given a raw JSON response:
```json
{ "id": 1, "full_name": "Jane Doe", "avatar_url": "https://..." }
```
→ Generate DTO with exact field names from JSON keys, mapped to idiomatic naming in the target language.

---

## Step 3 — Android Output

Generate these files (adjust names to the resource/feature):

```
data/
  network/
    ApiService.kt              — Retrofit service interface
    dto/
      UserDto.kt               — Request/response DTO data classes
    interceptors/
      AuthInterceptor.kt       — OkHttp interceptor for auth headers
      LoggingInterceptor.kt    — (reference only; use okhttp3-logging-interceptor in prod)
  repository/
    UserRepository.kt          — Repository implementation
    UserRepositoryImpl.kt
  local/
    UserDao.kt                 — Room DAO (if caching required)
    UserEntity.kt              — Room entity
di/
  NetworkModule.kt             — Hilt module providing OkHttp, Retrofit, service, repo
```

### ApiService.kt pattern
```kotlin
interface UserApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<PostDto>>
}
```

### DTO classes pattern
```kotlin
@Serializable  // kotlinx.serialization
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("created_at") val createdAt: String  // parse with kotlinx-datetime
)

// Domain model (separate from DTO)
data class User(
    val id: String,
    val displayName: String,
    val avatarUrl: String?,
    val createdAt: Instant
)

// Mapper extension
fun UserDto.toDomain() = User(
    id = id,
    displayName = fullName,
    avatarUrl = avatarUrl,
    createdAt = Instant.parse(createdAt)
)
```

### Result wrapper and error handling
```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int?, val message: String, val exception: Throwable? = null) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            ApiResult.Success(response.body()!!)
        } else {
            ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Unknown error")
        }
    } catch (e: IOException) {
        ApiResult.NetworkError
    } catch (e: HttpException) {
        ApiResult.Error(code = e.code(), message = e.message())
    }
}
```

### Repository pattern
```kotlin
interface UserRepository {
    suspend fun getUser(id: String): ApiResult<User>
    fun getUsersStream(): Flow<List<User>>  // for cached data
}

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val userDao: UserDao,          // Room DAO — omit if no caching
    private val dispatchers: CoroutineDispatchers
) : UserRepository {

    override suspend fun getUser(id: String): ApiResult<User> =
        withContext(dispatchers.io) {
            safeApiCall { apiService.getUser(id) }
                .map { it.toDomain() }
        }

    override fun getUsersStream(): Flow<List<User>> =
        userDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.io)
}
```

### Auth interceptor
```kotlin
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage  // SharedPreferences/EncryptedSharedPreferences wrapper
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = tokenStorage.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        return chain.proceed(request)
    }
}
```

### Hilt NetworkModule
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.example.com/"
    private const val TIMEOUT_SECONDS = 30L

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl
}
```

---

## Step 4 — iOS Output

Generate these files:

```
Data/
  Network/
    APIClient.swift            — Base async/await URLSession client
    UserService.swift          — Endpoint-specific service methods
    Models/
      UserDTO.swift            — Codable struct
  Repository/
    UserRepository.swift       — Protocol + implementation
  Local/
    UserStore.swift            — SwiftData/CoreData cache (if needed)
```

### APIClient.swift (base)
```swift
actor APIClient {
    private let session: URLSession
    private let baseURL: URL
    private let tokenStorage: TokenStorage
    
    init(baseURL: URL, tokenStorage: TokenStorage, session: URLSession = .shared) {
        self.baseURL = baseURL
        self.tokenStorage = tokenStorage
        self.session = session
    }
    
    func request<T: Decodable>(_ endpoint: Endpoint) async throws -> T {
        var urlRequest = try endpoint.urlRequest(baseURL: baseURL)
        
        if let token = await tokenStorage.token {
            urlRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let (data, response) = try await session.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        switch httpResponse.statusCode {
        case 200...299:
            return try JSONDecoder.iso8601.decode(T.self, from: data)
        case 401:
            throw APIError.unauthorized
        case 404:
            throw APIError.notFound
        default:
            let serverError = try? JSONDecoder().decode(ServerError.self, from: data)
            throw APIError.serverError(statusCode: httpResponse.statusCode,
                                        message: serverError?.message)
        }
    }
}

enum APIError: LocalizedError {
    case invalidResponse
    case unauthorized
    case notFound
    case networkError(Error)
    case decodingError(Error)
    case serverError(statusCode: Int, message: String?)
    
    var errorDescription: String? {
        switch self {
        case .unauthorized: return "Session expired. Please sign in again."
        case .notFound: return "The requested resource was not found."
        case .serverError(_, let message): return message ?? "Something went wrong."
        default: return "Network error. Please try again."
        }
    }
}
```

### Codable models
```swift
struct UserDTO: Codable {
    let id: String
    let fullName: String
    let avatarURL: URL?
    let createdAt: Date
    
    enum CodingKeys: String, CodingKey {
        case id
        case fullName = "full_name"
        case avatarURL = "avatar_url"
        case createdAt = "created_at"
    }
}

// Domain model
struct User: Identifiable {
    let id: String
    let displayName: String
    let avatarURL: URL?
    let createdAt: Date
}

extension UserDTO {
    func toDomain() -> User {
        User(id: id, displayName: fullName, avatarURL: avatarURL, createdAt: createdAt)
    }
}

// Decoder configured for ISO-8601 dates
extension JSONDecoder {
    static let iso8601: JSONDecoder = {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        return decoder
    }()
}
```

### Repository
```swift
protocol UserRepositoryProtocol {
    func getUser(id: String) async throws -> User
    func getUsers() async throws -> [User]
}

final class UserRepository: UserRepositoryProtocol {
    private let apiClient: APIClient
    private let store: UserStore?  // optional local cache
    
    init(apiClient: APIClient, store: UserStore? = nil) {
        self.apiClient = apiClient
        self.store = store
    }
    
    func getUser(id: String) async throws -> User {
        // Check cache first
        if let cached = try? await store?.getUser(id: id) { return cached }
        let dto: UserDTO = try await apiClient.request(.getUser(id: id))
        let user = dto.toDomain()
        try? await store?.save(user: user)
        return user
    }
}
```

---

## Step 5 — Offline Caching Skeleton

### Android (Room)
Generate `UserEntity.kt`, `UserDao.kt` with:
- `@Entity` data class mirroring domain model fields
- `@Dao` interface with `@Insert(onConflict = OnConflictStrategy.REPLACE)`, `@Query`, `@Delete`
- `fun observeAll(): Flow<List<UserEntity>>` for reactive updates
- `AppDatabase.kt` with `@Database` annotation and `databaseBuilder` setup in `DatabaseModule`

### iOS (SwiftData — iOS 17+)
```swift
import SwiftData

@Model
final class UserRecord {
    @Attribute(.unique) var id: String
    var displayName: String
    var avatarURLString: String?
    var createdAt: Date
    
    init(from user: User) {
        self.id = user.id
        self.displayName = user.displayName
        self.avatarURLString = user.avatarURL?.absoluteString
        self.createdAt = user.createdAt
    }
}
```

---

## Example

**Input:**
> "I have this JSON response from the API. Generate the Android network layer with Retrofit and caching."
> ```json
> { "user_id": "u123", "display_name": "Jane", "email": "jane@example.com", "premium": true }
> ```

**Expected output:**
- `UserDto.kt` — `@Serializable data class UserDto(@SerialName("user_id") val userId: String, @SerialName("display_name") val displayName: String, val email: String, val premium: Boolean)`
- `User.kt` — domain model with `userId`, `displayName`, `email`, `isPremium`, plus `UserDto.toDomain()` mapper
- `UserApiService.kt` — Retrofit interface with `suspend fun getUser(): Response<UserDto>`
- `UserRepository.kt` — interface + `UserRepositoryImpl` calling `safeApiCall { apiService.getUser() }.map { it.toDomain() }`
- `AuthInterceptor.kt` — adds `Authorization: Bearer <token>` header
- `NetworkModule.kt` — Hilt @Singleton providers for OkHttp, Retrofit, service, and repository

---

## Checklist Before Responding

- [ ] Platform confirmed
- [ ] All DTO fields mapped with correct serialization annotations
- [ ] Domain model separate from DTO; mapper function included
- [ ] Result/error wrapper used in repository
- [ ] Auth token injection shown
- [ ] Coroutine dispatchers / async/await pattern used consistently
- [ ] Hilt module (Android) or DI wiring (iOS) included
- [ ] Caching layer scaffolded if requested
- [ ] `// TODO: verify field names` comment added where inferred from description
