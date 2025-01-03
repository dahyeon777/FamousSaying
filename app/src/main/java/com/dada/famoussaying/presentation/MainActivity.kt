package com.dada.famoussaying.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import androidx.room.Room
import com.dada.famoussaying.R
import com.dada.famoussaying.data.AppDatabase
import com.dada.famoussaying.data.Quote
import com.dada.famoussaying.data.QuoteDAO
import com.dada.famoussaying.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var quoteDAO: QuoteDAO
    private val quotes = mutableListOf<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)



        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quote_database"
        ).fallbackToDestructiveMigration()  // 데이터베이스가 손상되면 새로 생성하도록 설정
            .build()

        quoteDAO = database.quoteDAO()



        //기록 창 이동
        binding.listViewBtn.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        binding.saveBtn.setOnClickListener {
            val quoteText = binding.quoteTextView.text.toString()
            val currentDate = System.currentTimeMillis() // 현재 시간 밀리초 값
            if (quoteText.isNotEmpty()) {
                val newQuote = Quote(
                    id = 0,
                    content = quoteText,
                    date = currentDate
                )

                lifecycleScope.launch {

                    val allData = quoteDAO.getAllQuotes()
                    allData.forEach { println(it) }

                    try {
                        quoteDAO.insertQuote(newQuote)
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("RoomData", allData.toString())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "오류가 발생했습니다: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "저장이 실행되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}