import com.github.dlu19.saroberta.services.Extractor
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import com.intellij.psi.PsiComment
import java.io.File

class ExtractorTest : LightPlatformCodeInsightFixtureTestCase() {

    private lateinit var extractor: Extractor

    override fun setUp() {
        super.setUp()
        // Initialize Extractor with the project instance provided by LightPlatformCodeInsightFixtureTestCase
        extractor = Extractor(project)
    }

    override fun tearDown() {
        // Any necessary cleanup
        super.tearDown()
    }

    fun testCommentExtractor() {
        // Define the content of your test file
        val fileContent = """
            // This is a comment
            fun testFunction() {
                // Another comment
            }
        """.trimIndent()

        // Create a test file in the test environment
        val testFile = myFixture.addFileToProject("TestFile.kt", fileContent)

        // Convert VirtualFile to File
        val ioFile = File(testFile.virtualFile.path)

        // Extract comments
        val comments = extractor.commentExtractor(ioFile)

        // Assert that the number of comments is as expected
        assertEquals("Expected number of comments did not match", 2, comments.size)
    }
}
