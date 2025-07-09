package com.robstore.features.authentication.login.data.datasource

import com.robstore.features.authentication.login.data.model.LoginRequest
import com.robstore.features.authentication.login.data.model.UserValidateDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService{
    /*@GET("users/{username}")
    suspend fun validateUsername(@Path("username") username : String) : Response<UsernameValidateDTO>
    */


    @POST("api/users/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<UserValidateDTO>



    //@POST("api/users/validate-token")
    //suspend fun validateToken(@Header("Authorization") authorization: String): Response<TokenValidateDTO>



    //@POST("users")
    //suspend fun createUser(@Body request : CreateUserRequest) : Response<UserDTO>
}