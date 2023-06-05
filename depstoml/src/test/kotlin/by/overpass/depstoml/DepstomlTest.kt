package by.overpass.depstoml

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DepstomlTest {

    private val tomlPath = "build/generated/main/kotlin/by/overpass/depstoml/libs.versions.toml"

    private val testErrorOutputStream = ByteArrayOutputStream()

    private val depstomlCommand = ConvertDependenciesToToml()

    @BeforeTest
    fun setup() {
        System.setErr(PrintStream(testErrorOutputStream))
    }

    @Test
    fun `file with single top-level Dependencies object is converted to TOML correctly`() {
        launchTest(
            "src/test/resources/DependenciesFile.kt",
            tomlPath,
            expected = """[versions]
                |kotlin-version = "1.8.21"
                |kotlin-date-time-version = "0.4.0"
                |kotlin-coroutines-version = "1.6.4"
                |log-version = "2.6.1"
                |plugins-version = "1.8.21-1.0.11"
                |
                |[libraries]
                |kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin-version" }
                |kotlin-date-time = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlin-date-time-version" }
                |kotlin-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlin-coroutines-version" }
                |kotlin-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlin-coroutines-version" }
                |log-napier = { group = "io.github.aakira", name = "napier", version.ref = "log-version" }
                |
                |[plugins]
                |plugins-ksp = { id = "com.google.devtools.ksp", version.ref = "plugins-version" }
                |
            """.trimMargin(),
        )
    }

    @Test
    fun `file with single top-level Dependencies object is converted to TOML correctly with dependencies sorted`() {
        launchTest(
            "src/test/resources/DependenciesFile.kt",
            "-o",
            tomlPath,
            expected = """[versions]
                |kotlin-coroutines-version = "1.6.4"
                |kotlin-date-time-version = "0.4.0"
                |kotlin-version = "1.8.21"
                |log-version = "2.6.1"
                |plugins-version = "1.8.21-1.0.11"
                |
                |[libraries]
                |kotlin-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlin-coroutines-version" }
                |kotlin-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlin-coroutines-version" }
                |kotlin-date-time = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlin-date-time-version" }
                |kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin-version" }
                |log-napier = { group = "io.github.aakira", name = "napier", version.ref = "log-version" }
                |
                |[plugins]
                |plugins-ksp = { id = "com.google.devtools.ksp", version.ref = "plugins-version" }
                |
            """.trimMargin(),
        )
    }

    @Test
    fun `file with Versions and Dependencies objects is converted to TOML correctly`() {
        launchTest(
            "src/test/resources/VersionsAndDependenciesFile.kt",
            tomlPath,
            expected = """[versions]
                |kotlin-version = "1.8.21"
                |kotlin-date-time-version = "0.4.0"
                |kotlin-coroutines-version = "1.6.4"
                |log-version = "2.6.1"
                |plugins-ksp-version = "1.8.21-1.0.11"
                |
                |[libraries]
                |kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin-version" }
                |kotlin-date-time = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlin-date-time-version" }
                |kotlin-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlin-coroutines-version" }
                |kotlin-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlin-coroutines-version" }
                |log-napier = { group = "io.github.aakira", name = "napier", version.ref = "log-version" }
                |
                |[plugins]
                |plugins-ksp = { id = "com.google.devtools.ksp", version.ref = "plugins-ksp-version" }
                |
            """.trimMargin(),
        )
    }

    @Test
    fun `file with Versions and Dependencies objects is converted to TOML correctly with dependencies sorted`() {
        launchTest(
            "src/test/resources/VersionsAndDependenciesFile.kt",
            "--order-lexicographically",
            tomlPath,
            expected = """[versions]
                |kotlin-coroutines-version = "1.6.4"
                |kotlin-date-time-version = "0.4.0"
                |kotlin-version = "1.8.21"
                |log-version = "2.6.1"
                |plugins-ksp-version = "1.8.21-1.0.11"
                |
                |[libraries]
                |kotlin-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlin-coroutines-version" }
                |kotlin-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlin-coroutines-version" }
                |kotlin-date-time = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlin-date-time-version" }
                |kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin-version" }
                |log-napier = { group = "io.github.aakira", name = "napier", version.ref = "log-version" }
                |
                |[plugins]
                |plugins-ksp = { id = "com.google.devtools.ksp", version.ref = "plugins-ksp-version" }
                |
            """.trimMargin(),
        )
    }

    @Test
    fun `prints a message that kotlin dependencies objects not found`() {
        depstomlCommand.main(
            arrayOf(
                "src/test/resources/EmptyFile.kt",
                tomlPath,
            ),
        )

        val actual = testErrorOutputStream.toString()

        assertEquals("Kotlin Dependencies object not found in file: src/test/resources/EmptyFile.kt\n", actual)
    }

    @Test
    fun `prints a message that version not found`() {
        depstomlCommand.main(
            arrayOf(
                "src/test/resources/NoVersionFoundFile.kt",
                tomlPath,
            ),
        )

        val actual = testErrorOutputStream.toString()

        assertEquals("Couldn't find version referenced by \"Versions.Kotlin.version\"\n", actual)
    }

    @Test
    fun `prints a message that path can't be determined`() {
        depstomlCommand.main(
            arrayOf(
                "src/test/resources/UnknownDependencyPathFile.kt",
                tomlPath,
            ),
        )

        val actual = testErrorOutputStream.toString()

        assertEquals("Can't determine if this is a version, library or plugin: [sample, path, test]\n", actual)
    }

    private fun launchTest(vararg args: String, expected: String) {
        depstomlCommand.main(args)

        val actual = Files.readString(Path(tomlPath))

        assertEquals(expected, actual)
    }

    @AfterTest
    fun teardown() {
        File(tomlPath).delete()
        System.setErr(System.err)
    }
}
