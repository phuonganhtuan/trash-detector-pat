package com.example.trashdetector.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashdetector.data.model.History
import com.example.trashdetector.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    fun insertHistory(history: History) = viewModelScope.launch {
        repository.insertHistory(history)
    }
}
