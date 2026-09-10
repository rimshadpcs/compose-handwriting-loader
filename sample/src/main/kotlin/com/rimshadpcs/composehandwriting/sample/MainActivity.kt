package com.rimshadpcs.composehandwriting.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rimshadpcs.composehandwriting.HandwritingLoader

private val swatches = listOf(
    Color.Black,
    Color(0xFF1E88E5),
    Color(0xFFD81B60),
    Color(0xFF43A047),
    Color(0xFFF4511E),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SampleScreen()
                }
            }
        }
    }
}

@Composable
private fun SampleScreen() {
    var text by remember { mutableStateOf("satia") }
    var fontSize by remember { mutableIntStateOf(48) }
    var durationMillis by remember { mutableIntStateOf(1500) }
    var color by remember { mutableStateOf(swatches.first()) }
    var replayKey by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            HandwritingLoader(
                text = text,
                key = text to replayKey,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                durationMillis = durationMillis,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Text") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Font size: ${fontSize}sp")
            Slider(
                value = fontSize.toFloat(),
                onValueChange = { fontSize = it.toInt() },
                valueRange = 16f..96f,
            )

            Text("Duration: ${durationMillis}ms")
            Slider(
                value = durationMillis.toFloat(),
                onValueChange = { durationMillis = it.toInt() },
                valueRange = 400f..4000f,
            )

            Text("Color")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                swatches.forEach { swatch ->
                    val border = if (swatch == color) {
                        Modifier.border(3.dp, Color.Gray, CircleShape).padding(3.dp)
                    } else {
                        Modifier.padding(3.dp)
                    }
                    Surface(
                        onClick = { color = swatch },
                        modifier = Modifier.size(38.dp).then(border),
                        color = swatch,
                        shape = CircleShape,
                    ) {}
                }
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { replayKey++ },
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Replay")
            }
        }
    }
}
