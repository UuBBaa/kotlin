package tn.skooli.kotlin.repository

import android.app.Application
import retrofit2.Response
import tn.skooli.kotlin.network.RetrofitInstance
import tn.skooli.kotlin.network.ApiService

class FakeDataRepository(application: Application) {
    private val api: ApiService = RetrofitInstance.createApi(application)

    suspend fun getFakeData(): Response<List<Map<String, String>>> {
        return api.getFakeData()
    }
}
