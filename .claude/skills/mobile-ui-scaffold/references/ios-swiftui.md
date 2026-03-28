# iOS SwiftUI — UI Reference

## Project Setup

### Swift Package Manager dependencies (Package.swift or via Xcode)
```swift
// Commonly needed packages
dependencies: [
    .package(url: "https://github.com/onevcat/Kingfisher.git", from: "7.0.0"),
    .package(url: "https://github.com/Swinject/Swinject.git", from: "2.8.0"),
]
```

### Minimum deployment target
- iOS 17+ preferred (NavigationStack stable, `@Observable` macro available)
- iOS 16 minimum if using `NavigationStack` without `@Observable`
- iOS 15 minimum if using `NavigationView` (deprecated in iOS 16)

---

## SwiftUI View Patterns

### Standard screen structure
```swift
import SwiftUI

struct FeatureView: View {
    @StateObject private var viewModel = FeatureViewModel()
    
    var body: some View {
        Group {
            switch viewModel.state {
            case .loading:
                LoadingView()
            case .error(let message):
                ErrorView(message: message, onRetry: { Task { await viewModel.load() } })
            case .empty:
                EmptyView(message: "No items found")
            case .success(let data):
                FeatureContentView(data: data, viewModel: viewModel)
            }
        }
        .navigationTitle("Feature Title")
        .navigationBarTitleDisplayMode(.large)
        .task { await viewModel.load() }
    }
}

private struct FeatureContentView: View {
    let data: FeatureData
    @ObservedObject var viewModel: FeatureViewModel
    
    var body: some View {
        List {
            // content
        }
        .listStyle(.insetGrouped)
        .refreshable { await viewModel.load() }
    }
}

#Preview {
    NavigationStack {
        FeatureView()
    }
}
```

### ViewModel pattern (iOS 17+ with @Observable)
```swift
import Observation

@Observable
final class FeatureViewModel {
    var state: ViewState = .loading
    
    enum ViewState {
        case loading
        case error(String)
        case empty
        case success(FeatureData)
    }
    
    private let repository: FeatureRepositoryProtocol
    
    init(repository: FeatureRepositoryProtocol = FeatureRepository()) {
        self.repository = repository
    }
    
    func load() async {
        state = .loading
        do {
            let data = try await repository.fetchData()
            state = data.isEmpty ? .empty : .success(data)
        } catch {
            state = .error(error.localizedDescription)
        }
    }
    
    func retry() {
        Task { await load() }
    }
}
```

### ViewModel pattern (iOS 16 compatible with ObservableObject)
```swift
@MainActor
final class FeatureViewModel: ObservableObject {
    @Published var state: ViewState = .loading
    
    // Use @StateObject at call site: @StateObject private var viewModel = FeatureViewModel()
    // Use @ObservedObject when injected: @ObservedObject var viewModel: FeatureViewModel
    
    private let repository: FeatureRepositoryProtocol
    
    init(repository: FeatureRepositoryProtocol = FeatureRepository()) {
        self.repository = repository
    }
    
    func load() async {
        state = .loading
        do {
            let data = try await repository.fetchData()
            await MainActor.run {
                self.state = data.isEmpty ? .empty : .success(data)
            }
        } catch {
            await MainActor.run {
                self.state = .error(error.localizedDescription)
            }
        }
    }
}
```

---

## Material-Equivalent SwiftUI Components

### Lists and grouped sections (settings-style)
```swift
List {
    Section("Appearance") {
        Toggle("Dark Mode", isOn: $viewModel.isDarkMode)
            .accessibilityLabel("Dark mode toggle")
            .accessibilityHint("Switches between light and dark theme")
    }
    
    Section("Notifications") {
        Toggle("Push Notifications", isOn: $viewModel.pushEnabled)
        Toggle("Email Notifications", isOn: $viewModel.emailEnabled)
    }
    
    Section("Account") {
        NavigationLink("Profile") { ProfileView() }
        NavigationLink("Change Password") { ChangePasswordView() }
        Button(role: .destructive) {
            viewModel.signOut()
        } label: {
            Text("Sign Out")
        }
    }
}
.listStyle(.insetGrouped)
```

### Navigation
```swift
// Top-level navigation host
NavigationStack {
    HomeView()
        .navigationDestination(for: AppDestination.self) { destination in
            switch destination {
            case .detail(let id): DetailView(itemId: id)
            case .settings: SettingsView()
            }
        }
}

// Navigating programmatically
@State private var path = NavigationPath()

NavigationStack(path: $path) {
    HomeView(onItemTap: { id in path.append(AppDestination.detail(id)) })
}

// Back navigation
@Environment(\.dismiss) private var dismiss
Button("Back") { dismiss() }
```

### Search
```swift
@State private var searchText = ""

List { /* items filtered by searchText */ }
    .searchable(text: $searchText, placement: .navigationBarDrawer(displayMode: .always))
    .onChange(of: searchText) { _, newValue in
        viewModel.search(query: newValue)
    }
```

### Pull to refresh
```swift
List { /* content */ }
    .refreshable {
        await viewModel.load()
    }
```

### Sheets and modals
```swift
@State private var showSheet = false
@State private var showFullScreen = false

// Bottom sheet
.sheet(isPresented: $showSheet) {
    SheetContentView()
        .presentationDetents([.medium, .large])
        .presentationDragIndicator(.visible)
}

// Full screen cover
.fullScreenCover(isPresented: $showFullScreen) {
    FullScreenView()
}
```

### Alert / confirmation dialog
```swift
@State private var showDeleteConfirmation = false

Button("Delete", role: .destructive) {
    showDeleteConfirmation = true
}
.confirmationDialog("Are you sure?", isPresented: $showDeleteConfirmation, titleVisibility: .visible) {
    Button("Delete", role: .destructive) { viewModel.delete() }
    Button("Cancel", role: .cancel) { }
} message: {
    Text("This action cannot be undone.")
}
```

### Image loading with Kingfisher
```swift
import Kingfisher

KFImage(URL(string: imageUrl))
    .placeholder { ProgressView() }
    .fade(duration: 0.25)
    .resizable()
    .aspectRatio(contentMode: .fill)
    .frame(width: 80, height: 80)
    .clipShape(Circle())
    .accessibilityLabel("Profile photo")
```

### Grid layout
```swift
let columns = [GridItem(.adaptive(minimum: 160), spacing: 12)]

ScrollView {
    LazyVGrid(columns: columns, spacing: 12) {
        ForEach(items) { item in
            ItemCard(item: item)
                .onTapGesture { viewModel.select(item) }
        }
    }
    .padding()
}
```

---

## Shared State / Environment

### Passing data through the view hierarchy
```swift
// EnvironmentObject for app-wide state
struct AppView: View {
    @StateObject private var authManager = AuthManager()
    
    var body: some View {
        ContentView()
            .environmentObject(authManager)
    }
}

// Consuming it
struct ProfileView: View {
    @EnvironmentObject var authManager: AuthManager
}

// Environment values (system)
@Environment(\.colorScheme) var colorScheme
@Environment(\.dismiss) var dismiss
@Environment(\.openURL) var openURL
@Environment(\.horizontalSizeClass) var sizeClass
```

---

## Accessibility Patterns

```swift
// Image accessibility
Image("banner")
    .accessibilityLabel("Promotional banner for summer sale")
    .accessibilityHidden(false)

// Decorative image
Image("decorative-line")
    .accessibilityHidden(true)

// Custom action
Text("Swipe to delete")
    .accessibilityAction(named: "Delete") {
        viewModel.delete()
    }

// Group elements (reads as one unit)
HStack {
    Image(systemName: "star.fill")
    Text("4.8")
    Text("(1,234 reviews)")
}
.accessibilityElement(children: .combine)
.accessibilityLabel("Rating: 4.8 out of 5, based on 1234 reviews")

// Hint
Button("Submit") { viewModel.submit() }
    .accessibilityHint("Submits the form and navigates to confirmation")

// Value for sliders/steppers
Slider(value: $volume, in: 0...1)
    .accessibilityValue("\(Int(volume * 100)) percent")

// Dynamic Type support — always use .font(.body) or named styles, never fixed sizes
Text(title)
    .font(.headline)  // Scales with user's preferred text size
```

---

## Common SwiftUI Idioms

### State ownership rules
- `@State` — owned by this view, primitive/simple types
- `@StateObject` — owned by this view, reference type (ViewModel created here)
- `@ObservedObject` — injected from outside, not owned
- `@Binding` — passed down, two-way connection to parent's state
- `@EnvironmentObject` — injected via `.environmentObject()` up the tree

### Conditional rendering
```swift
// Prefer opacity/hidden over conditional stacks when possible (keeps layout stable)
Button("Save") { viewModel.save() }
    .opacity(viewModel.isSaving ? 0 : 1)
    .overlay { if viewModel.isSaving { ProgressView() } }

// Animation on state change
withAnimation(.easeInOut(duration: 0.2)) {
    viewModel.toggleExpanded()
}
```

### Safe area handling
```swift
// Always use .ignoresSafeArea() intentionally, never as a hack
.ignoresSafeArea(edges: .top)  // For header images that extend under status bar

// Add padding at the bottom for home indicator
.padding(.bottom, 34)  // Only when ignoring safe area; prefer safeAreaInset
.safeAreaInset(edge: .bottom) {
    FloatingButtonBar()
}
```

### Preview with multiple configurations
```swift
#Preview("Light") {
    NavigationStack {
        FeatureView()
    }
}

#Preview("Dark") {
    NavigationStack {
        FeatureView()
    }
    .preferredColorScheme(.dark)
}

#Preview("Large Text") {
    NavigationStack {
        FeatureView()
    }
    .environment(\.sizeCategory, .accessibilityExtraLarge)
}
```
