package com.github.dlu19.saroberta.services

import com.github.dlu19.saroberta.listeners.FileTableMapListener
import com.intellij.psi.PsiComment
import javax.swing.table.DefaultTableModel

//Function: Parse the Mapping of comment and sentiment prediction
object MapParser {

    private var commentSentimentMap: Map<PsiComment, Int?> = emptyMap()
    private var fileTableMap: Map<String, DefaultTableModel?> = emptyMap()
    private lateinit var listener: FileTableMapListener

    fun setListener(listener: FileTableMapListener) {
        this.listener = listener
    }

    private fun notifyListener() {
        listener.onFileTableMapUpdated()
    }

    fun updateComSenMap(newResults: Map<PsiComment, Int?>) {
        commentSentimentMap = newResults

    }

    fun getComSenMap(comment: PsiComment): Int? {
        return commentSentimentMap[comment]
    }

    fun updateFileTableMap(newResults: Map<String, DefaultTableModel?>){
        fileTableMap = newResults
        notifyListener()
    }

    fun getFileTableMap(): Map<String, DefaultTableModel?> {
        return fileTableMap
    }
}




