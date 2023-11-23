package net.dstribe.customize_word_separators

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import net.dstribe.customize_word_separators.application.MoveCaretWordService

class NextPrevWordHandler(
    private val isNext: Boolean,
    private val isWithSelection: Boolean,
    val e: AnActionEvent
) : EditorActionHandler(true) {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (caret != null) {
            val moveCaretWordService = MoveCaretWordService()
            moveCaretWordService.moveCaretWord(
                editor,
                caret,
                isNext,
                isWithSelection,
                dataContext
            )
        }
    }
}
