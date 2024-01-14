package com.github.dlu19.saroberta.services

import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizer
import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizerResources
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger

// Tokenize the comment text as input suitable for roberta model
@Service(Service.Level.PROJECT)
class Tokenizer() {
    // Load the necessary files for building robertaTokenizer
    object FileConstants {
        const val BASE_VOCABULARY_FILE_NAME = "test-vocabularies/base_vocabulary.json"
        const val VOCABULARY_FILE_NAME = "test-vocabularies/roberta-base-vocab.json"
        const val MERGES_FILE_NAME = "test-vocabularies/roberta-base-merges.txt"
//        const val VOCABULARY_FILE_NAME = "test-vocabularies/vocabulary.json"
//        const val MERGES_FILE_NAME = "test-vocabularies/merges.txt"
    }

    private val FILE_NAME_LIST = listOf(
        FileConstants.BASE_VOCABULARY_FILE_NAME,
        FileConstants.VOCABULARY_FILE_NAME,
        FileConstants.MERGES_FILE_NAME)

    private val robertaTokenizerResources = RobertaTokenizerResources.getInstance(FILE_NAME_LIST)
    private val robertaTokenizer = RobertaTokenizer(robertaTokenizerResources)


    // Obtain the token for comment text
    fun commentTokenizer(comment: String): LongArray {
        thisLogger().info("Processing comment for tokenization: $comment")
        val token = robertaTokenizer.tokenize(comment)
        return token
    }
}