package com.test.questions.feature.load.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.test.questions.feature.load.viewModel.QuizMode
import com.test.questions.feature.question.navigation.QuestionKey
import com.test.questions.navigation.LocalSharedViewModelStoreOwner
import com.test.questions.LocalEntryBuilders
import com.test.questions.ui.theme.QuestionsTheme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.test.questions.navigation.rememberSharedViewModelStoreNavEntryDecorator
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionContainerScreen(
    viewModel: QuestionContainerViewModel,
    onClick: () -> Unit = {},
) {
    val entryBuilders = LocalEntryBuilders.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentUserQuestion.collectAsStateWithLifecycle()
    val userAnswers by viewModel.userQuestionAnswers.collectAsStateWithLifecycle()
    val timerProgress by viewModel.timerProgress.collectAsStateWithLifecycle()
    val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()
    val quizMode by viewModel.quizMode.collectAsStateWithLifecycle()
    val streakCount by viewModel.streakCount.collectAsStateWithLifecycle()

    val hasAnswered = remember(userAnswers, currentIndex) {
        userAnswers.containsKey(currentIndex)
    }

    AnimatedContent(
        targetState = state,
        label = "QuestionContainerTransition",
        transitionSpec = {
            if (initialState is QuestionUiState.Ready && targetState is QuestionUiState.InProgress) {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(600)
                ) togetherWith fadeOut(animationSpec = tween(600))
            } else {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            }
        }
    ) { uiState ->
        when (uiState) {
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
                val totalQuestions = remember(uiState) { uiState.questions.size }

                LaunchedEffect(currentIndex) {
                    val current = nestedBackStack.lastOrNull() as? QuestionKey
                    if (current?.questionId != null && current?.questionId != currentIndex) {
                        val diff = abs(currentIndex - current.questionId)
                        if(diff == 1){
                            if(currentIndex < current.questionId){
                                (nestedBackStack as MutableList<NavKey>).removeAt(
                                    nestedBackStack.size - 1
                                )
                            } else {
                                (nestedBackStack as MutableList<NavKey>).add(
                                    QuestionKey(
                                        currentIndex
                                    )
                                )
                            }
                        }
                        else {
                            if(currentIndex < current.questionId){
                                (nestedBackStack as MutableList<NavKey>).removeIf{
                                    it as? QuestionKey != null && it.questionId > currentIndex
                                }
                            } else {
                                (nestedBackStack as MutableList<NavKey>).addAll(
                                    (current.questionId until currentIndex).map {
                                        QuestionKey(it)
                                    }
                                )
                            }
                        }
                    }
                }

                val currentOwner = LocalViewModelStoreOwner.current!!
                CompositionLocalProvider(LocalSharedViewModelStoreOwner provides currentOwner) {
                    Scaffold(
                        topBar = {
                            Column {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = "Question ${currentIndex + 1} of $totalQuestions",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    actions = {
                                        if(quizMode == QuizMode.ANSWER) StreakWidget(streakCount = streakCount)
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                LinearProgressIndicator(
                                    progress = { (currentIndex + 1).toFloat() / totalQuestions },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        },
                        bottomBar = {
                            Column {
                                if (quizMode == QuizMode.ANSWER) {
                                    val animatedProgress by animateFloatAsState(
                                        targetValue = timerProgress,
                                        animationSpec = tween(durationMillis = 100),
                                        label = "TimerProgressAnimation"
                                    )
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = if (timerProgress < 0.3f) Color.Red else MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (quizMode == QuizMode.REVIEW) {
                                        FilledTonalButton(
                                            onClick = {
                                                if (currentIndex > 0) {
                                                    viewModel.updateCurrentQuestion(currentIndex - 1)
                                                }
                                            },
                                            enabled = currentIndex > 0 && !isBusy,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Previous")
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                    } else {
                                        // Hidden in ANSWER mode
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                    
                                    Button(
                                        onClick = {
                                            if (quizMode == QuizMode.ANSWER) {
                                                viewModel.proceedToNextQuestionWithDelay()
                                            } else {
                                                if (currentIndex < uiState.questions.size - 1) {
                                                    viewModel.updateCurrentQuestion(currentIndex + 1)
                                                }
                                                else{
                                                    viewModel.proceedToNextQuestionWithDelay( delayAmount = 0)
                                                }
                                            }
                                        },
                                        enabled = if (quizMode == QuizMode.ANSWER) {
                                            !isBusy
                                        } else {
                                            !isBusy
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            if (currentIndex < uiState.questions.size - 1) "Next"
                                            else "Finish"
                                        )
                                    }
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
                                /*if (currentIndex > 0) {
                                    viewModel.updateCurrentQuestion(currentIndex - 1)
                                }*/
                            },
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator(),
                                rememberSharedViewModelStoreNavEntryDecorator()
                            ),
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
}

@Composable
fun StreakWidget(streakCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(end = 16.dp)
    ) {
        val streakColor by animateColorAsState(
            targetValue = if (streakCount > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
            animationSpec = tween(500),
            label = "StreakColor"
        )

        Text(
            text = "🔥 $streakCount",
            style = MaterialTheme.typography.titleMedium,
            color = streakColor,
            textAlign = TextAlign.Center
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { index ->
                val isLit = streakCount > index
                val badgeColor by animateColorAsState(
                    targetValue = if (isLit) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    animationSpec = tween(500),
                    label = "BadgeColor"
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLit) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onTertiaryContainer)
                        )
                    }
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
