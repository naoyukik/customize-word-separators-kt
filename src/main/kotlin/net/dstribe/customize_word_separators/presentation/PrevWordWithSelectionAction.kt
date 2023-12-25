package net.dstribe.customize_word_separators.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import net.dstribe.customize_word_separators.application.MoveCaretWordForTextAreaService
import net.dstribe.customize_word_separators.application.MoveCaretWordForTextFieldService
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import javax.swing.JTextArea
import javax.swing.JTextField

class PrevWordWithSelectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val actionOptions = ActionOptions(
            isNext = false,
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
            val context = e.getData(PlatformDataKeys.CONTEXT_COMPONENT)
            when (context) {
                is JTextField -> {
                    MoveCaretWordForTextFieldService().moveCaretWordForTextField(actionOptions, e)
                }
                is JTextArea -> {
                    MoveCaretWordForTextAreaService().moveCaretWordForTextArea(actionOptions, e)
                }
            }
        }
    }
}
