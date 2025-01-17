package com.dada.famoussaying.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.TypeConverter
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
    private lateinit var adapter: QuoteAdapter

    //툴바 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                finish()  // 뒤로 가기 버튼 (홈 버튼)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        //ToolBar 초기화
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        //뒤로 가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //툴바 제목 설정
        supportActionBar?.title = "명언을 선택하세요"

        // 데이터베이스 초기화
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quote_database"
        ).fallbackToDestructiveMigration()
            .build()
        quoteDAO = database.quoteDAO()

        // RecyclerView 설정
        val recyclerView = binding.quoteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 생성 및 삭제 콜백 설정
        adapter = QuoteAdapter(mutableListOf()) { quote ->
            deleteQuote(quote) // 삭제 콜백
        }
        recyclerView.adapter = adapter

        // 데이터 가져오기
        lifecycleScope.launch {
            val quotes = withContext(Dispatchers.IO) {
                quoteDAO.getAllQuotes()
            }
            updateUI(quotes)
        }

    }

    // UI 업데이트
    private fun updateUI(quotes: List<Quote>) {
        adapter.updateData(quotes)
    }

    // 데이터 삭제
    private fun deleteQuote(quote: Quote) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                quoteDAO.deleteQuote(quote) // 데이터베이스에서 삭제
            }
            val updatedQuotes = withContext(Dispatchers.IO) {
                quoteDAO.getAllQuotes() // 갱신된 데이터 가져오기
            }
            updateUI(updatedQuotes) // UI 업데이트
        }
    }
}