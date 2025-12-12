package com.example.simplenotes

import androidx.room.Entity
import androidx.room.PrimaryKey

// Колонки таблицы
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdDate: Long = System.currentTimeMillis()
)