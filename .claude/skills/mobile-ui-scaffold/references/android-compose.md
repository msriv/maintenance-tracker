# Android Jetpack Compose — UI Reference

## Project Setup

### Gradle dependencies (libs.versions.toml)
```toml
[versions]
compose-bom = "2024.05.00"
hilt = "2.51.1"
lifecycle = "2.7.0"
navigation-compose = "2.7.7"
coil = "2.6.0"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons = { group = "androidx.compose.material", name = "material-icons-extended" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
```

### build.gradle.kts (app module)
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.13" }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.coil.compose)
    debugImplementation(libs.compose.ui.tooling)
}
```

---

## Material 3 Components Cheat Sheet

### Scaffold (outer wrapper for every screen)
```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("Screen Title") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    },
    floatingActionButton = {
        FloatingActionButton(onClick = onFabClick) {
            Icon(Icons.Filled.Add, contentDescription = "Add item")
        }
    }
) { innerPadding ->
    Content(modifier = Modifier.padding(innerPadding))
}
```

### Cards
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    shape = MaterialTheme.shapes.medium,
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
```

### List items (settings-style)
```kotlin
ListItem(
    headlineContent = { Text("Dark Mode") },
    supportingContent = { Text("Use dark theme") },
    leadingContent = {
        Icon(Icons.Filled.DarkMode, contentDescription = null)
    },
    trailingContent = {
        Switch(
            checked = isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode(it) },
            modifier = Modifier.semantics { contentDescription = "Dark mode toggle" }
        )
    }
)
HorizontalDivider()
```

### Lazy lists
```kotlin
LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(
        items = items,
        key = { it.id }  // Always provide stable keys
    ) { item ->
        ItemCard(item = item, onClick = { onItemClick(item.id) })
    }
    
    item {
        if (isLoadingMore) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
```

### Pull to refresh
```kotlin
val pullRefreshState = rememberPullRefreshState(
    refreshing = isRefreshing,
    onRefresh = viewModel::refresh
)
Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
    LazyColumn { /* content */ }
    PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter)
    )
}
```

### Bottom Sheet
```kotlin
val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

if (showSheet) {
    ModalBottomSheet(
        onDismissRequest = { showSheet = false },
        sheetState = sheetState
    ) {
        SheetContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        )
    }
}
```

### Dialogs
```kotlin
if (showConfirmDialog) {
    AlertDialog(
        onDismissRequest = { showConfirmDialog = false },
        title = { Text("Confirm Action") },
        text = { Text("Are you sure you want to continue?") },
        confirmButton = {
            TextButton(onClick = { viewModel.confirm(); showConfirmDialog = false }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { showConfirmDialog = false }) {
                Text("Cancel")
            }
        }
    )
}
```

---

## ViewModel Patterns

### Standard MVVM ViewModel with Hilt
```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val repository: FeatureRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeatureUiState>(FeatureUiState.Loading)
    val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()

    // One-shot events (navigation, snackbar)
    private val _events = Channel<FeatureEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = FeatureUiState.Loading
            repository.getData()
                .onSuccess { data ->
                    _uiState.value = if (data.isEmpty()) FeatureUiState.Empty
                                     else FeatureUiState.Success(data)
                }
                .onFailure { e ->
                    _uiState.value = FeatureUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun retry() = loadData()

    fun onItemSelected(id: String) {
        viewModelScope.launch {
            _events.send(FeatureEvent.NavigateToDetail(id))
        }
    }
}
```

### Collecting events safely in Composable
```kotlin
val lifecycleOwner = LocalLifecycleOwner.current
LaunchedEffect(viewModel.events, lifecycleOwner) {
    viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle)
        .collect { event ->
            when (event) {
                is FeatureEvent.NavigateToDetail -> navController.navigate("detail/${event.id}")
                is FeatureEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
}
```

### Collecting StateFlow with lifecycle awareness
```kotlin
// In Composable — use collectAsStateWithLifecycle (not collectAsState)
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

---

## Accessibility Patterns

```kotlin
// Images must always have contentDescription
Image(
    painter = painterResource(R.drawable.profile),
    contentDescription = "Profile photo of ${user.name}"
)

// Decorative images use null
Icon(Icons.Filled.Star, contentDescription = null)

// Custom semantics for complex components
Box(
    modifier = Modifier.semantics {
        contentDescription = "Rating: ${rating} out of 5 stars"
        role = Role.Image
    }
)

// Section headings for TalkBack navigation
Text(
    text = "Account Settings",
    style = MaterialTheme.typography.titleSmall,
    modifier = Modifier.semantics { heading() }
)

// Merge descendants for list items (reads as one unit)
Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
    // child composables
}

// Toggle semantics
Switch(
    checked = isEnabled,
    onCheckedChange = { onToggle(it) },
    modifier = Modifier.semantics {
        contentDescription = if (isEnabled) "Notifications enabled" else "Notifications disabled"
    }
)
```

---

## Navigation Compose Patterns

### NavHost setup
```kotlin
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestinations.HOME) {
            HomeScreen(
                onNavigateToDetail = { id -> navController.navigate("${AppDestinations.DETAIL}/$id") }
            )
        }
        composable(
            route = "${AppDestinations.DETAIL}/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            DetailScreen(itemId = itemId, onNavigateUp = navController::navigateUp)
        }
    }
}

object AppDestinations {
    const val HOME = "home"
    const val DETAIL = "detail"
    const val SETTINGS = "settings"
}
```

### Passing ViewModel scoped to nav graph
```kotlin
// Share ViewModel between screens on the same back stack
val sharedViewModel: SharedViewModel = hiltViewModel(
    navController.getBackStackEntry(AppDestinations.PARENT_ROUTE)
)
```

---

## Common Compose Idioms

### Conditional rendering
```kotlin
// Prefer AnimatedVisibility for show/hide
AnimatedVisibility(visible = showBanner) {
    InfoBanner(message = bannerMessage)
}

// Use crossfade for state transitions
Crossfade(targetState = uiState, label = "state_transition") { state ->
    when (state) {
        is FeatureUiState.Loading -> LoadingContent()
        is FeatureUiState.Error -> ErrorContent(message = state.message, onRetry = viewModel::retry)
        is FeatureUiState.Empty -> EmptyContent()
        is FeatureUiState.Success -> SuccessContent(data = state.data)
    }
}
```

### Image loading with Coil
```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .crossfade(true)
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.error_image)
        .build(),
    contentDescription = "Product image",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(MaterialTheme.shapes.medium)
)
```

### Window insets (edge-to-edge)
```kotlin
// In Activity
WindowCompat.setDecorFitsSystemWindows(window, false)

// In Composable — consume insets properly
Scaffold(
    modifier = Modifier.fillMaxSize()
) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
        // content safely inside system bars
    }
}

// Manual inset consumption
Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
```

### Preview with sample data
```kotlin
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Preview(showBackground = true, name = "Light")
@Composable
private fun FeatureScreenPreview() {
    AppTheme {
        FeatureScreen(
            uiState = FeatureUiState.Success(
                data = FeatureData(
                    id = "1",
                    title = "Sample Title",
                    description = "Sample description text"
                )
            ),
            onNavigateUp = {}
        )
    }
}
```
