package com.example.budgetbuddy_prog7313.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyStats(
    @PrimaryKey
    val date: String,
    
    val totalSpent: Double = 0.0,
    val totalSaved: Double = 0.0,
    val goalsCompleted: Int = 0,
    val streakDays: Int = 0,
    val dailyChallengeCompleted: Boolean = false,
    val xpEarned: Float = 0f,
    
    // Achievement tracking
    val underBudget: Boolean = false,
    val noUnnecessaryExpenses: Boolean = false,
    val loggedAllExpenses: Boolean = false,
    
    // Daily challenge
    val dailyChallenge: String = "",
    val dailyChallengeProgress: Float = 0f
) 