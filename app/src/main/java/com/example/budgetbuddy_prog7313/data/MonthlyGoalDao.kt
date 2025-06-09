package com.example.budgetbuddy_prog7313.data



import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: MonthlyGoal)

    @Query("SELECT * FROM MonthlyGoal WHERE monthId = :monthId")
    fun getGoalForMonth(monthId: String): Flow<MonthlyGoal?>

    @Query("SELECT * FROM MonthlyGoal ORDER BY monthId DESC")
    fun getAllGoals(): Flow<List<MonthlyGoal>>
}

