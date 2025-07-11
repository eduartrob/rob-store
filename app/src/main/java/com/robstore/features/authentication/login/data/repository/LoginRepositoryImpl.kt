package com.robstore.features.authentication.login.data.repository

import com.robstore.features.authentication.login.data.datasource.LoginService
import com.robstore.features.authentication.login.data.model.LoginRequest
import com.robstore.features.authentication.login.domain.model.User
import com.robstore.features.authentication.login.domain.repository.LoginRepository


class LoginRepositoryImpl(
    private val loginService: LoginService
): LoginRepository {
    override suspend fun login(email: String, passwd: String): Result<User> {
        val loginRequest = LoginRequest(email, passwd)
        return try {
            val response = loginService.login(loginRequest)
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    val userData = loginResponse.data

                    Result.success(User(
                        name = userData.name,
                        email = userData.email,
                        phone = userData.phone,
                        region = userData.region
                    ))
                } else {
                    Result.failure(Exception("Login fallido: Respuesta exitosa pero cuerpo de datos nulo."))
                }
            }  else {
                Result.failure(Exception("Login fallido: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}