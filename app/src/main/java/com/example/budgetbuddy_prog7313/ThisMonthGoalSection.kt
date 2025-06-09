package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.MonthlyGoal
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun ThisMonthGoalSection() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.monthlyGoalDao()
    val scope = rememberCoroutineScope()

    val monthId = remember {
        LocalDate.now().let { "%04d-%02d".format(it.year, it.monthValue) }
    }

    val monthName = remember {
        LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    var currentGoal by remember { mutableStateOf<MonthlyGoal?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(monthId) {
        dao.getGoalForMonth(monthId).collectLatest {
            currentGoal = it
        }
    }

    Card(
        border = if (currentGoal != null) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("THIS MONTH: $monthName", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (currentGoal != null) {
                Text("Min: R${"%.2f".format(currentGoal!!.minAmount)}")
                Text("Max: R${"%.2f".format(currentGoal!!.maxAmount)}")
            } else {
                Button(onClick = { showDialog = true }) {
                    Text("Set Goal")
                }
            }
        }
    }

    if (showDialog) {
        SetMonthlyGoalDialog(
            monthId = monthId,
            onSave = { min, max ->
                scope.launch {
                    dao.insert(MonthlyGoal(monthId, min, max))
                    showDialog = false
                }
            },
            onCancel = { showDialog = false }
        )
    }
}

