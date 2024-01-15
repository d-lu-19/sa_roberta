package com.github.dlu19.saroberta.toolWindow

import com.github.dlu19.saroberta.listeners.ComSenMapListener
import com.github.dlu19.saroberta.services.MapParser
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.lang.Language
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project


// Add Sentiment Hints to the file selected for Analysis
class SentimentInlayHintProvider : InlayHintsProvider<SentimentInlayHintProvider.Settings>, ComSenMapListener {

    private var myProject: Project? = null
    private var myPsiFile: PsiFile? = null

    init {
        MapParser.setComSenListener(this)
    }
    data class Settings(
        var positive: Boolean = true,
        var negative: Boolean = true
    )

    override val key: SettingsKey<Settings> = SettingsKey("sentiment.inlay.hints")

    override val name: String get() = "Sentiment Analysis Hints"
    override val previewText: String get() = "This is a preview"

    override fun isLanguageSupported(language: Language): Boolean {
        return language.id == "kotlin"
    }
    override val isVisibleInSettings: Boolean
        get() = true

    override fun createSettings(): Settings = Settings()
    override fun createConfigurable(settings: Settings): ImmediateConfigurable = object : ImmediateConfigurable {
        override val mainCheckboxText: String
            get() = "Sentiment Analysis Hint"
        override val cases: List<ImmediateConfigurable.Case>
            get() = listOf(
                ImmediateConfigurable.Case("Positive", "positive", settings::positive, ),
                ImmediateConfigurable.Case("Negative", "negative", settings::negative, ),
            )

        override fun createComponent(listener: ChangeListener): JComponent = JPanel()
    }

    // Upon sentiment analysis results obtained, trigger collector for inlay hints
    override fun onComSenMapUpdated() {
        myPsiFile?.let { DaemonCodeAnalyzer.getInstance(myProject).restart(it) }
        val virtualFile = myPsiFile?.virtualFile
        if (virtualFile != null) {
            myProject?.let { FileEditorManager.getInstance(it).openFile(virtualFile, true) }
        }
    }


    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): InlayHintsCollector? {

        myProject = file.project
        myPsiFile = file

        return object : FactoryInlayHintsCollector(editor) {
            override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
                if (element is PsiComment) {
                    processInlayHint(element, sink, factory)
                    }
                return true
            }

            private fun processInlayHint(comment: PsiComment, sink: InlayHintsSink, factory: PresentationFactory) {
                val offset = comment.textRange.startOffset
                val inlayText = getInlayText(comment)

                // Custom presentation of inlay
                val presentation = inlayText?.let { factory.smallText(it) }?.let { factory.roundWithBackground(it) }

                // Add inlay hints
                if (presentation != null) {
                    sink.addInlineElement(offset, false, presentation, false)
                }
            }

            // Map sentiment analysis result of comment element to emoji hint
            fun getInlayText(comment: PsiComment): String? {
                val prediction = MapParser.getComSenMap()?.get(comment)
                return when (prediction) {
                    0 -> "😔: Negative" // Negative emoji
                    1 -> "😀: Positive" // Positive emoji
                    else -> null
                }
            }
        }
    }
}





