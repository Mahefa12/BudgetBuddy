package com.example.budgetbuddy_prog7313

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import java.io.File
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date



@Composable
fun AddExpenseDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val expenseDao = db.expenseDao()
    val categoryDao = db.categoryDao()
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imageFile = remember { File(context.cacheDir, "photo.jpg") }
    val imageUriForCamera = remember { FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = imageUriForCamera
        }
    }
    val categories by categoryDao.getAllCategories().collectAsState(initial = emptyList())
    var showPhotoOptions by remember { mutableStateOf(false)}
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank() && amount.isNotBlank()) {
                    coroutineScope.launch(Dispatchers.IO) {
                        val now = Date()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val timeFormat = SimpleDateFormat("HH:mm")
                        val finalDate = if (date.isBlank()) dateFormat.format(now) else date
                        val finalStartTime = if (startTime.isBlank()) timeFormat.format(now) else startTime
                        val finalEndTime = if (endTime.isBlank()) timeFormat.format(now) else endTime
                        expenseDao.insert(
                            ExpenseEntity(
                                name = name,
                                category = category,
                                description = description,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                date = finalDate,
                                startTime = finalStartTime,
                                endTime = finalEndTime,
                                photoUri = imageUri?.toString()
                            )
                        )
                        onDismiss()
                    }
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Expense") },
        text = {
            var showPhotoOptions by remember { mutableStateOf(false) }

            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )

                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })

                DropdownMenuBox(categories.map { cat -> cat.name }, category, onSelected = { category = it })

                Spacer(Modifier.height(8.dp))

                Button(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            date = "%04d-%02d-%02d".format(y, m + 1, d)
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(if (date.isBlank()) "Pick Date" else date)
                }

                Button(onClick = {
                    val cal = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, h, m -> startTime = "%02d:%02d".format(h, m) },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }) { Text(if (startTime.isBlank()) "Start Time" else startTime) }

                Button(onClick = {
                    val cal = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, h, m -> endTime = "%02d:%02d".format(h, m) },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }) { Text(if (endTime.isBlank()) "End Time" else endTime) }

                Button(onClick = { showPhotoOptions = true }) {
                    Text("Attach Photo")
                }

                imageUri?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Photo attached.")
                }

                if (showPhotoOptions) {
                    AlertDialog(
                        onDismissRequest = { showPhotoOptions = false },
                        confirmButton = {},
                        title = { Text("Add Photo") },
                        text = {
                            Column {
                                TextButton(onClick = {
                                    showPhotoOptions = false
                                    cameraLauncher.launch(imageUriForCamera)
                                }) { Text("Take Photo") }

                                TextButton(onClick = {
                                    showPhotoOptions = false
                                    imagePicker.launch("image/*")
                                }) { Text("Choose from Gallery") }
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPhotoOptions = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    )}
