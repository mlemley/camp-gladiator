# camp-gladiator ![Build Status](https://travis-ci.org/mlemley/camp-gladiator.svg?branch=master)

## Dev Setup

Project created using [Android Studio](https://developer.android.com/studio/index.html)

Checkout Project from [Camp Gladiator](https://github.com/mlemley/camp-gladiator)

### Google Map Api Access

In order for google maps to work follow the steps to [obtain an API token](https://developers.google.com/maps/documentation/android-sdk/get-api-key) for usage of the google map platform

Ensure that both:
1) [Map Api Support is enabled](https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com)
2) [Api Key has been created](https://console.cloud.google.com/apis/credentials)

update either the local gradle.properties or the global gradle.properties located in `$HOME/.gradle/gradle.properties` 
to contain the following values for the development and production API keys.  If a `gradle.properties` does not exist 
in `$HOME/.gradle/` directory you can create the file and add the keys

```gradle.properties

GOOGLE_API_KEY={$production.key}

GOOGLE_API_KEY_DEBUG={$debug.key}
```

### Test Frameworks Employed
* [Robolectric.org](https://robolectric.org) for integration / unit tests that touch the android framework
* [mockk](https://mockk.io/) for plain unit testing (Native Kotlin mocking DSL)

### Gradle Configuration

### Local Build

Developers can build on the command line with the appropriate android environment


**Robolectric / Unit Test Execution**

```sh

  ./gradlew lintRelease testReleaseUnitTest

```

**Artifact Generation**

```sh

  ./gradlew clean assemble

```

Artifacts will be located at `$project.dir`/app/build/outputs/debug/`**`.apk


## CI

Continuous Integration is brought to you by [Travis CI](https://travis-ci.org/mlemley/camp-gladiator)

