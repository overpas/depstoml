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