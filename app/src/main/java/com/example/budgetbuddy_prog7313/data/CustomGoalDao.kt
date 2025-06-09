package com.example.budgetbuddy_prog7313.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: CustomGoal)

    @Update
    suspend fun update(goal: CustomGoal)

    @Delete
    suspend fun delete(goal: CustomGoal)

    @Query("SELECT * FROM CustomGoal WHERE isCompleted = 0")
    fun getActiveGoals(): Flow<List<CustomGoal>>

    @Query("SELECT * FROM CustomGoal WHERE isCompleted = 1")
    fun getCompletedGoals(): Flow<List<CustomGoal>>
}

