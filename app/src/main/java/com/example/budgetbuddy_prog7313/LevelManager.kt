package com.example.budgetbuddy_prog7313

import android.content.Context
import android.content.SharedPreferences

class LevelManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("level_data", Context.MODE_PRIVATE)

    fun getLevel(): Int = prefs.getInt("level", 1)

    fun getXP(): Float = prefs.getFloat("xp", 0f)

    fun addProgress(percent: Float) {
        val currentXP = getXP()
        val newXP = currentXP + percent

        if (newXP >= 1f) {
            val currentLevel = getLevel()
            prefs.edit()
                .putInt("level", currentLevel + 1)
                .putFloat("xp", newXP - 1f)
                .apply()
        } else {
            prefs.edit().putFloat("xp", newXP).apply()
        }
    }
}

