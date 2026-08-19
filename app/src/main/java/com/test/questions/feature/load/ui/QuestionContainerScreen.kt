package com.test.questions.feature.load.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.test.questions.feature.load.viewModel.QuestionContainerViewModel
import com.test.questions.feature.load.viewModel.QuestionUiState
import com.test.questions.feature.question.navigation.QuestionKey
import com.test.questions.navigation.LocalSharedViewModelStoreOwner
import com.test.questions.LocalEntryBuilders
import com.test.questions.ui.theme.QuestionsTheme

@Composable
fun QuestionContainerScreen(
    viewModel: QuestionContainerViewModel,
    onClick: () -> Unit = {},
) {
    val entryBuilders = LocalEntryBuilders.current
    val state by viewModel.uiState.collectAsState()

    when (val uiState = state) {
        is QuestionUiState.Loading -> {
            LoadingScreen()
        }

        is QuestionUiState.Ready -> {
            ReadyScreen(
                questionCount = uiState.questions.size,
                onStartClick = { viewModel.proceed() }
            )
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

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Preparing your quiz...",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "We're getting things ready for you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReadyScreen(
    questionCount: Int,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ready to Start?",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$questionCount",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Questions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        FilledTonalButton(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Start Quiz",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    QuestionsTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadyScreenPreview() {
    QuestionsTheme {
        ReadyScreen(questionCount = 10, onStartClick = {})
    }
}
