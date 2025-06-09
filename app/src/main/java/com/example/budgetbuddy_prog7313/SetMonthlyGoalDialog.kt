package com.example.budgetbuddy_prog7313
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetMonthlyGoalDialog(
    monthId: String,
    onSave: (Float, Float) -> Unit,
    onCancel: () -> Unit
) {
    var minText by remember { mutableStateOf("") }
    var maxText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,

        confirmButton = {
            TextButton(
                onClick = {
                    val min = minText.toFloatOrNull() ?: 0f
                    val max = maxText.toFloatOrNull() ?: 0f
                    onSave(min, max)
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },

        title = { Text("Set Goal for $monthId") },

        text = {
            Column {
                OutlinedTextField(
                    value = minText,
                    onValueChange = { minText = it },
                    label = { Text("Min Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = maxText,
                    onValueChange = { maxText = it },
                    label = { Text("Max Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
