package com.github.dlu19.saroberta.services

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.LongBuffer

object ModelLoader {
    private var ortSession: OrtSession? = null

    val session: OrtSession
        get() {
            if (ortSession == null) {
                ortSession = loadSession()
            }
            return ortSession!!
        }

    private fun loadSession(): OrtSession {
        val modelPath = "model/roberta-sequence-classification-9.onnx"
        val modelInputStream = this::class.java.classLoader.getResourceAsStream(modelPath)

        val tempFile = File.createTempFile("model-", ".onnx")
        modelInputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val env = OrtEnvironment.getEnvironment()
        val session = env.createSession(tempFile.absolutePath)
        println("Roberta model was successfully loaded.")
        tempFile.deleteOnExit()

        return session
    }
}

@Service(Service.Level.PROJECT)
class Analyzer : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    fun sentimentAnalysis(token: LongArray): Int {
        val session = ModelLoader.session
        val longBuffer = LongBuffer.wrap(token)

        val env = OrtEnvironment.getEnvironment()
        val tensor = OnnxTensor.createTensor(env, longBuffer, longArrayOf(1, token.size.toLong()))

        val inputNames = session.inputNames
        println(inputNames)
        val inputMap = mapOf("input" to tensor)

        val output = session.run(inputMap)
        val outputTensor = output["output"].get() as OnnxTensor
        val buffer = outputTensor.floatBuffer
        val probabilities = FloatArray(buffer.remaining())
        buffer.get(probabilities)

        println("Probability of class 1: ${probabilities[0]}")
        println("Probability of class 2: ${probabilities[1]}")

        val result = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        return result

    }
}
