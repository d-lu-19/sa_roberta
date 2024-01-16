package com.github.dlu19.saroberta.actions
import com.github.dlu19.saroberta.listeners.FileHolder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import java.io.File
import javax.swing.JOptionPane

// Action triggered from EditorPopMenu, extends the action for sentiment analysis
class PopMenuAction () : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val fileEditorManager = e.project?.let { FileEditorManager.getInstance(it) }
        val currentFile = fileEditorManager?.selectedFiles?.firstOrNull()

        if (currentFile != null) {
            val filePath = currentFile.path
            if (filePath.endsWith(".kt", ignoreCase = true)) {
                // It's a Kotlin file
                FileHolder.selectedFiles = listOf(File(filePath))
                val sentimentAnalysisAction = SentimentAnalysisAction()
                sentimentAnalysisAction.actionPerformed(e)
            } else {
                // It's not a Kotlin file, inform the user
                JOptionPane.showMessageDialog(
                    null,
                    "The selected file is not a Kotlin file.",
                    "File Type Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }

        } else {
            // Handle the case when no file is currently open
            JOptionPane.showMessageDialog(
                null,
                "No file selected.",
                "Information",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
}

