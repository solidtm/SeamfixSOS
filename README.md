|          |             |                |       |
| :---:    |    :----:   |          :---: | :---: |
| ![1](https://drive.google.com/uc?export=view&id=1IIwVZc1X2dRYEs2xza5xqeBxJlQalS2f) | ![2](https://drive.google.com/uc?export=view&id=1hCSVqWT6Ak8WetckxMGMDNoS8iQlO2GT) | ![3](https://drive.google.com/uc?export=view&id=15fn7GHbi8L8PQQIXmXl6HXxMcgJ8nTMG) | ![4](https://drive.google.com/uc?export=view&id=1dy_ncPV1PevklRhNLOFfKk971Enw6xwz)


SeamfixSOS is an Android application designed to showcase an SOS scenario, that would help in contacting the police or family members, in a situation where you or someone is in distress and unable to make phone calls. 
It allows users to browse through a list of products and view more information about each product. The app is built entirely using Kotlin and XML.


## Architecture
I strive to keep my architecture "perfect" by putting software-design and code quality first.

### High-level view:

- [MVVM (Model-View-ViewModel)](https://www.techtarget.com/whatis/definition/Model-View-ViewModel#:~:text=Model%2DView%2DViewModel%20(MVVM)%20is%20a%20software%20design,Ken%20Cooper%20and%20John%20Gossman.)

## Usage
- Home Screen: Displays a directive and a button to start the SOS process.
- SOS Screen: Shows CTAs (buttons) to carry create an SOS contact, including the take photo, capture location, and submit buttons.

## Assumptions
- Networking: SOS contacts are created through a RESTful API endpoint.
- Error Handling: Basic error dialogs are shown for network errors or failures.
- Navigation: Simple navigation flow with two primary screens (Main Activity List and SOS Activity).

## Tech Stack - Choices Around Plugins and 3rd Party Libraries

### Core:
- 100% [Kotlin](https://kotlinlang.org/): Ensures modern language features and null safety.
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html): Simplifies asynchronous programming, making it easier to handle background tasks and manage concurrency.
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html): Used for reactive programming, handling streams of data efficiently.

### Dependency Injection:
- [Koin](https://insert-koin.io/): A lightweight dependency injection framework for Kotlin. It simplifies the management of dependencies and allows for easy testing and configuration.

### Networking:
- [Retrofit](https://square.github.io/retrofit/): A type-safe HTTP client for Android. It simplifies the process of making network requests and parsing the responses.
- [OkHttp](https://square.github.io/okhttp/) (REST Client): A robust HTTP client that provides efficient HTTP requests.
- [Moshi](https://github.com/square/retrofit/blob/trunk/retrofit-converters/moshi/README.md): A modern JSON library for Android and Java, used for parsing JSON into Kotlin objects and vice versa.

### CI/CD:
- [Gradle KTS](https://docs.gradle.org/current/userguide/kotlin_dsl.html): Uses Kotlin DSL for Gradle build scripts, offering better syntax highlighting, code completion, and type safety.

### Testing:
- [JUnit 5](https://junit.org/junit5/): The latest version of the popular testing framework for Java and Kotlin.
- [MockK](https://mockk.io/): A mocking library for Kotlin. It allows for creating mocks and stubs in a type-safe manner.
- [Espresso](https://developer.android.com/training/testing/espresso): A UI testing framework for Android, used for writing concise and reliable UI tests.


## Performance Optimization Considerations

### Efficient Networking:
- OkHttp and Retrofit: These libraries are chosen for their efficiency in handling network requests. OkHttp supports connection pooling and HTTP/2, which reduces latency and saves resources.
- Caching: OkHttp’s caching mechanism is utilized to store responses and reuse them when possible, reducing the number of network calls.

### Asynchronous Programming:
- Kotlin Coroutines and Flow: Used extensively to perform tasks asynchronously, ensuring that the main thread is not blocked, leading to a smoother user experience.

### Dependency Injection:
- Koin: Simplifies dependency management, reducing the boilerplate code and improving the maintainability and performance of dependency resolution.


## Project Requirements
- Java 11+
- **Android Studio Electric Eel+** (for easy install
  use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/))

## How to build?
1. Clone the repository
2. Open with Android Studio
3. Everything should sync and build automatically

