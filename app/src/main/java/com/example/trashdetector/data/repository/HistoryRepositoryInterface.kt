package com.example.trashdetector.data.repository

import com.example.trashdetector.data.model.History

interface HistoryRepositoryInterface {

    suspend fun getHistories(): List<History>
    suspend fun insertHistory(history: History)
}
