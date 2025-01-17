package com.dada.famoussaying

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.dada.famoussaying.data.AppDatabase
import com.dada.famoussaying.data.Quote
import com.dada.famoussaying.data.QuoteDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

private lateinit var quoteDAO: QuoteDAO
private lateinit var database: AppDatabase


class QuoteAdapter(
    private var data: MutableList<Quote>,
    private val onDeleteClick: (Quote) -> Unit/*, // 삭제 콜백 추가
    private val onQuoteSelected: (Quote) -> Unit // 선택된 명언 전달 콜백 추가*/
) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val quoteDeleteButton: Button = itemView.findViewById(R.id.quoteDeleteButton)
        val quoteSelectButton: Button = itemView.findViewById(R.id.quoteSelectButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = data[position]
        holder.contentTextView.text = quote.content

        val date = Date(quote.date)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.dateTextView.text = dateFormat.format(date)

        // 삭제 버튼 클릭 리스너 설정
        holder.quoteDeleteButton.setOnClickListener {
            onDeleteClick(quote) // 콜백 호출
        }

        /*// 선택 버튼 클릭 리스너 설정
        holder.quoteSelectButton.setOnClickListener {
            onQuoteSelected(quote) // 선택된 명언 전달 콜백 호출
        }*/
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newQuotes: List<Quote>) {
        data = newQuotes.toMutableList()
        notifyDataSetChanged()
    }
}
