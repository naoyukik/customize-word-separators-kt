package net.dstribe.customize_word_separators.application

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import net.dstribe.customize_word_separators.domain.SettingState
import net.dstribe.customize_word_separators.domain.TextOperations
import net.dstribe.customize_word_separators.domain.WordParser
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import net.dstribe.customize_word_separators.settings.AppSettingsState
import javax.swing.JTextField

class MoveCaretWordForTextFieldService {
    private var state: AppSettingsState? = null

    fun moveCaretWordForTextField(
        actionOptions: ActionOptions,
        e: AnActionEvent
    ) {
        state = SettingState().getAppSettingsState(e)

        val component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT)
        if (component !is JTextField) return

        val currentCaretPosition = component.caretPosition
        val textRangeStartOffset = 0

        val (isNext, isWithSelection) = actionOptions
        val textLength = if (isNext) {
            component.getText().length
        } else {
            TextOperations().getTextLength(textRangeStartOffset, currentCaretPosition)
        }
        val lineText = if (isNext) {
            component.getText(
                currentCaretPosition,
                textLength - currentCaretPosition
            )
        } else {
            component.getText(textRangeStartOffset, textLength)
        }

        if (currentCaretPosition <= -1) return

        val matchList = WordParser(state).wordParse(lineText)
        if (matchList.isEmpty()) return

        val wordLength = TextOperations().getWordLength(isNext, matchList)
        val movedCaretPosition = currentCaretPosition + wordLength

        if (isWithSelection) {
            if (isNext) {
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
