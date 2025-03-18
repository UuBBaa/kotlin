package tn.skooli.kotlin.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import tn.skooli.kotlin.utils.TokenManager

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Check if the function has @AuthRequired annotation
        val annotation = request.tag(Invocation::class.java)
            ?.method()
            ?.getAnnotation(AuthRequired::class.java)

        return if (annotation != null) {
            Log.d("Annotation", "auth required")
            val tokenManager = TokenManager(context)
            val token = tokenManager.getToken()
            Log.d("Token", "$token")
            if (token != null) {
                val newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }
        } else {
            chain.proceed(request)
        }
    }
}
