package com.github.dlu19.saroberta.listeners

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiComment
import com.intellij.ui.JBColor
import org.jetbrains.annotations.NotNull
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.UIManager.getFont

open class Displayer : InlayParameterHintsProvider {
    fun sentimentDisplayer(@NotNull editor: Editor?, commentSentimentMap: MutableMap<PsiComment, Int?>) {
        for ((comment, prediction) in commentSentimentMap) {
            println("Add emoji for comment: ${comment.text}")
            val hints = mutableListOf<InlayInfo>()

            if (comment is PsiComment) {
                val inlayText = prediction?.let { getInlayTextForComment(it) }
                val endOffset = comment.textRange.endOffset

                inlayText?.let {
                    hints.add(InlayInfo(it, endOffset))
                }

                val customElementRenderer: EditorCustomElementRenderer = object : EditorCustomElementRenderer {
                    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
                        // Return the width of the emoji
                        return 20
                    }

                    override fun calcHeightInPixels(inlay: Inlay<*>): Int {
                        // Return the height of the emoji
                        return 20
                    }

                    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
                        val editor = inlay.editor
                        g.color = JBColor.GRAY
                        g.font = getFont(editor)
                        val x = targetRegion.x + editor.offsetToXY(endOffset).x
                        val y = targetRegion.y + editor.offsetToXY(endOffset).y

                        g.drawString(inlayText, x, y)
                    }
                }
                editor?.inlayModel?.addInlineElement(endOffset, true, customElementRenderer)
//
//                editor?.inlayModel?.addBlockElement(10, false, true, 1, customElementRenderer)
            }
        }

    }

    fun getInlayTextForComment(prediction: Int?): String? {
        return when (prediction) {
            0 -> ":(" // Negative emoji
            1 -> ":)" // Positive emoji
            else -> null
        }
    }

    override fun getDefaultBlackList(): Set<String> {
        // Return an empty set if no specific patterns are to be excluded
        return emptySet()
    }

}
