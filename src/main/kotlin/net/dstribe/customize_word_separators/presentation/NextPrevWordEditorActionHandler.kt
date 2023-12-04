package net.dstribe.customize_word_separators.presentation

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import net.dstribe.customize_word_separators.application.MoveCaretWordService
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import net.dstribe.customize_word_separators.domain.dto.EditorContext

class NextPrevWordEditorActionHandler(
    val actionOptions: ActionOptions,
    val e: AnActionEvent
) : EditorActionHandler(true) {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (caret != null) {
            // val moveCaretWordService = MoveCaretWordService()
            MoveCaretWordService().moveCaretWord(
                editorContext = EditorContext(
                    caret = caret,
                    editor = editor,
                    dataContext = dataContext
                ),
                actionOptions = actionOptions
            )
        }
    }
}
