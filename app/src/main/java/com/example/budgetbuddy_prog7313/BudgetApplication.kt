package com.example.budgetbuddy_prog7313

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class BudgetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
} 