package com.test.questions.navigation

import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {

    private var navigate: ((NavKey) -> Unit)? = null
    private var back: (() -> Unit)? = null

    fun setHandlers(
        navigate: (NavKey) -> Unit,
        back: () -> Unit
    ) {
        this.navigate = navigate
        this.back = back
    }

    fun navigate(route: NavKey) {
        navigate?.invoke(route)
    }

    fun goBack() {
        back?.invoke()
    }
}