package sv.edu.utec.etps1.registroincidencias

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val tag = "TPIS1_LIFECYCLE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate ejecutado")
        setContent {
            MaterialTheme {
                PantallaRegistroIncidencia()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart ejecutado")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume ejecutado")
    }

    override fun onPause() {
        Log.d(tag, "onPause ejecutado")
        super.onPause()
    }

    override fun onStop() {
        Log.d(tag, "onStop ejecutado")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(tag, "onDestroy ejecutado")
        super.onDestroy()
    }
}

@Composable
fun PantallaRegistroIncidencia() {
    // Semana 15: rememberSaveable para conservar el texto ante rotaciones o recreaciones
    var titulo by rememberSaveable { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Técnica") }

    // Estados para los sensores (Semanas 11 y 12)
    var sensorX by remember { mutableStateOf(0f) }
    var sensorY by remember { mutableStateOf(0f) }
    var sensorZ by remember { mutableStateOf(0f) }

    var lightValue by remember { mutableStateOf(0f) }
    var proximityValue by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val proximitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }

    // Control del ciclo de vida y registro seguro de los sensores mediante DisposableEffect
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        sensorX = event.values[0]
                        sensorY = event.values[1]
                        sensorZ = event.values[2]
                    }
                    Sensor.TYPE_LIGHT -> {
                        lightValue = event.values[0]
                    }
                    Sensor.TYPE_PROXIMITY -> {
                        proximityValue = event.values[0]
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        lightSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        proximitySensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registro de Incidencias",
            style = MaterialTheme.typography.headlineMedium
        )

        // 1. Campo de texto con teclado contextual (Semana 10) y persistencia (Semana 15)
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

        // 2. Interacción táctil de selección de categoría (Semana 10)
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

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Tablero de Diagnóstico de Sensores (Semanas 11 y 12)
        Text(
            text = "Diagnóstico del Dispositivo",
            style = MaterialTheme.typography.titleLarge
        )

        // Tarjeta Acelerómetro
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Sensor: Acelerómetro (Movimiento)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                if (accelerometer == null) {
                    Text("Estado: No disponible en este dispositivo.")
                } else {
                    Text("X: %.2f | Y: %.2f | Z: %.2f".format(sensorX, sensorY, sensorZ))
                    val estadoMovimiento = if (kotlin.math.abs(sensorZ - 9.8f) > 2f || kotlin.math.abs(sensorX) > 2f) {
                        "Interpretación: ¡Movimiento o inclinación detectada!"
                    } else {
                        "Interpretación: Dispositivo estable"
                    }
                    Text(text = estadoMovimiento, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Tarjeta Sensor de Luz
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Sensor: Luz Ambiental", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                if (lightSensor == null) {
                    Text("Estado: No disponible en este dispositivo.")
                } else {
                    Text("Iluminación: %.1f lux".format(lightValue))
                    val estadoLuz = when {
                        lightValue > 5000f -> "Interpretación: Ambiente muy iluminado / Luz solar"
                        lightValue > 500f -> "Interpretación: Iluminación de oficina / interiores"
                        else -> "Interpretación: Ambiente oscuro"
                    }
                    Text(text = estadoLuz, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Tarjeta Sensor de Proximidad
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Sensor: Proximidad", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                if (proximitySensor == null) {
                    Text("Estado: No disponible en este dispositivo.")
                } else {
                    Text("Distancia: %.1f cm".format(proximityValue))
                    val estadoProximidad = if (proximityValue < (proximitySensor.maximumRange)) {
                        "Interpretación: Objeto cercano / En bolsillo"
                    } else {
                        "Interpretación: Sin obstáculos cercanos"
                    }
                    Text(text = estadoProximidad, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}