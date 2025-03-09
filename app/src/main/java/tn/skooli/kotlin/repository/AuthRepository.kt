package tn.skooli.kotlin.repository

import retrofit2.Response
import tn.skooli.kotlin.network.LoginRequest
import tn.skooli.kotlin.network.LoginResponse
import tn.skooli.kotlin.network.RetrofitInstance

class AuthRepository {
    suspend fun login(username: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(username, password)
        return RetrofitInstance.api.login(request)
    }

    // Function to get fake data from the server
    suspend fun getFakeData(): Response<List<Map<String, String>>> {
        return RetrofitInstance.api.getFakeData()
    }
}