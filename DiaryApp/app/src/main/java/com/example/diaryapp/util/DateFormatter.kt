package com.example.diaryapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.getDefault())

    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))
}
