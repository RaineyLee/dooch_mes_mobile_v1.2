package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityPauseBinding
import com.example.dooch_wms.databinding.CustomToastBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

        // 라디오 버튼 텍스트를 동적으로 설정
        pauseBinding.rbPause1.text = "[자재] 자재투입 지연"
        pauseBinding.rbPause2.text = "[품질] 자재품질 불량"
        pauseBinding.rbPause3.text = "[중지] 외출/퇴근"
        pauseBinding.rbPause4.text = "[개인] 개인 용무"
        pauseBinding.rbPause5.text = "[기타] 기타"

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

            // 중지 사유가 입력되지 않으면 확인 버튼을 클릭 안 되게
            if (pause_reason.isNullOrEmpty()){
                showCustomToast("알림", "중지 사유를 선택하여 주십시요.")
                return@setOnClickListener
            }

            val order_id = pauseBinding.txtPauseOrderId.text.toString()
            val item_id = pauseBinding.txtPauseProdId.text.toString()
            val item_name = pauseBinding.txtPauseProdName.text.toString()
            val batch_id = pauseBinding.txtPauseBatchId.text.toString()
            val stop_reason = pauseBinding.txtPauseSelectItem.text.toString() //pause_reason 동일




            inputStopReason(order_id, item_id, item_name, batch_id, stop_reason)

            val intent = Intent(this, W_work::class.java)
            intent.putExtra("pause_reason", pause_reason)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private fun showCustomToast(title: String, message: String) {
        // 커스텀 레이아웃을 ViewBinding으로 가져오기
        val toastBinding = CustomToastBinding.inflate(layoutInflater)

        // TextView에 메시지 설정
        toastBinding.toastTitle.text = title
        toastBinding.toastMessage.text = message

        // Toast 생성 및 설정
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastBinding.root

        // Toast 위치를 화면 중앙으로 설정
        toast.setGravity(Gravity.CENTER, 0, 0)

        toast.show()
    }

    private fun inputStopReason(order_id: String, item_id: String, item_name: String, batch_id: String, stop_reason: String){

        Log.d("에러", order_id+item_id+item_name+batch_id +stop_reason)

        // http 통신을 위한 retrofit 설정
        // retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.197:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val pauseservice: S_pause = retrofit.create(S_pause::class.java)

        pauseservice.inputstopreason(order_id, item_id, item_name, batch_id, stop_reason).enqueue(object : Callback<D_msg> { //받는 값이 List 형식이면 Callback<List<D_production>>
            override fun onFailure(call: Call<D_msg>, t: Throwable) {
//                val dialog = AlertDialog.Builder(this@W_pause)
//                dialog.setTitle("에러")
//                dialog.setMessage(t.message ?: "네트워크 오류가 발생 했습니다.")
//                dialog.show()
                Log.d("에러", t.message.toString())
            }
            override fun onResponse(call: Call<D_msg>, response: Response<D_msg>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
                val prodList = response.body()!!
                if (response.isSuccessful) {
                    // body로 받은 값을 할당 하는 코드
                    showCustomToast("알림", "중지 사유가 입력 되었습니다.")
                } else {
                    // 오류 처리 코드
                    // 오류 처리: errorBody() 파싱하여 서버에서 전달한 메시지 출력
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorJson).getString("message")
                        Log.d("전달값_try", "Response body: ${response.body()}")
                    } catch (e: Exception) {
                        "유효한 생산오더가 아닙니다. 생산오더를 다시 확인해 주세요."
                        Log.d("전달값_catch", "Response body: ${response.body()}")
                    }

                    Log.d("제품정보 오류", errorJson ?: "No error body")
                    showCustomToast("에러", errorMessage.toString())
                }
            }
        })
    }
}