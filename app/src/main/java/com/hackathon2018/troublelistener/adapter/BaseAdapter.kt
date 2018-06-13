package com.hackathon2018.troublelistener.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
class BaseAdapter(private var mItems: ArrayList<RecyclerItem>, listener : OnItemClickListener, context : Context) : RecyclerView.Adapter<BaseAdapter.ItemViewHolder>() {

    val listeners : OnItemClickListener = listener
    val contexts : Context = context
    var id: String? = null
    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        val sharedPreferences : SharedPreferences = contexts.getSharedPreferences("user", Activity.MODE_PRIVATE)
        id = sharedPreferences.getString("id", null)

        return ItemViewHolder(view)
    }

    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.title.text = mItems[position].title
        holder.writer.text = mItems[position].writer + "에게서 온"
        if(mItems[position].private) {
            holder.card.setCardBackgroundColor(ResourcesCompat.getColor(contexts.resources, R.color.color_ac, null))
            holder.title.setTextColor(ResourcesCompat.getColor(contexts.resources, R.color.color_white, null))
            holder.writer.setTextColor(ResourcesCompat.getColor(contexts.resources, R.color.color_white, null))
            if(mItems[position].writer == id)
                holder.writer.text = "내가 작성한"
            else
                holder.writer.text = "익명에게서 온"
        }

    }

    // 데이터 셋의 크기를 리턴
    override fun getItemCount(): Int {
        return mItems.size
    }

    // 커스텀 뷰홀더
    // binding widgets on item layout
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            listeners.onItemClick(layoutPosition)
        }
        val title: TextView = itemView.title
        val writer: TextView = itemView.writer
        val card: CardView = itemView.cardView
    }
}

class RecyclerItem(val idx: Int, val title: String, val writer: String, val content: String, val private: Boolean)