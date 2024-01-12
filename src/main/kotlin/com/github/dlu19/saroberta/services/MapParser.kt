package com.github.dlu19.saroberta.listeners

import com.intellij.psi.PsiComment


object ProviderReadyListener {

    private var commentSentimentMap: Map<PsiComment, Int?> = emptyMap()

    fun notifyProviderReady(s: String) {
        ProviderReadyListener.notifyProviderReady("Sentiment Analysis Hints successfully loaded")
    }

    fun providerTrigger(newResults: Map<PsiComment, Int?>) {
        commentSentimentMap = newResults
    }

    fun getSentimentForComment(comment: PsiComment): Int? {
        return commentSentimentMap[comment]
    }
}

