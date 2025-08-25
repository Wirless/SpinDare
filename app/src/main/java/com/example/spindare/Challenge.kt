package com.example.spindare

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
    RED("RED", android.R.color.holo_red_light),
    BLUE("BLUE", android.R.color.holo_blue_light),
    GREEN("GREEN", android.R.color.holo_green_light),
    YELLOW("YELLOW", android.R.color.holo_orange_light)
}
