package com.example.dooch_wms

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key.Companion.Window
import com.example.dooch_wms.databinding.ActivityPauseBinding
import com.example.dooch_wms.databinding.ActivityWorkBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timer

object TimerManager {
    private var time = 0
    private var pauseTime = 0 // 중지 시간을 기록할 변수
    private var totalPauseTime = 0 // 중지 시간의 합
    private var isRunning = false
    private var timerTask: Timer? = null
    private var pauseStart: Long = 0 // 중지 시작 시간 기록

    fun startTimer(updateUI: (Int, Int) -> Unit) {
        if (!isRunning) {
            isRunning = true
            timerTask = timer(period = 1000) {
                time++
                val min = time / 60
                val sec = time % 60
                updateUI(min, sec)
            }
        }
    }

    fun stopTimer() {
        timerTask?.cancel()
        timerTask = null
        isRunning = false
        pauseStart = System.currentTimeMillis() // 중지 시간 시작 기록
    }

    fun resumeTimer(updateUI: (Int, Int) -> Unit) {
        if (!isRunning) {
            isRunning = true
            val pauseEnd = System.currentTimeMillis()
            pauseTime = ((pauseEnd - pauseStart) / 1000).toInt()
            totalPauseTime += pauseTime // 중지 시간 합산
            timerTask = timer(period = 1000) {
                time++
                val min = time / 60
                val sec = time % 60
                updateUI(min, sec)
            }
        }
    }

    fun getTotalPauseTime(): Int {
        return totalPauseTime
    }

    fun resetTimer() {
        stopTimer()
        time = 0
        totalPauseTime = 0
    }

    fun getTime(): Int = time
    fun isTimerRunning(): Boolean = isRunning
}

class W_work : AppCompatActivity() {
    private lateinit var workBinding: ActivityWorkBinding
    private var updateTimer: Timer? = null
    private var batch = 0
    private var start_time:String? = null
    private var end_time:String? = null
    private var stop_s_time:String? = null
    private var stop_e_time:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflate(layoutInflater)로 초기화 한다
        workBinding = ActivityWorkBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(workBinding.root)

        // 초기 화면 설정
        // MyApp 인스턴스 가져오기
        val app = applicationContext as MyApp

        // 인텐트에서 값을 가져온다
        // 사원정보에서 가져온다
        val empIdFromIntent = intent.getStringExtra("emp_id") ?: ""
        val empNameFromIntent = intent.getStringExtra("emp_name") ?: ""
        //생산오더 정보에서 가져온다
        val orderIdFromIntent = intent.getStringExtra("order_id") ?: ""
        val prodIdFromIntent = intent.getStringExtra("prod_id") ?: ""
        val prodNameFromIntent = intent.getStringExtra("prod_name") ?: ""
        val prodQtyFromIntent = intent.getStringExtra("prod_qty") ?: ""

        val r_emp_id = intent.getStringExtra("r_emp_id") ?: ""
        val r_emp_name = intent.getStringExtra("r_emp_name") ?: ""

        // Intent에서 가져온 값이 null일 경우 기본값 설정
        app.emp_id = if (empIdFromIntent.isNotEmpty()) empIdFromIntent else ""
        app.emp_name = if (empNameFromIntent.isNotEmpty()) empNameFromIntent else ""

        app.order_id = if (orderIdFromIntent.isNotEmpty()) orderIdFromIntent else ""
        app.prod_id = if (prodIdFromIntent.isNotEmpty()) prodIdFromIntent else ""
        app.prod_name = if (prodNameFromIntent.isNotEmpty()) prodNameFromIntent else ""
        app.prod_qty = if (prodQtyFromIntent.isNotEmpty()) prodQtyFromIntent else ""

        app.r_emp_id = if (r_emp_id.isNotEmpty()) r_emp_id else ""
        app.r_emp_name = if (r_emp_name.isNotEmpty()) r_emp_name else ""

        // 전역 변수 가져오기 및 TextView에 표시
        // 사용자 정보 입력
        workBinding.txtWorkEmpId.text = app.emp_id
        workBinding.txtWorkEmpName.text = app.emp_name
        // 생산오더 정보 입력
        workBinding.txtWorkOrderId.text = app.order_id
        workBinding.txtWorkProdId.text = app.prod_id
        workBinding.txtWorkProdName.text = app.prod_name
        workBinding.txtWorkProdQty.text = app.prod_qty

        // 시작시간 설정
        workBinding.txtWorkStartTime.text = ""
        // 중지, 종료 버튼, 배치 번호 비활성화 및 배치번호 갱신
        workBinding.btnWorkStart.isEnabled = true
        workBinding.btnWorkPause.isEnabled = false
        workBinding.btnWorkEnd.isEnabled = false
        workBinding.txtWorkBatchId.text = batch.toString()

        // 인텐트에서 값을 가져온다
        // 중지 화면에서 중지 사유를 가져온다.
        // 20241106 화면구성 변경으로 사용하지 않는다.
//        val pause_reason = intent.getStringExtra("pause_reason")
//        workBinding.txtReason.text = pause_reason.toString()

//        // 임직원 화면에서 사용자 정보를 가져온다.
//        app.emp_id =  intent.getStringExtra("emp_id")
//        app.emp_name = intent.getStringExtra("emp_name")
//        workBinding.txtWorkEmpId.text = app.emp_id
//        workBinding.txtWorkEmpName.text = app.emp_name

        // 생산오더 화면에서 생산계획 정보를 가져온다.



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

            // http 통신을 위한 retrofit 설정
            // retrofit 객체 생성
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.197:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val prodservice: S_production = retrofit.create(S_production::class.java)

            prodservice.requestProdInfo(order_id).enqueue(object : Callback<D_production> { //받는 값이 List 형식이면 Callback<List<D_production>>
                override fun onFailure(call: Call<D_production>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@W_work)
                    dialog.setTitle("에러")
                    dialog.setMessage(t.message)
                    dialog.show()
                }
                override fun onResponse(call: Call<D_production>, response: Response<D_production>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
                    if (response.isSuccessful && response.body() != null) {
                        val prodList = response.body()!!
                        // body로 받은 값을 할당 하는 코드
                        workBinding.txtWorkOrderId.text = prodList.order_id.toString()
                        workBinding.txtWorkProdId.text = prodList.item_id.toString()
                        workBinding.txtWorkProdName.text = prodList.item_name.toString()
                        workBinding.txtWorkProdQty.text = prodList.item_qty.toString()
                    } else {
                        // 오류 처리 코드
                        Log.d("Response", response.errorBody()?.string() ?: "No error body")
                        Toast.makeText(this@W_work, "유효한 생산오더가 아닙니다. 생산오더를 다시 확인해 주세요.", Toast.LENGTH_LONG).show()
                    }
                }
            })

            // intent를 사용하지 않고 직접 조회후 없으면 에러 메시지 띄우기로 변경
//            val intent = Intent(this, W_production::class.java)
//            intent.putExtra("order_id", order_id)
//            intent.putExtra("emp_id", app.emp_id)
//            intent.putExtra("emp_name", app.emp_name)
//            startActivityForResult(intent, 88)

        }

        // 시작버튼 클릭 리스너 설정
        workBinding.btnWorkStart.setOnClickListener {
            workBinding.btnWorkStart.text = "재시작"
            workBinding.btnWorkPause.isEnabled = true

            if(start_time.isNullOrEmpty()) {
                start_time = System.currentTimeMillis().toString() // start_time에 현재 시간 저장
                workBinding.txtWorkStartTime.text = start_time // TextView에도 설정
            }

            // 20241106 화면구성 변경으로 사용하지 않음.
//            if(stop_e_time.isNullOrEmpty() && batch > 0) {
//                stop_e_time = System.currentTimeMillis().toString() // start_time에 현재 시간 저장
//                workBinding.txtWorkStopETime.text = stop_e_time // TextView에도 설정
//            }

            workBinding.txtWorkBatchId.text = batch.toString()

            // 시작버튼 클릭시 중지,작업종료 버튼 활성화
            workBinding.btnWorkPause.isEnabled = true
            workBinding.btnWorkEnd.isEnabled = true

            if (TimerManager.isTimerRunning()) {
//                stopTimerAndUpdateUI()
                return@setOnClickListener
            } else {
                startTimerAndUpdateUI()
            }
        }

        workBinding.btnWorkPause.setOnClickListener {

            // 중지 시간 기록(업데이트 값)
            // 20241106 화면구성 변경으로 사용하지 않음.
//            workBinding.txtWorkStopSTime.text = System.currentTimeMillis().toString()

            // 중지 버튼 클릭시 마다 batch 번호 +1
            batch++

            // 화면 복귀시 버튼 비활성화
            workBinding.btnWorkPause.isEnabled = false
            workBinding.btnWorkEnd.isEnabled = false

            if (TimerManager.isTimerRunning()) {
                stopTimerAndUpdateUI()
            } else {
                // 타이머가 중지상태 일경우 버튼을 눌러도 이벤트가 발생하지 않는다.
                return@setOnClickListener
            }
//            // 중지 화면으로 보낼 변수값 설정
//            val order_id: String = workBinding.txtWorkOrderId.text.toString()
//            val prod_id: String = workBinding.txtWorkProdId.text.toString()
//            val prod_name: String = workBinding.txtWorkProdName.text.toString()
//            val batch_id: String = workBinding.txtWorkBatchId.text.toString()

            // 20241106 화면변경으로 사용하지 않음
//            val currentTime = TimerManager.getTime()

//            // 중지 화면으로 값 전달
//            val intent = Intent(this, W_pause::class.java)
//            intent.putExtra("order_id", order_id) // 생산오더 id 전달
//            intent.putExtra("prod_id", prod_id) // 제품 id 전달
//            intent.putExtra("prod_name", prod_name) // 제품명 전달
//            intent.putExtra("batch_id", batch_id) // 배차 id 전달
//            startActivity(intent)
//            // start_time이 null이 아닌 경우에만 전달
//            if (!start_time.isNullOrEmpty()) {
//                intent.putExtra("start_time", start_time)
//            }
            startActivityForResult(intent, 1)
        }

        // "작업 종료" 버튼 클릭 리스너
        workBinding.btnWorkEnd.setOnClickListener {
            if(end_time.isNullOrEmpty()) {
                end_time = System.currentTimeMillis().toString() // start_time에 현재 시간 저장
                workBinding.txtWorkEndTime.text = start_time // TextView에도 설정
            }

            val start_time = workBinding.txtWorkStartTime.text.toString().toLong()
            val end_time = workBinding.txtWorkEndTime.text.toString().toLong()
            val time_gap = end_time - start_time

            val dateFormat = SimpleDateFormat("hh:mm:ss")
            val total_time = dateFormat.format(time_gap)

            workBinding.txtWorkTotalTime.text = total_time

            endWorkAndDisplayTimes()
            // 종료화면으로 이동 <-- 건네줄 값 셋팅 필요
//            val intent = Intent(this, W_result::class.java)
//            intent.putExtra("pause_reason", pause_reason)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
        }

        // 메인화면 돌아가기
        workBinding.btnWorkReturn.setOnClickListener {

            // 메인화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private fun startTimerAndUpdateUI() {
        TimerManager.startTimer { _, _ -> }

        updateTimer?.cancel()
        updateTimer = Timer()
        updateTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    updateTimerDisplay()
                }
            }
        }, 0, 1000)
    }

    private fun stopTimerAndUpdateUI() {
        TimerManager.stopTimer()
        updateTimer?.cancel()
        updateTimer = null

//        workBinding.btnWorkStart.text = "시작" // 버튼 텍스트 변경
        updateTimerDisplay() // 마지막으로 표시된 시간 유지
    }

    override fun onResume() {
        super.onResume()
        updateTimerDisplay() // 화면이 다시 보일 때 타이머 시간 업데이트
    }

    private fun updateTimerDisplay() {
        val time = TimerManager.getTime()
        val min = time / 60
        val sec = time % 60
        workBinding.txtWorkWorkTime.text = String.format("%02d:%02d", min, sec)
    }

    private fun updateButtonText() {
        workBinding.btnWorkStart.text = if (TimerManager.isTimerRunning()) "재시작" else "시작"
    }

    override fun onDestroy() {
        super.onDestroy()
        updateTimer?.cancel()
        updateTimer = null
    }

    private fun endWorkAndDisplayTimes() {
        // 타이머 중지
        TimerManager.stopTimer()

        // 전체 작업 시간 및 중지 시간 가져오기
        val totalWorkTime = TimerManager.getTime() // 전체 작업 시간
        val totalPauseTime = TimerManager.getTotalPauseTime() // 중지 시간 합산

        // 시간 분할 계산 (분:초 형식으로 변환)
        val workMin = totalWorkTime / 60
        val workSec = totalWorkTime % 60
        val pauseMin = totalPauseTime / 60
        val pauseSec = totalPauseTime % 60

        // 텍스트뷰에 시간 표시
        workBinding.txtWorkTotalTime.text = String.format("전체 작업시간: %02d:%02d", workMin, workSec)
        workBinding.txtWorkPauseTime.text = String.format("중지 시간: %02d:%02d", pauseMin, pauseSec)
    }

    private fun updateStartButtonText() {
        if (batch == 0) {
            workBinding.btnWorkStart.text = "시작"
        } else {
            workBinding.btnWorkStart.text = "재시작"
        }
    }

    /** 현재시간 구하기 ["yyyy-MM-dd HH:mm:ss"] (*HH: 24시간)*/
    fun getTime(): String {
        var now = System.currentTimeMillis()
        var date = Date(now)

        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var getTime = dateFormat.format(date)

        return getTime
    }

    // Bundle에 화면이 파괴 되어도 되살아 나야 하는 값을 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 시작 시간을 숫자 형식으로 저장
        val startTime = workBinding.txtWorkStartTime.text.toString()
        outState.putString("start_time", startTime)
    }


}