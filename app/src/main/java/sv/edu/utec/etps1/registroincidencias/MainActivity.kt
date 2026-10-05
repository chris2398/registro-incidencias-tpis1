package sv.edu.utec.etps1.registroincidencias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaRegistroIncidencia()
            }
        }
    }
}

@Composable
fun PantallaRegistroIncidencia() {
    var titulo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Técnica") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Registro de Incidencias",
            style = MaterialTheme.typography.headlineMedium
        )

        // 1. Campo con teclado contextual (KeyboardOptions)
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la incidencia") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Interacción táctil de selección con retroalimentación visual
        Text(
            text = "Categoría seleccionada: $categoria",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Técnica", "Mantenimiento", "Seguridad").forEach { opcion ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (categoria == opcion)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { categoria = opcion }
                ) {
                    Text(
                        text = opcion,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}