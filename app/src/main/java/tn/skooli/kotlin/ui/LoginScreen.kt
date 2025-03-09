package tn.skooli.kotlin.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.skooli.kotlin.viewmodel.AuthViewModel

@Composable
fun LoginScreen(application: Application, authViewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.login(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (authViewModel.isLoading.value) {
            CircularProgressIndicator()
        }
        if (authViewModel.error.value.isNotEmpty()) {
            Text("Error: ${authViewModel.error.value}", color = MaterialTheme.colors.error)
        }
        if (authViewModel.token.value.isNotEmpty()) {
            Text("Token: ${authViewModel.token.value}")
        }

        // Fetch List button
        Button(
            onClick = { authViewModel.fetchFakeData() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch List")
        }

        // Display the fetched list
        if (authViewModel.fakeData.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Fetched List:", style = MaterialTheme.typography.h6)
            LazyColumn {
                items(authViewModel.fakeData.value) { item ->
                    Text("${item["id"]}: ${item["name"]}, ${item["email"]}")
                }
            }
        }
    }
}
