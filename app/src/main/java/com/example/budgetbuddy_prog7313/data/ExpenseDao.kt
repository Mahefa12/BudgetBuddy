package com.example.budgetbuddy_prog7313.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: ExpenseEntity)
    // I used a simple insert here since each expense is added as a new record

    @Query("SELECT category, SUM(amount) as total FROM ExpenseEntity GROUP BY category")
    fun getAllCategoryTotals(): Flow<List<CategoryTotal>>
    // This groups all expenses by category and calculates the total per category — useful for category summaries

    @Query("""
        SELECT category, SUM(amount) as total 
        FROM ExpenseEntity 
        WHERE date BETWEEN :fromDate AND :toDate 
        GROUP BY category
    """)
    fun getCategoryTotalsBetweenDates(fromDate: String, toDate: String): Flow<List<CategoryTotal>>
    // This does the same thing but only for expenses within a specific date range

    @Query("SELECT * FROM ExpenseEntity ORDER BY date DESC")
    fun getAll(): Flow<List<ExpenseEntity>>
    // Retrieves every expense and sorts them by most recent first

    @Query("""
        SELECT SUM(amount) FROM ExpenseEntity 
        WHERE date BETWEEN :startDate AND :endDate
    """)
    fun getTotalSpentForMonth(startDate: String, endDate: String): Flow<Float?>
    // Used this to calculate the total spent during a specific month — helps with goal checking

    @Query("SELECT * FROM ExpenseEntity ORDER BY date DESC")
    fun getAllSorted(): Flow<List<ExpenseEntity>>
    // Redundant with getAll, but keeping it in case I want a named version to make sorting clearer

    @Query("SELECT * FROM ExpenseEntity WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun getBetweenDates(from: String, to: String): Flow<List<ExpenseEntity>>
    // This returns all expenses between two dates — good for filtering reports or user-defined views
}

