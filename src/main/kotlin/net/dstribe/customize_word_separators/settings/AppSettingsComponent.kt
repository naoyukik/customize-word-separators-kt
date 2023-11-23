package net.dstribe.customize_word_separators.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    companion object {
        private const val TEXT_AREA_ROWS = 5
        private const val TEXT_AREA_COLUMNS = 30
    }

    private var mainPanel: JPanel? = null
    private val customizedPatterns = JBTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS)

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Patterns: "), customizedPatterns, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel? {
        return mainPanel
    }

    fun getPreferredFocusedComponent(): JComponent? {
        return customizedPatterns
    }

    fun getCustomizedPatterns(): String {
        return customizedPatterns.text
    }

    fun setCustomizedPatterns(newText: String?) {
        customizedPatterns.text = newText
    }
}
