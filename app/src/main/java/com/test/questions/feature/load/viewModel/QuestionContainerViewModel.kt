package com.test.questions.feature.load.viewModel

import android.content.Context
import androidx.collection.buildIntList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rith.core.model.Question
import com.rith.core.model.QuestionData
import com.test.questions.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed class QuestionUiState {
    object Loading : QuestionUiState()
    data class Ready(val questions: List<Question>) : QuestionUiState()
    data class InProgress(val questions: List<Question>) : QuestionUiState()
}

@HiltViewModel
class QuestionContainerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionUiState>(QuestionUiState.Loading)
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            try {
                val jsonString = context.resources.openRawResource(R.raw.data)
                    .bufferedReader().use { it.readText() }
                val questionData = json.decodeFromString<QuestionData>(jsonString)
                _uiState.value = QuestionUiState.Ready(questionData.questions)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun proceed() {
        val currentState = _uiState.value
        if (currentState is QuestionUiState.Ready) {
            _uiState.value = QuestionUiState.InProgress(currentState.questions)
        }
    }
}
