package com.futsalmatch.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.futsalmatch.app.data.entity.TeamEntity

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams ORDER BY id ASC")
    suspend fun getAll(): List<TeamEntity>

    @Query("SELECT * FROM teams WHERE id = :teamId LIMIT 1")
    suspend fun getById(teamId: Int): TeamEntity?

    @Query("SELECT * FROM teams WHERE LOWER(namaTim) = LOWER(:namaTim) LIMIT 1")
    suspend fun findByNama(namaTim: String): TeamEntity?

    @Query("SELECT COUNT(*) FROM teams")
    suspend fun countAll(): Int

    @Insert
    suspend fun insert(team: TeamEntity): Long

    @Update
    suspend fun update(team: TeamEntity)
}
