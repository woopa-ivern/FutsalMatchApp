package com.futsalmatch.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.futsalmatch.app.data.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE teamId = :teamId LIMIT 1")
    suspend fun findByTeamId(teamId: Int): UserEntity?

    @Insert
    suspend fun insert(user: UserEntity): Long
}
