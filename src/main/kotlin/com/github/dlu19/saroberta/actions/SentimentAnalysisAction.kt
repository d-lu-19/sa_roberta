package com.github.dlu19.saroberta.actions

import com.github.dlu19.saroberta.listeners.FileHolder
import com.github.dlu19.saroberta.services.*
import com.github.dlu19.saroberta.toolWindow.Loader
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiComment
import kotlinx.coroutines.runBlocking
import javax.swing.ImageIcon
import javax.swing.table.DefaultTableModel


class SentimentAnalysisAction() : AnAction("Perform Sentiment Analysis") {
    override fun actionPerformed(e: AnActionEvent) {
        val selectedFiles = FileHolder.selectedFiles // Load selected files
        val fileTableMap = mutableMapOf<String, DefaultTableModel?>()

        if (selectedFiles != null) {
            for (file in selectedFiles) {
                // Instantiation of essential services
                val extractorService = e.project?.let { Extractor(it) }
                val tokenizerService = Tokenizer()
                val analyzerService = Analyzer()
                val loader = Loader()
                val comments = extractorService?.commentExtractor(file)
                val commentSentimentMap = mutableMapOf<PsiComment, Int?>()

                // Perform Sentiment Analysis for each comment
                if (comments != null) {
                    for (comment in comments) {
                        //Get the token of each comment
                        val commentText = comment.text
                        val token = tokenizerService?.commentTokenizer(commentText)

                        runBlocking {
                            val prediction = token?.let { analyzerService.sentimentAnalysis(it) }
                            commentSentimentMap[comment] = prediction
                            thisLogger().info(" The prediction of comment ${comment.text} is $prediction")
                        }
                    }
                }

                // Inform if no PsiComment observed
                else{
                    Messages.showMessageDialog(
                        "There are no comments in file ${file.name}",
                        "Information" ,
                        ImageIcon(javaClass.getResource("/icons/thinking.png"))
                    )
                }

                // Update the sentiment analysis result mapping to the table content
                e.project?.let { MapParser.updateComSenMap(commentSentimentMap) }
                val tableModel = e.project?.let { loader?.tableLoader(commentSentimentMap, it) }
                fileTableMap[file.name] = tableModel
            }
            // Update table content
            MapParser.updateFileTableMap(fileTableMap)

        }

    }
}


