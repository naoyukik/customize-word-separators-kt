package net.dstribe.customize_word_separators

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler

class PrevWordWithSelectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val isNext = false
        val isWithSelection = true
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        editor?.let {
            val actionHandler: EditorActionHandler = NextPrevWordHandler(
                    myNext = isNext,
                    myWithSelection = isWithSelection,
                    e = e
            )
            actionHandler.execute(it, null, e.dataContext)
        } ?: run {
            val moveCaretWordUtil = MoveCaretWordUtil()
            moveCaretWordUtil.moveCaretWordForTextField(isNext, isWithSelection, e)
        }
    }
}
