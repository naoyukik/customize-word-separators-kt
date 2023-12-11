package net.dstribe.customize_word_separators.application

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import net.dstribe.customize_word_separators.domain.MoveCaretAtLineBoundariesCommand
import net.dstribe.customize_word_separators.domain.MoveCaretForTextCommand
import net.dstribe.customize_word_separators.domain.SettingState
import net.dstribe.customize_word_separators.domain.TextOperations
import net.dstribe.customize_word_separators.domain.WordParser
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import net.dstribe.customize_word_separators.settings.AppSettingsState
import javax.swing.JTextArea

class MoveCaretWordForTextAreaService {
    private var state: AppSettingsState? = null

    fun moveCaretWordForTextArea(
        actionOptions: ActionOptions,
        e: AnActionEvent
    ) {
        state = SettingState().getAppSettingsState(e)

        val component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT)
        if (component !is JTextArea) return

        val currentCaretPosition = component.caretPosition
        val textRangeStartOffset = 0

        val (isNext) = actionOptions
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

        val boundariesResult = MoveCaretAtLineBoundariesCommand(
            actionOptions,
            component,
            currentCaretPosition).execute()
        if (boundariesResult) return

        val matchList = WordParser(state).wordParse(lineText)
        if (matchList.isEmpty()) return

        val wordLength = TextOperations().getWordLength(isNext, matchList)
        val movedCaretPosition = currentCaretPosition + wordLength

        MoveCaretForTextCommand(
            movedCaretPosition,
            actionOptions,
            component
        ).execute()
    }
}
