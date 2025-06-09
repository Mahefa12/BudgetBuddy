package com.example.budgetbuddy_prog7313.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: DailyStats)

    @Update
    suspend fun update(stats: DailyStats)

    @Query("SELECT * FROM DailyStats WHERE date = :date")
    fun getStatsForDate(date: String): Flow<DailyStats?>

    @Query("SELECT * FROM DailyStats ORDER BY date DESC LIMIT 7")
    fun getLastWeekStats(): Flow<List<DailyStats>>

    @Query("SELECT * FROM DailyStats ORDER BY date DESC LIMIT 30")
    fun getLastMonthStats(): Flow<List<DailyStats>>

    @Query("SELECT streakDays FROM DailyStats WHERE date = :date")
    suspend fun getCurrentStreak(date: String): Int

    @Query("SELECT COUNT(*) FROM DailyStats WHERE dailyChallengeCompleted = 1")
    suspend fun getTotalChallengesCompleted(): Int

    @Query("SELECT SUM(xpEarned) FROM DailyStats")
    suspend fun getTotalXPEarned(): Float
} 