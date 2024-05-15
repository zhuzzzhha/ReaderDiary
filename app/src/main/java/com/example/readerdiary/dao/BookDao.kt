package com.example.readerdiary.dao

import androidx.room.*
import com.example.readerdiary.models.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("""SELECT * FROM Book ORDER BY
        CASE WHEN :isAsc = 1 THEN bookTitle END ASC, 
        CASE WHEN :isAsc = 0 THEN bookTitle END DESC""")
    fun getBookListSortByBookTitle(isAsc: Boolean) : Flow<List<Book>>

    @Query("""SELECT * FROM Book ORDER BY
        CASE WHEN :isAsc = 1 THEN date END ASC, 
        CASE WHEN :isAsc = 0 THEN date END DESC""")
    fun getBookListSortByBookDate(isAsc: Boolean) : Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(task: Book): Long


    // First way
    @Delete
    suspend fun deleteBook(book: Book) : Int


    // Second Way
    @Query("DELETE FROM Book WHERE bookId == :bookId")
    suspend fun deleteBookUsingId(bookId: String) : Int


    @Update
    suspend fun updateBook(book: Book): Int


    @Query("UPDATE Book SET bookTitle=:title, description = :description WHERE bookId = :bookId")
    suspend fun updateBookParticularField(bookId:String,title:String,description:String): Int


    @Query("SELECT * FROM Book WHERE bookTitle LIKE :query ORDER BY date DESC")
    fun searchBookList(query: String) : Flow<List<Book>>
}