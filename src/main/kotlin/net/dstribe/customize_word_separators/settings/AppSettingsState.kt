package net.dstribe.customize_word_separators.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "CustomizeWordSeparatorsState",
    storages = [Storage("CustomizeWordSeparatorsState.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState.State> {
    val myState = State()

    var customizedPattern
        get() = myState.customPattern1
        set(value) {
            myState.customPattern1 = value
        }

    class State {
        var customPattern1: String = ""
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): AppSettingsState? {
            return project.getService(AppSettingsState::class.java)
        }
    }

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState.customPattern1 = state.customPattern1.trim()
    }
}
