package com.example.budgetbuddy_prog7313.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomGoal(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Here I used autoGenerate to ensure each goal gets a unique ID automatically

    val name: String, // This is the title or name of the custom goal

    val description: String, // Here I included a short description so the user knows what the goal is about

    val isCompleted: Boolean = false // I used this to track whether the user has finished the goal or not
)
