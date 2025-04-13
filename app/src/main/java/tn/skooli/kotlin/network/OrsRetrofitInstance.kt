package tn.skooli.kotlin.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OrsRetrofitInstance {
    private const val BASE_URL = "https://api.openrouteservice.org/v2/directions/"

    fun createApi(): OrsApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(OrsAuthInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(OrsApiService::class.java)
    }
}

// Interceptor that adds the Authorization header and JSON content type
class OrsAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .addHeader("Authorization", "5b3ce3597851110001cf6248644c71ec9e8e44d5a423d4151b79831e")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(req)
    }
}
