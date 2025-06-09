package com.example.budgetbuddy_prog7313

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment

@Composable
fun AddCategoryDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dao = db.categoryDao()
    val scope = rememberCoroutineScope()

    var categoryName by remember { mutableStateOf("") }
    var excludeFromSuggestions by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            TextButton(onClick = {
                if (categoryName.isNotBlank()) {
                    scope.launch(Dispatchers.IO) {
                        dao.insert(Category(name = categoryName, excludeFromSuggestions = excludeFromSuggestions))
                        onDismiss()
                    }
                }
            }) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },

        title = { Text("Add Category") },

        text = {
            Column {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exclude from AI suggestions", modifier = Modifier.weight(1f))
                    Switch(
                        checked = excludeFromSuggestions,
                        onCheckedChange = { excludeFromSuggestions = it }
                    )
                }
            }
        }
    )
}

