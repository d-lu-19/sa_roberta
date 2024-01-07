package com.github.dlu19.saroberta.robertaTokenizer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class RobertaTokenizerResources(FILE_NAME_LIST: List<String>) {

    private lateinit var baseVocabularyMap: Map<Int, String>
    private lateinit var vocabularyMap: Map<String, Long>
    private lateinit var bpeRanks: Map<BiGram, Int>

    init{
        loadResourceStream(FILE_NAME_LIST)
    }

    private fun getResourceStream(fileNames: List<String>): List<InputStream> {
        return fileNames.mapNotNull { fileName ->
            this::class.java.classLoader.getResourceAsStream(fileName)
        }
    }

    private fun loadResourceStream(FILE_NAME_LIST: List<String>) {
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
            val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            return reader.useLines { lines ->
                lines.mapIndexed { idx, line ->
                    val tokens = line.split(" ")
                    val biGram = BiGram.of(tokens)
                    biGram to idx
                }.toMap()
            }
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load merges file for Roberta from InputStream", e)
        }
    }

    fun encodeByte(key: Byte): String {
        val unsignedKey = key.toInt() and 0xFF
        return baseVocabularyMap[unsignedKey]
            ?: throw IllegalArgumentException("Invalid byte to encode: $key")
    }

    fun encodeWord(word: String, defaultValue: Long): Long {
        return vocabularyMap[word] ?: defaultValue
    }

    fun getRankOrDefault(biGram: BiGram, defaultValue: Int): Int {
        return bpeRanks[biGram] ?: defaultValue
    }
}
