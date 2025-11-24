package com.giotech.nlpproject

import java.io.Serializable

data class NLPResponse(
    val summary: String = "",
    val entities: HashMap<String, List<String>> = hashMapOf(),
    val error: String? = null, // if error
): Serializable

data class Tag(
    val text: String,
    val type: String,
): Serializable