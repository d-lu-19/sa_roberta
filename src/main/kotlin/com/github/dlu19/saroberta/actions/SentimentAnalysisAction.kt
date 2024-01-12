package com.github.dlu19.saroberta.actions


import com.github.dlu19.saroberta.listeners.Displayer
import com.github.dlu19.saroberta.services.Analyzer
import com.github.dlu19.saroberta.services.Extractor
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

    override fun actionPerformed(e: AnActionEvent) {
        val selectedFiles = FileHolder.selectedFiles

        thisLogger().info("Start performing Sentiment Analysis Action...")

        // Extract comments of each selected file
        if (selectedFiles != null) {
            for (file in selectedFiles) {
                val extractorService = e.project?.service<Extractor>()
                val tokenizerService = e.project?.service<Tokenizer>()
                val analyzerService = e.project?.service<Analyzer>()
                val displayer = object : Displayer() {}

                val comments = extractorService?.commentExtractor(file)
                val commentSentimentMap = mutableMapOf<PsiComment, Int?>()

                if (comments != null) {
                    for (comment in comments) {
                        //Get the token of each comment
                        val commentText = comment.text
                        val token = tokenizerService?.commentTokenizer(commentText)
                        val prediction = token?.let { analyzerService?.sentimentAnalysis(it) }
                        commentSentimentMap[comment] = prediction
                    }
                }
                val hints = file?.let { e.project?.let { it1 ->
                    displayer.sentimentDisplayer(it,
                        it1, commentSentimentMap) }
                }


            }
        }
    }
}

