package net.dstribe.customize_word_separators.domain

import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import javax.swing.text.JTextComponent

class MoveCaretForTextCommand(
    private val movedCaretPosition: Int,
    private val actionOptions: ActionOptions,
    private val component: JTextComponent
) {
    fun execute() {
        if (actionOptions.isWithSelection) {
            if (actionOptions.isNext) {
                component.select(component.selectionStart, movedCaretPosition)
            } else {
                component.setCaretPosition(component.selectionEnd)
                component.moveCaretPosition(movedCaretPosition)
            }
        } else {
            component.caretPosition = movedCaretPosition
        }
    }
}
