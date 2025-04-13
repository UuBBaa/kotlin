package tn.skooli.kotlin.ui

import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import tn.skooli.kotlin.network.OrsDirectionsRequest
import tn.skooli.kotlin.network.OrsRetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteMapScreen(
    navController: NavController,
    destination: GeoPoint
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val orsApi = remember { OrsRetrofitInstance.createApi() }

    // 1. Get current location (as you already do)
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

    // 2. Once we have both points, fetch the route
    LaunchedEffect(location) {
        val start = location ?: return@LaunchedEffect
        loading = true
        val req = OrsDirectionsRequest(
            coordinates = listOf(
                listOf(start.longitude, start.latitude),
                listOf(destination.longitude, destination.latitude)
            )
        )
        val resp = try {
            orsApi.getRoute(req)
        } catch (e: Exception) {
            loading = false; return@LaunchedEffect
        }
        if (resp.isSuccessful) {
            resp.body()?.features
                ?.firstOrNull()
                ?.geometry
                ?.coordinates
                ?.let { coords ->
                    routePoints = coords.map { GeoPoint(it[1], it[0]) }
                }
        }
        loading = false
    }

    // 3. UI
    Scaffold(
        topBar = { /* … */ }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                !hasLocationPermission -> Text("Enable location…")
                location == null || loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                else -> RouteMapView(location!!, destination, routePoints, context)
            }
        }
    }
}

@Composable
fun RouteMapView(
    start: GeoPoint,
    end: GeoPoint,
    route: List<GeoPoint>,
    context: Context
) {
    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().userAgentValue = context.packageName
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(14.0)
            controller.setCenter(start)

            // Start marker
            overlays.add(Marker(this).apply {
                position = start; title = "Start"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
            // End marker
            overlays.add(Marker(this).apply {
                position = end; title = "End"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
            // Route polyline
            if (route.isNotEmpty()) {
                overlays.add(Polyline().apply {
                    setPoints(route)
                    outlinePaint.strokeWidth = 8f
                })
            }
        }
    }
    AndroidView({ mapView }, Modifier.fillMaxSize().padding(8.dp))
}

