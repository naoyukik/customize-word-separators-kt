package net.dstribe.customize_word_separators.domain

import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import javax.swing.JTextArea

class MoveCaretAtLineBoundariesCommand(
    private val actionOptions: ActionOptions,
    private val component: JTextArea,
    private val currentCaretPosition: Int
) {
    fun execute(): Boolean {
        val currentLineNumber = component.getLineOfOffset(currentCaretPosition)
        val currentLineLastCharOffset = component.getLineEndOffset(currentLineNumber) - 1
        val currentLineStartCharOffset = component.getLineStartOffset(currentLineNumber)

        // If at BOL/EOL, move one character
        if (actionOptions.isNext && currentCaretPosition == currentLineLastCharOffset) {
            val lineStartOffset = component.getLineStartOffset(currentLineNumber + 1)
            if (actionOptions.isWithSelection) {
                component.select(component.selectionStart, lineStartOffset)
            } else {
                component.caretPosition = lineStartOffset
            }
            return true
        } else if (!actionOptions.isNext && currentCaretPosition == currentLineStartCharOffset) {
            if (currentLineNumber == 0) return true
            val lineStartOffset = component.getLineEndOffset(currentLineNumber - 1) - 1
            if (actionOptions.isWithSelection) {
                component.moveCaretPosition(lineStartOffset)
            } else {
                component.caretPosition = lineStartOffset
            }
            return true
        }
        return false
    }
}
