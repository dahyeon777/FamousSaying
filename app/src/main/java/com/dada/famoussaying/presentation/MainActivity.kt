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


        // 버튼 클릭 리스너 설정
        binding.randomBtn.setOnClickListener {
            // 랜덤 문장 목록
            val sentences = listOf(
                "삶이 있는 한 희망은 있다 -키케로",
                "산다는것 그것은 치열한 전투이다. -로망로랑",
                "하루에 3시간을 걸으면 7년 후에 지구를 한바퀴 돌 수 있다. -사무엘존슨",
                "언제나 현재에 집중할수 있다면 행복할것이다. -파울로 코엘료",
                "진정으로 웃으려면 고통을 참아야하며 , 나아가 고통을 즐길 줄 알아야 해 -찰리 채플린",
                "직업에서 행복을 찾아라. 아니면 행복이 무엇인지 절대 모를 것이다 -엘버트 허버드",
                "신은 용기있는자를 결코 버리지 않는다 -켄러",
                "행복의 문이 하나 닫히면 다른 문이 열린다 그러나 우리는 종종 닫힌 문을 멍하니 바라보다가 우리를 향해 열린 문을 보지 못하게 된다 – 헬렌켈러",
                "피할수 없으면 즐겨라 – 로버트 엘리엇",
                "단순하게 살아라. 현대인은 쓸데없는 절차와 일 때문에 얼마나 복잡한 삶을 살아가는가?-이드리스 샤흐",
                "먼저 자신을 비웃어라. 다른 사람이 당신을 비웃기 전에 – 엘사 맥스웰",
                "먼저핀꽃은 먼저진다 남보다 먼저 공을 세우려고 조급히 서둘것이 아니다 – 채근담",
                "행복한 삶을 살기위해 필요한 것은 거의 없다. -마르쿠스 아우렐리우스 안토니우스",
                "절대 어제를 후회하지 마라 . 인생은 오늘의 나 안에 있고 내일은 스스로 만드는 것이다 L.론허바드",
                "어리석은 자는 멀리서 행복을 찾고, 현명한 자는 자신의 발치에서 행복을 키워간다 -제임스 오펜하임",
                "너무 소심하고 까다롭게 자신의 행동을 고민하지 말라 . 모든 인생은 실험이다 . 더많이 실험할수록 더나아진다 – 랄프 왈도 에머슨",
                "한번의 실패와 영원한 실패를 혼동하지 마라 -F.스콧 핏제랄드",
                "피할수 없으면 즐겨라 -로버트 엘리엇"
            )

            // 랜덤으로 하나의 문장 선택
            val randomSentence = sentences.random()

            // EditText에 랜덤 문장 설정
            binding.quoteTextView.setText(randomSentence)
        }




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