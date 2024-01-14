package com.github.dlu19.saroberta.robertaTokenizer

import com.genesys.roberta.tokenizer.BiGram
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.diagnostic.thisLogger
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class RobertaTokenizerResources(FILE_NAME_LIST: List<String?>) {

    private lateinit var baseVocabularyMap: Map<Int, String>
    private lateinit var vocabularyMap: Map<String, Long>
    private lateinit var bpeRanks: Map<BiGram, Int>

    init {
        loadResourceStream(FILE_NAME_LIST)
        thisLogger().info("Tokenizer Resources were successfully loaded.")
    }

    companion object {
        private var instance: RobertaTokenizerResources? = null

        fun getInstance(FILE_NAME_LIST: List<String?>): RobertaTokenizerResources {
            return instance ?: synchronized(this) {
                instance ?: RobertaTokenizerResources(FILE_NAME_LIST).also { instance = it }
            }
        }
    }

    private fun getResourceStream(fileNames: List<String?>): List<InputStream> {
        return fileNames.mapNotNull { fileName ->
            this::class.java.classLoader.getResourceAsStream(fileName)
                ?: throw IllegalStateException("File not found for path: $fileName")
        }
    }

    private fun loadResourceStream(FILE_NAME_LIST: List<String?>) {
        val streamList = getResourceStream(FILE_NAME_LIST)

        this.baseVocabularyMap = loadBaseVocabularyStream(streamList[0])
        this.vocabularyMap = loadVocabularyStream(streamList[1])
        this.bpeRanks = loadMergesFileStream(streamList[2])
    }

    private fun loadBaseVocabularyStream(inputStream: InputStream): Map<Int, String> {
        return try {
            // Define the correct type token for Gson
            val type = object : TypeToken<Map<Int, String>>() {}.type
            val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
            val tempMap: Map<Int, String> = Gson().fromJson(reader, type)
            tempMap
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load vocabulary map from InputStream", e)
        }
    }


    private fun loadVocabularyStream(inputStream: InputStream): Map<String, Long> {
        return try {
            // Define the correct type token for Gson
            val type = object : TypeToken<Map<String, Long>>() {}.type
            val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
            val tempMap: Map<String, Long> = Gson().fromJson(reader, type)
            tempMap
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load vocabulary map from InputStream", e)
        }
    }

    private fun loadMergesFileStream(inputStream: InputStream): Map<BiGram, Int> {
        try {
            inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                val lines = reader.readLines()
                return lines.mapIndexedNotNull { idx, line ->
                    val parts = line.trim().split("\\s+".toRegex())
                    if (parts.size == 2) BiGram.of(parts) to idx else null
                }.toMap()
            }
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load merges file for Roberta", e)
        }
    }

    fun encodeByte(key: Byte): String {
        return baseVocabularyMap[key.toInt() and 0xFF] ?: ""
    }

    fun encodeWord(word: String, defaultValue: Long): Long {
        return vocabularyMap[word] ?: defaultValue
    }

    fun getRankOrDefault(biGram: BiGram, defaultValue: Int): Int {
        bpeRanks.keys.forEachIndexed { idx, key ->
            if (key.left == biGram.left && key.right == biGram.right) {
                return idx
            }
        }
        return defaultValue
    }
}
