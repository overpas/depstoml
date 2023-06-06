# depstoml
[![CI](https://github.com/overpas/depstoml/actions/workflows/ci.yml/badge.svg)](https://github.com/overpas/depstoml/actions/workflows/ci.yml)

A CLI tool facilitating migration from `buildSrc`-hardcoded dependencies to Gradle version catalog TOML file in Kotlin projects

### Installation

Download the `depstoml-x.x.jar` file from the latest GitHub release and launch it with corresponding arguments

### Usage

```
$ java -jar depstoml-x.x.jar [OPTIONS] deps [toml]
```

Sample deps value: `buildSrc/src/main/kotlin/Dependencies.kt`

Sample toml value: `gradle/libs.versions.toml`

#### Options
- `-o`, `--order-lexicographically` - if you want to sort the dependencies lexicographically

#### Arguments
- `deps` - path to Kotlin dependencies file
- `toml` - generated libs.versions.toml file path (defaults to current directory)

Your Kotlin dependencies file in `buildSrc` (or `build-logic`, or whatever) is expected to be structured in 2 possible ways:
1) versions grouped together with artifacts
```kotlin
object Dependencies {

    object Kotlin {

        private const val version = "1.8.21"
        private const val dateTimeVersion = "0.4.0"

        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion"

        object Coroutines {

            private const val version = "1.6.4"

            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }
    }

    object Log {

        private const val version = "2.6.1"

        const val napier = "io.github.aakira:napier:$version"
    }

    object Plugins {

        private const val version = "1.8.21-1.0.11"

        const val ksp = "com.google.devtools.ksp:$version"
    }
}
```

2) versions and artifacts separately
```kotlin
object Versions {

    object Kotlin {

        const val version = "1.8.21"
        const val dateTimeVersion = "0.4.0"

        object Coroutines {

            const val version = "1.6.4"
        }
    }

    object Log {

        const val version = "2.6.1"
    }

    object Plugins {

        const val kspVersion = "1.8.21-1.0.11"
    }
}

object Deps {

    object Kotlin {

        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.version}"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.Kotlin.dateTimeVersion}"

        object Coroutines {

            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.Coroutines.version}"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.Coroutines.version}"
        }
    }

    object Log {

        const val napier = "io.github.aakira:napier:${Versions.Log.version}"
    }

    object Plugins {

        const val ksp = "com.google.devtools.ksp:${Versions.Plugins.kspVersion}"
    }
}
```

###### ! Note that the program can't parse complex logic with `by lazy`, `fun` etc.

Sample dependencies file 1 above will generate the following:
```toml
[versions]
kotlin-version = "1.8.21"
kotlin-date-time-version = "0.4.0"
kotlin-coroutines-version = "1.6.4"
log-version = "2.6.1"
plugins-version = "1.8.21-1.0.11"

[libraries]
kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin-version" }
kotlin-date-time = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlin-date-time-version" }
kotlin-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlin-coroutines-version" }
kotlin-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlin-coroutines-version" }
log-napier = { group = "io.github.aakira", name = "napier", version.ref = "log-version" }

[plugins]
plugins-ksp = { id = "com.google.devtools.ksp", version.ref = "plugins-version" }
```

Sample dependencies file 2 above will generate the following:
```toml
[versions]
kotlin-version = "1.8.21"
kotlin-date-time-version = "0.4.0"
kotlin-coroutines-version = "1.6.4"
log-version = "2.6.1"
plugins-ksp-version = "1.8.21-1.0.11"

[libraries]
kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin-version" }
kotlin-date-time = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlin-date-time-version" }
kotlin-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlin-coroutines-version" }
kotlin-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlin-coroutines-version" }
log-napier = { group = "io.github.aakira", name = "napier", version.ref = "log-version" }

[plugins]
plugins-ksp = { id = "com.google.devtools.ksp", version.ref = "plugins-ksp-version" }
```