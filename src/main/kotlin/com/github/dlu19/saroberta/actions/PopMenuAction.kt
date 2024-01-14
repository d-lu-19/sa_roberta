package com.github.dlu19.saroberta.actions
import com.github.dlu19.saroberta.listeners.FileHolder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import java.io.File

// Action triggered from EditorPopMenu, extends the action for sentiment analysis
class PopMenuAction () : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val fileEditorManager = e.project?.let { FileEditorManager.getInstance(it) }
        val currentFile = fileEditorManager?.selectedFiles?.firstOrNull()

        if (currentFile != null) {
            // Add the current file to FileHolder
            FileHolder.selectedFiles = listOf(File(currentFile.path))

            // Reload the file to trigger InlayHintProvider
            if (currentFile?.let { fileEditorManager.isFileOpen(it) } == true) {
                currentFile?.let { fileEditorManager.closeFile(it) } // Close the file if it's already open
            }
            currentFile?.let { fileEditorManager.openFile(it, true) }

            val sentimentAnalysisAction = SentimentAnalysisAction()
            sentimentAnalysisAction.actionPerformed(e)
        } else {
            // Handle the case when no file is currently open
            Messages.showMessageDialog(
                e.project,
                "No file selected.",
                "Information",
                Messages.getInformationIcon()
            )
        }
    }
}

