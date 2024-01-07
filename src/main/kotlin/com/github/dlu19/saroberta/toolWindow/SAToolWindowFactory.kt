package com.github.dlu19.saroberta.toolWindow

import com.github.dlu19.saroberta.actions.SentimentAnalysisAction
import com.github.dlu19.saroberta.toolWindow.FileHolder.selectedFiles
import com.intellij.ide.DataManager
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.toNioPath
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import okio.Path.Companion.toPath
import javax.swing.*
import javax.swing.JComponent
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView


object FileHolder {
    var selectedFiles: List<File>? = null
}

class SAToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(createToolWindowPanel(project), "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolWindowPanel(project: Project): JComponent {

        thisLogger().info("Initializing a tool window...")
        val panel = JPanel()

        // Create and configure JFileChooser
        val projectBasePath = project.basePath
        val projectDirFile = projectBasePath?.let { File(it) }

        val fileSystemView = object : FileSystemView() {
            override fun createNewFolder(containingDir: File): File {
                throw UnsupportedOperationException("Not supported.")
            }

            override fun getDefaultDirectory(): File = projectDirFile ?: super.getDefaultDirectory()

            override fun getHomeDirectory(): File = projectDirFile ?: super.getHomeDirectory()

            override fun getRoots(): Array<File> = arrayOf(projectDirFile ?: super.getHomeDirectory())

            override fun isRoot(f: File): Boolean = f == projectDirFile
        }

        // Create and configure JFileChooser with the custom FileSystemView
        val fileChooser = JFileChooser(fileSystemView)

        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.isMultiSelectionEnabled = true

        // Set the file filter to only show Kotlin (.kt) files
        val kotlinFileFilter = FileNameExtensionFilter("Kotlin Files (*.kt)", "kt")
        fileChooser.addChoosableFileFilter(kotlinFileFilter)
        fileChooser.fileFilter = kotlinFileFilter

        // Add file chooser to panel
        panel.add(fileChooser)

        // Open a file chooser under the current project when the tool window content is created

        fileChooser.addActionListener { event ->
            // Check if a file was selected
            if (event.actionCommand == JFileChooser.APPROVE_SELECTION) {
                val selectedFiles: List<File> = fileChooser.selectedFiles.toList()
                FileHolder.selectedFiles = selectedFiles

                val selectedFilePaths = selectedFiles.joinToString(separator = "\n") { it.absolutePath }
                // Construct the message with the selected file paths
                val message = "You selected the following files:\n\n$selectedFilePaths \nDo you want to perform sentiment analysis on the selected file?"

                val customIcon: Icon = ImageIcon(javaClass.getResource("/icons/icon.png"))
                val confirmation = Messages.showOkCancelDialog(
                    panel,
                    message,
                    "Confirmation",
                    "Perform Sentiment Analysis",
                    "Cancel",
                    customIcon
                )

                // Perform Sentiment Analysis actions with the selected files
                if (confirmation == Messages.OK) {
                    // Call the SentimentAnalysisAction.kt here or perform sentiment analysis as needed

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

        return panel
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}


