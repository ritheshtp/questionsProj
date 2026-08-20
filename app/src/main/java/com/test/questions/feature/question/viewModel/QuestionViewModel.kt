package com.test.questions.feature.question.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor() : ViewModel() {

    private val _timeLeft = MutableStateFlow(10)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isTimerFinished = MutableStateFlow(false)
    val isTimerFinished: StateFlow<Boolean> = _isTimerFinished.asStateFlow()

    private val _timerFinishedSignal = MutableSharedFlow<Unit>()
    val timerFinishedSignal: SharedFlow<Unit> = _timerFinishedSignal.asSharedFlow()

    private var timerJob: Job? = null

    fun startTimer(initialAnswerSelected: Boolean) {
        if (initialAnswerSelected) {
            _isTimerRunning.value = false
            _isTimerFinished.value = true // It IS finished (skipped)
            _timeLeft.value = 0
            return
        }

        if (timerJob != null) return

        timerJob = viewModelScope.launch {
            _isTimerRunning.value = true
            _isTimerFinished.value = false
            for (i in 10 downTo 0) {
                _timeLeft.value = i
                if (i > 0) {
                    delay(1000)
                }
            }
            _isTimerRunning.value = false
            _isTimerFinished.value = true
            _timerFinishedSignal.emit(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
