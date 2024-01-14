package com.github.dlu19.saroberta.robertaTokenizer

import com.genesys.roberta.tokenizer.BiGram
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.containers.reverse
import kotlinx.html.ARel.index
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class RobertaTokenizerResources(FILE_NAME_LIST: List<String?>) {

    private lateinit var baseVocabularyMap: Map<Int, String>
    private lateinit var vocabularyMap: Map<String, Long>
    private lateinit var bpeRanks: Map<BiGram, Int>

    init{
        loadResourceStream(FILE_NAME_LIST)
        thisLogger().info("Tokenizer Resources was successfully loaded.")
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
            val type = object : TypeToken<HashMap<Int, String>>() {}.type
            Gson().fromJson(InputStreamReader(inputStream), type)
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load base vocabulary map for Roberta from InputStream", e)
        }
    }

    private fun loadVocabularyStream(inputStream: InputStream): Map<String, Long> {
        return try {
            val type = object : TypeToken<HashMap<String, Long>>() {}.type
            Gson().fromJson(InputStreamReader(inputStream), type)
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load vocabulary map for Roberta from InputStream", e)
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
