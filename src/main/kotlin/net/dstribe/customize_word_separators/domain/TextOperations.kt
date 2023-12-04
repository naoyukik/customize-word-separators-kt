package net.dstribe.customize_word_separators.domain

class TextOperations {
    fun getWordLength(isNext: Boolean, matchList: List<String>): Int {
        val matchSize = matchList.size
        val position = if (isNext) 0 else matchSize - 1
        val orientation = if (isNext) 1 else -1
        return matchList[position].length * orientation
    }

    fun getTextLength(textRangeStartOffset: Int, textRangeEndOffset: Int): Int {
        return textRangeEndOffset - textRangeStartOffset
    }
}
