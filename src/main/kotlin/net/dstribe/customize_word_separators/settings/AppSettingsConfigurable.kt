package net.dstribe.customize_word_separators.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.rows
import org.jetbrains.annotations.Nls

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable(private val project: Project) : Configurable {
    private val mySettingsState
        get() = AppSettingsState.getInstance(project)

    private val mainPanel: DialogPanel by lazy { createUIComponents() }

    companion object {
        private const val TEXT_AREA_ROWS = 20
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Customize Word Separators"
    }

    override fun createComponent(): DialogPanel {
        return mainPanel
    }

    override fun isModified(): Boolean {
        return mainPanel.isModified()
    }

    override fun reset() {
        mainPanel.reset()
    }

    override fun apply() {
        mainPanel.apply()
    }

    private fun createUIComponents(): DialogPanel {
        val state = mySettingsState ?: return panel {}
        return panel {
            row("Patterns: ") {
                textArea()
                    .bindText(state::customizedPattern)
                    .align(AlignX.FILL)
                    .rows(TEXT_AREA_ROWS)
            }
        }
    }
}
