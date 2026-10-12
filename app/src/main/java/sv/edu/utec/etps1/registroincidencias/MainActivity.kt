package sv.edu.utec.etps1.registroincidencias

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    // Estados para almacenar las lecturas del acelerómetro
    var sensorX by remember { mutableStateOf(0f) }
    var sensorY by remember { mutableStateOf(0f) }
    var sensorZ by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    // Registro y limpieza segura del listener del sensor con Jetpack Compose
    DisposableEffect(accelerometer) {
        if (accelerometer == null) {
            onDispose {}
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    sensorX = event.values[0]
                    sensorY = event.values[1]
                    sensorZ = event.values[2]
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registro de Incidencias (Semana 11)",
            style = MaterialTheme.typography.headlineMedium
        )

        // 1. Campo con teclado contextual (Semana 10)
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

        // 2. Interacción táctil de selección (Semana 10)
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
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // 3. NUEVO: Sección del Sensor Acelerómetro (Semana 11)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sensor: Acelerómetro en vivo",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (accelerometer == null) {
                    Text("Aviso: El acelerómetro no está disponible en este dispositivo.")
                } else {
                    Text("Eje X: %.2f".format(sensorX))
                    Text("Eje Y: %.2f".format(sensorY))
                    Text("Eje Z: %.2f".format(sensorZ))
                    Spacer(modifier = Modifier.height(4.dp))

                    val estadoMovimiento = if (kotlin.math.abs(sensorZ - 9.8f) > 2f || kotlin.math.abs(sensorX) > 2f) {
                        "¡Movimiento o inclinación detectada!"
                    } else {
                        "Dispositivo estable"
                    }
                    Text(
                        text = "Estado: $estadoMovimiento",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}