package com.example.budgetbuddy_prog7313.data



import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: MonthlyGoal)
    // I used REPLACE so users can update an existing monthly goal without needing to delete first

    @Query("SELECT * FROM MonthlyGoal WHERE monthId = :monthId")
    fun getGoalForMonth(monthId: String): Flow<MonthlyGoal?>
    // This fetches the goal for a specific month, using the monthId as a unique identifier (like "2025-05")

    @Query("SELECT * FROM MonthlyGoal ORDER BY monthId DESC")
    fun getAllGoals(): Flow<List<MonthlyGoal>>
    // I ordered by monthId descending so the most recent goals appear first in any list
}

