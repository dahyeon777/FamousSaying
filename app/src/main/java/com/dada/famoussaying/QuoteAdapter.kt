package com.dada.famoussaying

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dada.famoussaying.data.Quote
import java.text.SimpleDateFormat
import java.util.*


class QuoteAdapter(private var data: List<Quote>) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = data[position]
        holder.contentTextView.text = quote.content

        // Long 값을 Date로 변환하고, SimpleDateFormat을 사용하여 문자열로 변환
        val date = Date(quote.date)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // 날짜 형식 지정
        holder.dateTextView.text = dateFormat.format(date)  // 형식에 맞는 날짜 문자열 설정
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newQuotes: List<Quote>) {
        data = newQuotes
        notifyDataSetChanged()
    }
}
