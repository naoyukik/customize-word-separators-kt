package net.dstribe.customize_word_separators.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.*


/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable : Configurable {

    private var mySettingsComponent: AppSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Customize Word Separators"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent!!.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings: AppSettingsState = AppSettingsState.getInstance()
        return mySettingsComponent!!.getCustomizedPatterns() != settings.customPattern1
    }

    override fun apply() {
        val settings: AppSettingsState = AppSettingsState.getInstance()
        settings.customPattern1 = mySettingsComponent!!.getCustomizedPatterns()
    }

    override fun reset() {
        val settings: AppSettingsState = AppSettingsState.getInstance()
        mySettingsComponent!!.setCustomizedPatterns(settings.customPattern1)
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}