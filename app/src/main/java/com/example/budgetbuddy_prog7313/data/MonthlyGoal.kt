package com.example.budgetbuddy_prog7313.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MonthlyGoal(
    @PrimaryKey val monthId: String,
    val minAmount: Float,
    val maxAmount: Float
)
