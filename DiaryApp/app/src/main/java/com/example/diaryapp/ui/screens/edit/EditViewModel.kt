package com.example.diaryapp.ui.screens.edit

import android.net.Uri
import androidx.lifecycle.*
import com.example.diaryapp.data.local.DiaryEntry
import com.example.diaryapp.data.repository.DiaryRepository
import com.example.diaryapp.domain.ImageStorageManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EditUiState(
    val title: String = "",
    val content: String = "",
    val imagePaths: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val titleError: Boolean = false
)

class EditViewModel(
    private val repository: DiaryRepository,
    private val imageManager: ImageStorageManager,
    private val existingId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    init {
        if (existingId != null) {
            viewModelScope.launch {
                repository.getEntryById(existingId)?.let { entry ->
                    _uiState.update {
                        it.copy(
                            title = entry.title,
                            content = entry.content,
                            imagePaths = entry.imagePaths.split(",").filter { p -> p.isNotBlank() }
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = false) }
    fun onContentChange(value: String) = _uiState.update { it.copy(content = value) }

    fun addImage(uri: Uri) {
        viewModelScope.launch {
            val path = imageManager.copyImageToInternalStorage(uri) ?: return@launch
            _uiState.update { it.copy(imagePaths = it.imagePaths + path) }
        }
    }

    fun removeImage(path: String) {
        imageManager.deleteImage(path)
        _uiState.update { it.copy(imagePaths = it.imagePaths - path) }
    }

    fun save(onComplete: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = true) }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val entry = if (existingId != null) {
                DiaryEntry(
                    id = existingId,
                    title = state.title,
                    content = state.content,
                    imagePaths = state.imagePaths.joinToString(","),
                    updatedAt = now
                )
            } else {
                DiaryEntry(
                    title = state.title,
                    content = state.content,
                    imagePaths = state.imagePaths.joinToString(",")
                )
            }
            if (existingId != null) repository.updateEntry(entry) else repository.insertEntry(entry)
            onComplete()
        }
    }
}

class EditViewModelFactory(
    private val repository: DiaryRepository,
    private val imageManager: ImageStorageManager,
    private val existingId: Long?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EditViewModel(repository, imageManager, existingId) as T
}
