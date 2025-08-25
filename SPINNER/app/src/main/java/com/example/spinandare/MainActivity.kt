package com.example.spinandare

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.spinandare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var challengeManager: ChallengeManager
    private lateinit var vibrator: Vibrator
    
    private var isSpinning = false
    private var currentRotation = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize managers and services
        challengeManager = ChallengeManager(this)
        vibrator = ContextCompat.getSystemService(this, Vibrator::class.java)!!
        
        // Set up UI
        setupUI()
        setupClickListeners()
        
        // Show initial state
        showInitialState()
    }

    private fun setupUI() {
        // Set app title styling
        binding.appTitle.text = getString(R.string.app_name)
        
        // Initialize challenge display
        binding.challengeText.text = getString(R.string.challenge_text)
        binding.challengeCategory.text = ""
        
        // Hide tap to spin text initially
        binding.tapToSpinText.visibility = View.GONE
    }

    private fun setupClickListeners() {
        // Spin button click
        binding.spinButton.setOnClickListener {
            if (!isSpinning) {
                spinWheel()
            }
        }
        
        // Wheel click (alternative way to spin)
        binding.spinnerWheel.setOnClickListener {
            if (!isSpinning) {
                spinWheel()
            }
        }
    }

    private fun showInitialState() {
        binding.challengeText.text = getString(R.string.challenge_text)
        binding.challengeCategory.text = ""
        binding.challengeContainer.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.surface)
        )
    }

    private fun spinWheel() {
        if (isSpinning) return
        
        isSpinning = true
        binding.spinButton.isEnabled = false
        
        // Hide tap to spin text
        binding.tapToSpinText.visibility = View.GONE
        
        // Generate random rotation (multiple full spins + random section)
        val baseSpins = (3..6).random() // 3-6 full rotations
        val randomSection = (0..3).random() * 90f // Random section (0, 90, 180, 270)
        val randomOffset = (0..89).random().toFloat() // Random position within section
        val targetRotation = baseSpins * 360f + randomSection + randomOffset
        
        // Create rotation animation
        val rotationAnimator = ObjectAnimator.ofFloat(
            binding.spinnerWheel,
            "rotation",
            currentRotation,
            targetRotation
        ).apply {
            duration = 3000 // 3 seconds
            interpolator = AccelerateDecelerateInterpolator()
            
            addUpdateListener { animator ->
                currentRotation = animator.animatedValue as Float
            }
        }
        
        // Start animation
        rotationAnimator.start()
        
        // Add party mode effects
        addPartyModeEffects()
        
        // Handle animation completion
        rotationAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onSpinComplete(targetRotation)
            }
        })
    }

    private fun addPartyModeEffects() {
        // Vibrate for party mode
        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 100, 50, 100, 50, 100)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        }
        
        // Flash effect on challenge container
        binding.challengeContainer.animate()
            .alpha(0.5f)
            .setDuration(100)
            .withEndAction {
                binding.challengeContainer.animate()
                    .alpha(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun onSpinComplete(finalRotation: Float) {
        isSpinning = false
        binding.spinButton.isEnabled = true
        
        // Get challenge based on final rotation
        val challenge = challengeManager.getChallengeFromRotation(finalRotation)
        
        // Display the challenge
        displayChallenge(challenge)
        
        // Show success message
        showPartyModeMessage(challenge.color)
        
        // Re-enable spinning
        binding.spinButton.text = getString(R.string.spin_button)
    }

    private fun displayChallenge(challenge: Challenge) {
        // Update challenge text
        binding.challengeText.text = challenge.description
        binding.challengeCategory.text = challenge.category
        
        // Update challenge container color based on challenge color
        val colorRes = challenge.color.colorRes
        binding.challengeContainer.setCardBackgroundColor(
            ContextCompat.getColor(this, colorRes)
        )
        
        // Add some party mode flair
        addChallengeDisplayEffects()
    }

    private fun addChallengeDisplayEffects() {
        // Bounce animation for challenge text
        binding.challengeText.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .withEndAction {
                binding.challengeText.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
        
        // Vibrate again for challenge reveal
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun showPartyModeMessage(color: ChallengeColor) {
        val colorName = color.colorName
        val message = "🎉 $colorName CHALLENGE! 🎉"
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel any ongoing vibrations
        vibrator.cancel()
    }
}
