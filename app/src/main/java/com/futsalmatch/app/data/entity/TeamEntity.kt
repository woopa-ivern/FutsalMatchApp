package com.futsalmatch.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.futsalmatch.app.data.SkillLevel

@Entity(
    tableName = "teams",
    indices = [Index(value = ["namaTim"], unique = true)]
)
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaTim: String,
    val kota: String,
    val skillLevel: SkillLevel,
    val noWhatsapp: String,
    val deskripsiSingkat: String,
    val fotoPath: String? = null
)
