package edu.cit.esparcia.barangayio.mobile.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import edu.cit.esparcia.barangayio.mobile.model.Appointment

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

data class CreateAppointmentRequest(
    val userId: String,
    val certificateType: String,
    val purpose: String,
    val specifyPurpose: String?,
    val preferredDate: String,
    val preferredTime: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val message: String
)

data class MessageResponse(
    val message: String
)

data class DashboardStatsResponse(
    val scheduledToday: Int,
    val completedToday: Int,
    val registeredResidents: Int
)

data class UserResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)

interface ApiService {
    @POST("/api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<MessageResponse>

    @POST("/api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/appointments/user/{id}")
    suspend fun getUserAppointments(@Path("id") userId: String): Response<List<Appointment>>

    @GET("/api/appointments")
    suspend fun getAllAppointments(): Response<List<Appointment>>

    @POST("/api/appointments")
    suspend fun createAppointment(@Body request: CreateAppointmentRequest): Response<Appointment>

    @GET("/api/stats/dashboard")
    suspend fun getDashboardStats(): Response<DashboardStatsResponse>

    @GET("/api/stats/users/{id}")
    suspend fun getUserDetails(@Path("id") userId: String): Response<UserResponse>

    @retrofit2.http.PUT("/api/appointments/{id}/complete")
    suspend fun completeAppointment(@Path("id") id: String): Response<Appointment>

    @retrofit2.http.PUT("/api/appointments/{id}/cancel")
    suspend fun cancelAppointment(@Path("id") id: String, @Body body: Map<String, String>): Response<Appointment>
}
