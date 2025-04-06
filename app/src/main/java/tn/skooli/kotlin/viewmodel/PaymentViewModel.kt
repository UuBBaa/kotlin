package tn.skooli.kotlin.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.skooli.kotlin.network.ApiService
import tn.skooli.kotlin.network.PaymentRequest
import tn.skooli.kotlin.network.RetrofitInstance

class PaymentViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val api: ApiService = RetrofitInstance.createApi(application)
    private val _paymentData = MutableStateFlow<PaymentData?>(null)
    val paymentData = _paymentData.asStateFlow()

    fun createPayment(paymentRequest: PaymentRequest) {

        viewModelScope.launch {
            try {
                // Call your backend endpoint (adjust ApiService accordingly)
                Log.d("payment", "payment triggered")
                val response: Response<Map<String, Any>> =
                    api.createPayment(paymentRequest)
                Log.d("payment", response.toString())
                if (response.isSuccessful) {
                    // Extract payment_url from response (adjust key based on your backend's response structure)
                    val data = response.body()?.get("data") as? Map<*, *>
                    val paymentUrl = data?.get("payment_url") as? String
                    if (paymentUrl != null) {
                        _paymentData.value = PaymentData(paymentUrl)
                    }
                } else {
                    Log.d("payment", response.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    data class PaymentData(val paymentUrl: String)
}
