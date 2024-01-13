package com.github.dlu19.saroberta

import com.github.dlu19.saroberta.services.Extractor
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class FileProcessTest : LightPlatformCodeInsightFixtureTestCase() {
    @Test
    fun `test comment extractor`() {

        super.setUp()
        val fileText = ""
        this.myFixture.addFileToProject("rename/test_empty.kt", fileText)
        val testFile = File("test_empty.kt")
        val extractor = Extractor(project)
        val comments = extractor.commentExtractor(testFile)

        Assertions.assertEquals(0, comments.size, "Expected number of comments did not match")
    }
    override fun getTestDataPath() = "src/test/testData/rename"
}


