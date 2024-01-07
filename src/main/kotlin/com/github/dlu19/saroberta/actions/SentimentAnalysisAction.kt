package com.github.dlu19.saroberta.actions



import com.github.dlu19.saroberta.services.Analyzer
import com.github.dlu19.saroberta.services.Tokenizer
import com.github.dlu19.saroberta.toolWindow.FileHolder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil


class SentimentAnalysisAction() : AnAction("Perform Sentiment Analysis") {

    init {
        templatePresentation.icon = IconLoader.getIcon("/icons/icon.png", SentimentAnalysisAction::class.java)
    }
    override fun actionPerformed(e: AnActionEvent) {
        val selectedFiles = FileHolder.selectedFiles
        val psiManager = e.project?.let { PsiManager.getInstance(it) }

        thisLogger().info("Start performing Sentiment Analysis Action...")

        // Extract comments of each selected file
        if (selectedFiles != null) {
            for (file in selectedFiles) {

                thisLogger().info("Processing file: ${file.absoluteFile}")

                val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
                val psiFile = virtualFile?.let { psiManager?.findFile(it) }
                val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)

                val tokenizerService = e.project?.service<Tokenizer>()
                val analyzerService = e.project?.service<Analyzer>()

                if (comments != null) {
                    for (comment in comments) {
                        //Get the token of each comment
                        val commentText = comment.text
                        println(commentText)
                        val token = tokenizerService?.commentTokenizer(commentText)
                        val prediction = token?.let { analyzerService?.sentimentAnalysis(it)}
                        println(prediction)
                    }



                        //Feed the comment token into roberta model to perform sentiment analysis

                    }
                }
            }
        }
}

