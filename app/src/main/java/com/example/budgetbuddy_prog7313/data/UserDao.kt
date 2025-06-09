package com.example.budgetbuddy_prog7313.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
    // I used REPLACE here so that if a user registers with an existing username, it updates their info

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): User?
    // This checks if the username and password match — if they do, it returns the user for login
}

