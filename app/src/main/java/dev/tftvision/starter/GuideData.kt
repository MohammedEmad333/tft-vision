package dev.tftvision.starter

data class Guide(val title: String, val detail: String)

object GuideData {
    // Illustrative templates only; no live patch information or dynamic advice.
    val guides = listOf(
        Guide("Front-to-back basics", "Frontline: durable champions\nBackline: protect ranged damage dealers\nItems: defensive frontline and carry damage"),
        Guide("Reroll fundamentals", "Learn low-cost carry strategies before your match.\nStudy item combinations and the importance of economy."),
        Guide("Flexible leveling", "Study different leveling patterns and flexible item use.\nKeep key carries protected behind your frontline.")
    )
}
