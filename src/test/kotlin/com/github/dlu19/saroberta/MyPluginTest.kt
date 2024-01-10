package com.github.dlu19.saroberta

import com.github.dlu19.saroberta.listeners.Displayer
import com.github.dlu19.saroberta.services.Analyzer
import com.github.weisj.jsvg.e
import com.intellij.openapi.components.service
import com.intellij.psi.PsiComment
import com.intellij.testFramework.fixtures.BasePlatformTestCase

//import com.github.dlu19.saroberta.services.MyProjectService

//@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testProjectService() {
        val projectService = project.service<Analyzer>()
        val token = longArrayOf(0, 3, 3, 114, 3, 3, 10, 8, 3, 3, 10, 114, 3, 5, 3, 3, 7, 13, 10, 9, 114, 5, 3, 114, 7, 3, 3, 3, 114, 9, 7, 4, 7, 3, 10, 7, 12, 114, 3, 11, 4, 7, 2)

        val result = projectService.sentimentAnalysis(token)
    }
//    fun testinlayhintsgeneration() {
        // Setup test environment
//        myFixture.configureByText("TestFile.kt", "// This is a comment")
//
//        val file = myFixture.file
//        val comment = file.findElementAt(0) as PsiComment
//        assertNotNull("Comment should not be null", comment)
//
//        val commentSentimentMap = mutableMapOf<PsiComment, Int>()
//        commentSentimentMap[comment] = 0 // Assuming negative sentiment
//
//        val displayer = object : Displayer() {}
//        val hints = displayer.sentimentDisplayer(commentSentimentMap)
//
//        assertTrue("Hints should not be empty", hints.isNotEmpty())
//        assertEquals("Inlay text should be a negative emoji", "\uD83D\uDE41", hints[0].text)
    }

