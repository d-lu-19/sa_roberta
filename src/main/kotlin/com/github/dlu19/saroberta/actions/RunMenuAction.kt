package com.github.dlu19.saroberta.actions

import com.github.dlu19.saroberta.listeners.FileHolder
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File

class RunMenuActionListener(private val project: Project) : ActionListener {

    override fun actionPerformed(e: ActionEvent) {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val currentFile = fileEditorManager.selectedFiles.firstOrNull()

        if (currentFile != null) {
            // Add the current file to FileHolder
            FileHolder.selectedFiles = listOf(File(currentFile.path))
        } else {
            // Handle the case when no file is currently open
            Messages.showMessageDialog(
                project,
                "No file selected.",
                "Information",
                Messages.getInformationIcon()
            )
        }
    }
}

