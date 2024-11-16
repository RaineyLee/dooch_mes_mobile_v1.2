package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityPauseBinding

class W_pause: AppCompatActivity() {
    // 바인딩 선언
    private lateinit var pauseBinding: ActivityPauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        pauseBinding = ActivityPauseBinding.inflate(layoutInflater)
        setContentView(pauseBinding.root)

//        // W_work 에서 정보를 전달 받는 변수 --> 추후 "중지사유"를 db에 입력하기 위해 사용
//        val emp_id = intent.getStringExtra("emp_id") // 사번
//        val emp_name = intent.getStringExtra("emp_name") // 사용자 이름
//        val order_id = intent.getStringExtra("order_id") // 생산오더 번호
//        val prod_id = intent.getStringExtra("prod_id") // 제품 아이디
//        val prod_name = intent.getStringExtra("prod_name") // 제품명
//        val prod_qty = intent.getStringExtra("prod_qty") // 제품 수량

        // 20241106 화면 변경으로 현재 사용하지 않음.
        // 인텐트에서 current_time 값을 가져온다
//        val cur_time = intent.getIntExtra("current_time", 0) // 기본값 0
//        val batch_id = intent.getIntExtra("batch_id", 0) // 기본값 0
//        val start_time = intent.getStringExtra("start_time") // 기본값 0

        // W_work 화면에서 값을 전달 받음
        val order_id = intent.getStringExtra("order_id")
        val prod_id = intent.getStringExtra("prod_id")
        val prod_name = intent.getStringExtra("prod_name")
        val batch_id = intent.getStringExtra("batch_id")
        Log.d("배치값", batch_id.toString())


        // intent를 통해 받은 값을 상위 항목에 입력 함.
        pauseBinding.txtPauseOrderId.text = order_id
        pauseBinding.txtPauseProdId.text = prod_id
        pauseBinding.txtPauseProdName.text = prod_name
        pauseBinding.txtPauseBatchId.text = batch_id

        // 20241106 화면구성 변경으로 사용하지 않음.
        // TextView에 현재 시간을 표시
//        val min = cur_time / 60
//        val sec = cur_time % 60
//        pauseBinding.txtPauseCurTime.text = String.format("%02d:%02d", min, sec)
//        pauseBinding.txtPauseBatchId.text = batch_id.toString()
//        pauseBinding.txtPauseStartTime.text = start_time.toString()

        pauseBinding.rgPause1.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_pause_1 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause1.text.toString())

                R.id.rb_pause_2 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause2.text.toString())

                R.id.rb_pause_3 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause3.text.toString())

                R.id.rb_pause_4 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause4.text.toString())

                R.id.rb_pause_5 -> pauseBinding.txtPauseSelectItem.setText(pauseBinding.rbPause5.text.toString())
            }
        }

//        // "W_work" 화면으로 이동
//        pauseBinding.btnPauseConfirm.setOnClickListener {
//            val intent = Intent(this, W_work::class.java)
//            intent.putExtra("batch_id", batch_id)
//            intent.putExtra("start_time", start_time)
//            intent.putExtra("stop_s_time", System.currentTimeMillis().toString())
//            startActivity(intent)
//        }

                // "W_work" 화면으로 이동 intent flag 사용(이전화면 내용 복구)
        pauseBinding.btnPauseConfirm.setOnClickListener {
            val pause_reason: String? = pauseBinding.txtPauseSelectItem.text.toString()

            val intent = Intent(this, W_work::class.java)
            intent.putExtra("pause_reason", pause_reason)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

    }
}