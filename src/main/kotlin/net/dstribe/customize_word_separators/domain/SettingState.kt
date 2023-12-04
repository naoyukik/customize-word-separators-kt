package net.dstribe.customize_word_separators.domain

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import net.dstribe.customize_word_separators.settings.AppSettingsState

class SettingState {
    fun getAppSettingsState(editor: Editor): AppSettingsState? {
        return editor.project?.let { AppSettingsState.getInstance(it) }
    }

    fun getAppSettingsState(e: AnActionEvent): AppSettingsState? {
        return e.project?.service<AppSettingsState>()
    }
}
