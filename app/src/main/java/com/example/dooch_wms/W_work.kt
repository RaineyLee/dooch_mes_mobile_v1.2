package com.example.dooch_wms


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.dooch_wms.databinding.ActivityWorkBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import android.text.format.DateFormat
import java.util.Date
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer



class W_work : AppCompatActivity() {
    private lateinit var workBinding: ActivityWorkBinding
    private var updateTimer: Timer? = null
    private var batch = 0
    private var start_time:String? = null
    private var end_time:String? = null

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

        // Intent에서 가져온 값이 null일 경우 기본값 설정
        app.emp_id = if (empIdFromIntent.isNotEmpty()) empIdFromIntent else ""
        app.emp_name = if (empNameFromIntent.isNotEmpty()) empNameFromIntent else ""

        // 전역 변수 가져오기 및 TextView에 표시
        // 사용자 정보 입력
        workBinding.txtWorkEmpId.text = app.emp_id
        workBinding.txtWorkEmpName.text = app.emp_name

        // 시작시간 설정
        workBinding.txtWorkStartTime.text = ""

        // 생산오더 정보, 중지, 종료 버튼, 배치 번호 비활성화 및 배치번호 갱신
        workBinding.btnWorkStart.isEnabled = false
        workBinding.btnWorkPause.isEnabled = false
        workBinding.btnWorkEnd.isEnabled = false
        workBinding.txtWorkBatchId.text = batch.toString()

        if (workBinding.txtWorkEmpId.text.toString() != "") {
            workBinding.btnWorkSearch2.isEnabled = true
        }
        // 생산오더 아이디 확인
        workBinding.txtWorkOrderId.addTextChangedListener {
            val inputText = workBinding.txtWorkOrderId.text.toString()
            // 입력된 텍스트가 비어 있지 않으면 버튼 활성화, 비어 있으면 비활성화
            if (inputText.length > 0){
                workBinding.btnWorkStart.isEnabled = true
            }
        }

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
            // 키보드 숨기기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(workBinding.txtWorkOrderId.windowToken, 0)

            // EditText의 커서 없애기 (포커스 제거)
            workBinding.txtWorkOrderId.clearFocus()

            if (workBinding.txtWorkOrderId.text.toString() == ""){
                Toast.makeText(this@W_work, "생산오더 번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                    val prodList = response.body()!!
                    if (response.isSuccessful && response.body()?.order_id != null) {
                        // body로 받은 값을 할당 하는 코드
                        workBinding.txtWorkOrderId.setText(prodList.order_id.toString())
                        workBinding.txtWorkProdId.text = prodList.item_id.toString()
                        workBinding.txtWorkProdName.text = prodList.item_name.toString()
                        workBinding.txtWorkProdQty.text = prodList.item_qty.toString()
                        Log.d("전달값", "Response body: ${response.body()}")

                    } else if (response.isSuccessful && response.body()?.order_id == null) {
                        workBinding.txtWorkOrderId.setText("")
                        workBinding.txtWorkProdId.text = ""
                        workBinding.txtWorkProdName.text = ""
                        workBinding.txtWorkProdQty.text = ""
                        Log.d("전달값_null", "Response body: ${response.body()}")
                        Toast.makeText(this@W_work, "조회된 생산오더가 없습니다. 생산오더를 확인해 주세요.", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@W_work, errorMessage, Toast.LENGTH_LONG).show()
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
            if (workBinding.txtWorkProdId.text.toString() == ""){
                Toast.makeText(this@W_work, "유효한 생산오더가 아닙니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            workBinding.btnWorkStart.text = "재시작"
            workBinding.btnWorkPause.isEnabled = true

            app.emp_id = workBinding.txtWorkEmpId.text.toString()
            app.emp_name = workBinding.txtWorkEmpName.text.toString()
            app.order_id = workBinding.txtWorkOrderId.text.toString()
            app.prod_id = workBinding.txtWorkProdId.text.toString()
            app.prod_name = workBinding.txtWorkProdName.text.toString()

            if(start_time.isNullOrEmpty()) {
                start_time = System.currentTimeMillis().toString() // start_time에 현재 시간 저장
                workBinding.txtWorkStartTime.text = start_time // TextView에도 설정
            }

            // 20241106 화면구성 변경으로 사용하지 않음.
//            if(stop_e_time.isNullOrEmpty() && batch > 0) {
//                stop_e_time = System.currentTimeMillis().toString() // start_time에 현재 시간 저장
//                workBinding.txtWorkStopETime.text = stop_e_time // TextView에도 설정
//            }
            batch++
            workBinding.txtWorkBatchId.text = batch.toString()

            // 사용자에게 보여주는 현재시간(millis는 시간 계산에 사용)
            // 현재 시간 가져오기
            val currentTime = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

            // 날짜와 시간을 기본 형식으로 변환 후 textview_new에 설정
            workBinding.txtWorkCurTime.text = dateFormat.format(currentTime)

            // 시작버튼 클릭시 중지,작업종료 버튼 활성화
            workBinding.btnWorkPause.isEnabled = true
            workBinding.btnWorkEnd.isEnabled = true

            if (TimerManager.isTimerRunning()) {
//                stopTimerAndUpdateUI()
                return@setOnClickListener
            } else {
                startTimerAndUpdateUI()
            }

            // 생산오더에 emp_id, emp_name을 입력하고, status를 '릴리스됨'에서 '시작됨'으로 변경
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.197:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val prodservice: S_production = retrofit.create(S_production::class.java)
        }

        workBinding.btnWorkPause.setOnClickListener {

            // 중지 시간 기록(업데이트 값)
            // 20241106 화면구성 변경으로 사용하지 않음.
//            workBinding.txtWorkStopSTime.text = System.currentTimeMillis().toString()

            // 화면 복귀시 버튼 비활성화
            workBinding.btnWorkPause.isEnabled = false
            workBinding.btnWorkEnd.isEnabled = false

            if (TimerManager.isTimerRunning()) {
                stopTimerAndUpdateUI()
            } else {
                // 타이머가 중지상태 일경우 버튼을 눌러도 이벤트가 발생하지 않는다.
                return@setOnClickListener
            }
            // 중지 화면으로 보낼 변수값 설정
            val order_id: String = workBinding.txtWorkOrderId.text.toString()
            val prod_id: String = workBinding.txtWorkProdId.text.toString()
            val prod_name: String = workBinding.txtWorkProdName.text.toString()
            val batch_id: String = workBinding.txtWorkBatchId.text.toString()

            // 20241106 화면변경으로 사용하지 않음
//            val currentTime = TimerManager.getTime()

//            // 중지 화면으로 값 전달
            val intent = Intent(this, W_pause::class.java)
            intent.putExtra("order_id", order_id) // 생산오더 id 전달
            intent.putExtra("prod_id", prod_id) // 제품 id 전달
            intent.putExtra("prod_name", prod_name) // 제품명 전달
            intent.putExtra("batch_id", batch_id) // 배차 id 전달
            startActivity(intent)
//            // start_time이 null이 아닌 경우에만 전달
//            if (!start_time.isNullOrEmpty()) {
//                intent.putExtra("start_time", start_time)
//            }
            startActivityForResult(intent, 1)
        }

        // "작업 종료" 버튼 클릭 리스너
        workBinding.btnWorkEnd.setOnClickListener {
            if(end_time.isNullOrEmpty()) {
                end_time = System.currentTimeMillis().toString() // 종료시간 측정
                workBinding.txtWorkEndTime.text = end_time // TextView에 종료시간 입력
            }

            val start_time = workBinding.txtWorkStartTime.text.toString().toLong()
            val end_time = workBinding.txtWorkEndTime.text.toString().toLong()
            endWorkAndDisplayTimes(start_time, end_time)

            val working_time = workBinding.txtWorkWorkTime.text.toString()
            val total_time = workBinding.txtWorkTotalTime.text.toString()
            val pause_time = calculateTimeDifference(total_time, working_time)

            val intent = Intent(this, W_result::class.java)
            intent.putExtra("emp_id", app.emp_id)
            intent.putExtra("emp_name", app.emp_name)
            intent.putExtra("order_id", app.order_id)
            intent.putExtra("prod_id", app.prod_id)
            intent.putExtra("prod_name", app.prod_name)
            intent.putExtra("working_time", working_time) // 생산오더 id 전달
            intent.putExtra("total_time", total_time) // 제품 id 전달
            intent.putExtra("pause_time", pause_time) // 중지시간 전달
            startActivity(intent)

            TimerManager.resetTimer()
            // 필요하다면 UI를 업데이트하여 타이머가 초기화된 것을 표시
            workBinding.txtWorkWorkTime.text = "00:00"

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

    private fun endWorkAndDisplayTimes(start_time:Long, end_time:Long) {
        // 타이머 중지
        TimerManager.stopTimer()

        // 전체 작업 시간 및 중지 시간 가져오기
        val totalWorkTime = TimerManager.getTime() // 전체 작업 시간
        val totalPauseTime = TimerManager.getTotalPauseTime() // 중지 시간 합산

        val time_gap = end_time - start_time
        val dateFormat = SimpleDateFormat("mm:ss")
        val total_time = dateFormat.format(time_gap)

//        // 시간 분할 계산 (분:초 형식으로 변환)
//        val workMin = totalWorkTime / 60
//        val workSec = totalWorkTime % 60
//        val pauseMin = totalPauseTime / 60
//        val pauseSec = totalPauseTime % 60

        // 텍스트뷰에 시간 표시
        workBinding.txtWorkTotalTime.text = total_time.toString()
//        workBinding.txtWorkPauseTime.text = String.format("중지 시간: %02d:%02d", pauseMin, pauseSec)
    }

    // Bundle에 화면이 파괴 되어도 되살아 나야 하는 값을 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 시작 시간을 숫자 형식으로 저장
        val startTime = workBinding.txtWorkStartTime.text.toString()
        outState.putString("start_time", startTime)
    }

    fun calculateTimeDifference(timeA: String, timeB: String): String {
        // Define the time format
        val timeFormat = SimpleDateFormat("mm:ss")

        // Parse the input times
        val dateA: Date = timeFormat.parse(timeA)
        val dateB: Date = timeFormat.parse(timeB)

        // Calculate the difference in milliseconds
        val differenceInMillis = dateA.time - dateB.time

        // Convert milliseconds to minutes and seconds
        val minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis) % 60

        // Format and return the result as mm:ss
        return String.format("%02d:%02d", minutes, seconds)
    }
}

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