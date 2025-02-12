package com.dada.famoussaying.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener

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

        //ToolBar초기화하기
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        //뒤로 가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //툴바 제목 설정
        supportActionBar?.title = "상단알림 명언"
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setBackgroundColor(Color.parseColor("#00FF0000"))

        // 데이터베이스 초기화
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quote_database"
        ).fallbackToDestructiveMigration()
            .build()
        quoteDAO = database.quoteDAO()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        //상단바 알림설정
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "default_channel_id"
                val channelName = "Default Channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                channel.description = "This is the default notification channel."

                // 앱 배지 표시 비활성화
                channel.setShowBadge(false)

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        createNotificationChannel(this) // 앱 시작 시 채널 생성

        // RecyclerView 설정
        val recyclerView = binding.quoteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = QuoteAdapter(
            mutableListOf(),
            onDeleteClick = { quote -> showDeleteConfirmationDialog(quote) },  // 삭제 버튼 클릭 시 실행
            onSelectClick = { quote ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            101
                        )
                    }
                }

                // 다이얼로그 생성
                val dialog = AlertDialog.Builder(this)
                    .setMessage("해당 명언으로 알림이 띄워집니다.")
                    .setPositiveButton("예") { _, _ ->
                        // "예" 버튼 클릭 시 알림 호출
                        showNotification(quote.content)
                    }
                    .setNegativeButton("아니오", null) // "아니오" 버튼 클릭 시 아무 동작하지 않음
                    .create()
                dialog.show()  // 다이얼로그 표시
            },  // 선택 버튼 클릭 시 실행
            // 기존 코드 내 onQuoteClick 수정 부분
            onQuoteClick = { quote ->
                // 수정할 내용을 입력할 다이얼로그 생성
                val editText = EditText(this).apply {
                    setText(quote.content) // 기존 명언 내용을 입력창에 표시
                }

                // 수정할 내용 변수
                var newContent = quote.content

                val dialog = AlertDialog.Builder(this)
                    .setView(editText)  // EditText를 다이얼로그에 추가
                    .setPositiveButton("수정하기") { _, _ ->
                        newContent = editText.text.toString()
                        val updatedQuote = quote.copy(content = newContent)
                        updateQuoteInDatabase(updatedQuote)  // 데이터베이스 업데이트
                    }
                    .setNegativeButton("취소", null)
                    .create()

                dialog.show()
            }
        )

        recyclerView.adapter = adapter

        // 데이터 가져오기
        lifecycleScope.launch {
            val quotes = withContext(Dispatchers.IO) {
                quoteDAO.getAllQuotes()
            }
            updateUI(quotes)

            if (quotes.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
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



    // 데이터 삭제 전에 다이얼로그 띄우기
    private fun showDeleteConfirmationDialog(quote: Quote) {
        AlertDialog.Builder(this)
            .setMessage("명언을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                deleteQuote(quote)
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun showNotification(quoteContent: String) {
        val channelId = "default_channel_id"  // 채널 ID
        val notificationId = 1  // 알림 ID

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.alarm_icon) // 알림 아이콘
            .setContentText(quoteContent) // 명언을 알림 내용으로 설정
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 알림 우선순위
            .setOngoing(true) // 지우기 눌러도 안 지워지게 설정 (영구 알림)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quoteContent)) // 긴 텍스트를 여러 줄로 표시

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // 데이터베이스에서 명언을 업데이트하는 함수
    private fun updateQuoteInDatabase(updatedQuote: Quote) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                quoteDAO.updateQuote(updatedQuote) // 데이터베이스에서 업데이트
            }
            // 업데이트 후 UI를 갱신
            val updatedQuotes = withContext(Dispatchers.IO) {
                quoteDAO.getAllQuotes()
            }
            updateUI(updatedQuotes)
        }
    }
}