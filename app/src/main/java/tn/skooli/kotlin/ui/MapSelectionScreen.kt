package tn.skooli.kotlin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Button
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

@OptIn(ExperimentalMaterial3Api::class) // Opt-in to experimental features in Material3
@Composable
fun MapSelectionScreen(navController: NavController) {
    // Access the current context to configure the OpenStreetMap
    val context = LocalContext.current

    // State to store the selected location (GeoPoint)
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }

    // Function to configure the map with a given map view and interactivity option
    fun configureMapView(mapView: MapView, interactive: Boolean) {
        with(mapView) {
            // Set the user agent for OpenStreetMap configuration
            Configuration.getInstance().userAgentValue = context.packageName
            // Set the map tile source (default OpenStreetMap tiles)
            setTileSource(TileSourceFactory.MAPNIK)
            // Set the default zoom level of the map
            controller.setZoom(15.0)
            // Enable or disable multi-touch controls based on the interactive flag
            setMultiTouchControls(interactive)
            // If interactive, center the map at a specific location
            if (interactive) {
                controller.setCenter(GeoPoint(33.339987, 10.492259)) // Example coordinates (Googleplex)
            }
        }
    }

    // Selection Map - A map where users can select a location
    val selectionMapView = remember {
        MapView(context).apply {
            // Configure the map to be interactive
            configureMapView(this, true)
            // Add a MapEventsOverlay to handle user interactions (e.g., taps)
            overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                // Handle single tap event on the map
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    selectedLocation = p // Update the selected location with tapped coordinates
                    // Remove all existing markers
                    overlays.removeAll { it is Marker }
                    // Create and add a new marker at the selected location
                    Marker(this@apply).apply {
                        position = p
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Selected Location"
                    }.also { overlays.add(it) }
                    invalidate() // Redraw the map to reflect changes
                    return true
                }
                // Handle long press event (unused in this case)
                override fun longPressHelper(p: GeoPoint) = false
            }))
        }
    }

    // Preview Map - A non-interactive map for previewing the confirmed location
    val previewMapView = remember {
        MapView(context).apply {
            // Configure the map to be non-interactive
            configureMapView(this, false)
        }
    }

    // Scaffold structure to hold the screen layout
    Scaffold(
        topBar = {
            // Top app bar with a title and a back button
            TopAppBar(
                title = { Text("Map Selection") },
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
                .verticalScroll(rememberScrollState()) // Make the content scrollable
                .padding(16.dp) // Additional padding
        ) {
            // Row to hold the "Confirm Location" and "Clear Selection" buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Make the row take the full width
                    .padding(bottom = 16.dp), // Bottom padding
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between buttons
            ) {
                // Button to confirm the selected location
                Button(
                    onClick = {
                        selectedLocation?.let { loc ->
                            // Update the preview map to show the selected location
                            previewMapView.controller.setCenter(loc)
                            // Remove all markers from the preview map
                            previewMapView.overlays.removeAll { it is Marker }
                            // Create and add a new marker to the preview map
                            Marker(previewMapView).apply {
                                position = loc
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Confirmed Location"
                            }.also { previewMapView.overlays.add(it) }
                            previewMapView.invalidate() // Redraw the preview map
                        }
                    },
                    modifier = Modifier.weight(1f) // Make the button take equal space in the row
                ) {
                    Text("Confirm Location") // Text on the confirm button
                }

                // Button to clear the selected location
                Button(
                    onClick = {
                        selectedLocation = null // Clear the selected location
                        // Remove markers from both maps
                        selectionMapView.overlays.removeAll { it is Marker }
                        previewMapView.overlays.removeAll { it is Marker }
                        selectionMapView.invalidate() // Redraw the selection map
                        previewMapView.invalidate() // Redraw the preview map
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Clear Selection") // Text on the clear button
                }
            }

            // Card to display the selection map
            Card(
                modifier = Modifier
                    .fillMaxWidth() // Make the card take the full width
                    .height(300.dp) // Set the card height
                    .padding(bottom = 16.dp), // Padding at the bottom
                elevation = CardDefaults.cardElevation(4.dp) // Card elevation for shadow effect
            ) {
                // Display the selection map inside the card
                AndroidView(
                    factory = { selectionMapView },
                    modifier = Modifier.fillMaxSize() // Make the map take the full size of the card
                )
            }

            // Card to display the preview map (non-interactive)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                // Display the preview map inside the card
                AndroidView(
                    factory = { previewMapView },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}