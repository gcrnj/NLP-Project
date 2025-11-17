package com.giotech.nlpproject

//import android.content.Context
//import android.content.res.AssetFileDescriptor
//import android.util.Log
//import org.tensorflow.lite.Interpreter
//import java.io.FileInputStream
//import java.nio.MappedByteBuffer
//import java.nio.channels.FileChannel


//class SummarizerService(private val context: Context) {
//
//    private var tflite: Interpreter? = null
//    private val TAG = "SummarizerService"
//
//    private val MODEL_NAME = "model_t5_greedy.tflite"
//    private val MAX_INPUT_LEN = 256
//    private val MAX_OUTPUT_LEN = 128
//
//    init {
//        try {
//            tflite = Interpreter(loadModelFile(MODEL_NAME))
//            Log.i(TAG, "Model loaded successfully.")
//        } catch (e: Exception) {
//            Log.e(TAG, "Error loading TFLite model: ${e.message}", e)
//        }
//    }
//
//    /** Summarize text input */
//    fun summarize(text: String): String {
//        if (tflite == null) {
//            return "Model not loaded"
//        }
//
//        // Tokenize
//        val inputIds = tokenizeToIds(text)
//
//        // Prepare input tensor [1, MAX_INPUT_LEN]
//        val inputBuffer = IntArray(MAX_INPUT_LEN) { 0 }
//        for (i in inputIds.indices) {
//            if (i >= MAX_INPUT_LEN) break
//            inputBuffer[i] = inputIds[i]
//        }
//        val inputArray = arrayOf(inputBuffer)
//
//        // Prepare output tensor [1, MAX_OUTPUT_LEN]
//        val outputBuffer = Array(1) { IntArray(MAX_OUTPUT_LEN) { 0 } }
//
//        // Run inference
//        return try {
//            tflite?.run(arrayOf(inputArray), mapOf(0 to outputBuffer))
//            val genIds = outputBuffer[0].toList()
//            decodeIdsToText(genIds)
//        } catch (e: Exception) {
//            Log.e(TAG, "Inference error: ${e.message}", e)
//            "Inference error: ${e.message}"
//        }
//    }
//
//    fun close() {
//        tflite?.close()
//    }
//
//    // --- Model Loader ---
//    private fun loadModelFile(modelFilename: String): MappedByteBuffer {
//        val afd: AssetFileDescriptor = context.assets.openFd(modelFilename)
//        val fis = FileInputStream(afd.fileDescriptor)
//        val fc: FileChannel = fis.channel
//        return fc.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.length)
//    }
//
//    // --- Tokenizer (placeholder, must match Python tokenizer) ---
//    private fun tokenizeToIds(text: String): IntArray {
//        val tokens = text.trim().split("\\s+".toRegex())
//        return tokens.map { tokenToId(it) }.toIntArray()
//    }
//
//    private fun tokenToId(token: String): Int {
//        val hash = token.hashCode()
//        val vocabSize = 32000
//        return (Math.abs(hash) % (vocabSize - 5)) + 5
//    }
//
//    private fun decodeIdsToText(ids: List<Int>): String {
//        val eosId = 1
//        val sb = StringBuilder()
//        for (id in ids) {
//            if (id == eosId) break
//            if (id == 0) continue
//            sb.append(idToToken(id)).append(" ")
//        }
//        return sb.toString().trim()
//    }
//
//    private fun idToToken(id: Int): String {
//        return "<tok$id>"
//    }
//}
