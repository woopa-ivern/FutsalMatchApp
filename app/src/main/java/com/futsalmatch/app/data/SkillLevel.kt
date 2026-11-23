package com.futsalmatch.app.data

enum class SkillLevel(val label: String, val rank: Int) {
    JAGO("Jago", 3),
    SEDANG("Sedang", 2),
    PEMULA("Pemula", 1);

    fun isSameOrAdjacent(other: SkillLevel): Boolean {
        return kotlin.math.abs(this.rank - other.rank) <= 1
    }

    fun adjacentDescription(): String {
        return when (this) {
            JAGO -> "Termasuk level berdekatan (Sedang)"
            SEDANG -> "Termasuk level berdekatan (Jago dan Pemula)"
            PEMULA -> "Termasuk level berdekatan (Sedang)"
        }
    }

    companion object {
        fun fromLabel(label: String): SkillLevel {
            return entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: SEDANG
        }
    }
}
