package net.dstribe.customize_word_separators

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.util.TextRange
import net.dstribe.customize_word_separators.settings.AppSettingsState
import kotlin.collections.set


class MoveCaretWordUtil {
    private var state: AppSettingsState? = null

    fun moveCaretWord(
        editor: Editor,
        caret: Caret,
        isNext: Boolean,
        isWithSelection: Boolean,
        dataContext: DataContext?,
        camel: Boolean?
    ) {
        val document: Document = editor.document
        val logicalPos = caret.logicalPosition
        val currentCaretOffset: Int = caret.offset
        val selectionStart: Int = caret.leadSelectionOffset

        state = editor.project?.let { AppSettingsState.getInstance() }

        if (isNext && currentCaretOffset == document.textLength) return

        val newOffset: Int

        val currentFoldRegion: FoldRegion? =
            editor.foldingModel.getCollapsedRegionAtOffset(currentCaretOffset)
        if (currentFoldRegion != null) {
            newOffset = currentFoldRegion.endOffset
        } else {
            val startLineOffset: Int = document.getLineStartOffset(logicalPos.line)
            val currentLineNumber: Int = logicalPos.line
            val endLineOffset: Int = document.getLineEndOffset(currentLineNumber)
            if (currentLineNumber >= document.lineCount) return

            // Preparation get words
            var moveRange = 1
            var textRangeStartOffset: Int = currentCaretOffset
            var textRangeEndOffset: Int = endLineOffset
            var boundaryOffset: Int = textRangeEndOffset
            if (!isNext) {
                moveRange = -1
                textRangeStartOffset = startLineOffset
                textRangeEndOffset = currentCaretOffset
                boundaryOffset = textRangeStartOffset
            }

            val documentLength = document.textLength
            if (currentCaretOffset < 0 || currentCaretOffset > documentLength) return

            /* If at BOL/EOL, move one character */
            if (currentCaretOffset == boundaryOffset) {
                caret.moveToOffset(currentCaretOffset + moveRange)
                EditorModificationUtil.scrollToCaret(editor)
                setupSelection(caret, isWithSelection, selectionStart)
                return
            }

            val textLength = getTextLength(textRangeStartOffset, textRangeEndOffset)
            val textRange = TextRange.from(textRangeStartOffset, textLength)
            val lineText = document.getText(textRange)

            val matchList = wordParse(lineText)
            if (matchList.isEmpty()) {
                if (dataContext != null) {
                    useBuiltinWordAction(editor, caret, isNext, isWithSelection, dataContext)
                }
                return
            }
            val wordLength = getWordLength(isNext, matchList)
            newOffset = currentCaretOffset + wordLength
        }

        caret.moveToOffset(newOffset)
        EditorModificationUtil.scrollToCaret(editor)
        setupSelection(caret, isWithSelection, selectionStart)
    }

    private fun getWordLength(isNext: Boolean, matchList: List<String>): Int {
        val matchSize = matchList.size
        var position = 0
        var orientation = 1
        if (!isNext) {
            position = matchSize - 1
            orientation = -1
        }
        val lastWord = matchList[position]
        return lastWord.length * orientation

    }

    private fun useBuiltinWordAction(
        editor: Editor,
        caret: Caret,
        isNext: Boolean,
        isWithSelection: Boolean,
        dataContext: DataContext
    ) {
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
        actionHandler.execute(editor, caret, dataContext)
    }

    private fun wordParse(character: String): List<String> {
        val result = ArrayList<String>()
        val userPatterns = ArrayList<String>(getUserPatterns().values)
        val defaultPatterns = ArrayList<String>(getDefaultPatterns().values)
        val pattern = userPatterns.plus(defaultPatterns).joinToString("|")
        val matchedResults = Regex(pattern).findAll(character)
        for (matchedText in matchedResults) {
            result.add(matchedText.value)
        }

        return result
    }

    private fun getDefaultPatterns(): LinkedHashMap<String, String> {
        val patterns = LinkedHashMap<String, String>(26)
        patterns["cjk"] = "[\\u3400-\\u9FFF\\uF900-\\uFAFF]+"
        patterns["hiragana"] = "[\\u3040-\\u309F]+"
        patterns["katakana"] = "[\\u30A1-\\u30FA\\u30FC-\\u30FE]+"
        patterns["kanaSymbol"] = "[\\u30A0\\u30FB]+"
        patterns["cjkSymbol"] =
            "[\\u2190-\\u2193\\u25A0\\u25A1\\u25B2\\u25B3\\u25BC\\u25BD\\u25C6\\u25C7\\u25CB\\u25CE\\u25CF\\u2605\\u2606\\u3000-\\u3020]+"
        patterns["fullDigit"] = "[\\uFF10-\\uFF19]+"
        patterns["fullLatin"] = "[\\uFF21-\\uFF3A\\uFF41-\\uFF5A]+"
        patterns["halfCjkPunctuation"] = "[\\uFF61-\\uFF65]+"
        patterns["halfKatakana"] = "[\\uFF66-\\uFF9F]+"
        patterns["fullSymbol"] =
            "[\\uFF01-\\uFF0F\\uFF1A-\\uFF20\\uFF3B-\\uFF40\\uFF5B-\\uFF60\\uFFE0-\\uFFE6\\u005C\\u00A2\\u00A3\\u00A7\\u00A8\\u00AC\\u00B0\\u00B1\\u00B4\\u00B6\\u00D7\\u00F7\\u2010\\u2015\\u2016\\u2018\\u2019\\u201C\\u201D\\u2020\\u2021\\u2025\\u2026\\u2030\\u2032\\u2033\\u203B\\u2103]+"
        patterns["halfSymbol"] = "[\\uFFE8-\\uFFEE]+"
        patterns["latin"] = "[\\u0030-\\u0039\\u0041-\\u005A\\u0061-\\u007A]+"
        patterns["latinSymbol"] =
            "[\\u0020-\\u0021\\u0023\\u0025-\\u0026\\u002A-\\u002F\\u003A-\\u003B\\u003D\\u003F-\\u0040\\u005C\\u005E\\u0060\\u00A5\\u007C\\u007E\\u203E]+"
        patterns["czeroControls"] = "[\\u0000-\\u0009\\u000B\\u000C\\u000E-\\u001F]+"
        patterns["controlCharacters"] = "[\\u000A\\u000D]+"
        patterns["\""] = "\\u0022"
        patterns["'"] = "\\u0027"
        patterns["("] = "\\u0028"
        patterns[")"] = "\\u0029"
        patterns["<"] = "\\u003C"
        patterns[">"] = "\\u003E"
        patterns["["] = "\\u005B"
        patterns["]"] = "\\u005D"
        patterns["{"] = "\\u007B"
        patterns["}"] = "\\u007D"
        patterns["ideaSigns"] = "[\\u0024\\u005F]+"

        return patterns
    }

    private fun getUserPatterns(): LinkedHashMap<String, String> {
        val patternMap = LinkedHashMap<String, String>()
//        val userPatterns = state?.myState?.customPattern1
        val userPatterns: String? = state?.customPattern1
        if (userPatterns.isNullOrEmpty()) {
            return patternMap
        }
        var items: Array<String>
        val lines: Array<String> = userPatterns.split("\n").toTypedArray()
        for (line: String in lines) {
            items = line.split(",").toTypedArray()
            patternMap[items[0]] = items[1].trim()
        }

        return patternMap
    }

    private fun getTextLength(textRangeStartOffset: Int, textRangeEndOffset: Int): Int {
        return textRangeEndOffset - textRangeStartOffset
    }

    private fun setupSelection(caret: Caret, isWithSelection: Boolean, startOffset: Int) {
        if (isWithSelection) {
            caret.setSelection(startOffset, caret.offset, true)
        } else {
            caret.removeSelection()
        }
    }
}
