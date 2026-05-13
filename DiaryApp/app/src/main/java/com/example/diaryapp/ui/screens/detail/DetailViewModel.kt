package com.example.diaryapp.ui.screens.detail

import androidx.lifecycle.*
import com.example.diaryapp.data.local.DiaryEntry
import com.example.diaryapp.data.repository.DiaryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: DiaryRepository,
    private val entryId: Long
) : ViewModel() {

    private val _entry = MutableStateFlow<DiaryEntry?>(null)
    val entry: StateFlow<DiaryEntry?> = _entry.asStateFlow()

    init {
        viewModelScope.launch {
            _entry.value = repository.getEntryById(entryId)
        }
    }

    fun deleteEntry(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _entry.value?.let {
                repository.deleteEntry(it)
                onDeleted()
            }
        }
    }
}

class DetailViewModelFactory(
    private val repository: DiaryRepository,
    private val entryId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DetailViewModel(repository, entryId) as T
}
