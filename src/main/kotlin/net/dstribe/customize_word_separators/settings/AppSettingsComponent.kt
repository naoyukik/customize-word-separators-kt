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
    private var mainPanel: JPanel? = null
    private val customizedPatterns = JBTextArea(25, 10)

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

    fun setCustomizedPatterns(newText: String) {
        customizedPatterns.text = newText
    }
}