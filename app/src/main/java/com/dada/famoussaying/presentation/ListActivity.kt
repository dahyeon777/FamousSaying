package com.dada.famoussaying.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.dada.famoussaying.QuoteAdapter
import com.dada.famoussaying.R
import com.dada.famoussaying.data.AppDatabase
import com.dada.famoussaying.databinding.ActivityListBinding
import com.dada.famoussaying.data.Quote
import com.dada.famoussaying.data.QuoteDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var database: AppDatabase
    private lateinit var quoteDAO: QuoteDAO
    private val quotes = mutableListOf<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quote_database"
        ).fallbackToDestructiveMigration()  // 데이터베이스가 손상되면 새로 생성하도록 설정
            .build()

        quoteDAO = database.quoteDAO()

        // RecyclerView 설정
        val recyclerView = binding.quoteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = QuoteAdapter(quotes)
        recyclerView.adapter = adapter

        // 홈 이동 버튼
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /*// quoteText를 받아와 newQuote로 변환
        val quoteText = intent.getStringExtra("QUOTE_TEXT")
        var newQuote: Quote? = null

        if (quoteText != null) {
            newQuote = Quote(
                id = 0,  // Room이 자동 생성할 수 있도록 0 또는 null 값 설정
                content = quoteText,
                date = "2024-12-17"  // 기본값 설정
            )

            // 비동기 작업으로 데이터베이스에 저장 및 RecyclerView 갱신
            lifecycleScope.launch {
                // 데이터베이스에 삽입
                newQuote?.let { quoteDAO.insertQuote(it) }

                // 전체 데이터를 불러와 RecyclerView 갱신
                val updatedQuotes = quoteDAO.getAllQuotes()
                quotes.clear()
                quotes.addAll(updatedQuotes.reversed()) // 최신 항목이 위로 오도록 역순 정렬

                // UI 작업은 메인 스레드에서 실행됨
                adapter.notifyDataSetChanged()
            }
        }*/

        val updatedQuotes = quoteDAO.getAllQuotes()
        quotes.clear()
        quotes.addAll(updatedQuotes.reversed()) // 최신 항목이 위로 오도록 역순 정렬

        // UI 작업은 메인 스레드에서 실행됨
        adapter.notifyDataSetChanged()
    }
}
