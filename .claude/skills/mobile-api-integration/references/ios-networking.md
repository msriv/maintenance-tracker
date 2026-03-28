# iOS Networking — Reference

## Endpoint Abstraction

```swift
enum Endpoint {
    case getUser(id: String)
    case login(email: String, password: String)
    case getPosts(page: Int, limit: Int)
    case updateProfile(id: String, body: UpdateProfileRequest)
    
    var method: HTTPMethod { /* GET/POST/PUT/DELETE */ }
    var path: String { /* "/users/{id}" etc. */ }
    var queryItems: [URLQueryItem]? { /* optional query params */ }
    var body: Encodable? { /* optional request body */ }
    
    func urlRequest(baseURL: URL) throws -> URLRequest {
        var components = URLComponents(url: baseURL.appendingPathComponent(path), resolvingAgainstBaseURL: true)!
        components.queryItems = queryItems
        
        var request = URLRequest(url: components.url!)
        request.httpMethod = method.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.timeoutInterval = 30
        
        if let body = body {
            request.httpBody = try JSONEncoder.iso8601.encode(body)
        }
        return request
    }
}

enum HTTPMethod: String {
    case get = "GET", post = "POST", put = "PUT", patch = "PATCH", delete = "DELETE"
}

extension JSONEncoder {
    static let iso8601: JSONEncoder = {
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        encoder.keyEncodingStrategy = .convertToSnakeCase
        return encoder
    }()
}
```

## Codable Edge Cases

### Snake case ↔ camelCase automatic conversion
```swift
let decoder = JSONDecoder()
decoder.keyDecodingStrategy = .convertFromSnakeCase  // "full_name" → fullName automatically
// CAUTION: This doesn't work for ALL cases (acronyms like "url" → "url", not "URL")
// Prefer explicit CodingKeys for reliability
```

### Nested / polymorphic responses
```swift
struct APIResponse<T: Codable>: Codable {
    let data: T?
    let error: APIErrorResponse?
    let meta: PaginationMeta?
}

struct PaginationMeta: Codable {
    let currentPage: Int
    let totalPages: Int
    let totalItems: Int
    let hasMore: Bool
}
```

### Custom date decoding
```swift
// ISO-8601 with fractional seconds (common in APIs)
decoder.dateDecodingStrategy = .custom { decoder in
    let container = try decoder.singleValueContainer()
    let string = try container.decode(String.self)
    
    let formatters: [ISO8601DateFormatter] = [
        { let f = ISO8601DateFormatter(); f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]; return f }(),
        { let f = ISO8601DateFormatter(); f.formatOptions = [.withInternetDateTime]; return f }()
    ]
    
    for formatter in formatters {
        if let date = formatter.date(from: string) { return date }
    }
    throw DecodingError.dataCorruptedError(in: container, debugDescription: "Cannot decode date: \(string)")
}
```

### Optional vs required fields strategy
```swift
// Use Optional only for fields the API actually omits (not just null)
struct ProfileDTO: Codable {
    let id: String              // always present
    let bio: String?            // sometimes omitted entirely
    let website: String?        // can be null
    
    // Handle missing field gracefully
    var displayBio: String { bio ?? "" }
}
```

## Token Storage

```swift
// Use Keychain, not UserDefaults, for tokens
import Security

actor TokenStorage {
    private let service = "com.example.app"
    
    var token: String? {
        get async { KeychainHelper.read(service: service, key: "access_token") }
    }
    
    func save(accessToken: String, refreshToken: String) async {
        KeychainHelper.save(service: service, key: "access_token", value: accessToken)
        KeychainHelper.save(service: service, key: "refresh_token", value: refreshToken)
    }
    
    func clear() async {
        KeychainHelper.delete(service: service, key: "access_token")
        KeychainHelper.delete(service: service, key: "refresh_token")
    }
}

// Minimal Keychain wrapper
enum KeychainHelper {
    static func save(service: String, key: String, value: String) {
        let data = Data(value.utf8)
        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrService: service,
            kSecAttrAccount: key,
            kSecValueData: data
        ]
        SecItemDelete(query as CFDictionary)
        SecItemAdd(query as CFDictionary, nil)
    }
    
    static func read(service: String, key: String) -> String? {
        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrService: service,
            kSecAttrAccount: key,
            kSecReturnData: true,
            kSecMatchLimit: kSecMatchLimitOne
        ]
        var result: AnyObject?
        guard SecItemCopyMatching(query as CFDictionary, &result) == errSecSuccess,
              let data = result as? Data else { return nil }
        return String(data: data, encoding: .utf8)
    }
    
    static func delete(service: String, key: String) {
        let query: [CFString: Any] = [kSecClass: kSecClassGenericPassword,
                                       kSecAttrService: service, kSecAttrAccount: key]
        SecItemDelete(query as CFDictionary)
    }
}
```

## Retry and Backoff

```swift
extension APIClient {
    func requestWithRetry<T: Decodable>(
        _ endpoint: Endpoint,
        maxRetries: Int = 3,
        baseDelay: TimeInterval = 0.5
    ) async throws -> T {
        var lastError: Error = APIError.invalidResponse
        
        for attempt in 0..<maxRetries {
            do {
                return try await request(endpoint)
            } catch APIError.serverError(let code, _) where code >= 500 {
                lastError = APIError.serverError(statusCode: code, message: nil)
                let delay = baseDelay * pow(2.0, Double(attempt))
                try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000))
            } catch APIError.networkError(let error) {
                lastError = error
                let delay = baseDelay * pow(2.0, Double(attempt))
                try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000))
            } catch {
                throw error  // Don't retry 4xx or decoding errors
            }
        }
        throw lastError
    }
}
```

## SwiftData Caching (iOS 17+)

```swift
import SwiftData

// Model container setup (in App entry point)
@main
struct ExampleApp: App {
    let modelContainer: ModelContainer = {
        let schema = Schema([UserRecord.self, PostRecord.self])
        let config = ModelConfiguration(isStoredInMemoryOnly: false)
        return try! ModelContainer(for: schema, configurations: config)
    }()
    
    var body: some Scene {
        WindowGroup { ContentView() }
            .modelContainer(modelContainer)
    }
}

// Accessing in Repository
final class UserRepository: UserRepositoryProtocol {
    private let modelContext: ModelContext
    
    init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }
    
    func getCachedUser(id: String) throws -> User? {
        let descriptor = FetchDescriptor<UserRecord>(
            predicate: #Predicate { $0.id == id }
        )
        return try modelContext.fetch(descriptor).first?.toDomain()
    }
    
    func cacheUser(_ user: User) throws {
        let record = UserRecord(from: user)
        modelContext.insert(record)
        try modelContext.save()
    }
}
```

## Combine (Legacy — iOS 13+)

```swift
// For older codebases still using Combine publishers
import Combine

class LegacyUserRepository {
    private var cancellables = Set<AnyCancellable>()
    
    func getUser(id: String) -> AnyPublisher<User, APIError> {
        URLSession.shared.dataTaskPublisher(for: buildRequest(for: .getUser(id: id)))
            .tryMap { data, response in
                guard let http = response as? HTTPURLResponse, 200...299 ~= http.statusCode
                else { throw APIError.invalidResponse }
                return data
            }
            .decode(type: UserDTO.self, decoder: JSONDecoder.iso8601)
            .map { $0.toDomain() }
            .mapError { APIError.networkError($0) }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
}
```

## Alamofire (Optional Alternative)

```swift
// Only use Alamofire if the team already has it or needs advanced certificate pinning
import Alamofire

class AlamofireAPIClient {
    private let session: Session
    
    init(tokenStorage: TokenStorage) {
        let interceptor = AuthInterceptor(tokenStorage: tokenStorage)
        session = Session(interceptor: interceptor)
    }
    
    func request<T: Decodable>(_ convertible: URLRequestConvertible) async throws -> T {
        try await session.request(convertible)
            .validate()
            .serializingDecodable(T.self, decoder: JSONDecoder.iso8601)
            .value
    }
}
```
