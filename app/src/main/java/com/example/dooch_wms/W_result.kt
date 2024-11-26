package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityResultBinding
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.TimeUnit

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

        val c_time = intent.getStringExtra("c_time") // 시작시간
        val end_time = intent.getStringExtra("end_time") // 종료시간
        var working_time = intent.getStringExtra("working_time")?: "" // 작업시간
        var pause_time = intent.getStringExtra("pause_time")?: "" //중지시간

        working_time = convertSecondsToTimeFormat(working_time.toInt())
        pause_time = convertSecondsToTimeFormat(pause_time.toInt())


        // intent를 통해 받은 값을 상위 항목에 입력 함.
        resultBinding.txtResultEmpId.text = emp_id
        resultBinding.txtResultEmpName.text = emp_name
        resultBinding.txtResultOrderId.text = order_id
        resultBinding.txtResultProdId.text = prod_id
        resultBinding.txtResultProdName.text = prod_name

        resultBinding.txtResultCTime.text = c_time // 시작시간
        resultBinding.txtResultEndTime.text = end_time // 종료시간
        resultBinding.txtResultWorkingTime.text = working_time // 작업시간
        resultBinding.txtResultPauseTime.text = pause_time // 중지시간

       // "W_work" 화면으로 이동 intent flag 사용(이전화면 내용 복구)
        resultBinding.btnResultReturn.setOnClickListener {
            val intent = Intent(this, W_work::class.java)
            intent.putExtra("emp_id", emp_id)
            intent.putExtra("emp_name", emp_name)
            startActivity(intent)
        }

    }

    private fun differenceTime(startDateString:String, endDateString:String): Pair<Long, Long> {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
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

    fun convertSecondsToTimeFormat(sec: Int): String {
        val hours = sec / 3600
        val minutes = (sec % 3600) / 60
        val seconds = sec % 60

        return String.format("%02d시간 %02d분 %02d초", hours, minutes, seconds)
    }
}