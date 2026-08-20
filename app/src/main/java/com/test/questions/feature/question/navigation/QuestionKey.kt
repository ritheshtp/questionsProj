package com.test.questions.feature.question.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.test.questions.feature.load.viewModel.QuestionContainerViewModel
import com.test.questions.feature.load.viewModel.QuestionUiState
import com.test.questions.feature.question.ui.QuestionScreen
import com.test.questions.feature.question.viewModel.QuestionViewModel
import com.test.questions.navigation.LocalSharedViewModelStoreOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class QuestionKey(val questionId: Int) : NavKey

@Module
@InstallIn(ActivityComponent::class)
object QuestionScreenModule {

    @IntoSet
    @Provides
    fun provideEntryProvider(): EntryProviderScope<NavKey>.() -> Unit = {
        entry<QuestionKey> { key ->
            val parentOwner = LocalSharedViewModelStoreOwner.current
            val containerViewModel = hiltViewModel<QuestionContainerViewModel>(viewModelStoreOwner = parentOwner)
            val questionViewModel = hiltViewModel<QuestionViewModel>()

            val state by containerViewModel.uiState.collectAsStateWithLifecycle()
            val userAnswers by containerViewModel.userQuestionAnswers.collectAsStateWithLifecycle()
            val userAnswer = remember(userAnswers, state) { userAnswers[key.questionId] ?: -1 }

            val timeLeft by questionViewModel.timeLeft.collectAsStateWithLifecycle()
            val isTimerRunning by questionViewModel.isTimerRunning.collectAsStateWithLifecycle()
            val isTimerFinished by questionViewModel.isTimerFinished.collectAsStateWithLifecycle()
            
            val lifecycleOwner = LocalLifecycleOwner.current
            if (state is QuestionUiState.InProgress) {
                (state as QuestionUiState.InProgress).questions.getOrNull(key.questionId)?.let { question ->
                    QuestionScreen(
                        question = question.question,
                        options = question.options,
                        userAnswer = userAnswer,
                        correctAnswer = question.answer,
                        timeLeft = timeLeft,
                        onAnswerSelected = { answerIndex ->
                            containerViewModel.updateUserAnswer(key.questionId, answerIndex)
                        }
                    )
                }
            }
        }
    }

}

