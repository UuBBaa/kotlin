package tn.skooli.kotlin.ui
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import tn.skooli.kotlin.viewmodel.PaymentViewModel
import tn.skooli.kotlin.network.PaymentRequest

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentScreen(
    navController: NavController,
    paymentViewModel: PaymentViewModel = viewModel()
) {
    val paymentRequest = PaymentRequest(
        amount = 220.25,
        note = "Order #123",
        first_name = "John",
        last_name = "Doe",
        email = "test@paymee.tn",
        phone = "+21611222333",
        return_url = null,
        cancel_url = null,
        webhook_url = "https://www.webhook_url.tn",
        order_id = "244557"
    )

    // Trigger createPayment only once when the screen is first composed.
    LaunchedEffect(Unit) {
        paymentViewModel.createPayment(paymentRequest)
    }

    // Observe the paymentData state.
    val paymentData by paymentViewModel.paymentData.collectAsState()

    if (paymentData == null) {
        // Show a loading indicator until the payment URL is ready.
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        // Once the payment URL is ready, load the WebView.
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url.toString()
                            if (url.contains("/loader")) {
                                // Payment process is complete
                                navController.popBackStack()
                                return true
                            }
                            return false
                        }

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            // You can add additional actions here if needed
                        }
                    }
                    loadUrl(paymentData!!.paymentUrl)
                }
            }
        )
    }
}
