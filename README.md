# camp-gladiator

## Dev Setup

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
## Deployment
