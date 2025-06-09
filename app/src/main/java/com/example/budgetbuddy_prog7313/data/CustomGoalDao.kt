package com.example.budgetbuddy_prog7313.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: CustomGoal)
    // I used REPLACE here so that if a goal with the same ID exists, it just gets updated instead of causing an error

    @Update
    suspend fun update(goal: CustomGoal)

    @Delete
    suspend fun delete(goal: CustomGoal)

    @Query("SELECT * FROM CustomGoal WHERE isCompleted = 0")
    fun getActiveGoals(): Flow<List<CustomGoal>>
    // This returns all the goals that the user hasn’t marked as completed yet

    @Query("SELECT * FROM CustomGoal WHERE isCompleted = 1")
    fun getCompletedGoals(): Flow<List<CustomGoal>>
    // And this one fetches the ones that are finished — helps for organizing them separately
}

