package net.dstribe.customize_word_separators.application

import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.util.TextRange
import net.dstribe.customize_word_separators.domain.BuiltinWordActionCommand
import net.dstribe.customize_word_separators.domain.MoveCaretCommand
import net.dstribe.customize_word_separators.domain.SettingState
import net.dstribe.customize_word_separators.domain.TextOperations
import net.dstribe.customize_word_separators.domain.WordParser
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import net.dstribe.customize_word_separators.domain.dto.EditorContext
import net.dstribe.customize_word_separators.settings.AppSettingsState
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
                TextOperations().getTextLength(textRangeStartOffset, textRangeEndOffset)
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
            val wordLength = TextOperations().getWordLength(isNext, matchList)
            newOffset = currentCaretOffset + wordLength
        }

        MoveCaretCommand(
            editorContext,
            newOffset,
            isWithSelection,
            selectionStart
        ).execute()
    }
}
