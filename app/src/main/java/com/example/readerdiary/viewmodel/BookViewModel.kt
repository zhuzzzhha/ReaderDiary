package com.example.readerdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Query
import com.example.readerdiary.models.Book
import com.example.readerdiary.repository.BookRepository
import com.example.readerdiary.utils.Resource

class BookViewModel(application: Application): AndroidViewModel(application) {
    private val bookRepository = BookRepository(application)

    val bookStateFlow get() =   bookRepository.bookStateFlow
    val statusLiveData get() =  bookRepository.statusLiveData
    val sortByLiveData get() =  bookRepository.sortByLiveData

    fun setSortBy(sort:Pair<String,Boolean>){
        bookRepository.setSortBy(sort)
    }

    fun getBookList(isAsc : Boolean, sortByName:String) {
        bookRepository.getBookList(isAsc, sortByName)
    }

    fun insertBook(book: Book){
        bookRepository.insertBook(book)
    }

    fun deleteBook(book: Book) {
        bookRepository.deleteBook(book)
    }

    fun deleteBookUsingId(bookId: String){
        bookRepository.deleteBookUsingId(bookId)
    }

    fun updateBook(book: Book) {
        bookRepository.updateBook(book)
    }

    fun updateBookParticularField(bookId: String,title:String,description:String) {
        bookRepository.updateBookPaticularField(bookId, title, description)
    }
    fun searchBookList(query: String){
        bookRepository.searchBookList(query)
    }
}