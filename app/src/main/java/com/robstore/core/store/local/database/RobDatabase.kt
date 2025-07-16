package com.robstore.core.store.local.database

import com.robstore.core.store.local.database.dao.UserDao
import com.robstore.core.store.local.database.entities.UserEntity


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class RobDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: RobDatabase? = null

        fun getInstance(context: Context): RobDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RobDatabase::class.java,
                    "robstore.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
