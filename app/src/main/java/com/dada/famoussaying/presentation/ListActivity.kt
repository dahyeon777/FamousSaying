package com.dada.famoussaying.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.withContext

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var database: AppDatabase
    private lateinit var quoteDAO: QuoteDAO
    private lateinit var adapter: QuoteAdapter  // RecyclerView 어댑터 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quote_database"
        ).fallbackToDestructiveMigration()
            .build()

        quoteDAO = database.quoteDAO()

        // RecyclerView 설정 (초기 빈 리스트)
        val recyclerView = binding.quoteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = QuoteAdapter(emptyList()) // 초기 리스트는 빈 상태
        recyclerView.adapter = adapter

        // 데이터 가져오기
        lifecycleScope.launch {
            val quotes = withContext(Dispatchers.IO) {
                quoteDAO.getAllQuotes()  // 백그라운드에서 데이터 가져오기
            }

            // 데이터가 준비된 후 UI 업데이트
            updateUI(quotes)
        }

        // 홈 이동 버튼
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(quotes: List<Quote>) {
        // RecyclerView 어댑터에 데이터 설정
        adapter.updateData(quotes)
    }
}
