package com.github.dlu19.saroberta.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.Unmodifiable
import java.io.File

@Service(Service.Level.PROJECT)
class Extractor(private val project: Project) {

    // Load the file and return its PsiComment
    fun commentExtractor(file: File): @Unmodifiable MutableCollection<PsiComment> {
        val psiManager = PsiManager.getInstance(project)
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
        val psiFile = virtualFile?.let { psiManager.findFile(it) }
        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)

        return comments
    }
}
