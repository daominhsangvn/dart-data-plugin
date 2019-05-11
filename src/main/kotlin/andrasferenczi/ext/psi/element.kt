package andrasferenczi.ext.psi

import andrasferenczi.traversal.TraversalType
import andrasferenczi.traversal.createPsiFilterTraversal
import andrasferenczi.traversal.createPsiTraversal
import com.intellij.psi.PsiElement
import com.intellij.psi.util.siblings
import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.*

/**
 * Does not omit children that are PsiWhiteSpace or LeafPsiNode
 */
fun PsiElement.allChildren(): Sequence<PsiElement> {
    return firstChild?.siblings() ?: emptySequence()
}

fun PsiElement.calculateGlobalOffset(): Int {
    return this.startOffsetInParent + (this.parent?.startOffsetInParent ?: 0)
}

inline fun <reified T : PsiElement> PsiElement.findFirstParentOfType(): T? {
    var current: PsiElement? = this

    while (true) {
        if (current == null) {
            return null
        }

        if (current is T) {
            return current
        }

        current = current.parent
    }
}

inline fun <reified T : PsiElement> PsiElement.findFirstChildByType(
    traversalType: TraversalType = TraversalType.Breadth
): T? = this.findChildrenByType<T>(traversalType).firstOrNull()

inline fun <reified T : PsiElement> PsiElement.findChildrenByType(
    traversalType: TraversalType = TraversalType.Breadth
): Sequence<T> {
    return createPsiFilterTraversal(traversalType) {
        if (it is T) it
        else null
    }.invoke(this)
}

/**
 * Depth First Search
 *
 * action: returns 'false' if DFS should not go deeper into that element
 */
fun PsiElement.iterateDFS(action: (element: PsiElement) -> Boolean) {
    for (child in this.allChildren()) {
        if (action(child)) {
            child.iterateDFS(action)
        }
    }
}
