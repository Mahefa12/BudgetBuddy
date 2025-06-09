package com.example.budgetbuddy_prog7313



import android.content.Context
import android.content.SharedPreferences

class GoalManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("budget_goals", Context.MODE_PRIVATE)
    // I’m using SharedPreferences to persist min and max goal values locally

    fun setMinGoal(amount: Float) {
        prefs.edit().putFloat("min_goal", amount).apply()
    }

    fun setMaxGoal(amount: Float) {
        prefs.edit().putFloat("max_goal", amount).apply()
    }

    fun getMinGoal(): Float = prefs.getFloat("min_goal", 0f)
    fun getMaxGoal(): Float = prefs.getFloat("max_goal", 0f)
    // If there's no saved value, it defaults to 0
}
