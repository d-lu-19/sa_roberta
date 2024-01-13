package com.github.dlu19.saroberta.actions
import com.github.dlu19.saroberta.listeners.FileHolder
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import java.io.File

class RunMenuAction () : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val fileEditorManager = e.project?.let { FileEditorManager.getInstance(it) }
        val currentFile = fileEditorManager?.selectedFiles?.firstOrNull()

        if (currentFile != null) {
            // Add the current file to FileHolder
            FileHolder.selectedFiles = listOf(File(currentFile.path))

            if (currentFile?.let { fileEditorManager.isFileOpen(it) } == true) {
                // Close the file if it's already open
                currentFile?.let { fileEditorManager.closeFile(it) }
            }

            // Open the file (this will reopen it if it was already open)
            currentFile?.let { fileEditorManager.openFile(it, true) }

//            val psiManager = PsiManager.getInstance(e.project!!)
//            val psiFile = currentFile?.let { psiManager.findFile(it) }
//            if (psiFile != null) {
//                DaemonCodeAnalyzer.getInstance(e.project).restart(psiFile)
//            }

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

