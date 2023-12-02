package net.dstribe.customize_word_separators.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import net.dstribe.customize_word_separators.application.MoveCaretWordService
import net.dstribe.customize_word_separators.domain.dto.ActionOptions

class NextWordWithSelectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val actionOptions = ActionOptions(
            isNext = true,
            isWithSelection = true
        )
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        editor?.let {
            val actionHandler: EditorActionHandler = NextPrevWordEditorActionHandler(
                actionOptions = actionOptions,
                e = e
            )
            actionHandler.execute(it, null, e.dataContext)
        } ?: run {
            val moveCaretWordService = MoveCaretWordService()
            moveCaretWordService.moveCaretWordForTextField(actionOptions, e)
        }
    }
}
