package tn.skooli.kotlin.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.skooli.kotlin.repository.FakeDataRepository

class FakeDataViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FakeDataRepository(application)

    var isLoading = mutableStateOf(false)
    var error = mutableStateOf("")
    var fakeData = mutableStateOf<List<Map<String, String>>>(emptyList())

    // Search and filter states
    var searchQuery = mutableStateOf("")
    var selectedType = mutableStateOf("All")
    var sortByNearest = mutableStateOf(false)

    fun fetchFakeData() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = repository.getFakeData()
                if (response.isSuccessful) {
                    fakeData.value = response.body() ?: emptyList()
                    Log.d("FakeDataViewModel", "Fetched Fake Data: ${fakeData.value}")
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    error.value = "Failed to fetch data: ${response.code()} - $errorResponse"
                    Log.e("FakeDataViewModel", "Failed to fetch data: ${response.code()} - $errorResponse")
                }
            } catch (e: Exception) {
                error.value = "Error: ${e.localizedMessage}"
                Log.e("FakeDataViewModel", "Fetch Data error", e)
            }
            isLoading.value = false
        }
    }

    // Compute filtered data based on search query and type filter
    fun filteredData(): List<Map<String, String>> {
        val data = fakeData.value.filter { item ->
            val matchesQuery = searchQuery.value.isEmpty() ||
                    (item["name"]?.contains(searchQuery.value, ignoreCase = true) == true ||
                            item["email"]?.contains(searchQuery.value, ignoreCase = true) == true)
            val matchesType = selectedType.value == "All" || item["type"] == selectedType.value
            matchesQuery && matchesType
        }
        return if (sortByNearest.value) {
            data.sortedBy { it["distance"]?.toDoubleOrNull() ?: Double.MAX_VALUE }
        } else {
            data
        }
    }

    fun toggleSortByNearest() {
        sortByNearest.value = !sortByNearest.value
    }

    // Derive available types from the fetched data (plus an "All" option)
    fun availableTypes(): List<String> {
        val types = fakeData.value.mapNotNull { it["type"] }.toSet().toList()
        return listOf("All") + types
    }
}