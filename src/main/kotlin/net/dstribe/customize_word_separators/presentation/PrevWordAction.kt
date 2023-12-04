package net.dstribe.customize_word_separators.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import net.dstribe.customize_word_separators.application.MoveCaretWordForTextFieldService
import net.dstribe.customize_word_separators.domain.dto.ActionOptions

class PrevWordAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val actionOptions = ActionOptions(
            isNext = false,
            isWithSelection = false
        )
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        editor?.let {
            val actionHandler: EditorActionHandler = NextPrevWordEditorActionHandler(
                actionOptions = actionOptions,
                e = e
            )
            actionHandler.execute(it, null, e.dataContext)
        } ?: run {
            MoveCaretWordForTextFieldService().moveCaretWordForTextField(actionOptions, e)
        }
    }
}
