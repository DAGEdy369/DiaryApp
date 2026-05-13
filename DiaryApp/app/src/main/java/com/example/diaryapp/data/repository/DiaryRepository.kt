package com.example.diaryapp.data.repository

import com.example.diaryapp.data.local.DiaryDao
import com.example.diaryapp.data.local.DiaryEntry
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val dao: DiaryDao) {
    val allEntries: Flow<List<DiaryEntry>> = dao.getAllEntries()

    suspend fun getEntryById(id: Long): DiaryEntry? = dao.getEntryById(id)
    suspend fun insertEntry(entry: DiaryEntry): Long = dao.insertEntry(entry)
    suspend fun updateEntry(entry: DiaryEntry) = dao.updateEntry(entry)
    suspend fun deleteEntry(entry: DiaryEntry) = dao.deleteEntry(entry)
}
