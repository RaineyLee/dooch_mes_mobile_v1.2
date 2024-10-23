package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.activity.ComponentActivity
import com.example.dooch_wms.databinding.ActivityPauseBinding
import com.example.dooch_wms.databinding.ActivityProductionBinding

class W_pause: ComponentActivity() {
    // 바인딩 선언
    private lateinit var pauseBinding: ActivityPauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // W_work 에서 정보를 전달 받는 변수 --> 추후 "중지사유"를 db에 입력하기 위해 사용
        val emp_id = intent.getStringExtra("emp_id") // 사번
        val emp_name = intent.getStringExtra("emp_name") // 사용자 이름
        val order_id = intent.getStringExtra("order_id") // 생산오더 번호
        val prod_id = intent.getStringExtra("prod_id") // 제품 아이디
        val prod_name = intent.getStringExtra("prod_name") // 제품명
        val prod_qty = intent.getStringExtra("prod_qty") // 제품 수량

        pauseBinding.rgPause1.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_pause_1 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause1.text.toString())

                R.id.rb_pause_2 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause2.text.toString())

                R.id.rb_pause_3 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause3.text.toString())

                R.id.rb_pause_4 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause4.text.toString())

                R.id.rb_pause_5 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause5.text.toString())
            }
        }

        // "W_work" 화면으로 이동
        pauseBinding.btnPauseConfirm.setOnClickListener {
            val intent = Intent(this, W_work::class.java)
            startActivity(intent)
        }

    }
}