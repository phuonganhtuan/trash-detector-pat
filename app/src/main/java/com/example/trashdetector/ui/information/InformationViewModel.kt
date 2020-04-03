package com.example.trashdetector.ui.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashdetector.data.model.History
import com.example.trashdetector.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class InformationViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    val historyList: LiveData<List<History>> get() = _historyList

    private val _historyList = MutableLiveData<List<History>>()

    fun getHistories() = viewModelScope.launch {
        _historyList.value = repository.getHistories().asReversed()
    }

    fun resetHistories() = viewModelScope.launch {
        repository.resetHistory()
    }
}
