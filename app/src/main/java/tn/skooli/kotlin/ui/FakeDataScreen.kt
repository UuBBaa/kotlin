package tn.skooli.kotlin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.skooli.kotlin.viewmodel.FakeDataViewModel
import androidx.compose.foundation.lazy.items

@Composable
fun FakeDataScreen(fakeDataViewModel: FakeDataViewModel = viewModel()) {
    // Fetch data when the screen loads
    LaunchedEffect(Unit) {
        fakeDataViewModel.fetchFakeData()
    }
    // Local state for dropdown expansion
    var expanded by remember { mutableStateOf(false) }
    // Get current search query and filter from the view model
    val searchQuery = fakeDataViewModel.searchQuery.value
    val selectedType = fakeDataViewModel.selectedType.value

    // Filtered fake data and available types computed in the view model
    val filteredData = fakeDataViewModel.filteredData()
    val types = fakeDataViewModel.availableTypes()
    val sortByNearest = fakeDataViewModel.sortByNearest.value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Search bar
        TextField(
            value = searchQuery,
            onValueChange = { fakeDataViewModel.searchQuery.value = it },
            label = { Text("Search by name or email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Type filter dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Filter by type: $selectedType")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                types.forEach { type ->
                    DropdownMenuItem(onClick = {
                        fakeDataViewModel.selectedType.value = type
                        expanded = false
                    }) {
                        Text(type)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { fakeDataViewModel.toggleSortByNearest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (sortByNearest) "Sort: Nearest First" else "Sort: Default Order")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Table header
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("ID", modifier = Modifier.weight(1f))
            Text("Name", modifier = Modifier.weight(2f))
            Text("Email", modifier = Modifier.weight(3f))
            Text("Type", modifier = Modifier.weight(1f))
            Text("Distance", modifier = Modifier.weight(1f))
        }
        Divider()
        // Table rows
        LazyColumn {
            items(filteredData) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = item["id"] ?: "", modifier = Modifier.weight(1f))
                    Text(text = item["name"] ?: "", modifier = Modifier.weight(2f))
                    Text(text = item["email"] ?: "", modifier = Modifier.weight(3f))
                    Text(text = item["type"] ?: "", modifier = Modifier.weight(1f))
                    Text(text = item["distance"] ?: "", modifier = Modifier.weight(1f))
                }
                Divider()
            }
        }

    }
}
