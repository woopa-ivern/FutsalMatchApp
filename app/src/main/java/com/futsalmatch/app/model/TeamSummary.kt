package com.futsalmatch.app.model

import com.futsalmatch.app.data.entity.TeamEntity

data class TeamSummary(
    val team: TeamEntity,
    val averageRating: Double,
    val reviewCount: Int,
    val recentForm: List<String>,
    val matchLabel: String? = null
)
