package com.fanstaticapps.randomticker.ui.bookmarks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import kotlin.properties.Delegates

class BookmarkAdapter(private val context: Context,
                      private val select: (Bookmark) -> Unit,
                      private val delete: (Bookmark) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: List<Bookmark> = listOf()
        private set

    fun setData(list: List<Bookmark>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_add_bookmark, parent, false)
            BookmarkSelectableViewHolder(itemView, select)
        } else {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false)
            BookmarkViewHolder(itemView, select, delete)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookmarkSelectableViewHolder) {
            val bookmark = list[position]
            holder.render(bookmark)
        }
    }

    private class BookmarkViewHolder(itemView: View, select: (Bookmark) -> Unit, delete: (Bookmark) -> Unit) : BookmarkSelectableViewHolder(itemView, select) {
        private var tvBookmarkName: TextView = itemView.findViewById(R.id.tvBookmarkName)
        private var btnDelete: View = itemView.findViewById(R.id.btnDelete)


        init {
            btnDelete.setOnClickListener { delete(bookmark) }
        }

        override fun render(bookmark: Bookmark) {
            tvBookmarkName.text = bookmark.name
        }
    }

    private open class BookmarkSelectableViewHolder(itemView: View, select: (Bookmark) -> Unit) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener { select(bookmark) }
        }

        protected var bookmark: Bookmark by Delegates.notNull()

        open fun render(bookmark: Bookmark) {
            this.bookmark = bookmark
        }
    }
}