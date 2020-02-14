# camp-gladiator ![Build Status](https://travis-ci.org/mlemley/camp-gladiator.svg?branch=master)

## Dev Setup

Project created using [Android Studio](https://developer.android.com/studio/index.html)

Checkout Project from [Camp Gladiator](https://github.com/mlemley/camp-gladiator)


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

