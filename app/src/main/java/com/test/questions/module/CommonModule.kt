package com.test.questions.module

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

@Module
@InstallIn(ActivityRetainedComponent::class)
object CommonModule {




}