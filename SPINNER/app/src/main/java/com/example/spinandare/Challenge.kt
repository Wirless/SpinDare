package com.example.spinandare

/**
 * Represents a challenge card in the SPIN & DARE game
 * @param id Unique identifier for the challenge
 * @param color Color category of the challenge (RED, BLUE, GREEN, YELLOW)
 * @param category Human-readable category name
 * @param description The actual challenge text
 */
data class Challenge(
    val id: Int,
    val color: ChallengeColor,
    val category: String,
    val description: String
)

/**
 * Enum representing the four challenge colors
 */
enum class ChallengeColor(val colorName: String, val colorRes: Int) {
    RED("RED", R.color.challenge_red),
    BLUE("BLUE", R.color.challenge_blue),
    GREEN("GREEN", R.color.challenge_green),
    YELLOW("YELLOW", R.color.challenge_yellow)
}
