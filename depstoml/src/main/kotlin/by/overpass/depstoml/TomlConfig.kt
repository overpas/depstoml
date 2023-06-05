package by.overpass.depstoml

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

data class TomlConfig(
    val versions: List<Version>,
    val libraries: List<Library>,
    val plugins: List<Plugin>,
)

data class Version(
    val key: String,
    val number: String,
)

data class Library(
    val key: String,
    val group: String,
    val name: String,
    val versionRef: String,
)

data class Plugin(
    val key: String,
    val id: String,
    val versionRef: String,
)

fun TomlConfig.writeTo(file: File) {
    BufferedWriter(FileWriter(file)).use { writer ->
        with(writer) {
            writeVersions(versions)
            writeLibraries(libraries)
            writePlugins(plugins)
        }
    }
}

private fun BufferedWriter.writeVersions(versions: List<Version>) {
    if (versions.isNotEmpty()) {
        append("[versions]")
        newLine()
        for ((key, number) in versions) {
            append("$key = \"$number\"")
            newLine()
        }
    }
}

private fun BufferedWriter.writeLibraries(libraries: List<Library>) {
    if (libraries.isNotEmpty()) {
        newLine()
        append("[libraries]")
        newLine()
        for ((key, group, name, versionRef) in libraries) {
            append("$key = { group = \"$group\", name = \"$name\", version.ref = \"$versionRef\" }")
            newLine()
        }
    }
}

private fun BufferedWriter.writePlugins(plugins: List<Plugin>) {
    if (plugins.isNotEmpty()) {
        newLine()
        append("[plugins]")
        newLine()
        for ((key, id, versionRef) in plugins) {
            append("$key = { id = \"$id\", version.ref = \"$versionRef\" }")
            newLine()
        }
    }
}
