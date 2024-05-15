package com.example.readerdiary.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity()
data class Book(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "bookId")
    val id: String,
    @ColumnInfo(name = "bookTitle")
    val title: String,
    val description: String,
    val date: Date,
)
