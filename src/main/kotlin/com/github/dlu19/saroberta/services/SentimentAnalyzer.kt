package com.github.dlu19.saroberta.services

import ai.onnxruntime.OnnxTensor
import com.intellij.openapi.components.Service
import io.kinference.ort.data.tensor.ORTTensor
import io.kinference.ort.data.utils.createORTData
import kotlinx.coroutines.*


@Service(Service.Level.PROJECT)
//Perform Sentiment Analysis based on the token
class Analyzer : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    fun sentimentAnalysis(token: LongArray, callback: (Int) -> Unit) {
        launch {
            // Load roberta model
            val modelLoader = ModelLoader()
            val modelPath = "model/roberta-sequence-classification-9.onnx"
            val model = modelLoader.loadONNXModel(modelPath)

            // convert token into int64 tensor as model input
            val ortTensor = ORTTensor.invoke(token,longArrayOf(1, token.size.toLong()))
            val tensor = ortTensor.data
            val input = listOf(createORTData("input", tensor))

            // Obtain prediction result as tensor of sentiment analysis task
            val outputMap = model.predict(input)
            val output = outputMap["output"]
            val outputTensor = output?.data as OnnxTensor

            // Interpret output tensor as 0 or 1
            val buffer = outputTensor.floatBuffer
            val probabilities = FloatArray(buffer.remaining())
            buffer.get(probabilities)
            val result = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

            // Invoke the callback with the result
            callback(result)
        }
    }

}
