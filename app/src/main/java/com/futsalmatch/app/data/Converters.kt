package com.futsalmatch.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSkillLevel(value: SkillLevel): String = value.name

    @TypeConverter
    fun toSkillLevel(value: String): SkillLevel {
        return try {
            SkillLevel.valueOf(value)
        } catch (e: IllegalArgumentException) {
            SkillLevel.SEDANG
        }
    }
}
