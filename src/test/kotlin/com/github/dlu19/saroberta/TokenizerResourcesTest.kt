package com.github.dlu19.saroberta

import com.genesys.roberta.tokenizer.BiGram
import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizer
import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizerResources
import com.intellij.testFramework.TestDataPath
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@TestDataPath("src/test/resources")
class RobertaTokenizerResourcesTest {

    companion object {
        private const val BASE_VOCABULARY_FILE_NAME = "test-vocabularies/base_vocabulary.json"
        private const val VOCABULARY_FILE_NAME = "test-vocabularies/roberta-base-vocab.json"
        private const val MERGES_FILE_NAME = "test-vocabularies/roberta-base-merges.txt"
        private const val unknownToken = RobertaTokenizer.DEFAULT_UNK_TOKEN
        private val fileNameList = listOf(
            BASE_VOCABULARY_FILE_NAME,
            VOCABULARY_FILE_NAME,
            MERGES_FILE_NAME
        )
        private lateinit var robertaTokenizerResources: RobertaTokenizerResources

        @BeforeAll
        @JvmStatic
        fun init() {
            robertaTokenizerResources = RobertaTokenizerResources(fileNameList)
        }
    }

    // 6 tests on TokenizerResources features: Convert characters to Bigram, Get index of character and Bigram
    @Test
    fun `test min byte value encoding`() {
        val key: Byte = -128
        val encodedChar = robertaTokenizerResources.encodeByte(key)
        assertNotNull(encodedChar, "Encoded character should not be null for min byte value")
    }

    @Test
    fun `test max byte value encoding`() {
        val key: Byte = 127
        val encodedChar = robertaTokenizerResources.encodeByte(key)
        assertNotNull(encodedChar, "Encoded character should not be null for max byte value")
    }

    @Test
    fun `test encoding for non-existent word`() {
        val word = "Funnel"
        val actualToken = robertaTokenizerResources.encodeWord(word, unknownToken)
        assertEquals(actualToken, unknownToken, "Encoding for non-existent word should match unknown token")
    }

    @Test
    fun `test encoding for existing word`() {
        val word = "er"
        val expectedToken: Long = 254
        val actualToken = robertaTokenizerResources.encodeWord(word, unknownToken)
        assertEquals(actualToken, expectedToken, "Encoding for existing word should match expected token")
    }

    @Test
    fun `test rank for existing pair`() {
        val bigram = BiGram.of("e", "r")
        val actualRank = robertaTokenizerResources.getRankOrDefault(bigram, Int.MAX_VALUE)
        val expectedRank = 8
        assertEquals(expectedRank, actualRank, "Rank for existing pair should match expected rank")
    }

    @Test
    fun `test rank for non-existent pair`() {
        val bigram = BiGram.of("Zilpa", "Funnel")
        val actualRank = robertaTokenizerResources.getRankOrDefault(bigram, Int.MAX_VALUE)
        assertEquals(actualRank, Int.MAX_VALUE, "Rank for non-existent pair should be Int.MAX_VALUE")
    }
}

