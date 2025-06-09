package com.example.budgetbuddy_prog7313.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val username: String,
    val password: String
)