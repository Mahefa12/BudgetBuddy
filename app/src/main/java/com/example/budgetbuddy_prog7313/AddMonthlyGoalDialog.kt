package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetbuddy_prog7313.data.MonthlyGoal
import com.example.budgetbuddy_prog7313.utils.DateUtils
import com.example.budgetbuddy_prog7313.viewmodels.BudgetViewModel
import org.threeten.bp.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMonthlyGoalDialog(
    onDismiss: () -> Unit,
    viewModel: BudgetViewModel = viewModel()
) {
    var minAmount by remember { mutableStateOf("") }
    var maxAmount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun validateInputs(): Boolean {
        val minVal = minAmount.toFloatOrNull()
        val maxVal = maxAmount.toFloatOrNull()

        return when {
            minVal == null -> {
                errorMessage = "Please enter a valid minimum amount"
                false
            }
            maxVal == null -> {
                errorMessage = "Please enter a valid maximum amount"
                false
            }
            minVal < 0 -> {
                errorMessage = "Minimum amount cannot be negative"
                false
            }
            maxVal < 0 -> {
                errorMessage = "Maximum amount cannot be negative"
                false
            }
            maxVal < minVal -> {
                errorMessage = "Maximum amount must be greater than or equal to minimum amount"
                false
            }
            else -> {
                errorMessage = null
                true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (validateInputs()) {
                        val now = ZonedDateTime.now()
                        val monthId = DateUtils.formatMonth(now)
                        val minVal = minAmount.toFloatOrNull() ?: 0f
                        val maxVal = maxAmount.toFloatOrNull() ?: 0f

                        val goal = MonthlyGoal(
                            monthId = monthId,
                            minAmount = minVal,
                            maxAmount = maxVal
                        )
                        viewModel.setMonthlyGoal(goal)
                        onDismiss()
                    }
                },
                enabled = minAmount.isNotEmpty() && maxAmount.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Monthly Goal") },
        text = {
            Column {
                OutlinedTextField(
                    value = minAmount,
                    onValueChange = { 
                        minAmount = it.filter { char -> char.isDigit() || char == '.' }
                        errorMessage = null
                    },
                    label = { Text("Min Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && errorMessage?.contains("minimum") == true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = maxAmount,
                    onValueChange = { 
                        maxAmount = it.filter { char -> char.isDigit() || char == '.' }
                        errorMessage = null
                    },
                    label = { Text("Max Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && errorMessage?.contains("maximum") == true
                )

                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    )
}
