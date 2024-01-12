package com.github.dlu19.saroberta

import com.github.dlu19.saroberta.listeners.Displayer
import com.github.dlu19.saroberta.services.Analyzer
import com.github.dlu19.saroberta.services.Extractor
import com.intellij.openapi.components.service
import com.intellij.psi.PsiComment
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test
import java.io.File

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    // Test to verify correct extraction of comments
    override fun getTestDataPath() = "src/test/testData"
    fun testCommentExtractor() {
        val testDataPath = "src/test/testData/test.kt"
        val file = File(testDataPath)
        val extractor = project.service<Extractor>()
        val comments = extractor.commentExtractor(file)
        val expectedComments = listOf(
            "// This is a nice comment",
            "// This is a bad comment."
        )

        // Asserting that the extracted comments match the expected comments
        assertEquals(expectedComments.toString(), comments.map { it.text })
    }

//    @Test
//    fun testSentimentDisplayer() {
//        val testDataPath = "test.kt"
//        val file = File(testDataPath)
//        val commentSentimentMap = mutableMapOf<PsiComment, Int?>()
//        val comments = extractor.commentExtractor(file)
//
//        // Assigning sentiment values to comments
//        for ((index, comment) in comments.withIndex()) {
//            val sentimentValue = if (index == 0) 1 else 0 // 1 for positive, 0 for negative
//            commentSentimentMap[comment] = sentimentValue
//        }
//
//        // Displaying the sentiments
//
//        val hints = displayer.sentimentDisplayer(file, project, commentSentimentMap)
//
//        // Asserting that hints are not empty and checking the sentiment display
//        assertTrue("Hints should not be empty", hints.isNotEmpty())
//        assertEquals("Inlay text for the first comment should be a positive emoji", ":)", hints[0].text) // Assuming ":)" is a positive emoji
//    }

}
