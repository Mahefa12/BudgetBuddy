package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.User
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.budgetbuddy_prog7313.data.ExpenseEntity
@Composable
fun ExpenseDetailDialog(
    expense: ExpenseEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {},

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },

        title = { Text(expense.name) },

        text = {
            Column {
                if (!expense.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = expense.photoUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Show additional expense info
                Text("Description: ${expense.description}")
                Text("Category: ${expense.category}")
                Text("Amount: R${"%.2f".format(expense.amount)}")
                Text("Date: ${expense.date}")
                Text("Time: ${expense.startTime} - ${expense.endTime}")
            }
        }
    )
}
