package com.test.questions

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class QuestionsAndroidApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant( Timber.DebugTree())

    }

}