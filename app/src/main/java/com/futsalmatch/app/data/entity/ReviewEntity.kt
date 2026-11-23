package com.futsalmatch.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["reviewerTeamId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["matchId"], unique = true),
        Index(value = ["reviewerTeamId"])
    ]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val matchId: Int,
    val reviewerTeamId: Int,
    val ratingSportivitas: Int,
    val ratingKetepatanWaktu: Int,
    val ratingSkillMatch: Int,
    val catatanTeks: String,
    val tanggalSubmit: Long,
    val isLocked: Boolean = true
)
