package tn.skooli.kotlin.repository

import android.app.Application
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import tn.skooli.kotlin.network.RetrofitInstance
import tn.skooli.kotlin.network.ApiService

class FakeDataRepository(application: Application) {
    private val api: ApiService = RetrofitInstance.createApi(application)

    suspend fun getFakeData(): Response<List<Map<String, String>>> = api.getFakeData()

    suspend fun uploadImage(filePart: MultipartBody.Part): Response<ResponseBody> {
        return api.uploadFile(filePart)
    }

    suspend fun getImages(): Response<List<String>> = api.getFiles()
}
