package com.test.questions.feature.load.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.test.questions.feature.load.viewModel.QuestionContainerViewModel
import com.test.questions.feature.load.viewModel.QuestionUiState
import com.test.questions.feature.question.navigation.QuestionKey
import com.test.questions.navigation.LocalSharedViewModelStoreOwner
import com.test.questions.LocalEntryBuilders

@Composable
fun QuestionContainerScreen(
    viewModel: QuestionContainerViewModel,
    onClick: () -> Unit = {},
) {
    val entryBuilders = LocalEntryBuilders.current
    val state by viewModel.uiState.collectAsState()

    when (val uiState = state) {
        is QuestionUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is QuestionUiState.Ready -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { viewModel.proceed() }) {
                    Text("Proceed")
                }
            }
        }
        is QuestionUiState.InProgress -> {
            val nestedBackStack = rememberNavBackStack(QuestionKey(0))
            var currentIndex by rememberSaveable { mutableIntStateOf(0) }

            val currentOwner = LocalViewModelStoreOwner.current!!
            CompositionLocalProvider(LocalSharedViewModelStoreOwner provides currentOwner) {
                Scaffold(
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    if (currentIndex > 0) {
                                        currentIndex--
                                        (nestedBackStack as MutableList<NavKey>).removeAt(nestedBackStack.size - 1)
                                    }
                                },
                                enabled = currentIndex > 0
                            ) {
                                Text("Previous")
                            }
                            Button(
                                onClick = {
                                    if (currentIndex < uiState.questions.size - 1) {
                                        currentIndex++
                                        (nestedBackStack as MutableList<NavKey>).add(QuestionKey(currentIndex))
                                    }
                                },
                                enabled = currentIndex < uiState.questions.size - 1
                            ) {
                                Text("Next")
                            }
                        }
                    }
                ) { padding ->
                    NavDisplay(
                        modifier = Modifier.padding(padding),
                        backStack = nestedBackStack,
                        entryProvider = entryProvider {
                            entryBuilders.forEach { builder ->
                                builder.invoke(this)
                            }
                        },
                        onBack = {
                            if (currentIndex > 0) {
                                currentIndex--
                                (nestedBackStack as MutableList<NavKey>).removeAt(nestedBackStack.size - 1)
                            }
                        },
                        transitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(600)
                            ) togetherWith
                                    slideOutHorizontally(targetOffsetX = { -it },
                                        animationSpec = tween(600)
                                    )
                        },
                        popTransitionSpec = {
                            slideInHorizontally(initialOffsetX = { -it },
                                animationSpec = tween(600)) togetherWith
                                    slideOutHorizontally(targetOffsetX = { it },
                                        animationSpec = tween(600))
                        },
                        predictivePopTransitionSpec = {
                            slideInHorizontally(initialOffsetX = { -it },
                                animationSpec = tween(600)) togetherWith
                                    slideOutHorizontally(targetOffsetX = { it },
                                        animationSpec = tween(600))
                        },
                    )
                }
            }
        }
    }
}
