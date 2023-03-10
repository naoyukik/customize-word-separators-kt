package net.dstribe.customize_word_separators.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


@State(
    name = "CustomizeWordSeparatorsState",
    storages = [Storage("CustomizeWordSeparatorsState.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    public var customPattern1: String = "John Q. Public"
    public var ideaStatus: Boolean = false

    companion object {
        @JvmStatic
        fun getInstance(): AppSettingsState {
            return ApplicationManager.getApplication().getService(AppSettingsState::class.java)
        }
    }

    override fun getState(): AppSettingsState? {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}