package com.example.trashdetector.data.repository

import com.example.trashdetector.data.model.History
import com.example.trashdetector.data.dao.HistoryDao

class HistoryRepository(private val dao: HistoryDao) : HistoryRepositoryInterface {

    override suspend fun getHistories() = dao.getHistories()
    override suspend fun insertHistory(history: History) = dao.insertHistory(history)
    override suspend fun resetHistory() = dao.resetHistory()
}
