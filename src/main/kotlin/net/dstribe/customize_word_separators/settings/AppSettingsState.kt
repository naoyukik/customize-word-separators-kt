package net.dstribe.customize_word_separators.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project


@State(
    name = "CustomizeWordSeparatorsState",
    storages = [Storage("CustomizeWordSeparatorsState.xml")]
)
class AppSettingsState(val project: Project) : PersistentStateComponent<AppSettingsState.State> {
    val myState = State()

    class State {
        var customPattern1: String = ""
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): AppSettingsState {
            return project.service()
        }
    }

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState.customPattern1 = state.customPattern1.trim()
    }
}