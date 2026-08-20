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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import com.test.questions.feature.result.navigation.ResultKey
import com.test.questions.navigation.Navigator
import timber.log.Timber
import javax.inject.Inject

sealed class QuestionUiState {
    object Loading : QuestionUiState()
    data class Ready(val questions: List<Question>) : QuestionUiState()
    data class InProgress(val questions: List<Question>) : QuestionUiState()
}

enum class QuizMode {
    ANSWER, REVIEW
}

@HiltViewModel
class QuestionContainerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionUiState>(QuestionUiState.Loading)
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()
    private val _userQuestionAnswers: MutableStateFlow<Map<Int, Int>> = MutableStateFlow(emptyMap());
    val userQuestionAnswers: StateFlow<Map<Int, Int>> = _userQuestionAnswers.asStateFlow()

    private val _currentUserQuestion: MutableStateFlow<Int> = MutableStateFlow(0);

    val currentUserQuestion: StateFlow<Int> = _currentUserQuestion.asStateFlow()

    private val _timerProgress = MutableStateFlow(1f)
    val timerProgress: StateFlow<Float> = _timerProgress.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _showCorrectAnswer = MutableStateFlow(false)
    val showCorrectAnswer: StateFlow<Boolean> = _showCorrectAnswer.asStateFlow()

    private val _quizMode = MutableStateFlow(QuizMode.ANSWER)
    val quizMode: StateFlow<QuizMode> = _quizMode.asStateFlow()

    private var timerJob: Job? = null
    private val MAX_TIME = 10L

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
                Timber.tag("ERROR").d("loadQuestions: ${e.message}")
            }
        }
    }

    fun proceed() {
        val currentState = _uiState.value
        if (currentState is QuestionUiState.Ready) {
            _uiState.value = QuestionUiState.InProgress(currentState.questions)
        }
    }

    fun updateUserAnswer(questionIndex: Int, answerIndex: Int) {
        if (_quizMode.value != QuizMode.ANSWER) return
        val currentState = _uiState.value
        if (currentState is QuestionUiState.InProgress) {
            _userQuestionAnswers.value += (questionIndex to answerIndex)
        }
    }

    fun updateCurrentQuestion(questionIndex: Int) {
        stopTimer()
        _isBusy.value = false
        _showCorrectAnswer.value = false
        _timerProgress.value = 1f
        _currentUserQuestion.value = questionIndex
    }

    fun startTimer() {
        if (_quizMode.value != QuizMode.ANSWER) return
        if (timerJob?.isActive == true) return
        
        timerJob = viewModelScope.launch {
            _timerProgress.value = 1f
            
            val totalTicks = MAX_TIME * 10 // 100ms intervals for smooth progress
            for (tick in totalTicks downTo 0) {
                if (_isBusy.value) break // Stop if we become busy (e.g. answer reveal)
                _timerProgress.value = tick.toFloat() / totalTicks
                delay(100)
            }
            
            if (!_isBusy.value) {
                proceedToNextQuestionWithDelay(isAutomatic = true)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun proceedToNextQuestionWithDelay(isAutomatic: Boolean = false,noDelay: Boolean = false) {
        if (_isBusy.value) return
        
        viewModelScope.launch {
            _isBusy.value = true
            stopTimer()
            _showCorrectAnswer.value = true
            
            if(!noDelay) delay(1000) // 1 second delay as requested
            
            val currentState = _uiState.value
            if (currentState is QuestionUiState.InProgress) {
                val isLastQuestion = _currentUserQuestion.value >= currentState.questions.size - 1
                if (isLastQuestion) {
                    val correctCount = currentState.questions.indices.count { index ->
                        userQuestionAnswers.value[index] == currentState.questions[index].answer
                    }
                    val skippedCount = currentState.questions.indices.count { index ->
                        !userQuestionAnswers.value.containsKey(index) || userQuestionAnswers.value[index] == -1
                    }
                    _quizMode.value = QuizMode.REVIEW
                    navigator.navigate(ResultKey(correctCount, currentState.questions.size, skippedCount))
                    _isBusy.value = false
                } else {
                    // Move to next question
                    updateCurrentQuestion(_currentUserQuestion.value + 1)
                }
            }
        }
    }

    fun resetQuiz() {
        stopTimer()
        val questions = when (val state = _uiState.value) {
            is QuestionUiState.InProgress -> state.questions
            is QuestionUiState.Ready -> state.questions
            else -> emptyList()
        }
        
        _userQuestionAnswers.value = emptyMap()
        _currentUserQuestion.value = 0
        _timerProgress.value = 1f
        _isBusy.value = false
        _showCorrectAnswer.value = false
        _quizMode.value = QuizMode.ANSWER
        
        if (questions.isNotEmpty()) {
            _uiState.value = QuestionUiState.Ready(questions)
        } else {
            loadQuestions()
        }
    }
}
