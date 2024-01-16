package com.github.dlu19.saroberta.services

import com.github.dlu19.saroberta.listeners.ComSenMapListener
import com.github.dlu19.saroberta.listeners.FileTableMapListener
import com.intellij.psi.PsiComment
import javax.swing.table.DefaultTableModel

//Function: Parse the Mappings of comment and sentiment prediction, file and its analysis results
object MapParser {

    private var commentSentimentMap: Map<PsiComment, Int?> = emptyMap()
    private var fileTableMap: Map<String, DefaultTableModel?> = emptyMap()
    private lateinit var fileTablelistener: FileTableMapListener
    private lateinit var comSenlistener: ComSenMapListener

    fun setFileTableListener(fileTablelistener: FileTableMapListener) {
        this.fileTablelistener = fileTablelistener
    }
    private fun notifyFileTableListener() {
        fileTablelistener.onFileTableMapUpdated()
    }

    fun updateFileTableMap(newResults: Map<String, DefaultTableModel?>){
        fileTableMap = newResults
        notifyFileTableListener() // Upon results update, initiate listener for updating results display on the tool window
    }

    fun getFileTableMap(): Map<String, DefaultTableModel?> {
        return fileTableMap
    }


    fun setComSenListener(comSenlistener: ComSenMapListener) {
        this.comSenlistener = comSenlistener
    }
    private fun notifyComSenListener() {
        comSenlistener.onComSenMapUpdated()
    }

    fun updateComSenMap(newResults: Map<PsiComment, Int?>) {
        commentSentimentMap = newResults
        notifyComSenListener()
    }

    fun getComSenMap(): Map<PsiComment, Int?>? {
        return commentSentimentMap
    }


}




