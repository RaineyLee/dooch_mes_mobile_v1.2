package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflate(layoutInflater)로 초기화 한다
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(mainBinding.root)

        // 메인 화면에서 작업화면으로 전환 버튼클릭 리스너
        mainBinding.btnProcess.setOnClickListener {
            val intent = Intent(this, W_work::class.java)
            startActivity(intent)
        }

    }
}

