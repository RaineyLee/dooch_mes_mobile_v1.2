package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityResultBinding

class W_result: AppCompatActivity() {
    // 바인딩 선언
    private lateinit var resultBinding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(resultBinding.root)

        // W_work 화면에서 값을 전달 받음
        val emp_id = intent.getStringExtra("emp_id")
        val emp_name = intent.getStringExtra("emp_name")
        val order_id = intent.getStringExtra("order_id")
        val prod_id = intent.getStringExtra("prod_id")
        val prod_name = intent.getStringExtra("prod_name")

        val working_time = intent.getStringExtra("working_time") // 생산오더 id 전달
        val total_time = intent.getStringExtra("total_time")
        val pause_time = intent.getStringExtra("pause_time")

        // intent를 통해 받은 값을 상위 항목에 입력 함.
        resultBinding.txtResultEmpId.text = emp_id
        resultBinding.txtResultEmpName.text = emp_name
        resultBinding.txtResultOrderId.text = order_id
        resultBinding.txtResultProdId.text = prod_id
        resultBinding.txtResultProdName.text = prod_name

        resultBinding.txtResultWorkingTime.text = working_time
        resultBinding.txtResultTotalTime.text = total_time
        resultBinding.txtResultPauseTime.text = pause_time

       // "W_work" 화면으로 이동 intent flag 사용(이전화면 내용 복구)
        resultBinding.btnResultReturn.setOnClickListener {
            val intent = Intent(this, W_work::class.java)
            intent.putExtra("emp_id", emp_id)
            intent.putExtra("emp_name", emp_name)
            startActivity(intent)
        }

    }
}