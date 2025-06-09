package com.example.budgetbuddy_prog7313.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM Category")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE excludeFromSuggestions = 0")
    fun getIncludedCategories(): Flow<List<Category>>

    @Query("UPDATE Category SET excludeFromSuggestions = :exclude WHERE id = :categoryId")
    suspend fun setExcludeFromSuggestions(categoryId: Int, exclude: Boolean)
}
