package com.github.dlu19.saroberta.actions


import com.github.dlu19.saroberta.listeners.Displayer
import com.github.dlu19.saroberta.services.Analyzer
import com.github.dlu19.saroberta.services.Tokenizer
import com.github.dlu19.saroberta.toolWindow.FileHolder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil


class SentimentAnalysisAction() : AnAction("Perform Sentiment Analysis") {

    init {
        templatePresentation.icon = IconLoader.getIcon("/icons/icon.png", SentimentAnalysisAction::class.java)
    }

    private fun getEditorInstance(virtualFile: VirtualFile, project: Project): Editor? {
        val fileEditor = FileEditorManager.getInstance(project).getEditors(virtualFile).firstOrNull()
        return if (fileEditor is TextEditor) {
            fileEditor.editor
        } else {
            null
        }
    }


    override fun actionPerformed(e: AnActionEvent) {
        val selectedFiles = FileHolder.selectedFiles
        val psiManager = e.project?.let { PsiManager.getInstance(it) }

        thisLogger().info("Start performing Sentiment Analysis Action...")

        // Extract comments of each selected file
        if (selectedFiles != null) {
            for (file in selectedFiles) {

                println("Processing file: ${file.absoluteFile}")

                val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
                val psiFile = virtualFile?.let { psiManager?.findFile(it) }
                val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)
                val commentSentimentMap = mutableMapOf<PsiComment, Int?>()
                val editor = virtualFile?.let { e.project?.let { it1 -> getEditorInstance(it, it1) } }

                val tokenizerService = e.project?.service<Tokenizer>()
                val analyzerService = e.project?.service<Analyzer>()
                val displayer = object : Displayer() {}


                for (comment in comments) {
                    //Get the token of each comment
                    val commentText = comment.text
                    val token = tokenizerService?.commentTokenizer(commentText)
                    val prediction = token?.let { analyzerService?.sentimentAnalysis(it) }
                    commentSentimentMap[comment] = prediction
                }
                val hints = displayer.sentimentDisplayer(editor, commentSentimentMap)


            }
        }
    }
}

