package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.CustomGoal
import kotlinx.coroutines.launch

@Composable
fun AddCustomGoalDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.customGoalDao()
    val scope = rememberCoroutineScope()

    var goalName by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    // I renamed these just to make it more clear what the user is actually typing in

    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            TextButton(onClick = {
                if (goalName.isNotBlank() && goalDescription.isNotBlank()) {
                    scope.launch {
                        dao.insert(CustomGoal(name = goalName, description = goalDescription))
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

        title = { Text("Add Custom Goal") },

        text = {
            Column {
                OutlinedTextField(
                    value = goalName,
                    onValueChange = { goalName = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = goalDescription,
                    onValueChange = { goalDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

