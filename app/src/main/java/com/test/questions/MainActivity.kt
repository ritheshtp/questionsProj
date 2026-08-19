package com.test.questions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.test.questions.feature.load.navigation.QuestionContainer
import com.test.questions.navigation.Navigator
import com.test.questions.navigation.rememberSharedViewModelStoreNavEntryDecorator
import com.test.questions.ui.theme.QuestionsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestionsTheme {
                CompositionLocalProvider(LocalEntryBuilders provides entryBuilders) {
                    QuestionsApp()
                }
            }
        }
    }

    @PreviewScreenSizes
    @Composable
    fun QuestionsApp() {
        val backStack = rememberNavBackStack(QuestionContainer)
        LaunchedEffect(Unit) {
            navigator.setHandlers(
                navigate = {
                    backStack.add(it)
                },
                back = {
                    backStack.removeLastOrNull()
                }
            )
        }

        Scaffold {
            NavDisplay(
                modifier = Modifier.padding(it),
                backStack = backStack,
                onBack = {
                    backStack.removeLastOrNull()
                },
                entryProvider = entryProvider {
                    entryBuilders.forEach {build -> this.build() }
                },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                    rememberSharedViewModelStoreNavEntryDecorator()
                    )
            )
        }
    }
}

val LocalEntryBuilders =
    staticCompositionLocalOf<Set<EntryProviderScope<NavKey>.() -> Unit>> {
        error("No LocalEntryBuilders provided!")
    }
