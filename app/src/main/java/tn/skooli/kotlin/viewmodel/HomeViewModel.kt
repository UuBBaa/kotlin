package tn.skooli.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tn.skooli.kotlin.utils.TokenManager

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)

    private val _token = MutableStateFlow(tokenManager.getToken() ?: "")
    val token = _token.asStateFlow()

    fun logout() {
        tokenManager.clearToken()
        _token.value = ""
    }
}