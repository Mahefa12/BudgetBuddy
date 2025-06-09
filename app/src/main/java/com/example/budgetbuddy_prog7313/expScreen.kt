package com.example.budgetbuddy_prog7313
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.app.DatePickerDialog
import com.example.budgetbuddy_prog7313.CategoryItem
import com.example.budgetbuddy_prog7313.data.CategoryTotal
import kotlinx.coroutines.launch
import com.example.budgetbuddy_prog7313.data.ExpenseEntity
import com.example.budgetbuddy_prog7313.data.AppDatabase
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Movie
import com.example.budgetbuddy_prog7313.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Expenses", "Categories")
    var showDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        showDialog = true
                    } else if (selectedTab == 1) {
                        showCategoryDialog = true
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            when (selectedTab) {
                0 -> ExpenseListScreen()
                1 -> CategoryListScreen()
            }

            if (selectedTab == 0 && showDialog) {
                AddExpenseDialog(onDismiss = { showDialog = false })
            }

            if (selectedTab == 1 && showCategoryDialog) {
                AddCategoryDialog(onDismiss = { showCategoryDialog = false })
            }
        }
    }
}

@Composable
fun ExpenseListScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val expenseDao = db.expenseDao()

    var fromDate by remember { mutableStateOf<String?>(null) }
    var toDate by remember { mutableStateOf<String?>(null) }
    var showDateDialog by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    val expensesFlow = if (fromDate != null && toDate != null) {
        expenseDao.getBetweenDates(fromDate!!, toDate!!)
    } else {
        expenseDao.getAllSorted()
    }

    val expenses by expensesFlow.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { showDateDialog = true }) {
                Text("Filter")
            }

            Button(onClick = {
                fromDate = null
                toDate = null
            }) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (expenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No expenses yet")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(expenses) { expense ->
                    ExpenseItem(expense = expense, onClick = { selectedExpense = it })
                }
            }
        }

        selectedExpense?.let {
            ExpenseDetailDialog(expense = it, onDismiss = { selectedExpense = null })
        }

        if (showDateDialog) {
            DateRangeDialog(
                onConfirm = { from, to ->
                    fromDate = from
                    toDate = to
                    showDateDialog = false
                },
                onDismiss = { showDateDialog = false }
            )
        }
    }
}

@Composable
fun ExpenseItem(
    expense: ExpenseEntity,
    onClick: (ExpenseEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(expense) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image (if available)
            if (!expense.photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = expense.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp)
                )
            }

            // Expense info
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.name, style = MaterialTheme.typography.titleMedium)
                Text(expense.description, style = MaterialTheme.typography.bodyMedium)
                Text("Category: ${expense.category}", style = MaterialTheme.typography.bodySmall)
            }

            Text(
                text = "R${"%.2f".format(expense.amount)}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Data class for expenses
data class Expense(
    val name: String,
    val category: String,
    val amount: Double,
    val icon: ImageVector
)

@Composable
fun CategoryListScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val categoryDao = db.categoryDao()
    val expenseDao = db.expenseDao()

    var showDateDialog by remember { mutableStateOf(false) }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }

    val categories by categoryDao.getAllCategories().collectAsState(initial = emptyList())
    var totals by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    val scope = rememberCoroutineScope()

    var editingCategory by remember { mutableStateOf<Category?>(null) }

    // Loads totals either for a date range or all time
    fun loadTotals() {
        scope.launch {
            val flow = if (fromDate.isNotBlank() && toDate.isNotBlank()) {
                expenseDao.getCategoryTotalsBetweenDates(fromDate, toDate)
            } else {
                expenseDao.getAllCategoryTotals()
            }

            flow.collectLatest { result ->
                totals = result.associate { it.category to it.total }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadTotals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(
            onClick = { showDateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filter by Date Range")
        }

        Button(
            onClick = {
                fromDate = ""
                toDate = ""
                loadTotals()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Filter", color = MaterialTheme.colorScheme.onError)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No categories yet.")
            }
        } else {
            LazyColumn {
                items(categories) { category ->
                    val total = totals[category.name] ?: 0.0
                    CategoryItem(
                        name = category.name,
                        total = total,
                        onClick = { editingCategory = category }
                    )
                }
            }
        }
    }

    // Show the date filter dialog when triggered
    if (showDateDialog) {
        DateRangeDialog(
            onConfirm = { from, to ->
                fromDate = from
                toDate = to
                loadTotals()
                showDateDialog = false
            },
            onDismiss = { showDateDialog = false }
        )
    }

    // Show the edit dialog when a category is tapped
    editingCategory?.let { category ->
        EditCategoryDialog(
            category = category,
            onDismiss = { editingCategory = null },
            onSave = { updatedCategory ->
                scope.launch {
                    categoryDao.update(updatedCategory)
                    editingCategory = null
                }
            }
        )
    }
}

@Composable
fun CategoryItem(name: String, total: Double, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text("R${"%.2f".format(total)}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun EditCategoryDialog(category: com.example.budgetbuddy_prog7313.data.Category, onDismiss: () -> Unit, onSave: (com.example.budgetbuddy_prog7313.data.Category) -> Unit) {
    var categoryName by remember { mutableStateOf(category.name) }
    var excludeFromSuggestions by remember { mutableStateOf(category.excludeFromSuggestions) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (categoryName.isNotBlank()) {
                    onSave(category.copy(name = categoryName, excludeFromSuggestions = excludeFromSuggestions))
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
        title = { Text("Edit Category") },
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

