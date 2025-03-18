package tn.skooli.kotlin.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import tn.skooli.kotlin.repository.AuthRepository
import tn.skooli.kotlin.utils.TokenManager

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)
    var token = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf("")

    private val tokenManager = TokenManager(application)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = repository.login(username, password)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        token.value = responseBody.token
                        Log.d("AuthViewModel", "Login successful, Token: ${token.value}")
                        // Save the token using TokenManager
                        tokenManager.saveToken(token.value)
                    } else {
                        error.value = "Empty response from server"
                        Log.e("AuthViewModel", "Login failed: Empty response")
                    }

                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    error.value = "Login failed: ${response.code()} - $errorResponse"
                    Log.e("AuthViewModel", "Login failed: ${response.code()} - $errorResponse")
                }

            } catch (e: JsonSyntaxException) {
                error.value = "Invalid JSON format received"
                Log.e("AuthViewModel", "JSON Parsing Error", e)
            } catch (e: Exception) {
                error.value = "Error: ${e.localizedMessage}"
                Log.e("AuthViewModel", "Login error", e)
            }
            isLoading.value = false
        }
    }
}