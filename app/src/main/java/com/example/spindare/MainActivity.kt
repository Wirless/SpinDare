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
import androidx.compose.ui.draw.scale
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
    var isWinningColor by remember { mutableStateOf(false) }
    var winningColor by remember { mutableStateOf<ChallengeColor?>(null) }
    var wheelScale by remember { mutableStateOf(1f) }
    var wheelWobble by remember { mutableStateOf(0f) }
    var colorOverlayProgress by remember { mutableStateOf(0f) }
    
    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = if (isSpinning) 4000 else 300),
        label = "rotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = wheelScale,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )
    
    val wobble by animateFloatAsState(
        targetValue = wheelWobble,
        animationSpec = tween(durationMillis = 200),
        label = "wobble"
    )
    
    val overlayProgress by animateFloatAsState(
        targetValue = colorOverlayProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "overlay"
    )
    
    // Handle spinning logic with dramatic color change
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
            
            // Start color overlay BEFORE spinning ends (like Pac-Man eating the wheel)
            val overlayStartTime = delayTime - 1000L // Start overlay 1 second before end
            kotlinx.coroutines.delay(overlayStartTime)
            
            // FIRST: Generate which color the challenge should be
            val targetColor = listOf(ChallengeColor.RED, ChallengeColor.BLUE, ChallengeColor.GREEN, ChallengeColor.YELLOW).random()
            val challenge = challengeManager.getRandomChallengeByColor(targetColor)
            
            // Start the Pac-Man effect - color gradually covers the wheel
            winningColor = targetColor
            isWinningColor = true
            colorOverlayProgress = 0f
            
            // THEN: Calculate the rotation needed to land on that color
            val targetRotationForColor = calculateRotationForColor(targetColor)
            targetRotation = currentRotation + targetRotationForColor
            
            // Continue the overlay animation while finishing the spin
            kotlinx.coroutines.delay(1000L)
            colorOverlayProgress = 1f
            
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
    
    // Handle dramatic color change effect
    LaunchedEffect(currentChallenge) {
        if (currentChallenge != null) {
            // Add ripple effect (10% size increase)
            wheelScale = 1.1f
            kotlinx.coroutines.delay(300)
            wheelScale = 1f
            
            // Add wobble effect with sinus wave
            repeat(5) {
                wheelWobble = 5f
                kotlinx.coroutines.delay(100)
                wheelWobble = -5f
                kotlinx.coroutines.delay(100)
            }
            wheelWobble = 0f
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
                    .rotate(rotation + wheelWobble)
                    .scale(wheelScale)
            ) {
                if (isWinningColor && winningColor != null) {
                    drawPacManWheel(winningColor!!, overlayProgress)
                } else {
                    drawSpinnerWheel()
                }
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
        
        // Debug info - show current rotation and target color
        if (currentChallenge != null) {
            Text(
                text = "Wheel landed on: ${currentChallenge!!.color.colorName} | Rotation: ${(currentRotation % 360).toInt()}°",
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
                    // Reset to show all colors while spinning
                    isWinningColor = false
                    winningColor = null
                    colorOverlayProgress = 0f
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
        ChallengeColor.GREEN -> Color(0xFF4CAF50)
        ChallengeColor.YELLOW -> Color(0xFFFFE66D)
    }
}

fun calculateAngle(center: androidx.compose.ui.geometry.Offset, point: androidx.compose.ui.geometry.Offset): Float {
    val dx = point.x - center.x
    val dy = point.y - center.y
    return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
}



fun calculateRotationForColor(targetColor: ChallengeColor): Float {
    // Calculate the rotation needed to land on a specific color
    // The wheel sections are:
    // RED: 0°-90° (top-right)
    // BLUE: 90°-180° (bottom-right)  
    // GREEN: 180°-270° (bottom-left)
    // YELLOW: 270°-360° (top-left)
    
    // To land on a color, we need to rotate so that section is at the top (0°)
    // We also want some randomness within that section for natural feel
    
    val baseRotation = when (targetColor) {
        ChallengeColor.RED -> 0f
        ChallengeColor.BLUE -> 90f
        ChallengeColor.GREEN -> 180f
        ChallengeColor.YELLOW -> 270f
    }
    
    // Add random offset within the section (0-89 degrees)
    val randomOffset = (0..89).random().toFloat()
    
    // Add multiple full rotations for dramatic effect (3-6 full spins)
    val fullSpins = (3..6).random() * 360f
    
    return fullSpins + baseRotation + randomOffset
}

fun DrawScope.drawPacManWheel(winningColor: ChallengeColor, overlayProgress: Float) {
    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
    val radius = size.width / 2
    
    // Get the winning color
    val color = when (winningColor) {
        ChallengeColor.RED -> Color(0xFFFF6B6B)
        ChallengeColor.BLUE -> Color(0xFF4ECDC4)
        ChallengeColor.GREEN -> Color(0xFF4CAF50)
        ChallengeColor.YELLOW -> Color(0xFFFFE66D)
    }
    
    // Draw the original wheel first (background)
    drawSpinnerWheel()
    
    // Draw the Pac-Man effect - winning color gradually covers the wheel
    // overlayProgress goes from 0f to 1f, creating a sweeping effect
    
    // Calculate the sweep angle based on progress
    val sweepAngle = overlayProgress * 360f
    
    // Draw the winning color as a pie slice that grows
    drawArc(
        color = color,
        startAngle = -90f, // Start from top
        sweepAngle = sweepAngle,
        useCenter = true,
        topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
    )
    
    // Draw center circle
    drawCircle(
        color = Color.White,
        radius = radius * 0.15f,
        center = center
    )
}

