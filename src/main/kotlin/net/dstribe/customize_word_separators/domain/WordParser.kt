package net.dstribe.customize_word_separators.domain

import net.dstribe.customize_word_separators.settings.AppSettingsState

class WordParser(private val state: AppSettingsState?) {
    private var userCustomPatterns: String? = null

    companion object {
        const val COUNT_PATTERN = 26
    }

    init {
        userCustomPatterns = state?.myState?.customPattern1?.trim()
    }

    fun wordParse(character: String): List<String> {
        val pattern = (getUserPatterns().values + getDefaultPatterns().values).joinToString("|")
        val matchedResults = Regex(pattern).findAll(character)
        return matchedResults.map { it.value }.toList()
    }

    @Suppress("MaxLineLength")
    private fun getDefaultPatterns(): LinkedHashMap<String, String> {
        val patterns = LinkedHashMap<String, String>(WordParser.COUNT_PATTERN)
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
        val tmpUserCustomPatterns = userCustomPatterns
        if (tmpUserCustomPatterns.isNullOrEmpty()) {
            return patternMap
        }

        var items = emptyArray<String>()
        val lines = tmpUserCustomPatterns.split("\n").toTypedArray()
        for (line in lines) {
            items = line.split(",").toTypedArray()
            patternMap[items[0]] = items[1].trim()
        }

        return patternMap
    }
}
