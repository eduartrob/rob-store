package com.robstore.core.store.local.database.repository

import com.robstore.core.store.local.database.dao.UserDao
import com.robstore.core.store.local.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun upsertUser(user: UserEntity) {
        userDao.upsertUser(user)
    }

    fun getPendingUser(): Flow<UserEntity?> {
        return userDao.getPendingUser()
    }

    suspend fun clearPendingUserFlag(userId: String) {
        userDao.clearPendingUserFlag(userId)
    }

    suspend fun deleteUserLocal(userId: String){
        userDao.deleteUser(userId)
    }
}
