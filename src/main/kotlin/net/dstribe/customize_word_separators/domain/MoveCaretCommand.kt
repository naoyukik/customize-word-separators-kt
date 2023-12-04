package net.dstribe.customize_word_separators.domain

import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.EditorModificationUtil
import net.dstribe.customize_word_separators.domain.dto.EditorContext

class MoveCaretCommand(
    private val context: EditorContext,
    private val newOffset: Int,
    private val isWithSelection: Boolean,
    private val selectionStart: Int
) {
    fun execute() {
        context.caret.moveToOffset(newOffset)
        EditorModificationUtil.scrollToCaret(context.editor)
        setupSelection(context.caret, isWithSelection, selectionStart)
    }

    private fun setupSelection(caret: Caret, isWithSelection: Boolean, startOffset: Int) {
        if (isWithSelection) {
            caret.setSelection(startOffset, caret.offset, true)
        } else {
            caret.removeSelection()
        }
    }
}
