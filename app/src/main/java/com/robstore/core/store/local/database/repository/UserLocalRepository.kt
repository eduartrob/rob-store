package com.robstore.core.store.local.database.repository

import com.robstore.core.store.local.database.dao.UserDao
import com.robstore.core.store.local.database.entities.UserEntity

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun upsertUser(user: UserEntity) {
        userDao.upsertUser(user)
    }
}
