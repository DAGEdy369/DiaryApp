package com.example.diaryapp

import android.app.Application
import com.example.diaryapp.data.local.DiaryDatabase
import com.example.diaryapp.data.repository.DiaryRepository
import com.example.diaryapp.domain.ImageStorageManager

class DiaryApplication : Application() {
    val database by lazy { DiaryDatabase.getDatabase(this) }
    val repository by lazy { DiaryRepository(database.diaryDao()) }
    val imageManager by lazy { ImageStorageManager(this) }
}
