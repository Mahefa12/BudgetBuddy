package com.example.budgetbuddy_prog7313.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(

    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,

    val name: String,

    val excludeFromSuggestions: Boolean = false
)
