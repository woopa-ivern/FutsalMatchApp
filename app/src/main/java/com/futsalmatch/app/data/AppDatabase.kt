package com.futsalmatch.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.futsalmatch.app.data.dao.MatchDao
import com.futsalmatch.app.data.dao.PlayerDao
import com.futsalmatch.app.data.dao.ReviewDao
import com.futsalmatch.app.data.dao.TeamDao
import com.futsalmatch.app.data.dao.UserDao
import com.futsalmatch.app.data.entity.MatchEntity
import com.futsalmatch.app.data.entity.PlayerEntity
import com.futsalmatch.app.data.entity.ReviewEntity
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.data.entity.UserEntity

@Database(
    entities = [
        TeamEntity::class,
        UserEntity::class,
        PlayerEntity::class,
        MatchEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun userDao(): UserDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "futsalmatch_database"
                ).build().also { INSTANCE = it }
            }
    }
}
