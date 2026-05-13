package com.example.diaryapp.ui.screens.home

import androidx.lifecycle.*
import com.example.diaryapp.data.local.DiaryEntry
import com.example.diaryapp.data.repository.DiaryRepository
import kotlinx.coroutines.flow.*

class HomeViewModel(private val repository: DiaryRepository) : ViewModel() {
    val entries: StateFlow<List<DiaryEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class HomeViewModelFactory(private val repository: DiaryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(repository) as T
}
