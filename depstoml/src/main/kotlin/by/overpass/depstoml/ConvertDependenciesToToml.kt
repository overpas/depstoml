package by.overpass.depstoml

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.extension

class ConvertDependenciesToToml : CliktCommand(
    name = "depstoml",
    help = "Convert Kotlin Dependencies object to Gradle libs.versions.toml file",
) {

    private val dependenciesFile by argument(
        name = "deps",
        help = "path to Kotlin dependencies file",
    ).file(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val tomlFile by argument(
        name = "toml",
        help = "generated libs.versions.toml file path",
    ).file(mustExist = false)
        .default(File("libs.versions.toml"))

    private val orderLexicographically by option(
        names = arrayOf("-o", "--order-lexicographically"),
        help = "order libraries lexicographically",
    ).flag(default = false)

    private val replaceUsages by option(
        names = arrayOf("-r", "--replace-usages"),
        help = "replace artifact usages with new identifiers"
    ).flag(default = false)

    override fun run() {
        dependenciesFile.asKotlinFile()
            ?.extractDepsTree()
            ?.apply {
                createTomlConfig(orderLexicographically)
                    .writeTo(
                        tomlFile.apply {
                            if (!exists()) {
                                parentFile?.mkdirs()
                                createNewFile()
                            }
                        },
                    )
                if (replaceUsages) {
                    val replacements = findReplacements()
                    Files.find(
                        Path(""),
                        Int.MAX_VALUE,
                        { path, _ -> path.extension == "kts" },
                    ).forEach { path ->
                        path.replaceAll(replacements)
                    }
                }
            }
            ?: System.err.println("Kotlin Dependencies object not found in file: ${dependenciesFile.path}")
    }
}
