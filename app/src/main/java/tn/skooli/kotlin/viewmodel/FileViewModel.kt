package tn.skooli.kotlin.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import tn.skooli.kotlin.repository.FakeDataRepository
import java.io.File

class FileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FakeDataRepository(application) // Initialize repository

    // State variables for images, loading state, and errors
    val images = mutableStateOf<List<String>>(emptyList()) // Store list of image URLs
    val isLoading = mutableStateOf(false) // To track loading state
    val error = mutableStateOf<String?>(null) // To store any error messages

    // Function to upload an image by URI
    fun uploadImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                isLoading.value = true // Set loading state to true

                // Safely open input stream for the image file
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    throw Exception("Failed to open input stream from URI: $uri")
                }

                // Create a temporary file to store the image
                val file = File.createTempFile("image", ".jpg", context.cacheDir).apply {
                    outputStream().use { output ->
                        inputStream.use { input ->
                            input.copyTo(output) // Copy content from input stream to output file
                        }
                    }
                }

                // Ensure the file exists and is not empty
                if (!file.exists() || file.length() == 0L) {
                    throw Exception("Temporary file creation failed or is empty")
                }

                // Create RequestBody for uploading the file
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Upload the image using the repository
                val response = repository.uploadImage(part)

                if (response.isSuccessful) {
                    Log.d("FileUpload", "Image uploaded successfully: ${response.body()}")
                    loadImages() // Load images after successful upload
                } else {
                    // Handle upload failure
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    throw Exception("Upload failed: ${response.code()} - $errorResponse")
                }
            } catch (e: Exception) {
                // Handle errors during image upload
                error.value = "Upload failed: ${e.message}"
                Log.e("FileUpload", "Upload failed", e)
            } finally {
                isLoading.value = false // Reset loading state
            }
        }
    }

    // Function to load all images
    fun loadImages() {
        viewModelScope.launch {
            try {
                isLoading.value = true // Set loading state to true
                val response = repository.getImages() // Fetch images from the repository
                Log.d("FileUpload", "Response: ${response.body()}") // Debug log
                images.value = response.body() ?: emptyList() // Update images state
            } catch (e: Exception) {
                // Handle errors during image fetching
                error.value = "Failed to load images: ${e.message}"
                Log.e("FileUpload", "Load images failed", e)
            } finally {
                isLoading.value = false // Reset loading state
            }
        }
    }
}