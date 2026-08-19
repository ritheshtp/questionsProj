package com.test.questions.feature.load.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.test.questions.feature.load.viewModel.QuestionContainerViewModel

@Composable
fun QuestionContainerScreen(
    viewModel: QuestionContainerViewModel= hiltViewModel(),
    onClick: () -> Unit = {},
){
    
}