package tn.skooli.kotlin.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Data classes for request and response
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

interface ApiService {
    @POST("api/login")  // adjust the endpoint path as needed
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/fakedata")  // Fetch the fake data
    suspend fun getFakeData(): Response<List<Map<String, String>>>
}