package com.github.dlu19.saroberta.listeners

import com.github.dlu19.saroberta.services.Mapper
import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.lang.Language
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JPanel


class SentimentInlayHintProvider : InlayHintsProvider<SentimentInlayHintProvider.Settings> {

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

    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): InlayHintsCollector? {
        return object : FactoryInlayHintsCollector(editor) {
            override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
                if (element is PsiComment) {
                    val inlayText = getInlayText(element)
                    inlayText?.let {
                        val offset = element.textRange.startOffset
                        sink.addInlineElement(offset, true, factory.smallText(it), false)
                    }
                }
                return true
            }
        }
    }
    private fun getInlayText(comment: PsiComment): String? {
        val prediction = Mapper.getSentimentForComment(comment)
        return when (prediction) {
            0 -> "😔: Negative" // Negative emoji
            1 -> "😀: Positive" // Positive emoji
            else -> null
        }
    }
}
