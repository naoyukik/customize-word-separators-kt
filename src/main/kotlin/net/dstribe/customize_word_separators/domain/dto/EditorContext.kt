package net.dstribe.customize_word_separators.domain.dto

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor

data class EditorContext(
    val editor: Editor,
    val caret: Caret,
    val dataContext: DataContext?
)
