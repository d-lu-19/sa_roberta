package com.github.dlu19.saroberta.services

import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizer
import com.github.dlu19.saroberta.robertaTokenizer.RobertaTokenizerResources
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.Unmodifiable
import java.io.File

@Service(Service.Level.PROJECT)
class Extractor(private val project: Project) {

    private val logger = Logger.getInstance(Extractor::class.java)

    init {
        logger.info("Start building tokenizer...")
    }

    fun commentExtractor(file: File): @Unmodifiable MutableCollection<PsiComment> {
        println("Processing file: ${file.absoluteFile}")

        val psiManager = PsiManager.getInstance(project)
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
        val psiFile = virtualFile?.let { psiManager.findFile(it) }
        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)

        return comments
    }
}
