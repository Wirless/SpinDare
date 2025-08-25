package com.example.spindare

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.spindare.ui.theme.SpinDareTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpinDareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpinnerGame()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SpinnerGame() {
    val context = LocalContext.current
    val challengeManager = remember { ChallengeManager(context) }
    val vibrator = remember { ContextCompat.getSystemService(context, Vibrator::class.java) }
    
    var isSpinning by remember { mutableStateOf(false) }
    var currentRotation by remember { mutableStateOf(0f) }
    var targetRotation by remember { mutableStateOf(0f) }
    var currentChallenge by remember { mutableStateOf<Challenge?>(null) }
    var currentCategory by remember { mutableStateOf("") }
    
    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = if (isSpinning) 3000 else 0),
        label = "rotation"
    )
    
    // Handle spinning logic
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Generate random rotation (multiple full spins + random section)
            val baseSpins = (3..6).random() // 3-6 full rotations
            val randomSection = (0..3).random() * 90f // Random section (0, 90, 180, 270)
            val randomOffset = (0..89).random().toFloat() // Random position within section
            val finalRotation = baseSpins * 360f + randomSection + randomOffset
            
            targetRotation = finalRotation
            
            // Wait for animation to complete
            kotlinx.coroutines.delay(3000)
            
            // Get challenge based on final rotation
            val challenge = challengeManager.getChallengeFromRotation(finalRotation)
            
            // Update state
            isSpinning = false
            currentRotation = finalRotation
            currentChallenge = challenge
            currentCategory = challenge.category
            
            // Vibrate for challenge reveal
            vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            
            // Show success message
            Toast.makeText(context, "🎉 ${challenge.color.colorName} CHALLENGE! 🎉", Toast.LENGTH_SHORT).show()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title
        Text(
            text = "SPIN & DARE",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Spinner Wheel
        Box(
            modifier = Modifier
                .size(300.dp)
                .clickable(
                    enabled = !isSpinning
                ) {
                    if (!isSpinning) {
                        isSpinning = true
                    }
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation)
            ) {
                drawSpinnerWheel()
            }
            
            // Center pointer
            Canvas(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopCenter)
            ) {
                drawPointer()
            }
        }
        
        // Spin Button
        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                }
            },
            enabled = !isSpinning,
            modifier = Modifier
                .padding(top = 24.dp)
                .height(56.dp)
                .width(200.dp)
        ) {
            Text(
                text = if (isSpinning) "Spinning..." else "SPIN!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Challenge Display
        if (currentChallenge != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getColorForChallenge(currentChallenge!!.color)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentCategory,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = currentChallenge!!.description,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        } else {
            // Initial state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tap the wheel or press SPIN to start!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun DrawScope.drawSpinnerWheel() {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.width / 2
    
    // Draw four colored sections
    val colors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Blue
        Color(0xFF45B7D1), // Green
        Color(0xFFFFE66D)  // Yellow
    )
    
    colors.forEachIndexed { index, color ->
        val startAngle = index * 90f
        val sweepAngle = 90f
        
        rotate(startAngle, center) {
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }
    }
    
    // Draw center circle
    drawCircle(
        color = Color.White,
        radius = radius * 0.15f,
        center = center
    )
}

fun DrawScope.drawPointer() {
    val center = Offset(size.width / 2, size.height / 2)
    
    // Draw triangle pointer
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x, center.y - size.height / 2)
        lineTo(center.x - size.width / 2, center.y)
        lineTo(center.x + size.width / 2, center.y)
        close()
    }
    
    drawPath(
        path = path,
        color = Color.Black
    )
}

fun getColorForChallenge(color: ChallengeColor): Color {
    return when (color) {
        ChallengeColor.RED -> Color(0xFFFF6B6B)
        ChallengeColor.BLUE -> Color(0xFF4ECDC4)
        ChallengeColor.GREEN -> Color(0xFF45B7D1)
        ChallengeColor.YELLOW -> Color(0xFFFFE66D)
    }
}

