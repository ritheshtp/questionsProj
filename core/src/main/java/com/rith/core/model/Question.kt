package com.rith.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Question(
    val question: String,
    val options: List<String>,
    val answer: Int,
    var userAnswer: Int = -1
)

@Serializable
data class QuestionData(
    val questions: List<Question>
)
