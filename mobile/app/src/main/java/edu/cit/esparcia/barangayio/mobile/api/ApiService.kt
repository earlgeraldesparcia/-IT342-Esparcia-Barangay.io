package edu.cit.esparcia.barangayio.mobile.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val address: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val message: String
)

data class MessageResponse(
    val message: String
)

interface ApiService {
    @POST("/api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<MessageResponse>

    @POST("/api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}
