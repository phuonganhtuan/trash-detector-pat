package com.example.trashdetector.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.trashdetector.data.model.History
import com.example.trashdetector.data.room.AppDatabase.Companion.DATABASE_NAME

@Dao
interface HistoryDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertHistory(history: History)
    @Query("SELECT * FROM $DATABASE_NAME")
    suspend fun getHistories(): List<History>
    @Query("DELETE FROM $DATABASE_NAME")
    suspend fun resetHistory()
}
