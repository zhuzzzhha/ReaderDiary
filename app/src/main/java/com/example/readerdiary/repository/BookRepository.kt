package com.example.readerdiary.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.readerdiary.dao.BookDao
import com.example.readerdiary.database.BookDatabase
import com.example.readerdiary.models.Book
import com.example.readerdiary.utils.Resource
import com.example.readerdiary.utils.Resource.Error
import com.example.readerdiary.utils.Resource.Loading
import com.example.readerdiary.utils.Resource.Success
import com.example.readerdiary.utils.StatusResult
import com.example.readerdiary.utils.Resource.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookRepository(application: Application) {

    private val bookDao = BookDatabase.getInstance(application).bookDao


    private val _bookStateFlow = MutableStateFlow<Resource<Flow<List<Book>>>>(Loading())
    val bookStateFlow: StateFlow<Resource<Flow<List<Book>>>>
        get() = _bookStateFlow

    private val _statusLiveData = MutableLiveData<Resource<StatusResult>>()
    val statusLiveData: LiveData<Resource<StatusResult>>
        get() = _statusLiveData


    private val _sortByLiveData = MutableLiveData<Pair<String,Boolean>>().apply {
        postValue(Pair("title",true))
    }
    val sortByLiveData: LiveData<Pair<String, Boolean>>
        get() = _sortByLiveData


    fun setSortBy(sort:Pair<String,Boolean>){
        _sortByLiveData.postValue(sort)
    }

    fun getBookList(isAsc : Boolean, sortByName:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _bookStateFlow.emit(Loading())
                delay(500)
                val result = if (sortByName == "title"){
                    bookDao.getBookListSortByBookTitle(isAsc)
                }else{
                    bookDao.getBookListSortByBookDate(isAsc)
                }
                _bookStateFlow.emit(Success("loading", result))
            } catch (e: Exception) {
                _bookStateFlow.emit(Error(e.message.toString()))
            }
        }
    }


    fun insertBook(book: Book) {
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = bookDao.insertBook(book)
                handleResult(result.toInt(), "Inserted Book Successfully", StatusResult.Added)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }


    fun deleteBook(book: Book) {
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = bookDao.deleteBook(book)
                handleResult(result, "Deleted Book Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

    fun deleteBookUsingId(bookId: String) {
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = bookDao.deleteBookUsingId(bookId)
                handleResult(result, "Deleted Book Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }


    fun updateBook(book: Book) {
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = bookDao.updateBook(book)
                handleResult(result, "Updated Book Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

    fun updateBookPaticularField(bookId: String, title: String, description: String) {
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = bookDao.updateBookParticularField(bookId, title, description)
                handleResult(result, "Updated Book Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

    fun searchBookList(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _bookStateFlow.emit(Loading())
                val result = bookDao.searchBookList("%${query}%")
                _bookStateFlow.emit(Success("loading", result))
            } catch (e: Exception) {
                _bookStateFlow.emit(Error(e.message.toString()))
            }
        }
    }


    private fun handleResult(result: Int, message: String, statusResult: StatusResult) {
        if (result != -1) {
            _statusLiveData.postValue(Success(message, statusResult))
        } else {
            _statusLiveData.postValue(Error("Something Went Wrong", statusResult))
        }
    }
}