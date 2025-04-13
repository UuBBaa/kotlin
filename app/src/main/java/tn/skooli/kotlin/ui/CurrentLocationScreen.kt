package tn.skooli.kotlin.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.navigation.NavController
import android.location.LocationManager

@OptIn(ExperimentalMaterial3Api::class) // Opt-in to experimental features in Material3
@Composable
fun CurrentLocationScreen(navController: NavController) {
    // Access the current context (Android context)
    val context = LocalContext.current

    // State variables to store location data and permission status
    var location by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Permission launcher to request location permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if either fine or coarse location permissions are granted
        val fine = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarse = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasLocationPermission = fine || coarse
    }

    // Request location permissions when the screen is launched
    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    // Fetch location data when permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            // Get LocationManager system service to access device's location
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                // Get the last known location from the GPS provider
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                lastKnownLocation?.let {
                    // If location is available, update the location state
                    location = GeoPoint(it.latitude, it.longitude)
                }
            } catch (e: SecurityException) {
                // Handle security exception if permissions are denied or any other issue arises
            }
        }
    }

    // Scaffold structure to hold the screen layout
    Scaffold(
        topBar = {
            // Top app bar with a title and a back button
            TopAppBar(
                title = { Text("Current Location") },
                navigationIcon = {
                    // Back button that navigates to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content of the screen, wrapped in a Column layout
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(innerPadding) // Apply padding from Scaffold
        ) {
            // If location permission is not granted, display a message
            if (!hasLocationPermission) {
                Text(
                    "Please enable location permissions in settings",
                    modifier = Modifier
                        .fillMaxWidth() // Make the text span across the width
                        .padding(16.dp) // Apply padding around the text
                )
            } else if (location == null) {
                // If location is not yet available, show a loading spinner
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // If location is available, display the map with the location
                LocationMapView(location!!, context)
            }
        }
    }
}

// Composable function to display the map with a marker for the current location
@Composable
fun LocationMapView(geoPoint: GeoPoint, context: Context) {
    // Initialize the MapView and configure it
    val mapView = remember {
        MapView(context).apply {
            // Set the user agent for OpenStreetMap configuration
            Configuration.getInstance().userAgentValue = context.packageName
            // Set the map tile source (default OpenStreetMap tiles)
            setTileSource(TileSourceFactory.MAPNIK)
            // Set the initial zoom level
            controller.setZoom(15.0)
            // Set the initial map center to the current location
            controller.setCenter(geoPoint)

            // Add a marker at the current location
            overlays.add(Marker(this).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "You are here!" // Label for the marker
            })
        }
    }

    // Display the map view inside an AndroidView
    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxSize() // Make the map view fill the available space
            .padding(8.dp) // Apply padding around the map
    )
}