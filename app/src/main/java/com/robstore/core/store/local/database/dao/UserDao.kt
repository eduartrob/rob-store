package com.robstore.core.store.local.database.dao

import com.robstore.core.store.local.database.entities.UserEntity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun clearUser()

    @Query("SELECT * FROM user WHERE isPendingSync = 1 LIMIT 1")
    fun getPendingUser(): Flow<UserEntity?>

    @Query("UPDATE user SET isPendingSync = 0 WHERE id = :userId")
    suspend fun clearPendingUserFlag(userId: String)
}
