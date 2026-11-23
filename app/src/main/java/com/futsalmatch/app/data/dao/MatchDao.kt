package com.futsalmatch.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.futsalmatch.app.data.entity.MatchEntity

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE teamHomeId = :teamId OR teamAwayId = :teamId ORDER BY tanggal DESC, id DESC")
    suspend fun getMatchesForTeam(teamId: Int): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE teamHomeId = :teamId OR teamAwayId = :teamId ORDER BY tanggal DESC, id DESC LIMIT 5")
    suspend fun getRecent5MatchesForTeam(teamId: Int): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    suspend fun getById(matchId: Int): MatchEntity?

    @Insert
    suspend fun insert(match: MatchEntity): Long

    @Query("UPDATE matches SET sudahDiReview = 1 WHERE id = :matchId")
    suspend fun markReviewed(matchId: Int)
}
