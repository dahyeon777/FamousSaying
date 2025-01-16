package com.dada.famoussaying.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dada.famoussaying.R
import com.dada.famoussaying.databinding.ActivityMainBinding
import com.dada.famoussaying.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding



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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        //ToolBar 초기화
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        //뒤로 가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*//툴바 제목 설정
        supportActionBar?.title = "명언모음"*/


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

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "default_channel_id"
                val channelName = "Default Channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                channel.description = "This is the default notification channel."

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context) {
            val channelId = "default_channel_id" // 위에서 설정한 채널 ID
            val notificationId = 1

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // 알림 아이콘
                .setContentTitle("알림앱") // 알림 제목
                .setContentText("명언~~~~~~~~~~~~~~~~~~~~~~~~~~~~") // 알림 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 알림 우선순위

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }

        createNotificationChannel(this) // 앱 시작 시 채널 생성

        binding.noticeBtn.setOnClickListener {
            showNotification(this)
        }

    }
}