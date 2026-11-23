package com.futsalmatch.app.model

import com.futsalmatch.app.data.entity.MatchEntity
import com.futsalmatch.app.data.entity.TeamEntity

data class MatchWithTeams(
    val match: MatchEntity,
    val homeTeam: TeamEntity,
    val awayTeam: TeamEntity
)
