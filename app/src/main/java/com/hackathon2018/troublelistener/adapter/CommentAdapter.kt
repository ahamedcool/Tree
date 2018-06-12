package com.hackathon2018.troublelistener.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hackathon2018.troublelistener.util.OnItemClickListener
import com.hackathon2018.troublelistener.R
import kotlinx.android.synthetic.main.item_app.view.*
import org.jetbrains.anko.textColor
import java.util.ArrayList

/**
* Created by hyunjin on 2018. 5. 11..
*/
class CommentAdapter(private var mItems: ArrayList<CommentItem>) : RecyclerView.Adapter<CommentAdapter.ItemViewHolder>() {


    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

        return ItemViewHolder(view)
    }

    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.title.text = mItems[position].title
        holder.writer.text = mItems[position].writer

    }

    // 데이터 셋의 크기를 리턴
    override fun getItemCount(): Int {
        return mItems.size
    }

    // 커스텀 뷰홀더
    // binding widgets on item layout
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val title: TextView = itemView.title
        val writer: TextView = itemView.writer
    }
}

class CommentItem(val title: String, val writer: String)