package net.dstribe.customize_word_separators.application

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.util.TextRange
import net.dstribe.customize_word_separators.domain.BuiltinWordActionCommand
import net.dstribe.customize_word_separators.domain.MoveCaretCommand
import net.dstribe.customize_word_separators.domain.SettingState
import net.dstribe.customize_word_separators.domain.WordParser
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import net.dstribe.customize_word_separators.domain.dto.EditorContext
import net.dstribe.customize_word_separators.settings.AppSettingsState
import javax.swing.JTextField
import kotlin.collections.set

class MoveCaretWordService {
    private var state: AppSettingsState? = null

    @Suppress("ReturnCount")
    fun moveCaretWord(
        editorContext: EditorContext,
        actionOptions: ActionOptions
    ) {
        val (editor, caret, dataContext) = editorContext
        val (isNext, isWithSelection) = actionOptions
        state = SettingState().getAppSettingsState(editor)

        val document = editor.document
        val currentCaretOffset = caret.offset

        if (isNext && currentCaretOffset == document.textLength) return

        val newOffset: Int

        val selectionStart = caret.leadSelectionOffset
        val currentFoldRegion: FoldRegion? =
            editor.foldingModel.getCollapsedRegionAtOffset(currentCaretOffset)
        if (currentFoldRegion != null) {
            newOffset = currentFoldRegion.endOffset
        } else {
            if (currentCaretOffset < 0 || currentCaretOffset > document.textLength) return

            val logicalPos = caret.logicalPosition

            val currentLineNumber = logicalPos.line
            if (currentLineNumber >= document.lineCount) return

            // Preparation get words
            val startLineOffset = document.getLineStartOffset(logicalPos.line)
            val textRangeStartOffset = if (isNext) currentCaretOffset else startLineOffset
            val textRangeEndOffset =
                if (isNext) document.getLineEndOffset(currentLineNumber) else currentCaretOffset
            val boundaryOffset = if (isNext) textRangeEndOffset else textRangeStartOffset

            /* If at BOL/EOL, move one character */
            if (currentCaretOffset == boundaryOffset) {
                newOffset = currentCaretOffset + (if (isNext) 1 else -1)
                MoveCaretCommand(
                    editorContext,
                    newOffset,
                    isWithSelection,
                    selectionStart
                ).execute()
                return
            }

            val textRange = TextRange.from(
                textRangeStartOffset,
                getTextLength(textRangeStartOffset, textRangeEndOffset)
            )
            val lineText = document.getText(textRange)

            val matchList = WordParser(state).wordParse(lineText)
            if (matchList.isEmpty()) {
                if (dataContext != null) {
                    BuiltinWordActionCommand(
                        editorContext,
                        actionOptions
                    ).execute()
                }
                return
            }
            val wordLength = getWordLength(isNext, matchList)
            newOffset = currentCaretOffset + wordLength
        }

        MoveCaretCommand(
            editorContext,
            newOffset,
            isWithSelection,
            selectionStart
        ).execute()
    }

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
        val textLength =
            if (isNext) component.getText().length else getTextLength(textRangeStartOffset, currentCaretPosition)
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

        val wordLength = getWordLength(isNext, matchList)
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

    private fun getWordLength(isNext: Boolean, matchList: List<String>): Int {
        val matchSize = matchList.size
        val position = if (isNext) 0 else matchSize - 1
        val orientation = if (isNext) 1 else -1
        return matchList[position].length * orientation
    }

    private fun useBuiltinWordAction(
        editorContext: EditorContext,
        actionOptions: ActionOptions,
    ) {
        val (isNext, isWithSelection) = actionOptions
        val actionManager = EditorActionManager.getInstance()

        var ideAction = IdeActions.ACTION_EDITOR_NEXT_WORD
        if (isWithSelection && !isNext) {
            ideAction = IdeActions.ACTION_EDITOR_PREVIOUS_WORD_WITH_SELECTION
        } else if (!isNext) {
            ideAction = IdeActions.ACTION_EDITOR_PREVIOUS_WORD
        } else if (isWithSelection) {
            ideAction = IdeActions.ACTION_EDITOR_NEXT_WORD_WITH_SELECTION
        }
        val actionHandler = actionManager.getActionHandler(ideAction)
        actionHandler.execute(
            editorContext.editor,
            editorContext.caret,
            editorContext.dataContext
        )
    }

    private fun getTextLength(textRangeStartOffset: Int, textRangeEndOffset: Int): Int {
        return textRangeEndOffset - textRangeStartOffset
    }
}
