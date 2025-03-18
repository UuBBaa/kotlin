package tn.skooli.kotlin.repository

import android.app.Application
import retrofit2.Response
import tn.skooli.kotlin.network.LoginRequest
import tn.skooli.kotlin.network.LoginResponse
import tn.skooli.kotlin.network.RetrofitInstance
import tn.skooli.kotlin.network.ApiService

class AuthRepository(application: Application) {
    private val api: ApiService = RetrofitInstance.createApi(application)

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(username, password))
    }
}
