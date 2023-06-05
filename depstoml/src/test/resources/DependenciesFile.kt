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