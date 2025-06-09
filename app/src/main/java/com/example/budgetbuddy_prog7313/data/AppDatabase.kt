package com.example.budgetbuddy_prog7313.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        User::class,
        Category::class,
        ExpenseEntity::class,
        CustomGoal::class,
        MonthlyGoal::class,
        DailyStats::class
    ],
    version = 12 // Incremented for new field
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun monthlyGoalDao(): MonthlyGoalDao
    abstract fun customGoalDao(): CustomGoalDao
    abstract fun dailyStatsDao(): DailyStatsDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS DailyStats (
                        date TEXT NOT NULL PRIMARY KEY,
                        totalSpent REAL NOT NULL DEFAULT 0.0,
                        totalSaved REAL NOT NULL DEFAULT 0.0,
                        goalsCompleted INTEGER NOT NULL DEFAULT 0,
                        streakDays INTEGER NOT NULL DEFAULT 0,
                        dailyChallengeCompleted INTEGER NOT NULL DEFAULT 0,
                        xpEarned REAL NOT NULL DEFAULT 0.0,
                        underBudget INTEGER NOT NULL DEFAULT 0,
                        noUnnecessaryExpenses INTEGER NOT NULL DEFAULT 0,
                        loggedAllExpenses INTEGER NOT NULL DEFAULT 0,
                        dailyChallenge TEXT NOT NULL DEFAULT '',
                        dailyChallengeProgress REAL NOT NULL DEFAULT 0.0
                    )
                """)
            }
        }

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Category ADD COLUMN excludeFromSuggestions INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_db"
                )
                    .addMigrations(MIGRATION_10_11, MIGRATION_11_12)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

