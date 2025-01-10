package com.dada.famoussaying

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dada.famoussaying.data.Quote
import com.dada.famoussaying.data.QuoteDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

private lateinit var quoteDAO: QuoteDAO


class QuoteAdapter(private var data: MutableList<Quote>) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val quoteDeleteButton: Button = itemView.findViewById(R.id.quoteDeleteButton)
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

        // 삭제 버튼 클릭 리스너 설정
        holder.quoteDeleteButton.setOnClickListener {
            // 해당 아이템을 삭제하고 RecyclerView 갱신
            deleteItem(quote, position)
        }
    }

    override fun getItemCount(): Int = data.size

    private fun deleteItem(quote: Quote, position: Int) {
        // 데이터베이스에서 삭제
        CoroutineScope(Dispatchers.IO).launch {
            quoteDAO.deleteQuote(quote)  // 데이터베이스에서 삭제

            // 메인 스레드에서 RecyclerView 갱신
            withContext(Dispatchers.Main) {
                // 데이터 리스트에서 삭제된 아이템 제거
                data.removeAt(position) // MutableList에서 삭제
                notifyItemRemoved(position) // 삭제된 아이템만 갱신
            }
        }
    }

    fun updateData(newQuotes: List<Quote>) {
        data = newQuotes.toMutableList() // 새 데이터를 MutableList로 변환하여 갱신
        notifyDataSetChanged()
    }
}
