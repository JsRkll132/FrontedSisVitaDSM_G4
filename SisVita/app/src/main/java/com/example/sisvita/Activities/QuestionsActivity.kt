package com.example.sisvita.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.ContentFormModel
import com.example.sisvita.data.models.FormularioEnvioModel
import com.example.sisvita.data.models.PreguntasFormularioModel
import com.example.sisvita.data.models.Respuesta
import com.example.sisvita.getToken
import com.example.sisvita.getUserIdFromToken
import com.example.sisvita.ui.theme.SisVitaTheme
import kotlinx.coroutines.launch

data class Answer(val questionId: Int, var selectedOption: String = "", var score: Int = 0)

class QuestionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val formularioId = intent.getIntExtra("FORM_ID", 1)
        val token = getToken(this)
        val userId = token?.let { getUserIdFromToken(it) }

        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish() // Cierra la actividad si no hay usuario autenticado
            return
        }

        setContent {
            SisVitaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuestionsScreen(formularioId, userId)
                }
            }
        }
    }

    @Composable
    fun QuestionsScreen(formularioId: Int, userId: Int) {
        var questions by remember { mutableStateOf<List<PreguntasFormularioModel>>(emptyList()) }
        var contentForm by remember { mutableStateOf<List<ContentFormModel>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var answers by remember { mutableStateOf<List<Answer>>(emptyList()) }

        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                val service = RetrofitServiceFactory.makeRetrofitService()
                try {
                    questions = service.getQuestions(formularioId)
                    contentForm = service.getContentForm(formularioId)
                    answers = questions.map { Answer(it.id) } // Inicializa las respuestas con IDs de preguntas
                    isLoading = false
                } catch (e: Exception) {
                    Log.e("QuestionsActivity", "Error loading data", e)
                    isLoading = false
                    Toast.makeText(this@QuestionsActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            QuestionsList(questions, answers, contentForm) { questionId, selectedOption, score ->
                val updatedAnswers = answers.map { answer ->
                    if (answer.questionId == questionId) {
                        answer.copy(selectedOption = selectedOption, score = score)
                    } else {
                        answer
                    }
                }
                answers = updatedAnswers
            }
        }
    }

    @Composable
    fun QuestionsList(
        questions: List<PreguntasFormularioModel>,
        answers: List<Answer>,
        contentForm: List<ContentFormModel>,
        onAnswerSelected: (Int, String, Int) -> Unit
    ) {
        val token = getToken(this)
        val userId = token?.let { getUserIdFromToken(it) }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier.weight(1.0f),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(questions) { index, question ->
                    val answer = answers.find { it.questionId == question.id }
                    val options = contentForm.filter { it.formulario_id == question.formulario_id }
                    QuestionItem(index, question, answer, options) { selectedOption, score ->
                        onAnswerSelected(question.id, selectedOption, score)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (userId != null) {
                SubmitButton(questions, answers, userId)
            }
        }
    }

    @Composable
    fun QuestionItem(
        index: Int,
        question: PreguntasFormularioModel,
        answer: Answer?,
        options: List<ContentFormModel>,
        onOptionSelected: (String, Int) -> Unit
    ) {
        Column {
            Text(text = "\t\t\t${index + 1}. ${question.pregunta}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = answer?.selectedOption == option.respuesta_formulario,
                        onClick = {
                            onOptionSelected(option.respuesta_formulario, option.puntaje)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option.respuesta_formulario)
                }
            }
        }
    }

    @Composable
    fun SubmitButton(
        questions: List<PreguntasFormularioModel>,
        answers: List<Answer>,
        userId: Int
    ) {
        Button(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            onClick = {
                if (answers.all { it.selectedOption.isNotEmpty() }) {
                    val formularioEnvio = FormularioEnvioModel(
                        formulario_id = questions.first().formulario_id,
                        paciente_id = userId, // Usa el ID real del paciente
                        respuestas = answers.map {
                            Respuesta(
                                paciente_id = userId, // Usa el ID real del paciente
                                pregunta_id = it.questionId,
                                puntuacion = it.score,
                                respuesta = it.selectedOption
                            )
                        }
                    )
                    lifecycleScope.launch {
                        val service = RetrofitServiceFactory.makeRetrofitService()
                        try {
                            val response = service.submitForm(formularioEnvio)
                            Log.d("RESPONSE STATUS", response.result_status.toString())
                            if (response.result_status == 1) {
                                Toast.makeText(this@QuestionsActivity, "Formulario enviado exitosamente", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@QuestionsActivity, "No es posible añadir el formulario", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@QuestionsActivity, "Error al enviar el formulario", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@QuestionsActivity, "Responde todas las preguntas antes de enviar", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Enviar Respuestas")
        }
    }
}
