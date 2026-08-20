package com.test.questions.feature.load.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.test.questions.feature.load.ui.QuestionContainerScreen
import com.test.questions.feature.load.viewModel.QuestionContainerViewModel
import com.test.questions.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object QuestionContainer: NavKey

@Module
@InstallIn(ActivityComponent::class)
object LoadScreenModule {

    @IntoSet
    @Provides
    fun provideEntryProvider(navigator: Navigator): EntryProviderScope<NavKey>.() -> Unit = {
        entry<QuestionContainer>(
            clazzContentKey = {key -> key.toString() }
        ) {
            val viewModel = hiltViewModel<QuestionContainerViewModel>()
            QuestionContainerScreen(viewModel)
        }
    }

}