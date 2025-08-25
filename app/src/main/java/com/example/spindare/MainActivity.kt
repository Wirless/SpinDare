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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.spindare.ui.theme.SpinDareTheme
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.sqrt

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
        animationSpec = tween(durationMillis = if (isSpinning) 4000 else 300),
        label = "rotation"
    )
    
    // Handle spinning logic
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Check if this is a manual drag (targetRotation already set)
            val isManualDrag = targetRotation != currentRotation
            
            if (!isManualDrag) {
                // Generate random rotation for automatic spinning
                val baseSpins = (8..15).random() // 8-15 full rotations for crazy spinning!
                val randomSection = (0..3).random() * 90f // Random section (0, 90, 180, 270)
                val randomOffset = (0..89).random().toFloat() // Random position within section
                val finalRotation = baseSpins * 360f + randomSection + randomOffset
                targetRotation = currentRotation + finalRotation
                
                // Ensure we have significant rotation for visual effect
                if (finalRotation < 1000f) {
                    targetRotation = currentRotation + 1000f + (0..360).random().toFloat()
                }
            }
            
            // Wait for animation to complete (shorter for manual drags)
            val delayTime = if (isManualDrag) 2000L else 4000L
            kotlinx.coroutines.delay(delayTime)
            
            // Get challenge based on VISUAL COLOR the pointer is pointing at
            val visualColor = getVisualColorAtPointer(targetRotation)
            val challenge = challengeManager.getRandomChallengeByColor(visualColor)
            
            // Update state
            isSpinning = false
            currentRotation = targetRotation
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
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (!isSpinning) {
                                // Start manual spinning
                                val center = androidx.compose.ui.geometry.Offset(150f, 150f) // Fixed center for 300dp wheel
                                val angle = calculateAngle(center, offset)
                                val newRotation = currentRotation + angle
                                targetRotation = newRotation
                                isSpinning = true
                            }
                        },
                        onDrag = { change, _ ->
                            if (!isSpinning) {
                                val center = androidx.compose.ui.geometry.Offset(150f, 150f) // Fixed center for 300dp wheel
                                val angle = calculateAngle(center, change.position)
                                val newRotation = currentRotation + angle
                                targetRotation = newRotation
                            }
                        },
                        onDragEnd = {
                            if (!isSpinning) {
                                // Add some momentum for natural feel
                                val momentum = (targetRotation - currentRotation) * 2f
                                targetRotation = currentRotation + momentum
                                isSpinning = true
                            }
                        }
                    )
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
                    .size(30.dp)
                    .align(Alignment.TopCenter)
            ) {
                drawPointer()
            }
        }
        
        // Instructions
        Text(
            text = "Drag the wheel like a DJ deck or tap SPIN!",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        
        // Debug info - show current rotation and detected color
        if (currentChallenge != null) {
            val visualColor = getVisualColorAtPointer(currentRotation)
            Text(
                text = "Pointer at: ${visualColor.colorName} | Rotation: ${(currentRotation % 360).toInt()}°",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
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
                .padding(top = 8.dp)
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
    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
    val radius = size.width / 2
    
    // Draw four colored sections
    val colors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Blue
        Color(0xFF4CAF50), // Green (actually green now!)
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
                topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
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
    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
    
    // Draw triangle pointer with better visibility
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x, center.y - size.height / 2)
        lineTo(center.x - size.width / 2, center.y)
        lineTo(center.x + size.width / 2, center.y)
        close()
    }
    
    // Draw white outline first
    drawPath(
        path = path,
        color = Color.White
    )
    
    // Draw black fill
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

fun calculateAngle(center: androidx.compose.ui.geometry.Offset, point: androidx.compose.ui.geometry.Offset): Float {
    val dx = point.x - center.x
    val dy = point.y - center.y
    return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
}



fun getVisualColorAtPointer(rotation: Float): ChallengeColor {
    // This function shows what color is ACTUALLY visually at the pointer
    // based on the wheel drawing logic
    
    val normalizedRotation = (rotation % 360 + 360) % 360
    
    // Looking at the wheel drawing code:
    // colors.forEachIndexed { index, color ->
    //     val startAngle = index * 90f  // 0°, 90°, 180°, 270°
    //     val sweepAngle = 90f
    // }
    // 
    // So the sections are:
    // index 0: RED starts at 0° (top-right)
    // index 1: BLUE starts at 90° (bottom-right)  
    // index 2: GREEN starts at 180° (bottom-left)
    // index 3: YELLOW starts at 270° (top-left)
    
    // The pointer is at the top (0°), so we need to see what section is at the top
    // after the wheel has rotated. Since the wheel rotates clockwise, we need to
    // find which section is now at the top position.
    
    // The rotation is the angle the wheel has turned, so the section at the top
    // is the one that was at (rotation) degrees from the original top position
    val sectionAtTop = normalizedRotation
    
    return when {
        sectionAtTop >= 0 && sectionAtTop < 90 -> ChallengeColor.RED
        sectionAtTop >= 90 && sectionAtTop < 180 -> ChallengeColor.BLUE
        sectionAtTop >= 180 && sectionAtTop < 270 -> ChallengeColor.GREEN
        sectionAtTop >= 270 && sectionAtTop < 360 -> ChallengeColor.YELLOW
        else -> ChallengeColor.RED // Fallback
    }
}

