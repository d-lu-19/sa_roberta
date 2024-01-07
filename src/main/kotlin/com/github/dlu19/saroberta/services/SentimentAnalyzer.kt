package com.github.dlu19.saroberta.services

import com.github.weisj.jsvg.T
import com.intellij.openapi.components.Service
import io.kinference.InferenceEngine
import io.kinference.core.KIEngine
import io.kinference.core.model.KIModel
import io.kinference.data.ONNXData
import io.kinference.data.ONNXDataType
import io.kinference.ort.model.ORTModel
import io.kinference.ort.ORTEngine
import io.kinference.ort.ORTData

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

@Service(Service.Level.PROJECT)
class Analyzer : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var model: ORTModel? = null
    private val ENGINE = KIEngine
    private var model_engine = KIModel

    init {
        // Load the model asynchronously
        launch {
            loadModel()
        }
    }

    private suspend fun loadModel() {
        val modelPath = "model/roberta-sequence-classification-9.onnx".toPath()
        model = ORTEngine.loadModel(modelPath)

    }

    fun sentimentAnalysis(token: LongArray): Map<String, ORTData<*>> {

        val tokenArray = longArrayToByteArray(token)
//        required input: bytearray and model tensor, return ORTData
        val ortInputData = ORTEngine.loadData(tokenArray, ONNXDataType.ONNX_TENSOR)

        val inputList = listOf(ortInputData)
        val loadedModel = model ?: throw IllegalStateException("Model not loaded.")

        // Perform the prediction within a coroutine context
        return runBlocking {
//            required input is ORTData
            val result = loadedModel.predict(inputList)

            result
        }
    }

    private fun longArrayToByteArray(longArray: LongArray): ByteArray {
        val byteArray = ByteArray(longArray.size * 8) // Each Long is 8 bytes
        for (i in longArray.indices) {
            val longValue = longArray[i]
            for (j in 0 until 8) {
                byteArray[i * 8 + j] = (longValue shr (j * 8)).toByte()
            }
        }
        return byteArray
    }
}
