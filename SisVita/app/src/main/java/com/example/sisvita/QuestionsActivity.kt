package com.example.sisvita

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.sisvita.data.RetrofitService
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.PreguntasFormularioModel
import com.example.sisvita.ui.theme.SisVitaTheme
import kotlinx.coroutines.launch

class QuestionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SisVitaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuestionsScreen()
                }
            }
        }
    }

    @Composable
    fun QuestionsScreen() {
        var questions by remember { mutableStateOf<List<PreguntasFormularioModel>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                val service = RetrofitServiceFactory.makeRetrofitService()
                try {
                    questions = service.getQuestions()
                    isLoading = false
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(this@QuestionsActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            QuestionsList(questions)
        }
    }

    @Composable
    fun QuestionsList(questions: List<PreguntasFormularioModel>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(questions) { question ->
                QuestionItem(question)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @Composable
    fun QuestionItem(question: PreguntasFormularioModel) {
        Column {
            Text(text = question.pregunta, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            val options = listOf(
                "en absoluto",
                "levemente, no me molesta mucho",
                "moderadamente, fue muy desagradable pero podía soportarlo",
                "severamente, casi no podía soportarlo"
            )
            var selectedOption by remember { mutableStateOf(-1) }

            options.forEachIndexed { index, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == index,
                        onClick = { selectedOption = index }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option)
                }
            }
        }
    }
}