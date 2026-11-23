package com.futsalmatch.app.util

import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.model.TeamSummary

object TeamQueryHelper {
    suspend fun buildTeamSummary(
        db: AppDatabase,
        team: TeamEntity,
        matchLabel: String? = null
    ): TeamSummary {
        val reviews = db.reviewDao().getReviewsReceivedByTeam(team.id)
        val avgRating = FormatUtil.computeAverageRating(reviews)
        val recentMatches = db.matchDao().getRecent5MatchesForTeam(team.id)
        val form = recentMatches.map { FormatUtil.getMatchResultForTeam(it, team.id) }
        return TeamSummary(
            team = team,
            averageRating = avgRating,
            reviewCount = reviews.size,
            recentForm = form,
            matchLabel = matchLabel
        )
    }

    suspend fun getAllTeamSummaries(db: AppDatabase): List<TeamSummary> {
        val teams = db.teamDao().getAll()
        return teams.map { buildTeamSummary(db, it) }
    }
}
