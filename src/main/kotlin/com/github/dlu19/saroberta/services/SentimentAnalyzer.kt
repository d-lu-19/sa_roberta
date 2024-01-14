package com.github.dlu19.saroberta.services

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.nio.LongBuffer

@Service(Service.Level.PROJECT)
//Perform Sentiment Analysis based on the token
class Analyzer : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    fun sentimentAnalysis(token: LongArray): Int {
        // Load roberta model
        val modelLoader = ModelLoader()
        val modelPath = "model/roberta-sequence-classification-9.onnx"
        val session = modelLoader.loadSession(modelPath)

        // Feed input token into the loaded model as int64tensor
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
        val result = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

        return result

    }
}
