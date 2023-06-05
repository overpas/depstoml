package by.overpass.depstoml

internal fun DepsTreeNode.getAllLeafPaths(): MutableList<MutableList<String>> {
    val paths = mutableListOf<MutableList<String>>()
    fun dfs(node: DepsTreeNode, currentPath: MutableList<String>) {
        currentPath.add(node.id)
        if (node.children.isEmpty()) {
            paths.add(currentPath.toMutableList())
        } else {
            for (child in node.children) {
                dfs(child.value, currentPath)
            }
        }
        currentPath.removeAt(currentPath.lastIndex)
    }
    dfs(this, mutableListOf())
    return paths
}

internal fun getVersionNodePath(root: DepsTreeNode, node: DepsTreeNode): List<String> {
    return node.children
        .values
        .firstOrNull()
        ?.let { versionValueNode ->
            getPathToTarget(root, versionValueNode)
        }
        ?.let { path ->
            path.subList(2, path.size - 1)
        }
        ?: emptyList()
}

private fun getPathToTarget(root: DepsTreeNode, target: DepsTreeNode): List<String> {
    val path = mutableListOf<String>()
    fun findPath(node: DepsTreeNode, target: DepsTreeNode, path: MutableList<String>): Boolean {
        path.add(node.id)
        if (node == target) {
            return true
        }
        for (child in node.children) {
            if (findPath(child.value, target, path)) {
                return true
            }
        }
        path.removeAt(path.size - 1)
        return false
    }
    if (findPath(root, target, path)) {
        return path
    }
    return emptyList()
}

fun findNode(node: DepsTreeNode, target: String): DepsTreeNode? {
    val q = ArrayDeque<DepsTreeNode>()
    q.add(node)
    while (q.isNotEmpty()) {
        val current = q.removeFirst()
        if (current.id == target) return current
        for (child in current.children.values) {
            q.add(child)
        }
    }
    return null
}

private fun findNodes(node: DepsTreeNode, target: String): List<DepsTreeNode> {
    val result = mutableListOf<DepsTreeNode>()
    val q = ArrayDeque<DepsTreeNode>()
    q.add(node)
    while (q.isNotEmpty()) {
        val current = q.removeFirst()
        if (current.id == target) {
            result.add(current)
        }
        for (child in current.children.values) {
            q.add(child)
        }
    }
    return result
}

internal fun findClosestNonDescendant(root: DepsTreeNode, node: DepsTreeNode, target: String): DepsTreeNode? {
    val targetNodes = findNodes(root, target)
    var minDistance = Int.MAX_VALUE
    var closestTarget: DepsTreeNode? = null
    for (targetNode in targetNodes) {
        val distance = findDistance(root, node, targetNode)
        if (distance != null && distance < minDistance) {
            minDistance = distance
            closestTarget = targetNode
        }
    }
    return closestTarget
}

private fun findDistance(root: DepsTreeNode, node1: DepsTreeNode, node2: DepsTreeNode): Int? {
    var parent = lowestCommonAncestor(root, node1, node2)
    if (parent == null) parent = root
    val distance1 = getDistance(parent, node1)
    val distance2 = getDistance(parent, node2)
    if (distance1 == null || distance2 == null) return null
    return distance1 + distance2
}

private fun getDistance(root: DepsTreeNode, node: DepsTreeNode): Int? {
    if (root == node) {
        return 0
    }
    for (child in root.children.values) {
        val distance = getDistance(child, node)
        if (distance != null) {
            return distance + 1
        }
    }
    return null
}

private fun lowestCommonAncestor(root: DepsTreeNode, node1: DepsTreeNode, node2: DepsTreeNode): DepsTreeNode? {
    if (root == node1 || root == node2) {
        return root
    }
    var foundNodesCount = 0
    var commonAncestor: DepsTreeNode? = null
    for (child in root.children.values) {
        val ancestor = lowestCommonAncestor(child, node1, node2)
        if (ancestor != null) {
            foundNodesCount++
            if (foundNodesCount == 1) {
                commonAncestor = ancestor
            } else if (foundNodesCount == 2) {
                return root
            }
        }
    }
    return commonAncestor
}

internal fun findNodeByPath(root: DepsTreeNode, path: List<String>): DepsTreeNode? {
    if (path.isEmpty()) return null
    var node = findNode(root, path[0])
    for (i in 1 until path.size) {
        val nextChild = node?.children?.get(path[i])
        if (nextChild != null) {
            node = nextChild
        }
    }
    return node?.children?.get(path.last()) ?: node
}
