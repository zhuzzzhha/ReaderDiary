package com.example.readerdiary.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.readerdiary.databinding.ViewBookGridLayoutBinding
import com.example.readerdiary.databinding.ViewBookListLayoutBinding
import com.example.readerdiary.models.Book
import java.text.SimpleDateFormat
import java.util.Locale

class BookRVVBListAdapter (
    private val isList: MutableLiveData<Boolean>,
    private val deleteUpdateCallback: (type: String, position: Int, book: Book) -> Unit,
) :
    ListAdapter<Book,RecyclerView.ViewHolder>(DiffCallback()) {


    class ListBookViewHolder(private val viewBookListLayoutBinding: ViewBookListLayoutBinding) :
        RecyclerView.ViewHolder(viewBookListLayoutBinding.root) {

        fun bind(
            book: Book,
            deleteUpdateCallback: (type: String, position: Int, book: Book) -> Unit,
        ) {
            viewBookListLayoutBinding.titleTxt.text = book.title
            viewBookListLayoutBinding.descrTxt.text = book.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

            viewBookListLayoutBinding.dateTxt.text = dateFormat.format(book.date)

            viewBookListLayoutBinding.deleteImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("delete", adapterPosition, book)
                }
            }
            viewBookListLayoutBinding.editImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("update", adapterPosition, book)
                }
            }
        }
    }


    class GridBookViewHolder(private val viewBookGridLayoutBinding: ViewBookGridLayoutBinding) :
        RecyclerView.ViewHolder(viewBookGridLayoutBinding.root) {

        fun bind(
            book: Book,
            deleteUpdateCallback: (type: String, position: Int, book: Book) -> Unit,
        ) {
            viewBookGridLayoutBinding.titleTxt.text = book.title
            viewBookGridLayoutBinding.descrTxt.text = book.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

            viewBookGridLayoutBinding.dateTxt.text = dateFormat.format(book.date)

            viewBookGridLayoutBinding.deleteImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("delete", adapterPosition, book)
                }
            }
            viewBookGridLayoutBinding.editImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("update", adapterPosition, book)
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == 1) {  // Grid_Item
            GridBookViewHolder(
                ViewBookGridLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {  // List_Item
            ListBookViewHolder(
                ViewBookListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val book = getItem(position)

        if (isList.value!!) {
            (holder as ListBookViewHolder).bind(book, deleteUpdateCallback)
        } else {
            (holder as GridBookViewHolder).bind(book, deleteUpdateCallback)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (isList.value!!) {
            0 // List_Item
        } else {
            1 // Grid_Item
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }

    }

}