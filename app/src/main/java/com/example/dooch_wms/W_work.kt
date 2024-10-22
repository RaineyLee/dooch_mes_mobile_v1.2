package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityMainBinding
import com.example.dooch_wms.databinding.ActivityWorkBinding

class W_work : AppCompatActivity(){
    private lateinit var  workBinding: ActivityWorkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflate(layoutInflater)로 초기화 한다
        workBinding = ActivityWorkBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(workBinding.root)

        // "사원번호", "사원명" 조회버튼 클릭시 W_employee 화면으로 이동
        // 전환시 "사원번호", "사원명"을 같이 전달
        workBinding.btnWorkSearch1.setOnClickListener {
            val intent = Intent(this, W_employee::class.java)
            intent.putExtra("key_1", "99999")
            intent.putExtra("key_2", "hello")
            startActivity(intent)
        }

    }

}