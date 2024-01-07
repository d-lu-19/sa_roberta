package com.github.dlu19.saroberta.services


import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizer
import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizerResources
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project


@Service(Service.Level.PROJECT)
class Tokenizer() {

    init{
        thisLogger().info("Start building tokenizer...")
    }
//    private val baseDirPath = "src/main/resources/test-vocabularies"

    object FileConstants {
        const val BASE_VOCABULARY_FILE_NAME = "test-vocabularies/base_vocabulary.json"
        const val VOCABULARY_FILE_NAME = "test-vocabularies/vocabulary.json"
        const val MERGES_FILE_NAME = "test-vocabularies/merges.txt"
    }

    private val FILE_NAME_LIST = listOf(
        FileConstants.BASE_VOCABULARY_FILE_NAME,
        FileConstants.VOCABULARY_FILE_NAME,
        FileConstants.MERGES_FILE_NAME
    )

    private val robertaResources = RobertaTokenizerResources(FILE_NAME_LIST)
    private val robertaTokenizer = RobertaTokenizer(robertaResources)

    fun commentTokenizer(comment: String): LongArray {
        println("Processing comment: $comment")
        val token: LongArray = robertaTokenizer.tokenize(comment)
        return token
    }
}