package com.test.questions.feature.question.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.test.questions.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class QuestionKey(val questionId: Int,): NavKey

@Module
@InstallIn(ActivityComponent::class)
object QuestionScreenModule {

    @IntoSet
    @Provides
    fun provideEntryProvider(navigator: Navigator): EntryProviderScope<NavKey>.() -> Unit = {
        entry<QuestionKey> {

        }
    }

}
