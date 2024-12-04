package com.example.dooch_wms

import com.example.dooch_wms.databinding.ActivityDepartmentBinding
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class W_department : ComponentActivity() {
    private lateinit var departmentBinding: ActivityDepartmentBinding

//    var EmpList = arrayListOf<D_employee>(
//        D_employee("1000", "홍길동", "생산1팀"),
//        D_employee("1010", "이순신", "생산2팀"),
//        D_employee("1020", "세종대왕", "생산3팀")
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate(layoutInflater)로 초기화 한다
        departmentBinding = ActivityDepartmentBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(departmentBinding.root)

        // 조회를 위한 변수 설정 및 함수 불러 오기
        val use: String = 'y'.toString()
        get_data(use)

        // 리스트뷰의 특정 라인을 클릭하면 발생 하는 이벤트
        departmentBinding.listDepartment.setOnItemClickListener {parent, view, position, id ->
            // 리스트뷰의 선택 위치(라인?)를 D_employee에 대입하여 받음
            val selectItem = parent.getItemAtPosition(position) as D_department

            departmentBinding.txtDeptDeptId.setText(selectItem.dept_id)
            departmentBinding.txtDeptDeptName.setText(selectItem.dept_name)

        }

        // 화면 아래 확인 버튼을 클릭하여 선택한 '사원번호', '사원명'을 원)액티비티인 W_work로 보냄
        departmentBinding.btnDeptConfirm.setOnClickListener        {
            val dept_id = departmentBinding.txtDeptDeptId.text.toString()
            val dept_name = departmentBinding.txtDeptDeptName.text.toString()

//            // 인텐트를 생성하고 값을 전달 함
//            val returnIntent = Intent()
//            returnIntent.putExtra("emp_id", emp_id)
//            returnIntent.putExtra("emp_name", emp_name)
//            returnIntent.putExtra("dept_name", dept_name)
//            setResult(RESULT_OK, returnIntent)
//            finish()

            val intent = Intent(this, W_work::class.java)
            intent.putExtra("dept_id", dept_id)
            intent.putExtra("dept_name", dept_name)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
    private fun get_data(use: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.197:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.d("알림", use)

        val deptservice: S_department = retrofit.create(S_department::class.java)

        deptservice.requestDeptInfo(use).enqueue(object : Callback<List<D_department>> { //받는 값이 Object 형식이면 Callback<D_production>
            override fun onFailure(call: Call<List<D_department>>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@W_department)
                dialog.setTitle("에러")
                dialog.setMessage(t.message)
                dialog.show()
            }

            override fun onResponse(call: Call<List<D_department>>, response: Response<List<D_department>>) { //받는 값이 Object 형식이면 Callback<D_production>
                if (response.isSuccessful && response.body() != null) {
                    val deptList = response.body()!!
                    initializeViews(deptList) // 리스트뷰에 데이터 설정
                } else {
                    // 오류 처리 코드
                    Log.d("Response", response.errorBody()?.string() ?: "No error body")
                    Toast.makeText(this@W_department, "데이터 로드 실패", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initializeViews(result: List<D_department>) {
        // List를 ArrayList로 변환
        val deptArrayList = ArrayList(result)

        // ListView에 어댑터 설정
        val adapter = A_department(this, deptArrayList)
        departmentBinding.listDepartment.adapter = adapter
    }
}