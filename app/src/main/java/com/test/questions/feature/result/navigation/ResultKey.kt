package com.test.questions.feature.result.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import com.test.questions.feature.load.navigation.QuestionContainer
import com.test.questions.feature.load.viewModel.QuestionContainerViewModel
import com.test.questions.feature.result.ui.ResultScreen
import com.test.questions.navigation.Navigator
import com.test.questions.navigation.SharedViewModelStoreNavEntryDecorator
import com.test.questions.navigation.SharedViewModelStoreNavEntryDecorator.Companion.ParentKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class ResultKey(val correctCount: Int, val totalCount: Int) : NavKey

@Module
@InstallIn(ActivityComponent::class)
object ResultScreenModule {

    @IntoSet
    @Provides
    fun provideEntryProvider(navigator: Navigator): EntryProviderScope<NavKey>.() -> Unit = {
        entry<ResultKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(1000)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                }
                put(ParentKey, QuestionContainer.toString())
            }
        ) { key ->
            val viewModel = hiltViewModel<QuestionContainerViewModel>()
            ResultScreen(
                correctCount = key.correctCount,
                totalCount = key.totalCount,
                onFinish = {
                    navigator.goBack()
                }
            )
        }
    }
}
