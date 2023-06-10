package by.overpass.depstoml

import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ReplacementTest(
    private val dependenciesFilePath: String,
    buildGradlePath: String,
    private val expected: String,
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(
                "src/test/resources/DependenciesFile.kt",
                "src/test/resources/gradle/dependencies/build.gradle.kts",
                """ |plugins {
                    |    alias(libs.plugins.ksp)
                    |}
                    |
                    |dependencies {
                    |    implementation(libs.kotlin.gradle.plugin)
                    |    implementation(libs.kotlin.date.time)
                    |    implementation(libs.kotlin.coroutines.core)
                    |    implementation(libs.log.napier)
                    |    testImplementation(libs.kotlin.coroutines.test)
                    |}
                    |""".trimMargin(),
            ),
            arrayOf(
                "src/test/resources/VersionsAndDependenciesFile.kt",
                "src/test/resources/gradle/versions-and-dependencies/build.gradle.kts",
                """ |plugins {
                    |    alias(libs.plugins.ksp)
                    |}
                    |
                    |dependencies {
                    |    implementation(libs.kotlin.gradle.plugin)
                    |    implementation(libs.kotlin.date.time)
                    |    implementation(libs.kotlin.coroutines.core)
                    |    implementation(libs.log.napier)
                    |    testImplementation(libs.kotlin.coroutines.test)
                    |}
                    |""".trimMargin(),
            ),
        )
    }

    private val tomlPath = "build/generated/main/kotlin/by/overpass/depstoml/libs.versions.toml"
    private val buildGradlePath = Path(buildGradlePath)
    private val initialBuildGradleText = Files.readString(this.buildGradlePath)

    private val depstomlCommand = ConvertDependenciesToToml()

    @Test
    fun `usages are replaced in build gradle kts`() {
        depstomlCommand.main(
            arrayOf(
                dependenciesFilePath,
                "-r",
                tomlPath,
            ),
        )

        val actual = Files.readString(buildGradlePath)

        assertEquals(expected, actual)
    }

    @AfterTest
    fun teardown() {
        File(tomlPath).delete()
        Files.writeString(buildGradlePath, initialBuildGradleText)
    }
}
