package com.example.spindare

import android.content.Context
import kotlin.random.Random

/**
 * Manages all 80 challenge cards and provides game logic
 */
class ChallengeManager(private val context: Context) {

    /**
     * All 80 challenges organized by color
     */
    private val allChallenges = createAllChallenges()

    /**
     * Get a random challenge from all available challenges
     */
    fun getRandomChallenge(): Challenge {
        return allChallenges.random()
    }

    /**
     * Get a random challenge from a specific color category
     */
    fun getRandomChallengeByColor(color: ChallengeColor): Challenge {
        return allChallenges.filter { it.color == color }.random()
    }

    /**
     * Determine challenge color based on wheel rotation angle
     * @param degrees Rotation angle in degrees (0-360)
     * @return The color section the wheel landed on
     */
    fun getColorFromRotation(degrees: Float): ChallengeColor {
        val normalizedDegrees = (degrees + 360) % 360
        
        return when {
            normalizedDegrees >= 0 && normalizedDegrees < 90 -> ChallengeColor.RED
            normalizedDegrees >= 90 && normalizedDegrees < 180 -> ChallengeColor.BLUE
            normalizedDegrees >= 180 && normalizedDegrees < 270 -> ChallengeColor.GREEN
            else -> ChallengeColor.YELLOW
        }
    }

    /**
     * Get a random challenge from the color determined by wheel rotation
     */
    fun getChallengeFromRotation(degrees: Float): Challenge {
        val color = getColorFromRotation(degrees)
        return getRandomChallengeByColor(color)
    }

    /**
     * Create all 80 challenges with their respective colors and descriptions
     */
    private fun createAllChallenges(): List<Challenge> {
        val challenges = mutableListOf<Challenge>()
        var id = 1

        // RED Challenges (Pocałunek/Zbliżenie)
        val redChallenges = listOf(
            "Pocałuj osobę po lewej",
            "Pocałuj osobę po prawej",
            "Pocałuj osobę na wprost",
            "Zbliżenie z kimkolwiek w zasięgu ręki",
            "Pocałuj na policzek",
            "Pocałuj na czubek nosa",
            "Przyciągnij osobę do siebie i pocałuj",
            "Pocałuj w ramię",
            "Pocałuj w dłoń",
            "Pocałuj w czoło",
            "Pocałuj w policzek 2 osoby",
            "Pocałuj najbliższą osobę",
            "Pocałuj kogoś siedzącego obok",
            "Pocałuj w ucho (głupkowato)",
            "Pocałuj w rękę z gestem królewskim",
            "Pocałuj i zrób \'air kiss\' w stronę kogoś",
            "Pocałuj osobę, która najbardziej krzyczy",
            "Pocałuj w nos 2 osoby po kolei",
            "Pocałuj osobę, która ma najśmieszniejszą minę",
            "Pocałuj osobę w losowy sposób"
        )

        redChallenges.forEach { description ->
            challenges.add(
                Challenge(
                    id = id++,
                    color = ChallengeColor.RED,
                    category = "Pocałunek/Zbliżenie",
                    description = description
                )
            )
        }

        // BLUE Challenges (Przytulenie/Gest)
        val blueChallenges = listOf(
            "Przytul osobę po lewej",
            "Przytul osobę po prawej",
            "Przytul 2 osoby jednocześnie",
            "Przytul najbliższą osobę i udawaj misia",
            "Przytul kogoś stojącego obok",
            "Przytul osobę z największym uśmiechem",
            "Przytul i obróć 360 stopni",
            "Przytul osobę i zrób 'high five'",
            "Przytul i zaśpiewaj 'la la la'",
            "Przytul w łokieć",
            "Przytul w ramię",
            "Przytul i potrząśnij delikatnie",
            "Przytul i udawaj drzewo",
            "Przytul osobę najgłośniej krzyczącą",
            "Przytul osobę w losowy sposób",
            "Przytul osobę, która stoi w środku",
            "Przytul kogoś i zrób mini taniec",
            "Przytul i powiedz komplement",
            "Przytul osobę, która się śmieje najgłośniej",
            "Przytul losową osobę"
        )

        blueChallenges.forEach { description ->
            challenges.add(
                Challenge(
                    id = id++,
                    color = ChallengeColor.BLUE,
                    category = "Przytulenie/Gest",
                    description = description
                )
            )
        }

        // GREEN Challenges (Komplement/Śmieszna akcja)
        val greenChallenges = listOf(
            "Powiedz komuś komplement",
            "Powiedz coś absurdalnego o sobie",
            "Udawaj kogoś przez 10 sek",
            "Powiedz najgłupszy żart jaki znasz",
            "Udawaj zwierzę",
            "Powiedz sekret, który każdy powinien znać",
            "Udawaj upadek w zabawny sposób",
            "Powiedz coś w języku wymyślonym przez siebie",
            "Powiedz coś komplementującego wszystkich wokół",
            "Udawaj, że jesteś DJ-em",
            "Powiedz coś śmiesznego i energicznie",
            "Zrób minę strasznie głupią",
            "Powiedz kogo najbardziej lubisz w pokoju",
            "Udawaj, że pijesz niewidzialny shot",
            "Powiedz 'kocham imprezę!' z gestem",
            "Udawaj piosenkę i śpiewaj 5 sekund",
            "Powiedz coś, co każdy powinien powtórzyć",
            "Zrób głupią pozę",
            "Powiedz coś absurdalnego do losowej osoby",
            "Udawaj, że jesteś influencerem"
        )

        greenChallenges.forEach { description ->
            challenges.add(
                Challenge(
                    id = id++,
                    color = ChallengeColor.GREEN,
                    category = "Komplement/Śmieszna akcja",
                    description = description
                )
            )
        }

        // YELLOW Challenges (Taniec/Akcja absurdalna)
        val yellowChallenges = listOf(
            "Zrób mini taniec",
            "Zrób taniec w miejscu",
            "Zrób taniec na stole lub krześle",
            "Zrób 'air dance' przez 10 sek",
            "Obróć się 3 razy i zatańcz",
            "Zrób absurdalny taniec z rękami",
            "Zatańcz z losową osobą",
            "Udawaj robota i tańcz",
            "Zrób taniec misia",
            "Zrób taniec zombie",
            "Zrób taniec kangura",
            "Zrób taniec na jednej nodze",
            "Zrób taniec „podłoga w ogień",
            "Zrób taniec w parach",
            "Zrób absurdalny taniec solo",
            "Tańcz z wyciągniętymi rękami",
            "Zrób taniec i krzycz 'woo!'",
            "Zrób taniec po kole",
            "Zrób taniec jak w teledysku",
            "Zrób taniec naśladujący losową osobę"
        )

        yellowChallenges.forEach { description ->
            challenges.add(
                Challenge(
                    id = id++,
                    color = ChallengeColor.YELLOW,
                    category = "Taniec/Akcja absurdalna",
                    description = description
                )
            )
        }

        return challenges
    }

    /**
     * Get total number of challenges
     */
    fun getTotalChallenges(): Int = allChallenges.size

    /**
     * Get challenges by color
     */
    fun getChallengesByColor(color: ChallengeColor): List<Challenge> {
        return allChallenges.filter { it.color == color }
    }
}
