package com.example.budgetbuddy_prog7313.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.DailyStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class DailyStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dailyStatsDao = db.dailyStatsDao()
    private val expenseDao = db.expenseDao()
    private val monthlyGoalDao = db.monthlyGoalDao()
    private val customGoalDao = db.customGoalDao()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _todayStats = MutableStateFlow<DailyStats?>(null)
    val todayStats: StateFlow<DailyStats?> = _todayStats.asStateFlow()

    private val _weeklyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyStats: StateFlow<List<DailyStats>> = _weeklyStats.asStateFlow()

    private val _monthlyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val monthlyStats: StateFlow<List<DailyStats>> = _monthlyStats.asStateFlow()

    init {
        loadTodayStats()
        loadWeeklyStats()
        loadMonthlyStats()
    }

    private fun loadTodayStats() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        viewModelScope.launch {
            dailyStatsDao.getStatsForDate(today)
                .catch { e -> _error.value = e.message }
                .collect { stats ->
                    _todayStats.value = stats ?: createNewDailyStats(today)
                }
        }
    }

    private fun loadWeeklyStats() {
        viewModelScope.launch {
            dailyStatsDao.getLastWeekStats()
                .catch { e -> _error.value = e.message }
                .collect { stats ->
                    _weeklyStats.value = stats
                }
        }
    }

    private fun loadMonthlyStats() {
        viewModelScope.launch {
            dailyStatsDao.getLastMonthStats()
                .catch { e -> _error.value = e.message }
                .collect { stats ->
                    _monthlyStats.value = stats
                }
        }
    }

    private suspend fun createNewDailyStats(date: String): DailyStats {
        val stats = DailyStats(date = date)
        dailyStatsDao.insert(stats)
        return stats
    }

    fun updateDailyChallenge(progress: Float) {
        viewModelScope.launch {
            _todayStats.value?.let { stats ->
                val updatedStats = stats.copy(
                    dailyChallengeProgress = progress,
                    dailyChallengeCompleted = progress >= 1.0f
                )
                dailyStatsDao.update(updatedStats)
                if (progress >= 1.0f) {
                    awardXP(50f) // Award XP for completing daily challenge
                }
            }
        }
    }

    fun awardXP(amount: Float) {
        viewModelScope.launch {
            _todayStats.value?.let { stats ->
                val updatedStats = stats.copy(xpEarned = stats.xpEarned + amount)
                dailyStatsDao.update(updatedStats)
            }
        }
    }

    fun updateStreak() {
        viewModelScope.launch {
            _todayStats.value?.let { stats ->
                val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE)
                val yesterdayStreak = dailyStatsDao.getCurrentStreak(yesterday)
                val updatedStats = stats.copy(streakDays = yesterdayStreak + 1)
                dailyStatsDao.update(updatedStats)
            }
        }
    }

    fun refreshStats() {
        loadTodayStats()
        loadWeeklyStats()
        loadMonthlyStats()
    }
} 