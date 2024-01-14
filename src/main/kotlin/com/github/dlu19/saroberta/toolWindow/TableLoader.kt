package com.github.dlu19.saroberta.toolWindow

import com.intellij.psi.PsiComment
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import javax.swing.table.DefaultTableModel


class Loader() {
    fun scrollpaneLoader(model: DefaultTableModel): JBScrollPane {
        val table = JBTable(model)
        val scrollPane = JBScrollPane(table)

        return scrollPane
    }
    fun tableLoader(commentSentimentMap: MutableMap<PsiComment, Int?>): DefaultTableModel {
        val model = DefaultTableModel()
        model.addColumn("Comment")
        model.addColumn("Sentiment")

        commentSentimentMap.forEach { (comment, value) ->
            val sentiment = if (value == 0) "Negative" else "Positive"
            model.addRow(arrayOf(comment.text, sentiment))
        }

        return model
    }

}








