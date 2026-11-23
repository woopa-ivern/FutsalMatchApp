package com.futsalmatch.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.futsalmatch.app.data.entity.ReviewEntity

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE matchId = :matchId LIMIT 1")
    suspend fun getByMatchId(matchId: Int): ReviewEntity?

    @Query(
        "SELECT r.* FROM reviews r " +
        "INNER JOIN matches m ON r.matchId = m.id " +
        "WHERE (m.teamHomeId = :teamId OR m.teamAwayId = :teamId) " +
        "AND r.reviewerTeamId != :teamId " +
        "ORDER BY r.tanggalSubmit DESC"
    )
    suspend fun getReviewsReceivedByTeam(teamId: Int): List<ReviewEntity>

    @Insert
    suspend fun insert(review: ReviewEntity): Long
}
