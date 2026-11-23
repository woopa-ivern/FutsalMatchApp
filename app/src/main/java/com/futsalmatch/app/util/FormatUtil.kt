package com.futsalmatch.app.util

import com.futsalmatch.app.data.entity.MatchEntity
import com.futsalmatch.app.data.entity.ReviewEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtil {
    private val indoLocale = Locale("id", "ID")

    fun formatDateFull(millis: Long): String {
        return SimpleDateFormat("d MMM yyyy", indoLocale).format(Date(millis))
    }

    fun formatDateWithDay(millis: Long): String {
        return SimpleDateFormat("EEEE, d MMM yyyy", indoLocale).format(Date(millis))
    }

    fun formatDateShort(millis: Long): String {
        return SimpleDateFormat("dd MMM", indoLocale).format(Date(millis))
    }

    fun formatRating(rating: Double): String {
        return if (rating <= 0.0) "Baru" else String.format(Locale.US, "%.1f", rating)
    }

    fun computeAverageRating(reviews: List<ReviewEntity>): Double {
        if (reviews.isEmpty()) return 0.0
        val total = reviews.sumOf {
            (it.ratingSportivitas + it.ratingKetepatanWaktu + it.ratingSkillMatch) / 3.0
        }
        return total / reviews.size
    }

    fun getMatchResultForTeam(match: MatchEntity, teamId: Int): String {
        val myScore = if (match.teamHomeId == teamId) match.skorHome else match.skorAway
        val oppScore = if (match.teamHomeId == teamId) match.skorAway else match.skorHome
        return when {
            myScore > oppScore -> "M"
            myScore < oppScore -> "K"
            else -> "S"
        }
    }

    fun getMatchResultWordForTeam(match: MatchEntity, teamId: Int): String {
        return when (getMatchResultForTeam(match, teamId)) {
            "M" -> "Menang"
            "K" -> "Kalah"
            else -> "Seri"
        }
    }

    fun formatWhatsappNumberForUrl(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return when {
            digits.startsWith("0") -> "62" + digits.substring(1)
            digits.startsWith("62") -> digits
            else -> "62$digits"
        }
    }

    fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "FM"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0][0]}${parts[1][0]}".uppercase()
        }
    }
}
