package com.github.dlu19.saroberta.toolWindow

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable

import javax.swing.table.DefaultTableModel


class Loader() {
    fun scrollpaneLoader(model: DefaultTableModel): JBScrollPane {
        val table = JBTable(model)
        val scrollPane = JBScrollPane(table)

        return scrollPane
    }
    fun tableLoader(commentSentimentMap: MutableMap<PsiComment, Int?>, project: Project): DefaultTableModel {
        val model = DefaultTableModel()
        model.addColumn("Comment")
        model.addColumn("Line")
        model.addColumn("Sentiment")

        commentSentimentMap.forEach { (comment, value) ->
            val sentiment = if (value == 0) "\uD83D\uDE14 Negative" else "\uD83D\uDE00 Positive"

            // Obtain line index of comment
            val documentManager = PsiDocumentManager.getInstance(project)
            val document: Document? = documentManager.getDocument(comment.containingFile)

            val textOffset = comment.textOffset
            val lineNumber = document?.getLineNumber(textOffset)?.plus(1)

            model.addRow(arrayOf(comment.text, lineNumber, sentiment))
        }

        return model
    }

}









