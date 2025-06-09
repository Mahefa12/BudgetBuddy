package com.example.budgetbuddy_prog7313

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetbuddy_prog7313.utils.DateUtils
import com.example.budgetbuddy_prog7313.viewmodels.BudgetViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.threeten.bp.ZonedDateTime
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.sp
import com.example.budgetbuddy_prog7313.data.MonthlyGoal
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AnalyticsScreen(
    viewModel: BudgetViewModel = viewModel()
) {
    var selectedPeriod by remember { mutableStateOf("This Month") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val monthlyGoal by viewModel.monthlyGoal.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()

    // Calculate date range based on selected period
    val (startDate, endDate) = remember(selectedPeriod) {
        val now = ZonedDateTime.now()
        when (selectedPeriod) {
            "This Month" -> {
                val start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                val end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                    .withHour(23).withMinute(59).withSecond(59)
                Pair(start, end)
            }
            "Last Month" -> {
                val lastMonth = now.minusMonths(1)
                val start = lastMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                val end = lastMonth.withDayOfMonth(lastMonth.toLocalDate().lengthOfMonth())
                    .withHour(23).withMinute(59).withSecond(59)
                Pair(start, end)
            }
            else -> Pair(now, now)
        }
    }

    // Load data when period changes
    LaunchedEffect(selectedPeriod) {
        viewModel.loadData(startDate, endDate)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.loadData(startDate, endDate) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = error ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadData(startDate, endDate) }) {
                    Text("Retry")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Analytics",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Row {
                            FilterChip(
                                selected = selectedPeriod == "This Month",
                                onClick = { selectedPeriod = "This Month" },
                                label = { Text("This Month") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = selectedPeriod == "Last Month",
                                onClick = { selectedPeriod = "Last Month" },
                                label = { Text("Last Month") }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Monthly Budget Goal",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            monthlyGoal?.let { goal ->
                                Text(
                                    text = "Min: R${goal.minAmount}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Max: R${goal.maxAmount}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                
                                // Calculate total spent from category totals
                                val totalSpent = categoryTotals.sumOf { it.total.toDouble() }.toFloat()
                                val progress = (totalSpent / goal.maxAmount).coerceIn(0f, 1f)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Progress bar container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    // Progress indicator
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(progress)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    totalSpent < goal.minAmount -> MaterialTheme.colorScheme.primary
                                                    totalSpent <= goal.maxAmount -> MaterialTheme.colorScheme.tertiary
                                                    else -> MaterialTheme.colorScheme.error
                                                }
                                            )
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Status
                                Text(
                                    text = when {
                                        totalSpent < goal.minAmount -> 
                                            "Good progress! ${String.format("%.1f", (totalSpent/goal.minAmount * 100))}% of minimum goal"
                                        totalSpent <= goal.maxAmount -> 
                                            "On track! ${String.format("%.1f", (totalSpent/goal.maxAmount * 100))}% of maximum goal"
                                        else -> 
                                            "Over budget by R${String.format("%.2f", totalSpent - goal.maxAmount)}"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        totalSpent < goal.minAmount -> MaterialTheme.colorScheme.primary
                                        totalSpent <= goal.maxAmount -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                            } ?: run {
                                Text(
                                    text = "No goal set for this month",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Category Totals",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (categoryTotals.isNotEmpty()) {
                                AndroidView(
                                    factory = { context ->
                                        BarChart(context).apply {
                                            description.isEnabled = false
                                            setDrawGridBackground(false)
                                            setDrawBarShadow(false)
                                            setDrawValueAboveBar(true)
                                            setPinchZoom(false)
                                            setScaleEnabled(true)
                                            setTouchEnabled(true)
                                            
                                            legend.apply {
                                                isEnabled = true
                                                textSize = 12f
                                                formSize = 12f
                                                formToTextSpace = 5f
                                                xEntrySpace = 10f
                                                yEntrySpace = 5f
                                                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                                                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                                                orientation = Legend.LegendOrientation.HORIZONTAL
                                                setDrawInside(false)
                                                textColor = Color.parseColor("#000000")
                                            }

                                            val entries = categoryTotals.mapIndexed { index, total ->
                                                BarEntry(index.toFloat(), total.total.toFloat())
                                            }

                                            val dataSet = BarDataSet(entries, "Expenses").apply {
                                                color = primaryColor
                                                valueTextColor = Color.parseColor("#000000")
                                                valueTextSize = 10f
                                                setDrawValues(true)
                                            }

                                            val barData = BarData(dataSet).apply {
                                                barWidth = 0.5f
                                                setValueFormatter(DefaultValueFormatter(0))
                                            }

                                            data = barData

                                            xAxis.apply {
                                                position = XAxis.XAxisPosition.BOTTOM
                                                granularity = 1f
                                                setDrawGridLines(false)
                                                textColor = Color.parseColor("#000000")
                                                textSize = 10f
                                                labelRotationAngle = -45f
                                                valueFormatter = IndexAxisValueFormatter(
                                                    categoryTotals.map { it.category }
                                                )
                                            }

                                            axisLeft.apply {
                                                setDrawGridLines(true)
                                                textColor = Color.parseColor("#000000")
                                                textSize = 10f
                                                axisMinimum = 0f
                                            }

                                            axisRight.isEnabled = false

                                            invalidate()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No data available",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(categoryTotals) { total ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = total.category,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "R${total.total}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
} 