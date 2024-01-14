package com.github.dlu19.saroberta


import com.github.dlu19.saroberta.services.Analyzer
import com.github.dlu19.saroberta.services.Tokenizer
import com.intellij.testFramework.TestDataPath
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch


@TestDataPath("src/test/resources")
class SentimentAnalysisTest {

    companion object {
        lateinit var tokenizer: Tokenizer
        lateinit var analyzer: Analyzer
        const val clsToken = 0L
        const val sepToken = 2L

        @BeforeAll
        @JvmStatic
        fun init() {
            tokenizer = Tokenizer()
            analyzer = Analyzer()
        }
    }

    // 3 tests on comment tokenizer features: Transform comment text to tokens specific for roberta model
    @Test
    fun `test tokenizer`() {
        val sentence = "Hello world!"
        val actualToken = tokenizer.commentTokenizer(sentence).toList()
        val expectedToken = listOf(0L, 31414L, 3L, 328L, 2L)

        // Asserting that the extracted tokens match the expected tokens
        Assertions.assertEquals(
            expectedToken,
            actualToken,
            "Encoding for existing word should match expected token"
        )
    }

    @Test
    fun `test tokenizer empty string`() {
        val sentence = ""
        val actualToken = tokenizer.commentTokenizer(sentence).toList()
        Assertions.assertEquals(clsToken, actualToken[0])
        Assertions.assertEquals(sepToken, actualToken[1])
    }

    @Test
    fun `test tokenizer placeholder token`() {

        val sentence = "er"
        val expectedToken: Long = 254
        val actualToken = tokenizer.commentTokenizer(sentence)

        // Asserting that the extracted tokens match the expected tokens
        Assertions.assertEquals(clsToken, actualToken[0])
        Assertions.assertEquals(expectedToken, actualToken[1])
        Assertions.assertEquals(sepToken, actualToken[2])
    }

    // 3 tests on sentiment analyzer features: Binary classification on token input
    @Test
    fun `test analyzer positive`() = runBlocking {
        val sentence = "Hello world!"
        val token = tokenizer.commentTokenizer(sentence)
        var actualPrediction: Int? = null

        val latch = CountDownLatch(1) // Used to wait for the callback

        analyzer.sentimentAnalysis(token) { result ->
            actualPrediction = result
            latch.countDown() // Signal that the result is received
        }

        latch.await() // Wait for the callback to be called

        val expectedPrediction = 1

        // Asserting that the extracted tokens match the expected tokens
        Assertions.assertEquals(
            expectedPrediction,
            actualPrediction,
            "Prediction should be positive"
        )
    }

    @Test
    fun `test analyzer negative`() = runBlocking {
        val sentence = "Goodbye world!"
        val token = tokenizer.commentTokenizer(sentence)
        var actualPrediction: Int? = null

        val latch = CountDownLatch(1) // Used to wait for the callback

        analyzer.sentimentAnalysis(token) { result ->
            actualPrediction = result
            latch.countDown() // Signal that the result is received
        }

        latch.await() // Wait for the callback to be called

        val expectedPrediction = 0

        // Asserting that the extracted tokens match the expected tokens
        Assertions.assertEquals(
            expectedPrediction,
            actualPrediction,
            "Prediction should be negative"
        )
    }

    @Test
    fun `test analyzer long string`() = runBlocking {
        val sentence =
            "Whenever I met one of them who seemed to me at all clear-sighted, I tried the experiment of \n" +
                    "showing him my Drawing Number One, which I have always kept. I would try to find out, so, if this \n" +
                    "was a person of true understanding. But, whoever it was, he, or she, would always say: “That is a \n" +
                    "hat.” Then I would never talk to that person about boa constrictors, or primeval forests, or stars. I \n" +
                    "would bring myself down to his level. I would talk to him about bridge, and golf, and politics, and \n" +
                    "neckties. And the grown-up would be greatly pleased to have met such a sensible man. "
        val token = tokenizer.commentTokenizer(sentence)
        var actualPrediction: Int? = null

        val latch = CountDownLatch(1) // Used to wait for the callback

        analyzer.sentimentAnalysis(token) { result ->
            actualPrediction = result
            latch.countDown() // Signal that the result is received
        }

        latch.await() // Wait for the callback to be called

        val expectedPrediction = 0

        // Asserting that the extracted tokens match the expected tokens
        Assertions.assertEquals(
            expectedPrediction,
            actualPrediction,
        )
    }
}



