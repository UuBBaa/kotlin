package tn.skooli.kotlin.ui

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import coil.compose.rememberAsyncImagePainter
import tn.skooli.kotlin.viewmodel.FileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(navController: NavController, viewModel: FileViewModel = viewModel()) {
    val context = LocalContext.current // Access the current context
    var cameraImageFile by remember { mutableStateOf<File?>(null) } // State for storing the camera image file

    // Camera permission state, checking if permission is granted
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher to request camera permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted // Update permission state based on the result
    }

    // Request camera permission if not already granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Launcher for selecting a single image from the gallery
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Upload image immediately after selection
            viewModel.uploadImage(it, context)
        }
    }

    // Launcher for selecting multiple images from the gallery
    val multiImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        uris?.forEach { uri ->
            // Upload each selected image
            viewModel.uploadImage(uri, context)
        }
    }

    // Launcher for taking a photo using the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageFile?.let { file ->
                // Get URI for the camera image file and upload it
                val uri =
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                viewModel.uploadImage(uri, context)
            }
        }
    }

    // Function to create a temporary image file for the camera
    fun createImageFile(): File {
        val storageDir = context.cacheDir // Use the cache directory for temporary storage
        return File.createTempFile("IMG_", ".jpg", storageDir).apply {
            cameraImageFile = this // Store the file reference
        }
    }

    // Automatically load images when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.loadImages()
    }

    // Layout for the FileManager screen
    // Scaffold structure to hold the screen layout
    Scaffold(
        topBar = {
            // Top app bar with a title and a back button
            TopAppBar(
                title = { Text("Images manager") },
                navigationIcon = {
                    // Back button that navigates to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the available screen space
                .padding(innerPadding), // Apply padding from Scaffold
            horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
            verticalArrangement = Arrangement.spacedBy(16.dp) // Add vertical spacing between elements
        ) {
            // Section for buttons to pick or capture images
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button to pick a single image from the gallery
                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text("Select Image")
                }
                // Button to pick multiple images from the gallery
                Button(onClick = { multiImagePicker.launch("image/*") }) {
                    Text("Select Multiple Images")
                }
                // Button to capture a photo with the camera
                Button(onClick = {
                    val file = createImageFile() // Create a temporary file for the photo
                    val uri =
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    cameraLauncher.launch(uri) // Launch camera to capture a photo
                }) {
                    Text("Take Photo")
                }
            }

            // LazyColumn to display the list of uploaded images
            LazyColumn {
                items(viewModel.images.value.size) { index ->
                    val imageUrl = viewModel.images.value[index]
                    // Display each image in the list
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Uploaded Image",
                        modifier = Modifier
                            .size(200.dp) // Set the image size
                            .padding(8.dp) // Apply padding around the image
                    )
                }
            }
        }
    }
}
