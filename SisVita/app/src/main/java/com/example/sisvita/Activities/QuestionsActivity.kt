package com.example.sisvita.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(questions) { index, question ->
                    QuestionItem(index, question)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = {
                    // Acción al hacer clic en el botón
                }
            ) {
                Text("Enviar Respuestas")
            }
        }
    }

    @Composable
    fun QuestionItem(index : Int ,question: PreguntasFormularioModel) {
        Column {
            Text(text = "\t\t\t${index+1}. ${question.pregunta}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            val options = listOf(
                "En absoluto",
                "Levemente, no me molesta mucho",
                "Moderadamente, fue muy desagradable pero podía soportarlo",
                "Severamente, casi no podía soportarlo"
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