package com.github.dlu19.saroberta.services

import com.intellij.openapi.diagnostic.thisLogger
import io.kinference.ort.ORTEngine
import io.kinference.ort.model.ORTModel
import java.io.File


//Load the roberta model in resources
class ModelLoader() {
    suspend fun loadONNXModel(path: String): ORTModel {
        // Load inputstream of model and save as temp file
        val modelInputStream = this::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalStateException("Model was not found for path: $path")
        val tempFile = File.createTempFile("model-", ".onnx")
        modelInputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val model = ORTEngine.loadModel(tempFile.absolutePath)
        thisLogger().info("Model was successfully loaded.")
        tempFile.delete() // Delete temp file after usage

        return model
    }
}