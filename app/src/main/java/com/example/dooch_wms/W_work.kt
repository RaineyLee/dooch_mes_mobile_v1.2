package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

            val emp_id = workBinding.txtWorkEmpId.text.toString()
            val emp_name = workBinding.txtWorkEmpName.text.toString()

            val intent = Intent(this, W_employee::class.java)
            intent.putExtra("emp_id", emp_id)
            intent.putExtra("emp_name", emp_name)
            startActivityForResult(intent, 99)
        }

        // "생산오더 번호" 입력후 조회버튼 클릭시 W_production 화면으로 이동
        // 전환시 "생산오더 번호" 같이 전달
        workBinding.btnWorkSearch2.setOnClickListener {

            val order_id = workBinding.txtWorkOrderId.text.toString()

            val intent = Intent(this, W_production::class.java)
            intent.putExtra("order_id", order_id)
            startActivityForResult(intent, 88)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // resultCode와 requestCode를 각각 확인
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                99 -> {
                    val return_1 = data?.getStringExtra("return_1")
                    val return_2 = data?.getStringExtra("return_2")

                    workBinding.txtWorkEmpId.setText(return_1)
                    workBinding.txtWorkEmpName.setText(return_2)
                }
            }

            when (requestCode) {
                88 -> {
                    val return_1 = data?.getStringExtra("return_1")
                    val return_2 = data?.getStringExtra("return_2")
                    val return_3 = data?.getStringExtra("return_3")

                    workBinding.txtWorkProdId.setText(return_1)
                    workBinding.txtWorkProdName.setText(return_2)
                    workBinding.txtWorkProdQty.setText(return_3)
                }
            }
        }
    }



}