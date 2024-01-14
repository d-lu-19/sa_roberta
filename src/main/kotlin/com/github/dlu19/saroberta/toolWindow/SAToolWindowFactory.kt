package com.github.dlu19.saroberta.toolWindow

import com.github.dlu19.saroberta.listeners.FileSelectionListener
import com.github.dlu19.saroberta.listeners.FileTableMapListener
import com.github.dlu19.saroberta.services.MapParser
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.JComponent
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView

class SAToolWindowFactory : ToolWindowFactory, FileTableMapListener {

    private lateinit var tabbedPane: JBTabbedPane
    private var project: Project? = null
    private var loaderService: Loader? = null


    // Display content to the tool window
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        this.project = project
        this.loaderService = Loader()
        this.tabbedPane = JBTabbedPane()

        MapParser.setListener(this)

        val panel = createToolWindowPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    // Refresh the result table each time results of sentiment analysis are retrieved
    override fun onFileTableMapUpdated() {
        loadTabbedPane()
    }

    // Load sentiment analysis results into tables, each tab for a selected file
    private fun loadTabbedPane() {
        tabbedPane.removeAll()
        loaderService?.let { loader ->
            MapParser.getFileTableMap().forEach { (filename, model) ->
                model?.let {
                    val scrollpane = loader.scrollpaneLoader(it)
                    tabbedPane.addTab(filename, scrollpane)
                }
            }
        }
    }

    // Create a file chooser
    private fun createFileChooser(panel: JPanel, project: Project): JFileChooser {
        // Create and define the default root for FileChooser as project root
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
        val fileChooser = JFileChooser(fileSystemView)

        // Configure the size and file selection option of FileChooser
        val fileChooserHeight = fileChooser.preferredSize.height
        fileChooser.preferredSize = Dimension(panel.preferredSize.width, fileChooserHeight)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.isMultiSelectionEnabled = true

        // Set the file filter to only show Kotlin (.kt) files
        val kotlinFileFilter = FileNameExtensionFilter("Kotlin Files (*.kt)", "kt")
        fileChooser.addChoosableFileFilter(kotlinFileFilter)
        fileChooser.fileFilter = kotlinFileFilter

        fileChooser.addActionListener(FileSelectionListener(fileChooser, panel, project))

        return fileChooser
    }

    // Add main UI components to the tool window
    private fun createToolWindowPanel(project: Project): JComponent? {

        // Initializing panel for the plugin
        val panel = JPanel(BorderLayout())
        panel.add(createFileChooser(panel, project), BorderLayout.NORTH)
        panel.add(tabbedPane, BorderLayout.CENTER)

        return panel

    }



}




