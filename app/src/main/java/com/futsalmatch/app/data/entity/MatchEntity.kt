package com.futsalmatch.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["teamHomeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["teamAwayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["teamHomeId"]),
        Index(value = ["teamAwayId"])
    ]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val teamHomeId: Int,
    val teamAwayId: Int,
    val skorHome: Int,
    val skorAway: Int,
    val tanggal: Long,
    val lokasi: String,
    val sudahDiReview: Boolean = false
)
