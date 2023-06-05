package by.overpass.depstoml

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

fun KtFile.extractDepsTree(): DepsTreeNode? =
    createDepsTree()

private fun KtFile.createDepsTree(): DepsTreeNode? {
    val root = DepsTreeNode("root")
    children.filterIsInstance<KtObjectDeclaration>()
        .forEach { ktObjectDeclaration ->
            val id = ktObjectDeclaration.name!!
            val newNode = DepsTreeNode(id)
            root.children[id] = newNode
            addNodes(ktObjectDeclaration, newNode)
        }
    if (root.children.isEmpty()) return null
    return root
}

private fun addNodes(ktObjectDeclaration: KtObjectDeclaration, node: DepsTreeNode) {
    addProperties(ktObjectDeclaration, node)
    ktObjectDeclaration.declarations
        .filterIsInstance<KtObjectDeclaration>()
        .forEach { childObjectDeclaration ->
            val id = childObjectDeclaration.name!!
            val newNode = DepsTreeNode(id)
            node.children[id] = newNode
            addNodes(childObjectDeclaration, newNode)
        }
}

private fun addProperties(ktObjectDeclaration: KtObjectDeclaration, node: DepsTreeNode) {
    ktObjectDeclaration.declarations
        .filterIsInstance<KtProperty>()
        .forEach { ktProperty ->
            val id = ktProperty.name!!
            val value = ktProperty.children
                .firstIsInstanceOrNull<KtStringTemplateExpression>()
                ?.text
                ?.trim('\"')
            if (value != null) {
                val newNode = DepsTreeNode(id)
                newNode.children[value] = DepsTreeNode(value)
                node.children[id] = newNode
            }
        }
}
