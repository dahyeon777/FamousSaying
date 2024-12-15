package com.dada.famoussaying

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dada.famoussaying.databinding.ActivityListBinding
import com.dada.famoussaying.db.Quote


class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        // RecyclerView 설정
        val recyclerView = binding.quoteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 데이터 생성
        val quotes = listOf(
            Quote(0, "산다는것 그것은 치열한 전투이다.", "로망로랑","2024-12-15")
        )

        // Adapter 설정
        recyclerView.adapter = QuoteAdapter(quotes)

        // 홈 이동 버튼 클릭 리스너
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
