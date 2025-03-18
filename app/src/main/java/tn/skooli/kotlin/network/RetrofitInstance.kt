package tn.skooli.kotlin.network

import android.app.Application
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8081/"

    fun createApi(application: Application): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(application.applicationContext))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
