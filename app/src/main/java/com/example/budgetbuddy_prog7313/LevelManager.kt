package com.example.budgetbuddy_prog7313

import android.content.Context
import android.content.SharedPreferences

class LevelManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("level_data", Context.MODE_PRIVATE)
    // SharedPreferences stores the user's level and XP locally

    fun getLevel(): Int = prefs.getInt("level", 1)
    // Defaults to level 1 if no value is stored yet

    fun getXP(): Float = prefs.getFloat("xp", 0f)
    // Defaults to 0 XP when starting out

    fun addProgress(percent: Float) {
        val currentXP = getXP()
        val newXP = currentXP + percent

        if (newXP >= 1f) {
            // If XP reaches or exceeds 1.0, increase the level and carry over remaining XP
            val currentLevel = getLevel()
            prefs.edit()
                .putInt("level", currentLevel + 1)
                .putFloat("xp", newXP - 1f)
                .apply()
        } else {
            // Otherwise, just update the XP progress
            prefs.edit().putFloat("xp", newXP).apply()
        }
    }
}

