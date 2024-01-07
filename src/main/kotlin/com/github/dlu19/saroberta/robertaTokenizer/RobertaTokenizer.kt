package com.github.dlu19.saroberta.robertaTokenizer

import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import java.io.ByteArrayOutputStream

class RobertaTokenizer(
    private var robertaResources: RobertaTokenizerResources = RobertaTokenizerResources(listOf()),
    private var clsToken: Long = DEFAULT_CLS_TOKEN,
    private var sepToken: Long = DEFAULT_SEP_TOKEN,
    private var unkToken: Long = DEFAULT_UNK_TOKEN
) {
    private val bytePairEncoder = BytePairEncoder()

    fun tokenize(sentence: String): LongArray {
        println("The current comment: $sentence")
        val encodedStrings = mutableListOf<String>()

        val matcher = PATTERN.matcher(sentence)
        while (matcher.find()) {
            val matchedSequence = matcher.group()
            val matchedSequenceEncoded = StringBuilder()

            matchedSequence.toByteArray(StandardCharsets.UTF_8).forEach { b ->
                val encodedByte = robertaResources.encodeByte(b)
                matchedSequenceEncoded.append(encodedByte)
            }

            encodedStrings.add(matchedSequenceEncoded.toString())
        }

        val outputTokens = encodedStrings.asSequence()
            .map { encodedStr -> bytePairEncoder.encode(encodedStr, robertaResources) }
            .flatMap { encodedStrList -> encodedStrList.map { word -> robertaResources.encodeWord(word, unkToken) } }
            .flatMap { sequenceOf(it) }

        return sequenceOf(clsToken).plus(outputTokens).plus(sequenceOf(sepToken)).toList().toLongArray()
    }

    fun getClsToken() = clsToken
    fun getSepToken() = sepToken
    fun getUnkToken() = unkToken

    companion object {
        const val DEFAULT_CLS_TOKEN = 0L
        const val DEFAULT_SEP_TOKEN = 2L
        const val DEFAULT_UNK_TOKEN = 3L
        private val PATTERN: Pattern = Pattern.compile(
            "'s|'t|'re|'ve|'m|'ll|'d| ?\\p{L}+| ?\\p{N}+| ?[^\\s\\p{L}\\p{N}]+|\\s+(?!\\S)|\\s+"
        )
    }
}
