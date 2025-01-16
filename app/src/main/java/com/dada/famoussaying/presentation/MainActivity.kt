package com.dada.famoussaying.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> { // 설정 아이콘 클릭 시
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //ToolBar 초기화
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        //툴바 제목 설정
        supportActionBar?.title = "명언모음"




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
                "피할수 없으면 즐겨라 -로버트 엘리엇",
                "절대 어제를 후회하지 마라. 인생은 오늘의 내 안에 있고 내일은 스스로 만드는것이다. -L론허바드",
                "계단을 밟아야 계단 위에 올라설수 있다, -터키속담",
                "오랫동안 꿈을 그리는 사람은 마침내 그 꿈을 닮아 간다, -앙드레 말로",
                "좋은 성과를 얻으려면 한 걸음 한 걸음이 힘차고 충실하지 않으면 안 된다, -단테",
                "행복은 습관이다,그것을 몸에 지니라 -허버드",
                "성공의 비결은 단 한 가지, 잘할 수 있는 일에 광적으로 집중하는 것이다. -톰 모나건",
                "자신감 있는 표정을 지으면 자신감이 생긴다 -찰스다윈",
                "평생 살 것처럼 꿈을 꾸어라.그리고 내일 죽을 것처럼 오늘을 살아라. – 제임스 딘",
                "네 믿음은 네 생각이 된다 . 네 생각은 네 말이 된다. 네말은 네 행동이 된다 네행동은 네 습관이된다 . 네 습관은 네 가치가 된다 . 네 가치는 네 운명이 된다 – 간디",
                "일하는 시간과 노는 시간을 뚜렷이 구분하라 . 시간의 중요성을 이해하고 매순간을 즐겁게 보내고 유용하게 활용하라. 그러면 젋은 날은 유쾌함으로 가득찰것이고 늙어서도 후회할 일이 적어질것이며 비록 가난할 때라도 인생을 아름답게 살아갈수있다 – 루이사 메이올콧",
                "절대 포기하지 말라. 당신이 되고 싶은 무언가가 있다면, 그에 대해 자부심을 가져라. 당신 자신에게 기회를 주어라. 스스로가 형편없다고 생각하지 말라. 그래봐야 아무 것도 얻을 것이 없다. 목표를 높이 세워라.인생은 그렇게 살아야 한다. – 마이크 맥라렌",
                "1퍼센트의 가능성, 그것이 나의 길이다. -나폴레옹",
                "그대 자신의 영혼을 탐구하라.다른 누구에게도 의지하지 말고 오직 그대 혼자의 힘으로 하라. 그대의 여정에 다른 이들이 끼어들지 못하게 하라. 이 길은 그대만의 길이요, 그대 혼자 가야할 길임을 명심하라. 비록 다른 이들과 함께 걸을 수는 있으나 다른 그 어느 누구도 그대가 선택한 길을 대신 가줄 수 없음을 알라. -인디언 속담",
                "고통이 남기고 간 뒤를 보라! 고난이 지나면 반드시 기쁨이 스며든다. -괴테",
                "꿈을 계속 간직하고 있으면 반드시 실현할 때가 온다. -괴테",
                "화려한 일을 추구하지 말라. 중요한 것은 스스로의 재능이며, 자신의 행동에 쏟아 붓는 사랑의 정도이다. -머더 테레사",
                "마음만을 가지고 있어서는 안된다. 반드시 실천하여야 한다. -이소룡",
                "흔히 사람들은 기회를 기다리고 있지만 기회는 기다리는 사람에게 잡히지 않는 법이다. 우리는 기회를 기다리는 사람이 되기 전에 기회를 얻을 수 있는 실력을 갖춰야 한다. 일에 더 열중하는 사람이 되어야한다. -안창호",
                "나이가 60이다 70이다 하는 것으로 그 사람이 늙었다 젊었다 할 수 없다. 늙고 젊은 것은 그 사람의 신념이 늙었느냐 젊었느냐 하는데 있다. -맥아더",
                "만약 우리가 할 수 있는 일을 모두 한다면 우리들은 우리자신에 깜짝 놀랄 것이다. -에디슨"
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