package by.overpass.depstoml

import java.util.Locale

data class DepsTreeNode(
    val id: String,
    val children: MutableMap<String, DepsTreeNode> = mutableMapOf(),
)

private enum class PathType {

    VERSION, LIBRARY, PLUGIN, UNKNOWN;

    companion object {

        fun of(path: List<String>): PathType {
            for (id in path) {
                if (id.matches(versionRegex)) {
                    return VERSION
                } else if (id.matches(libraryRegex)) {
                    return LIBRARY
                } else if (id.matches(pluginRegex)) {
                    return PLUGIN
                }
            }
            return UNKNOWN
        }
    }
}

private val versionRefRegex = "\\$\\{?(.+)}?$".toRegex()
private val versionRegex = "^\\d+(\\.\\d+)*(-[a-zA-Z0-9]+(\\.\\d+(-[a-zA-Z0-9]+)*)*)?\$".toRegex()
private val libraryRegex = "^(.+):(.+):${versionRefRegex.pattern}$".toRegex()
private val pluginRegex = "^(.+):${versionRefRegex.pattern}$".toRegex()

fun DepsTreeNode.createTomlConfig(
    orderLexicographically: Boolean,
): TomlConfig {
    val paths = getAllLeafPaths().apply {
        appendVersionRefs(this@createTomlConfig)
        format(orderLexicographically)
    }
    val versions = mutableListOf<Version>()
    val libraries = mutableListOf<Library>()
    val plugins = mutableListOf<Plugin>()
    for (path in paths) {
        when (PathType.of(path)) {
            PathType.VERSION -> versions += createVersion(path)
            PathType.LIBRARY -> createLibrary(path)?.let(libraries::add)
            PathType.PLUGIN -> createPlugin(path)?.let(plugins::add)
            else -> System.err.println("Can't determine if this is a version, library or plugin: $path")
        }
    }
    return TomlConfig(
        versions = versions,
        libraries = libraries,
        plugins = plugins,
    )
}

private fun MutableList<MutableList<String>>.appendVersionRefs(root: DepsTreeNode) {
    for (path in this) {
        val id = path.last()
        val versionRef = versionRefRegex.find(id)
            ?.groups
            ?.get(1)
            ?.value
            ?.trimEnd('}')
        if (versionRef != null) {
            path.appendVersionNodePath(root, id, versionRef)
        }
    }
}

private fun MutableList<String>.appendVersionNodePath(root: DepsTreeNode, id: String, versionRef: String) {
    val artifactNode = findNode(root, id)
    if (artifactNode != null) {
        val versionNode = if (versionRef.contains('.')) {
            findNodeByPath(root, versionRef.split('.'))
        } else {
            findClosestNonDescendant(root, artifactNode, versionRef)
        }
        if (versionNode != null) {
            this += getVersionNodePath(root, versionNode)
        } else {
            System.err.println("Couldn't find version referenced by \"$versionRef\"")
        }
    }
}

private fun MutableList<MutableList<String>>.format(orderLexicographically: Boolean) {
    for (i in 0 until size) {
        val formattedList = mutableListOf<String>()
        for (j in 2 until this[i].size) {
            val path = this[i][j]
            if (path.matches(libraryRegex) || path.matches(pluginRegex)) {
                formattedList += path
            } else {
                formattedList += separateIds(this[i][j])
            }
        }
        this[i] = formattedList
    }
    if (orderLexicographically) {
        sortBy(MutableList<String>::joinToString)
    }
}

private fun createVersion(path: List<String>): Version {
    return Version(
        key = path.subList(0, path.size - 1).joinToString("-"),
        number = path.last(),
    )
}

private fun createLibrary(path: List<String>): Library? {
    val artifactIndex = path.indexOfFirst { id ->
        id.matches(libraryRegex)
    }
    if (artifactIndex == -1) return null
    val artifact = path[artifactIndex]
    val matches = libraryRegex.find(artifact)?.groups
    val group = matches?.get(1)?.value
    val name = matches?.get(2)?.value
    if (group == null || name == null) return null
    return Library(
        key = path.subList(0, artifactIndex).joinToString("-"),
        group = group,
        name = name,
        versionRef = path.subList(artifactIndex + 1, path.size).joinToString("-"),
    )
}

private fun createPlugin(path: List<String>): Plugin? {
    val pluginIndex = path.indexOfFirst { id ->
        id.matches(pluginRegex)
    }
    if (pluginIndex == -1) return null
    val plugin = path[pluginIndex]
    val id = pluginRegex.find(plugin)
        ?.groups
        ?.get(1)
        ?.value
        ?: return null
    return Plugin(
        key = path.subList(0, pluginIndex).joinToString("-"),
        id = id,
        versionRef = path.subList(pluginIndex + 1, path.size).joinToString("-"),
    )
}

private fun separateIds(input: String): List<String> {
    val formattedWords = mutableListOf<String>()
    val words = if (input.contains('_')) {
        input.split('_')
    } else {
        val regex = "(?<=.)(?=\\p{Lu})".toRegex()
        input.split(regex)
    }
    for (word in words) {
        formattedWords.add(word.lowercase(Locale.getDefault()))
    }
    return formattedWords
}
