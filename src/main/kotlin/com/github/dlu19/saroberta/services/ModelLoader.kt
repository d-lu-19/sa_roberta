package com.github.dlu19.saroberta.services

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.intellij.openapi.diagnostic.thisLogger
import java.io.File

//Load the model in resources
class ModelLoader {
    fun loadSession(modelPath: String): OrtSession {
        // Load the model as inputstream and save as temp file
        val modelInputStream = this::class.java.classLoader.getResourceAsStream(modelPath)
            ?: throw IllegalStateException("Fail to load model from $modelPath.")
        val tempFile = File.createTempFile("model-", ".onnx")
        modelInputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        thisLogger().info("Temp file path: ${tempFile.absoluteFile}")

        // Load temp file to obtain ortruntime session
        val env = OrtEnvironment.getEnvironment()
        val session = env.createSession(tempFile.absolutePath)
        thisLogger().info("Model: $modelPath was successfully loaded.")

        tempFile.deleteOnExit() //Delete temp file after usage

        return session
    }
}