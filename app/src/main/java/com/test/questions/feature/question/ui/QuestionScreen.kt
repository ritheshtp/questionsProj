package com.test.questions.feature.question.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.questions.feature.load.viewModel.QuizMode
import com.test.questions.ui.theme.QuestionsTheme

@Composable
fun QuestionScreen(
    question: String,
    options: List<String>,
    userAnswer: Int,
    correctAnswer: Int,
    timerProgress: Float,
    showCorrectAnswer: Boolean,
    quizMode: QuizMode,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        options.forEachIndexed { index, option ->
            val isSelected = userAnswer == index
            val isCorrect = index == correctAnswer
            val showResult = showCorrectAnswer || quizMode == QuizMode.REVIEW

            val borderColor = when {
                showResult && index == correctAnswer -> Color.Green
                showResult && isSelected && !isCorrect -> Color.Red
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }

            val containerColor = when {
                showResult && index == correctAnswer -> Color.Green.copy(alpha = 0.1f)
                showResult && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.1f)
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                else -> MaterialTheme.colorScheme.surface
            }

            val canSelect = quizMode == QuizMode.ANSWER && !showCorrectAnswer
            
            OutlinedCard(
                onClick = { if (canSelect) onAnswerSelected(index) },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    width = if (isSelected || (showResult && isCorrect)) 2.dp else 1.dp,
                    color = borderColor
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = containerColor
                ),
                enabled = canSelect
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    if (showResult && isCorrect) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Correct",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Green
                        )
                    } else if (showResult && isSelected && !isCorrect) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Wrong",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}
