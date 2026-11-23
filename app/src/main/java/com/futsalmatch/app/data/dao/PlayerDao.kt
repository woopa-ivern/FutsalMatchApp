package com.futsalmatch.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.futsalmatch.app.data.entity.PlayerEntity

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY id ASC")
    suspend fun getByTeamId(teamId: Int): List<PlayerEntity>

    @Insert
    suspend fun insertAll(players: List<PlayerEntity>)

    @Query("DELETE FROM players WHERE teamId = :teamId")
    suspend fun deleteByTeamId(teamId: Int)
}
