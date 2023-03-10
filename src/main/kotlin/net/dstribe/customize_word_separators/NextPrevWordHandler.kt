package net.dstribe.customize_word_separators

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler

class NextPrevWordHandler(
    private var myNext: Boolean,
    private var myWithSelection: Boolean,
    val e: AnActionEvent
) : EditorActionHandler(true) {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (caret != null) {
            val moveCaretWordUtil = MoveCaretWordUtil()
            moveCaretWordUtil.moveCaretWord(
                editor,
                caret,
                myNext,
                myWithSelection,
                dataContext,
                false
            )
        }
    }
}
