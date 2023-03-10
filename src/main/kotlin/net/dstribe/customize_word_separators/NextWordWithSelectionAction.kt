package net.dstribe.customize_word_separators

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler

class NextWordWithSelectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val actionHandler: EditorActionHandler = NextPrevWordHandler(
            myNext = true,
            myWithSelection = true,
            e = e
        )
        actionHandler.execute(editor, null, e.dataContext)
    }
}