import com.github.dlu19.saroberta.actions.SentimentInlayHintProvider
import com.intellij.testFramework.utils.inlays.InlayHintsProviderTestCase

class SentimentHintProviderTest : InlayHintsProviderTestCase() {

    fun testNegativeSentiment() {
        val testCode = """
            /* This is a nice good comment */
            
            /* This is a bad terrible comment */
        """.trimIndent()

        // The "<hint>" markers indicate where the inlays should appear
        val expected = """
            <"😔: Negative"/>/* This is a nice good comment */
            
            <"😔: Negative"/>* This is a bad terrible comment */
        """.trimIndent()

        doTestProvider(
            "Example.kt",
            expected,
            SentimentInlayHintProvider(),
            SentimentInlayHintProvider.Settings(), // Replace with actual settings class if you have one
            verifyHintPresence = true
        )
    }

    // Add more test methods for different scenarios
}
