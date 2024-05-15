package com.example.readerdiary

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.readerdiary.R.id.edBookDesc
import com.example.readerdiary.adapters.BookRVVBListAdapter
import com.example.readerdiary.databinding.ActivityMainBinding

import com.example.readerdiary.models.Book
import com.example.readerdiary.utils.Status
import com.example.readerdiary.utils.StatusResult
import com.example.readerdiary.utils.clearEditText
import com.example.readerdiary.utils.hideKeyBoard
import com.example.readerdiary.utils.longToastShow
import com.example.readerdiary.utils.setupDialog
import com.example.readerdiary.utils.validateEditText
import com.example.readerdiary.viewmodel.BookViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val addBookDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.add_book_dialog)
        }
    }

    private val updateBookDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.update_book_dialog)
        }
    }

    private val loadingDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.loading_dialog)
        }
    }

    private val bookViewModel : BookViewModel by lazy {
        ViewModelProvider(this)[BookViewModel::class.java]
    }

    private val isListMutableLiveData = MutableLiveData<Boolean>().apply {
        postValue(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        // Add task start
        val addCloseImg = addBookDialog.findViewById<ImageView>(R.id.closeImg)
        addCloseImg.setOnClickListener { addBookDialog.dismiss() }

        val addETTitle = addBookDialog.findViewById<TextInputEditText>(R.id.edBookTitle)
        val addETTitleL = addBookDialog.findViewById<TextInputLayout>(R.id.edBookTitleL)

        addETTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETTitle, addETTitleL)
            }

        })

        val addETDesc = addBookDialog.findViewById<TextInputEditText>(edBookDesc)
        val addETDescL = addBookDialog.findViewById<TextInputLayout>(R.id.edBookDescL)

        addETDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETDesc, addETDescL)
            }
        })

        mainBinding.addBookFABtn.setOnClickListener {
            clearEditText(addETTitle, addETTitleL)
            clearEditText(addETDesc, addETDescL)
            addBookDialog.show()
        }
        val saveBookBtn = addBookDialog.findViewById<Button>(R.id.saveBookBtn)
        saveBookBtn.setOnClickListener {
            if (validateEditText(addETTitle, addETTitleL)
                && validateEditText(addETDesc, addETDescL)
            ) {

                val newBook = Book(
                    UUID.randomUUID().toString(),
                    addETTitle.text.toString().trim(),
                    addETDesc.text.toString().trim(),
                    Date()
                )
                hideKeyBoard(it)
                addBookDialog.dismiss()
                bookViewModel.insertBook(newBook)
            }
        }

        // Add task end


        // Update Task Start
        val updateETTitle = updateBookDialog.findViewById<TextInputEditText>(R.id.edBookTitle)
        val updateETTitleL = updateBookDialog.findViewById<TextInputLayout>(R.id.edBookTitleL)

        updateETTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updateETTitle, updateETTitleL)
            }

        })

        val updateETDesc = updateBookDialog.findViewById<TextInputEditText>(edBookDesc)
        val updateETDescL = updateBookDialog.findViewById<TextInputLayout>(R.id.edBookDescL)

        updateETDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updateETDesc, updateETDescL)
            }
        })

        val updateCloseImg = updateBookDialog.findViewById<ImageView>(R.id.closeImg)
        updateCloseImg.setOnClickListener { updateBookDialog.dismiss() }

        val updateTaskBtn = updateBookDialog.findViewById<Button>(R.id.updateBookBtn)
        // Update Task End

        isListMutableLiveData.observe(this){
            if (it){
                mainBinding.bookRV.layoutManager = LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL,false
                )
                mainBinding.listOrGridImg.setImageResource(R.drawable.ic_view_module)
            }else{
                mainBinding.bookRV.layoutManager = StaggeredGridLayoutManager(
                    2, LinearLayoutManager.VERTICAL
                )
                mainBinding.listOrGridImg.setImageResource(R.drawable.ic_view_list)
            }
        }

        mainBinding.listOrGridImg.setOnClickListener {
            isListMutableLiveData.postValue(!isListMutableLiveData.value!!)
        }

        val bookRVVBListAdapter =
            BookRVVBListAdapter(isListMutableLiveData) { type, position, book ->
                if (type == "delete") {
                    bookViewModel
                        // Deleted Task
                    //    .deleteBook(book)
                        .deleteBookUsingId(book.id)

                    // Restore Deleted task
                    restoreDeletedBook(book)
                } else if (type == "update") {
                    updateETTitle.setText(book.title)
                    updateETDesc.setText(book.description)
                    updateTaskBtn.setOnClickListener {
                        if (validateEditText(updateETTitle, updateETTitleL)
                            && validateEditText(updateETDesc, updateETDescL)
                        ) {
                            val updateBook = Book(
                                book.id,
                                updateETTitle.text.toString().trim(),
                                updateETDesc.text.toString().trim(),
//                           here i Date updated
                                Date()
                            )
                            hideKeyBoard(it)
                            updateBookDialog.dismiss()
                            bookViewModel
                                .updateBook(updateBook)
                           //   .updateBookParticularField(
                           //     book.id,
                           //     updateETTitle.text.toString().trim(),
                           //     updateETDesc.text.toString().trim()
                           // )
                        }
                    }
                    updateBookDialog.show()
                }
            }
        mainBinding.bookRV.adapter = bookRVVBListAdapter
        ViewCompat.setNestedScrollingEnabled(mainBinding.bookRV, false)
        bookRVVBListAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                mainBinding.bookRV.smoothScrollToPosition(positionStart)
                mainBinding.nestedScrollView.smoothScrollTo(0, positionStart)
            }
        })
        callGetBookList(bookRVVBListAdapter)
        callSortByLiveData()
        statusCallback()

        callSearch()
    }
        private fun restoreDeletedBook(deletedBook : Book){
            val snackBar = Snackbar.make(
                mainBinding.root, "Deleted '${deletedBook.title}'",
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction("Undo"){
                bookViewModel.insertBook(deletedBook)
            }
            snackBar.show()
        }

        private fun callSearch() {
            mainBinding.edSearch.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(query: Editable) {
                    if (query.toString().isNotEmpty()){
                        bookViewModel.searchBookList(query.toString())
                    }else{
                        callSortByLiveData()
                    }
                }
            })

            mainBinding.edSearch.setOnEditorActionListener{ v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                   // hideKeyBoard(v)
                    return@setOnEditorActionListener true
                }
                false
            }

            callSortByDialog()
        }
        private fun callSortByLiveData(){
            bookViewModel.sortByLiveData.observe(this){
                bookViewModel.getBookList(it.second,it.first)
            }
        }

        private fun callSortByDialog() {
            var checkedItem = 0   // 2 is default item set
            val items = arrayOf("Title Ascending", "Title Descending","Date Ascending","Date Descending")

            mainBinding.sortImg.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Sort By")
                    .setPositiveButton("Ok") { _, _ ->
                        when (checkedItem) {
                            0 -> {
                                bookViewModel.setSortBy(Pair("title",true))
                            }
                            1 -> {
                                bookViewModel.setSortBy(Pair("title",false))
                            }
                            2 -> {
                                bookViewModel.setSortBy(Pair("date",true))
                            }
                            else -> {
                                bookViewModel.setSortBy(Pair("date",false))
                            }
                        }
                    }
                    .setSingleChoiceItems(items, checkedItem) { _, selectedItemIndex ->
                        checkedItem = selectedItemIndex
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        private fun statusCallback() {
            bookViewModel
                .statusLiveData
                .observe(this) {
                    when (it.status) {
                        Status.LOADING -> {
                            loadingDialog.show()
                        }

                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            when (it.data as StatusResult) {
                                StatusResult.Added -> {
                                    Log.d("StatusResult", "Added")
                                }

                                StatusResult.Deleted -> {
                                    Log.d("StatusResult", "Deleted")

                                }

                                StatusResult.Updated -> {
                                    Log.d("StatusResult", "Updated")

                                }
                            }
                            it.message?.let { it1 -> longToastShow(it1) }
                        }

                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            it.message?.let { it1 -> longToastShow(it1) }
                        }
                    }
                }
        }

        private fun callGetBookList(bookRecyclerViewAdapter: BookRVVBListAdapter) {

            CoroutineScope(Dispatchers.Main).launch {
                bookViewModel
                    .bookStateFlow
                    .collectLatest {
                        Log.d("status", it.status.toString())

                        when (it.status) {
                            Status.LOADING -> {
                                loadingDialog.show()
                            }

                            Status.SUCCESS -> {
                                loadingDialog.dismiss()
                                it.data?.collect { bookList ->
                                    bookRecyclerViewAdapter.submitList(bookList)
                                }
                            }

                            Status.ERROR -> {
                                loadingDialog.dismiss()
                                it.message?.let { it1 -> longToastShow(it1) }
                            }
                        }

                    }
            }


        }

    }

