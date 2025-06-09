package com.example.budgetbuddy_prog7313.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val amount: Double,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val photoUri: String? = null
)

