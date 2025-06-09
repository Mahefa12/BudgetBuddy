package com.example.budgetbuddy_prog7313
import android.app.DatePickerDialog
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.foundation.layout.*

@Composable
fun DateRangeDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var tempFrom by remember { mutableStateOf("") }
    var tempTo by remember { mutableStateOf("") }

    val openFrom = remember { mutableStateOf(false) }
    val openTo = remember { mutableStateOf(false) }

    // Show the "from" date picker dialog when openFrom is true
    if (openFrom.value) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y, m, d ->
                tempFrom = "%04d-%02d-%02d".format(y, m + 1, d)
                openFrom.value = false
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Show the "to" date picker dialog when openTo is true
    if (openTo.value) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y, m, d ->
                tempTo = "%04d-%02d-%02d".format(y, m + 1, d)
                openTo.value = false
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            TextButton(onClick = {
                // Only confirm if both dates are set
                if (tempFrom.isNotBlank() && tempTo.isNotBlank()) {
                    onConfirm(tempFrom, tempTo)
                }
            }) {
                Text("Apply")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },

        title = { Text("Select Date Range") },

        text = {
            Column {
                Button(
                    onClick = { openFrom.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (tempFrom.isBlank()) "Pick From Date" else "From: $tempFrom")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { openTo.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (tempTo.isBlank()) "Pick To Date" else "To: $tempTo")
                }
            }
        }
    )
}
