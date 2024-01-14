package com.github.dlu19.saroberta.listeners

import com.github.dlu19.saroberta.actions.SentimentAnalysisAction
import com.intellij.ide.DataManager
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JFileChooser
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import javax.swing.JPanel
import java.io.File
import javax.swing.ImageIcon


// Create a FileHolder to store selected files
object FileHolder {
    var selectedFiles: List<File>? = null
}

class FileSelectionListener(
    private val fileChooser: JFileChooser,
    private val panel: JPanel,
    private val project: Project
) : ActionListener {

    override fun actionPerformed(e: ActionEvent) {
        // Check if file selection is done
        if (e.actionCommand == JFileChooser.APPROVE_SELECTION) {
            val selectedFiles = fileChooser.selectedFiles.toList()
            FileHolder.selectedFiles = selectedFiles

            // Trigger a confirmation dialogue upon selection
            val selectedFilePaths = selectedFiles.joinToString(separator = "\n") { it.absolutePath }
            val confirmation = Messages.showOkCancelDialog(
                panel,
                "You selected the following files:\n\n$selectedFilePaths\n\nDo you want to perform sentiment analysis on the selected file?",
                "Confirmation",
                "OK",
                "Cancel",
                ImageIcon(javaClass.getResource("/icons/thinking.png"))
            )

            // Open the selected files in the editor and perform sentiment analysis
            if (confirmation == Messages.OK) {
                val fileEditorManager = FileEditorManager.getInstance(project)
                for (file in selectedFiles) {
                    val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)

                    if (virtualFile?.let { fileEditorManager.isFileOpen(it) } == true) {
                        // Close the file if it's already open
                        virtualFile?.let { fileEditorManager.closeFile(it) }
                    }

                    // Open the file (this will reopen it if it was already open)
                    virtualFile?.let { fileEditorManager.openFile(it, true) }
                }

                val sentimentAnalysisAction = SentimentAnalysisAction()
                val event = AnActionEvent.createFromDataContext(
                    "SentimentAnalysisAction",
                    null,
                    DataManager.getInstance().getDataContext(panel)
                )
                sentimentAnalysisAction.actionPerformed(event)
            }
        }
    }
}
