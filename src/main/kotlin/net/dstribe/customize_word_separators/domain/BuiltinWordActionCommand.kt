package net.dstribe.customize_word_separators.domain

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import net.dstribe.customize_word_separators.domain.dto.ActionOptions
import net.dstribe.customize_word_separators.domain.dto.EditorContext

class BuiltinWordActionCommand(
    private val editorContext: EditorContext,
    private val actionOptions: ActionOptions,
) {
    fun execute() {
        val (isNext, isWithSelection) = actionOptions
        val actionManager = EditorActionManager.getInstance()

        var ideAction = IdeActions.ACTION_EDITOR_NEXT_WORD
        if (isWithSelection && !isNext) {
            ideAction = IdeActions.ACTION_EDITOR_PREVIOUS_WORD_WITH_SELECTION
        } else if (!isNext) {
            ideAction = IdeActions.ACTION_EDITOR_PREVIOUS_WORD
        } else if (isWithSelection) {
            ideAction = IdeActions.ACTION_EDITOR_NEXT_WORD_WITH_SELECTION
        }
        val actionHandler = actionManager.getActionHandler(ideAction)
        actionHandler.execute(
            editorContext.editor,
            editorContext.caret,
            editorContext.dataContext
        )
    }
}
