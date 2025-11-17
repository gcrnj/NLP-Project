package com.giotech.nlpproject

import java.io.Serializable

data class NLPResponse(
    val summarized: String = "",
    val tags: List<Tag> = emptyList(),
    val error: String? = null, // if error
): Serializable

data class Tag(
    val text: String,
    val type: String,
): Serializable