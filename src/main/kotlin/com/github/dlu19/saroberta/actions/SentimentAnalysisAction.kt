package com.github.dlu19.saroberta.actions

import com.github.dlu19.saroberta.listeners.FileHolder
import com.github.dlu19.saroberta.services.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiComment
import javax.swing.ImageIcon
import javax.swing.table.DefaultTableModel


class SentimentAnalysisAction() : AnAction("Sentiment Analysis On Current File") {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedFiles = FileHolder.selectedFiles
        val fileTableMap = mutableMapOf<String, DefaultTableModel?>()

        if (selectedFiles != null) {
            for (file in selectedFiles) {
                // Instantiation of essential services
                val extractorService = e.project?.service<Extractor>()
                val tokenizerService = e.project?.service<Tokenizer>()
                val analyzerService = e.project?.service<Analyzer>()
                val loaderService = e.project?.service<Loader>()

                val comments = extractorService?.commentExtractor(file)
                val commentSentimentMap = mutableMapOf<PsiComment, Int?>()

                // Perform Sentiment Analysis for each comment
                if (comments != null) {
                    for (comment in comments) {
                        //Get the token of each comment
                        val commentText = comment.text
                        val token = tokenizerService?.commentTokenizer(commentText)
                        val prediction = token?.let { analyzerService?.sentimentAnalysis(it) }
                        commentSentimentMap[comment] = prediction
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
                e.project?.let { MapParser.updateComSenMap(commentSentimentMap) }

                val tableModel = loaderService?.tableLoader(commentSentimentMap)
                fileTableMap[file.name] = tableModel
            }
            MapParser.updateFileTableMap(fileTableMap)

        }

    }
}


