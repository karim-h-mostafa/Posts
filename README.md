# Posts Android App

A modern Android application built with Jetpack Compose that displays a paginated list of posts and post details with image. The app follows **Clean Architecture** with **MVI (Model-View-Intent)** pattern and implements best practices for Android development.

## Overview

The Posts app fetches and displays posts from the JSONPlaceholder API, providing a smooth user experience with offline support, pagination, and error handling. Users can browse through posts in a paginated list and view detailed information about each post.

## Architecture

This project follows **Clean Architecture** with **MVI (Model-View-Intent)** pattern, providing a clear separation of concerns across three main layers:

### Architecture Layers

1. **Presentation Layer** (`presentation/`)
    - UI components built with Jetpack Compose
    - ViewModels implementing MVI pattern
    - Navigation using Jetpack Navigation Compose
    - State management with Kotlin Flows
    - Contract definitions (State, Intent, Effect)

2. **Domain Layer** (`domain/`)
    - Business logic and use cases
    - Domain models
    - Repository interfaces

3. **Data Layer** (`data/`)
    - Remote data sources (API)
    - Local data sources (Room Database)
    - Repository implementations
    - Data mappers (DTO ↔ Entity ↔ Domain)
    - Paging implementation with RemoteMediator


### Key Architectural Patterns

- **MVI (Model-View-Intent)**: Unidirectional data flow with clear separation
- **Clean Architecture**: Separation into Presentation, Domain, and Data layers
- **Repository Pattern**: Single source of truth for data
- **Dependency Injection**: Hilt for managing dependencies
- **Unidirectional Data Flow**: Intent → ViewModel → State/Effect → UI

## Tech Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material Design 3**: UI components and theming

### Architecture Components
- **ViewModel**: Lifecycle-aware UI data management
- **Room**: Local database for offline support
- **Navigation Compose**: Type-safe navigation
- **Paging 3**: Efficient pagination and data loading

### Dependency Injection
- **Hilt**: Dependency injection framework built on Dagger

### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP client with interceptors
- **Moshi**: JSON serialization/deserialization
- **Coil**: Image loading library for Compose

### Testing
- **JUnit**: Unit testing framework
- **MockK**: Mocking library for Kotlin
- **Turbine**: Testing Kotlin Flows
- **Coroutines Test**: Testing coroutines
- **AssertK**: Fluent assertion library

### Build Tools
- **Gradle Kotlin DSL**: Build configuration
- **KSP (Kotlin Symbol Processing)**: Code generation
- **Version Catalog**: Centralized dependency management

## Project Structure

```
app/src/main/java/com/karim/posts/
├── common/                          # Shared utilities and components
│   ├── Constants.kt                 # App-wide constants
│   ├── Result.kt                    # Result wrapper for async operations
│   ├── designsystem/                # Reusable UI components
│   │   ├── Components.kt
│   │   ├── ErrorMessage.kt
│   │   └── SkeletonLoading.kt
│   └── theme/                       # Material Design 3 theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── data/                            # Data layer
│   ├── datasource/                  # Data source implementations
│   │   ├── PostsLocalDataSource.kt
│   │   ├── PostsLocalDataSourceImpl.kt
│   │   ├── PostsRemoteDataSource.kt
│   │   └── PostsRemoteDataSourceImpl.kt
│   ├── local/                       # Room database
│   │   ├── dao/PostDao.kt
│   │   ├── entity/PostEntity.kt
│   │   └── PostsRoomDB.kt
│   ├── mapper/                      # Data mappers
│   │   └── PostMapper.kt
│   ├── paging/                      # Paging configuration
│   │   └── PostRemoteMediator.kt
│   ├── remote/                      # API models and services
│   │   ├── model/PostDTO.kt
│   │   └── service/PostApi.kt
│   └── repository/                  # Repository implementations
│       └── PostsRepositoryImpl.kt
│
├── di/                              # Dependency Injection modules
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── domain/                          # Domain layer (business logic)
│   ├── model/Post.kt                # Domain models
│   ├── repository/                  # Repository interfaces
│   │   └── PostsRepository.kt
│   └── usecase/                     # Use cases
│       ├── GetPostDetailsUseCase.kt
│       └── GetPostsUseCase.kt
│
├── presentation/                    # Presentation layer (MVI)
│   ├── feature/                     # Feature modules
│   │   ├── postdetails/             # Post details feature
│   │   │   ├── PostDetailsContract.kt  # MVI: State, Intent, Effect
│   │   │   ├── PostDetailsScreen.kt     # View (Compose UI)
│   │   │   └── PostDetailsViewModel.kt  # Intent processor
│   │   └── postslist/               # Posts list feature
│   │       ├── PostsContract.kt         # MVI: Intent, Effect
│   │       ├── PostsListScreen.kt       # View (Compose UI)
│   │       └── PostsListViewModel.kt    # Intent processor
│   └── navigation/                  # Navigation setup
│       ├── PostsAppNavHost.kt
│       └── PostsGraph.kt
│
├── MainActivity.kt                   # Main entry point
├── PostsApplication.kt               # Application class
└── PostsAppState.kt                 # App-level state management
```

## Features

### Current Features
- **Paginated Posts List**: Efficiently loads and displays posts with pagination
- **Post Details**: View detailed information about individual posts
- **Offline Support**: Posts are cached locally using Room database
- **Error Handling**: Comprehensive error handling with user-friendly messages
- **Loading States**: Skeleton loading animations for better UX
- **Image Loading**: Efficient image loading with Coil
- **Type-Safe Navigation**: Navigation using Kotlin serialization

### User Experience
- Smooth scrolling with pagination
- Shimmer loading effects
- Error retry mechanisms
- Snackbar notifications for errors
- Material Design 3 UI components

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 21 or later
- Android SDK 24 (minimum) / 36 (target)
- Gradle 8.12.3 or later


### Configuration

The app uses the JSONPlaceholder API by default. The base URL is configured in `build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
```

To change the API endpoint, modify the `BASE_URL` in both `debug` and `release` build types.

## Data Flow

### Posts List Flow (MVI)
1. **UI** → User opens app or pulls to refresh
2. **ViewModel** → Calls `GetPostsUseCase` (initialized automatically)
3. **UseCase** → Calls `PostsRepository.getPosts()`
4. **Repository** → Returns `Flow<PagingData<Post>>` from Pager
5. **Pager** → Uses `PostRemoteMediator` to fetch from API
6. **RemoteMediator** → Fetches data from API and saves to Room
7. **Room** → Emits data to UI via PagingSource
8. **State** → ViewModel emits `PagingData<Post>` as state
9. **UI** → Collects state and displays posts in LazyColumn with pagination

**User Interaction Flow:**
- **Intent**: User clicks on a post → `PostsIntent.ClickPost(id)`
- **ViewModel**: Processes intent → Emits `PostsEffect.NavigateToPostDetails(id)`
- **Effect Handler**: Navigates to PostDetails screen

### Post Details Flow (MVI)
1. **Navigation** → PostDetails screen receives post ID from navigation arguments
2. **ViewModel Init** → Extracts ID from SavedStateHandle
3. **Intent**: Automatically triggers data fetch (or user clicks Retry → `PostDetailsIntent.Retry`)
4. **State**: ViewModel updates `PostDetailsState(isLoading = true)`
5. **ViewModel** → Calls `GetPostDetailsUseCase` with ID
6. **UseCase** → Calls `PostsRepository.getPostDetails(id)`
7. **Repository** → Fetches from Room database
8. **State**: ViewModel updates `PostDetailsState(post = data, isLoading = false)`
9. **UI** → Collects state and displays post details

**User Interaction Flow:**
- **Intent**: User clicks back → `PostDetailsIntent.ClickBack`
- **ViewModel**: Processes intent → Emits `PostDetailsEffect.NavigateBack`
- **Effect Handler**: Navigates back to previous screen

## Testing

The project includes comprehensive testing setup most of it using AI generation:

### Unit Tests
- ViewModel tests
- Repository tests
- Use case tests
- Mapper tests

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run tests for a specific module
./gradlew :app:testDebugUnitTest

# Run with coverage
./gradlew testDebugUnitTest --continue
```

### Test Libraries
- **MockK**: For mocking dependencies
- **Turbine**: For testing Kotlin Flows
- **Coroutines Test**: For testing coroutines
- **AssertK**: For fluent assertions

## Key Components

### RemoteMediator
The `PostRemoteMediator` handles fetching data from the remote API and storing it in the local database. It implements the Paging 3 RemoteMediator pattern for efficient data synchronization.

### Repository Pattern
The `PostsRepository` interface defines the contract for data operations, while `PostsRepositoryImpl` provides the implementation that coordinates between remote and local data sources.

### Use Cases
- `GetPostsUseCase`: Retrieves paginated list of posts
- `GetPostDetailsUseCase`: Retrieves details for a specific post

### ViewModels (MVI Pattern)
- `PostsListViewModel`:
    - Processes `PostsIntent` (e.g., `ClickPost`)
    - Emits `PagingData<Post>` as state
    - Emits `PostsEffect` for navigation and error handling

- `PostDetailsViewModel`:
    - Processes `PostDetailsIntent` (e.g., `ClickBack`, `Retry`)
    - Manages `PostDetailsState` (post, isLoading, errorMessage)
    - Emits `PostDetailsEffect` for navigation and error messages

### MVI Contracts
Each feature has a Contract file defining:
- **Intent**: User actions (sealed classes)
- **State**: UI state (data classes)
- **Effect**: Side effects (sealed classes)

Example from `PostsContract.kt`:
```kotlin
sealed class PostsIntent {
    data class ClickPost(val id: Int) : PostsIntent()
}

sealed class PostsEffect {
    data class NavigateToPostDetails(val id: Int) : PostsEffect()
    data class ShowErrorMessage(val message: String) : PostsEffect()
}
```

## Dependencies

### Core Dependencies
- **AndroidX Core KTX**: 1.17.0
- **Jetpack Compose BOM**: 2025.11.01
- **Material Design 3**: Latest
- **Lifecycle Runtime KTX**: 2.10.0

### Architecture Dependencies
- **Hilt**: 2.57.2
- **Room**: 2.8.4
- **Navigation Compose**: 2.9.6
- **Paging**: 3.3.6

### Networking Dependencies
- **Retrofit**: 3.0.0
- **OkHttp**: 5.3.2
- **Moshi**: 1.15.2

### Image Loading
- **Coil Compose**: 2.7.0

See `gradle/libs.versions.toml` for the complete list of dependencies and versions.

## UI/UX

### Design System
The app uses Material Design 3 components and follows Material Design guidelines:
- Material 3 theming
- Custom color scheme
- Typography system
- Reusable components

### Components
- **Skeleton Loading**: Shimmer effect for loading states
- **ErrorMessage**: Reusable error display with retry functionality
- **Elevated Cards**: Material 3 cards for post items

## Permissions

The app requires the following permissions:
- **INTERNET**: For fetching data from the API

## API Information

### Base URL
```
https://jsonplaceholder.typicode.com/
```

### Endpoints
- `GET /photos`: Retrieves list of photos/posts

### API Response Format
```json
{
  "title": "Post title",
  "url": "https://example.com/image.jpg"
}
```

## Architecture Decisions

### Why Clean Architecture + MVI?
- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Scalability**: Easy to add new features
- **Flexibility**: Can swap implementations without affecting other layers
- **Predictable State**: MVI ensures unidirectional data flow, making state predictable
- **Side Effect Management**: Effects are clearly separated from state, making navigation and one-time events easier to handle
- **Type Safety**: Sealed classes for Intents and Effects provide compile-time safety

### Why Paging 3?
- Efficient memory usage
- Built-in support for remote and local data sources
- Seamless integration with Room
- Automatic loading of next pages

### Why Hilt?
- Reduces boilerplate code
- Compile-time safety
- Easy testing with test modules
- Integration with Android components

## Known Issues / Limitations

- The API endpoint used (`/photos`) is not truly paginated, so the RemoteMediator only handles REFRESH load type
- Network errors are shown via Snackbar; consider adding a dedicated error screen
- No image caching configuration specified (uses Coil defaults)


## Author

**Karim Hamed**
- Project: Posts Android App

## Acknowledgments

- JSONPlaceholder API for providing test data
- Android Jetpack team for excellent libraries
- Material Design team for design guidelines
