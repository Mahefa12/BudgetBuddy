package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetbuddy_prog7313.components.*
import com.example.budgetbuddy_prog7313.utils.DateUtils
import com.example.budgetbuddy_prog7313.viewmodels.BudgetViewModel
import org.threeten.bp.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = viewModel()
) {
    val context = LocalContext.current
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf("This Month") }

    val dateRange = remember(selectedPeriod) {
        when (selectedPeriod) {
            "This Month" -> DateUtils.getCurrentMonthRange()
            else -> DateUtils.getLastMonthRange()
        }
    }

    val startDate = dateRange.first
    val endDate = dateRange.second

    LaunchedEffect(startDate, endDate) {
        viewModel.loadData(startDate, endDate)
    }

    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val monthlyGoal by viewModel.monthlyGoal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Period selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget Overview",
                    style = MaterialTheme.typography.headlineSmall
                )
                DropdownMenuBox(
                    items = listOf("This Month", "Last Month"),
                    selected = selectedPeriod,
                    onSelected = { selectedPeriod = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // This month's goal section
            ThisMonthGoalSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Custom goals section
            CustomGoalListSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Future months section
            MonthGoalListSection("Future Months", true)

            Spacer(modifier = Modifier.height(16.dp))

            // Previous months section
            MonthGoalListSection("Previous Months", false)
        }

        // Loading overlay
        LoadingOverlay(isLoading)

        // Error handling
        error?.let { errorMessage ->
            ErrorCard(
                errorMessage = errorMessage,
                onDismiss = { /* Error cleared on next data load */ }
            )
        }

        // Add goal button
        FloatingActionButton(
            onClick = { showAddGoalDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Goal")
        }
    }

    if (showAddGoalDialog) {
        AddMonthlyGoalDialog(
            onDismiss = { showAddGoalDialog = false }
        )
    }
}


