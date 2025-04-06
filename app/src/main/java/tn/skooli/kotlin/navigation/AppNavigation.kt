package tn.skooli.kotlin.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tn.skooli.kotlin.ui.CurrentLocationScreen
import tn.skooli.kotlin.ui.FakeDataScreen
import tn.skooli.kotlin.ui.FileManagerScreen
import tn.skooli.kotlin.ui.LoginScreen
import tn.skooli.kotlin.ui.HomeScreen
import tn.skooli.kotlin.ui.MapSelectionScreen
import tn.skooli.kotlin.ui.PaymentScreen
import tn.skooli.kotlin.utils.TokenManager

@Composable
fun AppNavigation(navController: NavHostController, application: Application) {
    val tokenManager = remember { TokenManager(application) }
    val startDestination = if (!tokenManager.getToken().isNullOrEmpty()) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("fakedata") { FakeDataScreen() }
        composable("maps") { MapSelectionScreen(navController) }
        composable("currentLocation") { CurrentLocationScreen(navController) }
        composable("fileManager") { FileManagerScreen(navController) }
        composable("payment") { PaymentScreen(navController) }
    }
}
