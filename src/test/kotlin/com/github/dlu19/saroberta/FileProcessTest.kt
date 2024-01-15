package com.github.dlu19.saroberta

import com.github.dlu19.saroberta.services.Extractor
import com.github.dlu19.saroberta.services.ModelLoader
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import java.io.File

class FileProcessTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
    }

    override fun tearDown() {
        super.tearDown()
    }

    // 2 tests on comment extractor: Extracting PsiComments from a given file
    fun testCommentExtractor() {
        // test implementation
        val testFile = File("src/test/resources/testExtractor/test.kt")
        val extractor = Extractor(project)
        val comments = extractor.commentExtractor(testFile)

        Assertions.assertEquals(2, comments.size, "Expected number of comments did not match")
    }

    fun testEmptyCommentExtractor() {
        // test implementation
        val testFile = File("src/test/resources/testExtractor/test_empty.kt")
        val extractor = Extractor(project)
        val comments = extractor.commentExtractor(testFile)

        Assertions.assertEquals(0, comments.size, "Expected number of comments did not match")
    }

    // 1 test on ModelLoader features: Load model from given path and return a ortruntime session

    fun testONNXModelLoader() =  runBlocking {
        val testFilePath = "testModel.onnx"
        val loader = ModelLoader()
        val model = loader.loadONNXModel(testFilePath)

        Assertions.assertNotNull(model, "The model was not loaded")
    }
    override fun getTestDataPath() = "src/test/resources"


}
