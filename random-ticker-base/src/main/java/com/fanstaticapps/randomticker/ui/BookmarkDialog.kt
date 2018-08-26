package com.fanstaticapps.randomticker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.mvp.BookmarksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_bookmark.*
import timber.log.Timber

class BookmarkDialog : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.dialog_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvBookmarks.layoutManager = LinearLayoutManager(activity)

        val database = TickerDatabase.getInstance(activity!!)!!
        val dao = database.tickerDataDao()
        ViewModelProviders.of(this)
                .get(BookmarksViewModel::class.java)
                .getAllBookmarks(dao)
                .observe(this, Observer { list ->
                    val addItems = listOf(Bookmark("Random Ticker"))
                    val allItems = addItems.plus(list)

                    val adapter: BookmarkAdapter
                    if (rvBookmarks.adapter == null) {
                        adapter = BookmarkAdapter(activity!!, {
                            val bookmark = allItems[it]
                            if (activity is BookmarkSelector) {
                                (activity as BookmarkSelector).onBookmarkSelected(bookmark)
                            }
                            dismiss()
                        }, { position ->
                            val bookmark = allItems[position]
                            Single.fromCallable { bookmark.let { dao.delete(it.id) } }
                                    .subscribeOn(Schedulers.computation())
                                    .subscribe({ Timber.d("Bookmark deleted") }, { t -> Timber.e(t) })
                        })
                        rvBookmarks.adapter = adapter
                    } else {
                        adapter = rvBookmarks.adapter as BookmarkAdapter
                    }
                    adapter.setData(allItems)
                })
    }

    interface BookmarkSelector {
        fun onBookmarkSelected(bookmark: Bookmark?)
    }
}

class BookmarkAdapter(private val context: Context, val select: (Int) -> Unit, val delete: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: List<Bookmark>? = null

    fun setData(list: List<Bookmark>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_add_bookmark, parent, false)
            AddViewHolder(itemView, select)
        } else {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false)
            BookmarkViewHolder(itemView, select, delete)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookmarkViewHolder) {
            val bookmark = list!![position]
            holder.render(bookmark.name)
        }
    }

    class AddViewHolder(itemView: View, select: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { select.invoke(adapterPosition) }
        }
    }

    class BookmarkViewHolder(itemView: View, select: (Int) -> Unit, delete: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private var tvTitle: TextView = itemView.findViewById(R.id.tvBookmarkName)
        private var btnDelete: View = itemView.findViewById(R.id.ivDelete)

        init {
            tvTitle.setOnClickListener { select.invoke(adapterPosition) }
            btnDelete.setOnClickListener { delete.invoke(adapterPosition) }
        }

        fun render(text: String) {
            tvTitle.text = text
        }

    }
}
