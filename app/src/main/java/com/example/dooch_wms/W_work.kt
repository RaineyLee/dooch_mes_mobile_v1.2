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
import android.view.Gravity
import android.view.View
import com.example.dooch_wms.databinding.CustomToastBinding
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit



class W_work : AppCompatActivity() {
    private lateinit var workBinding: ActivityWorkBinding
    private var batch = 0
    private var isBtnSearchEnabled = true // 버튼 리스너 활성화 여부 제어

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflate(layoutInflater)로 초기화 한다
        workBinding = ActivityWorkBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(workBinding.root)

        // 개발용 view 비활성화 visibility = View.GONE --> 안보임&공간 차지하지 않음, 
        // visibility = View.INVISIBLE 안보임&공간 차지함
        workBinding.txtWorkCTime.visibility = View.GONE
        workBinding.textView7.visibility = View.GONE
        workBinding.textView8.visibility = View.GONE
        workBinding.txtWorkStartTime.visibility = View.GONE
        workBinding.txtWorkEndTime.visibility = View.GONE
        workBinding.txtWorkProdTime.visibility = View.GONE
        workBinding.txtWorkDeptName2.visibility = View.GONE
        workBinding.textView28.visibility = View.GONE
        workBinding.txtWorkBatchId.visibility = View.GONE

        // 인텐트에서 값을 가져온다
        // 사원정보에서 가져온다
//        val empIdFromIntent = intent.getStringExtra("emp_id") ?: ""
//        val empNameFromIntent = intent.getStringExtra("emp_name") ?: ""
//        val deptNameFromIntent = intent.getStringExtra("dept_name") ?: ""
        // 부서 정보를 가져온다
        val deptIdFromIntent = intent.getStringExtra("dept_id") ?: ""
        val deptNameFromIntent = intent.getStringExtra("dept_name") ?: ""

        // Intent에서 가져온 값이 null일 경우 기본값 설정
        val dept_id = if (deptIdFromIntent.isNotEmpty()) deptIdFromIntent else ""
        val dept_name = if (deptNameFromIntent.isNotEmpty()) deptNameFromIntent else ""
//        val dept_name = if (deptNameFromIntent.isNotEmpty()) deptNameFromIntent else ""

        // 사용자 정보 입력
        workBinding.txtWorkDeptId.text = dept_id
        workBinding.txtWorkDeptName.text = dept_name

        // 시작시간 설정
        workBinding.txtWorkStartTime.text = ""

        // 생산오더 정보, 중지, 종료 버튼, 배치 번호 비활성화 및 배치번호 갱신
        workBinding.btnWorkStart.isEnabled = false
        workBinding.btnWorkPause.isEnabled = false
        workBinding.btnWorkEnd.isEnabled = false
        workBinding.txtWorkBatchId.text = String.format(Locale.getDefault(), "%d", batch)

        if (workBinding.txtWorkDeptId.text.toString() != "") {
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

            // 사번/사원명 입력시 검색 조회 조건으로 전달
//            val emp_id = workBinding.txtWorkEmpId.text.toString()
//            val emp_name = workBinding.txtWorkEmpName.text.toString()

            // 사원정보를 기준으로 검색할 때
//            val intent = Intent(this, W_employee::class.java)
//            intent.putExtra("emp_id", emp_id)
//            intent.putExtra("emp_name", emp_name)
////            startActivityForResult(intent, 99) // 처음에 만든 코드
//            startActivity(intent) // 이렇게 수정해도 문제가 발생하지 않는것 같다.(20241212)

            // 부서 정보를 가져 올 때(사용 하려고 하는 부서 정보만 가져온다)
            val intent = Intent(this, W_department::class.java)
//            intent.putExtra("emp_id", emp_id)
//            intent.putExtra("emp_name", emp_name)
////            startActivityForResult(intent, 99) // 처음에 만든 코드
            startActivity(intent) // 이렇게 수정해도 문제가 발생하지 않는것 같다.(20241212)
        }

        // "생산오더 번호" 입력후 조회버튼 클릭시 W_production 화면으로 이동
        // 전환시 "생산오더 번호" 같이 전달
        workBinding.btnWorkSearch2.setOnClickListener {

            // 키보드 숨기기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(workBinding.txtWorkOrderId.windowToken, 0)

            // EditText의 커서 없애기 (포커스 제거)
            workBinding.txtWorkOrderId.clearFocus()

            if (workBinding.txtWorkDeptId.text.toString() == ""){
                showCustomToast("알림", "작업자를 먼저 '조회/선택' 해 주세요.")
                return@setOnClickListener
            }

            if (workBinding.txtWorkOrderId.text.toString() == ""){
                showCustomToast("알림", "생산오더 번호를 입력해 주세요.")
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
                        workBinding.txtWorkProdQty.text = prodList.item_qty.toString() + " EA"
                        workBinding.txtWorkProdStatus.text = prodList.status.toString()
                        workBinding.txtWorkCTime.text = prodList.c_time.toString()
                        workBinding.txtWorkProdTime.text = prodList.w_time.toString()
                        workBinding.txtWorkStartTime.text = prodList.s_time.toString()

                    } else if (response.isSuccessful && response.body()?.order_id == null) {
                        workBinding.txtWorkOrderId.setText("")
                        workBinding.txtWorkProdId.text = ""
                        workBinding.txtWorkProdName.text = ""
                        workBinding.txtWorkProdQty.text = ""
                        workBinding.txtWorkProdStatus.text = ""
                        workBinding.txtWorkProdTime.text = ""
                        workBinding.txtWorkCTime.text = ""
                        showCustomToast("알림", "조회된 생산오더가 없습니다. 생산오더를 확인해 주세요.")
                    } else {
                        // 오류 처리 코드
                        // 오류 처리: errorBody() 파싱하여 서버에서 전달한 메시지 출력
                        val errorJson = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorJson).getString("message")
                            Log.d("전달값_try", "Response body: ${response.body()}")
                        } catch (e: Exception) {
                            Log.d("전달값_catch", "Response body: ${response.body()}")
                        }

                        Log.d("제품정보 오류", errorJson ?: "No error body")
                        showCustomToast("에러", errorMessage.toString())
                    }
                }
            })

        }

        // 시작버튼 클릭 리스너 설정
        workBinding.btnWorkStart.setOnClickListener {
            // 사용자에게 보여주는 현재시간(millis는 시간 계산에 사용)
            // 현재 시간 가져오기
            val currentTime = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

            if (workBinding.txtWorkProdId.text.toString() == ""){
                showCustomToast("알림", "유효한 생산오더가 아닙니다. 생산오더 번호를 확인해 주세요.")
                return@setOnClickListener
            }

            if(workBinding.txtWorkProdStatus.text.toString() == "릴리스됨" ){
                workBinding.txtWorkCTime.text = dateFormat.format(currentTime)
                workBinding.txtWorkStartTime.text = dateFormat.format(currentTime)

                workBinding.txtWorkProdStatus.text = "시작됨"
                workBinding.btnWorkStart.isEnabled = false
                workBinding.btnWorkPause.isEnabled = true
            }
            else if(workBinding.txtWorkProdStatus.text.toString() == "시작됨"){
                workBinding.btnWorkStart.isEnabled = false
                workBinding.btnWorkPause.isEnabled = true
                workBinding.btnWorkEnd.isEnabled = true

                showCustomToast("알림", "생산오더가 진행 상태입니다.")
                return@setOnClickListener
            }
            else if(workBinding.txtWorkProdStatus.text.toString() == "중지됨"){
                workBinding.txtWorkProdStatus.text = "시작됨"
                workBinding.txtWorkStartTime.text = dateFormat.format(currentTime)

                workBinding.btnWorkStart.isEnabled = false
                workBinding.btnWorkPause.isEnabled = true
                workBinding.btnWorkEnd.isEnabled = true
            }
            else if(workBinding.txtWorkProdStatus.text.toString() == "종료됨"){
                workBinding.btnWorkStart.isEnabled = false
                workBinding.btnWorkPause.isEnabled = false
                workBinding.btnWorkEnd.isEnabled = false

                // 조회된 생산오더 텍스트뷰 초기화
                workBinding.txtWorkProdId.text = ""
                workBinding.txtWorkProdStatus.text = ""
                workBinding.txtWorkOrderId.setText("")
                workBinding.txtWorkProdName.text = ""
                workBinding.txtWorkProdQty.text = ""

                showCustomToast("알림", "종료된 작업입니다.")
                return@setOnClickListener
            }
            else{
                workBinding.txtWorkStartTime.text = dateFormat.format(currentTime)
            }

            batch++
            workBinding.txtWorkBatchId.text = batch.toString()

            // 시작시 생산오더 시작 정보 기입을 위한 변수 설정
            val order_id = workBinding.txtWorkOrderId.text.toString()
            val dept_id = workBinding.txtWorkDeptId.text.toString()
            val dept_name = workBinding.txtWorkDeptName.text.toString()
            val status = workBinding.txtWorkProdStatus.text.toString()
            val s_time = workBinding.txtWorkStartTime.text.toString()
            val c_time = workBinding.txtWorkCTime.text.toString()

            // 시작버튼 클릭시 중지,작업종료 버튼 활성화
            workBinding.btnWorkPause.isEnabled = true
            workBinding.btnWorkEnd.isEnabled = true


            // 생산오더에 emp_id, emp_name을 입력하고, status를 '릴리스됨'에서 '시작됨'으로 변경
            editOrderStart(order_id, dept_id, dept_name, status, s_time, c_time)
        }

        workBinding.btnWorkPause.setOnClickListener {
            // end_time 설정
            // 현재 시간 가져오기
            val currentTime = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

            // 날짜와 시간을 기본 형식으로 변환 후 textview_new에 설정
            workBinding.txtWorkEndTime.text = dateFormat.format(currentTime)

            // 중지 버튼 클릭시 작업시간 계산을 위한 변수 추출
            val start_time = workBinding.txtWorkStartTime.text.toString()
            val end_time = workBinding.txtWorkEndTime.text.toString()
            var working_time = workBinding.txtWorkProdTime.text?.toString()?.toLongOrNull() ?: 0L

            // 누적 작업시간을 불러 와서 중지 버튼을 눌렀을 때 실제 작업시간을 계산
            val (dif_sec, dif_min) = differenceTime(start_time, end_time)
            working_time = working_time + dif_sec
            workBinding.txtWorkProdTime.text = working_time.toString()

            // 생산오더 상태값 변경
            workBinding.txtWorkProdStatus.text = "중지됨"

            // 화면 복귀시 버튼 비활성화
            workBinding.btnWorkPause.isEnabled = false
            workBinding.btnWorkEnd.isEnabled = false
            workBinding.btnWorkStart.text = "재시작"
            workBinding.btnWorkStart.isEnabled = true


            // 중지 화면으로 보낼 변수값 설정
            val order_id: String = workBinding.txtWorkOrderId.text.toString()
            val prod_id: String = workBinding.txtWorkProdId.text.toString()
            val prod_name: String = workBinding.txtWorkProdName.text.toString()
            val batch_id: String = workBinding.txtWorkBatchId.text.toString()
            val status: String = workBinding.txtWorkProdStatus.text.toString()

            // 작업시간을 업데이트 할 DB 연결 함수
            editOrderPause(order_id, working_time.toString(), status)

            // 중지 화면으로 값 전달
            val intent = Intent(this, W_pause::class.java)
            intent.putExtra("order_id", order_id) // 생산오더 id 전달
            intent.putExtra("prod_id", prod_id) // 제품 id 전달
            intent.putExtra("prod_name", prod_name) // 제품명 전달
            intent.putExtra("batch_id", batch_id) // 배차 id 전달
            startActivity(intent)

        }

        // "작업 종료" 버튼 클릭 리스너
        workBinding.btnWorkEnd.setOnClickListener {
            // 작업 상태값 변경(시작됨 --> 종료됨)
            workBinding.txtWorkProdStatus.text = "종료됨"

            // 현재 시간 가져오기
            val currentTime = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

            // 날짜와 시간을 기본 형식으로 변환 후 textview_new에 설정
            workBinding.txtWorkEndTime.text = dateFormat.format(currentTime)

            // 텍스트뷰에 입력된 날짜를 가져와서 두 시간 차이를 초로 계산
            val start_time = workBinding.txtWorkStartTime.text.toString()
            var end_time = workBinding.txtWorkEndTime.text.toString()

            // 사용자 함수를 통해서 차이 시간을 초,분 으로 받기
            val (dif_sec, dif_min) = differenceTime(start_time, end_time)

            // 종료 화면으로 보낼 변수값 확인
            val order_id = workBinding.txtWorkOrderId.text.toString()
            val prod_id = workBinding.txtWorkProdId.text.toString()
            val prod_name = workBinding.txtWorkProdName.text.toString()
            val c_time = workBinding.txtWorkCTime.text.toString()
            val w_time = workBinding.txtWorkProdTime.text?.toString()?.toLongOrNull() ?: 0L //저장된 작업시간 초기값 '0'
            val status = workBinding.txtWorkProdStatus.text.toString()

            // 작업시간 계산후 누적시킴
            val working_time = (w_time + dif_sec)
            workBinding.txtWorkProdTime.text = working_time.toString()

            // 중지시간계산
            val (t_dif_sec, t_dif_min) = differenceTime(c_time, end_time)
            val pause_time = (t_dif_sec - working_time).toString()


            // DB 입력
            editOrderEnd(order_id, end_time, working_time.toString(), pause_time, status)

            val intent = Intent(this, W_result::class.java)
            intent.putExtra("dept_id", dept_id)
            intent.putExtra("dept_name", dept_name)
            intent.putExtra("order_id", order_id)
            intent.putExtra("prod_id", prod_id)
            intent.putExtra("prod_name", prod_name)
            intent.putExtra("c_time", c_time)
            intent.putExtra("end_time", end_time)
            intent.putExtra("working_time", working_time.toString()) // 생산오더 id 전달
            intent.putExtra("pause_time", pause_time)
            startActivity(intent)

        }

        // 메인화면 돌아가기
        workBinding.btnWorkReturn.setOnClickListener {

            // 메인화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
        toast.duration = Toast.LENGTH_LONG
        toast.view = toastBinding.root

        // Toast 위치를 화면 중앙으로 설정
        toast.setGravity(Gravity.CENTER, 0, 0)

        toast.show()
    }

    // AlertDialog 표시 함수
    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)

        // '확인' 버튼 처리
        builder.setPositiveButton("확인") { dialog, _ ->
            isBtnSearchEnabled = false // 버튼 비활성화
            dialog.dismiss()
        }

        // '취소' 버튼 처리
        builder.setNegativeButton("취소") { dialog, _ ->
            isBtnSearchEnabled = false // 버튼 유지
            dialog.dismiss()
        }

        // 다이얼로그 표시
        val dialog = builder.create()
        dialog.show()
    }

    private fun differenceTime(startDateString:String, endDateString:String): Pair<Long, Long> {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        val startDate = dateFormat.parse(startDateString)
        val endDate = dateFormat.parse(endDateString)

        // 두 날짜의 차이를 밀리초 단위로 구함
        val differenceInMillis = endDate.time - startDate.time

        // 밀리초 차이를 다른 시간 단위로 변환
        val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis)
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(differenceInMillis)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis)

        return Pair(diffInSeconds, diffInMinutes)
    }

    private fun endWorkAndDisplayTimes(start_time:Long, end_time:Long) {

        val time_gap = end_time - start_time
        val dateFormat = SimpleDateFormat("mm:ss")
        val total_time = dateFormat.format(time_gap)

//        // 시간 분할 계산 (분:초 형식으로 변환)
//        val workMin = totalWorkTime / 60
//        val workSec = totalWorkTime % 60
//        val pauseMin = totalPauseTime / 60
//        val pauseSec = totalPauseTime % 60


    }


    private fun calculateTimeDifference(timeA: String, timeB: String): String {
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

    private fun editOrderStart(order_id: String, dept_id: String, dept_name: String, status: String, s_time: String, c_time: String){

        // 생산오더에 시작시간과 status를 변경
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.197:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val prodservice: S_production = retrofit.create(S_production::class.java)

        prodservice.startProdInfo(order_id, dept_id, dept_name, status, s_time, c_time).enqueue(object : Callback<D_msg> { //받는 값이 List 형식이면 Callback<List<D_production>>
            override fun onFailure(call: Call<D_msg>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@W_work)
                dialog.setTitle("에러")
                dialog.setMessage(t.message)
                dialog.show()
            }
            override fun onResponse(call: Call<D_msg>, response: Response<D_msg>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
                val success = response.body()!!
                val dialog = AlertDialog.Builder(this@W_work)
                if (response.isSuccessful) {
                    dialog.setTitle(success.code)
                    dialog.setMessage((success.msg))
                } else {
                    dialog.setTitle(success.code)
                    dialog.setMessage((success.msg))
                }
            }
        })
    }

    private fun editOrderEnd(order_id: String, end_time: String, working_time: String, pause_time: String, status: String){

        // 생산오더에 emp_id, emp_name을 입력하고, status를 '릴리스됨'에서 '시작됨'으로 변경
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.197:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val prodservice: S_production = retrofit.create(S_production::class.java)

        prodservice.endProdInfo(order_id, end_time, working_time, pause_time, status).enqueue(object : Callback<D_msg> { //받는 값이 List 형식이면 Callback<List<D_production>>
            override fun onFailure(call: Call<D_msg>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@W_work)
                dialog.setTitle("에러")
                dialog.setMessage(t.message)
                dialog.show()
            }
            override fun onResponse(call: Call<D_msg>, response: Response<D_msg>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
                val success = response.body()!!
                val dialog = AlertDialog.Builder(this@W_work)
                if (response.isSuccessful) {
                    dialog.setTitle(success.code)
                    dialog.setMessage((success.msg))
                } else {
                    dialog.setTitle(success.code)
                    dialog.setMessage((success.msg))
                }
            }
        })
    }

    private fun editOrderPause(order_id: String, working_time: String, status: String){

        // 생산오더에 emp_id, emp_name을 입력하고, status를 '릴리스됨'에서 '중지됨'으로 변경
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.197:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val prodservice: S_production = retrofit.create(S_production::class.java)

        prodservice.pauseProdInfo(order_id, working_time, status).enqueue(object : Callback<D_msg> { //받는 값이 List 형식이면 Callback<List<D_production>>
            override fun onFailure(call: Call<D_msg>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@W_work)
                dialog.setTitle("에러")
                dialog.setMessage(t.message)
                dialog.show()
            }
            override fun onResponse(call: Call<D_msg>, response: Response<D_msg>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
                val success = response.body()!!
                val dialog = AlertDialog.Builder(this@W_work)
                if (response.isSuccessful) {
                    dialog.setTitle(success.code)
                    dialog.setMessage((success.msg))
                } else {
                    dialog.setTitle(success.code)
                    dialog.setMessage((success.msg))
                }
            }
        })
    }
}

