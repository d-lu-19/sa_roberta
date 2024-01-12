package com.github.dlu19.saroberta.services

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    //Load the roberta model in resources
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
//Perform Sentiment Analysis based on the token
class Analyzer : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    fun sentimentAnalysis(token: LongArray): Int {
        // Feed token into the loaded model as int64tensor as input
        val session = ModelLoader.session
        val longBuffer = LongBuffer.wrap(token)
        val env = OrtEnvironment.getEnvironment()
        val tensor = OnnxTensor.createTensor(env, longBuffer, longArrayOf(1, token.size.toLong()))
        val inputMap = mapOf("input" to tensor)

        // Interpret the binary classification output as normalized prediction value (0: Negative, 1: Positive)
        val output = session.run(inputMap)
        val outputTensor = output["output"].get() as OnnxTensor
        val buffer = outputTensor.floatBuffer
        val probabilities = FloatArray(buffer.remaining())
        buffer.get(probabilities)
        println("Probability of Negative: ${probabilities[0]}")
        println("Probability of Positive: ${probabilities[1]}")
        val result = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

        return result

    }
}
