package com.github.dlu19.saroberta.services

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import com.intellij.openapi.components.Service
import io.kinference.ort.data.utils.createORTData
import io.kinference.ort.model.ORTModel
import kotlinx.coroutines.*
import java.nio.LongBuffer


@Service(Service.Level.PROJECT)
//Perform Sentiment Analysis based on the token
class Analyzer{
        private val modelLoader = ModelLoader()
        private val modelDeferred: Deferred<ORTModel> = CoroutineScope(Dispatchers.Default).async {
            val modelPath = "model/roberta-sequence-classification-9.onnx"
            modelLoader.loadONNXModel(modelPath)
        }

    suspend fun sentimentAnalysis(token: LongArray): Int {
            // convert token into int64 tensor as model input
            val model = modelDeferred.await()
            val longBuffer = LongBuffer.wrap(token)
            val env = OrtEnvironment.getEnvironment()
            val tensor = OnnxTensor.createTensor(env, longBuffer, longArrayOf(1, token.size.toLong()))
            val data = createORTData("input", tensor)

            // Obtain prediction result as tensor of sentiment analysis task
            val outputMap = model.predict(listOf(data))
            val output = outputMap["output"]
            val outputTensor = output?.data as OnnxTensor

            // Interpret output tensor as 0 or 1
            val buffer = outputTensor.floatBuffer
            val probabilities = FloatArray(buffer.remaining())
            buffer.get(probabilities)
            val result = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

            // Invoke the callback with the result
            return result
        }
    }

