package com.github.dlu19.saroberta.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.Unmodifiable
import java.io.File

// Load the file and return its PsiComment
@Service(Service.Level.PROJECT)
class Extractor(private val project: Project) {
    fun commentExtractor(file: File): @Unmodifiable MutableCollection<PsiComment> {
        thisLogger().info("Extracting comments from file: ${file.path}")

        // Load PsiFile from the path
        val psiManager = PsiManager.getInstance(project)
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
            ?: throw IllegalStateException("Virtual file not found for path: ${file.path}")
        val psiFile = virtualFile?.let { psiManager.findFile(it) }

        // Find the PsiComments from the file
        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)
        return comments
    }
}
