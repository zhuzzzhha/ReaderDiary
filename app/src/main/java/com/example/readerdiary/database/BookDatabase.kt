package com.example.readerdiary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.readerdiary.converters.TypeConverter
import com.example.readerdiary.dao.BookDao
import com.example.readerdiary.models.Book


@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class BookDatabase: RoomDatabase() {
    abstract val bookDao: BookDao


    companion object {
        @Volatile
        private var INSTANCE : BookDatabase? = null
        fun getInstance(context: Context) :BookDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_db"
                ).build().also{
                    INSTANCE = it
                }
            }
        }
    }
}