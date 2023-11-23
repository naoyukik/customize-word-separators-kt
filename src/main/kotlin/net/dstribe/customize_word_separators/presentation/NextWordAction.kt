package net.dstribe.customize_word_separators.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import net.dstribe.customize_word_separators.application.MoveCaretWordService
import net.dstribe.customize_word_separators.NextPrevWordHandler

class NextWordAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val isNext = true
        val isWithSelection = false
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        editor?.let {
            val actionHandler: EditorActionHandler = NextPrevWordHandler(
                myNext = isNext,
                myWithSelection = isWithSelection,
                e = e
            )
            actionHandler.execute(it, null, e.dataContext)
        } ?: run {
            val moveCaretWordService = MoveCaretWordService()
            moveCaretWordService.moveCaretWordForTextField(isNext, isWithSelection, e)
        }
    }
}