package tn.skooli.kotlin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import tn.skooli.kotlin.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val token by homeViewModel.token.collectAsState()
    // Redirect to login if the token is cleared (user logged out)
    LaunchedEffect(token) {
        if (token.isEmpty()) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true } // Clear home from backstack
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header area with welcome text and logout button
        Text("Welcome to Kotlin !")
        Button(
            onClick = { homeViewModel.logout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        // Spacer between logout and fetch list
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("fakedata") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Fake Data")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("maps") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Maps")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("currentLocation") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show My Location")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("fileManager") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go File manager")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Navigate to Payment Screen or call a ViewModel function to initiate payment
                navController.navigate("payment")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay with Paymee")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Example coordinates near downtown San Francisco
                val lat = 37.7749
                val lon = -122.4194
                navController.navigate("route/$lat/$lon")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show Route to San Francisco")
        }

    }
}
