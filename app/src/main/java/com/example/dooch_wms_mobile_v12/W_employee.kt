package com.example.dooch_wms_mobile_v12

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.dooch_wms_mobile_v12.databinding.ActivityEmployeeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class W_employee : ComponentActivity() {
    private lateinit var employeeBinding: ActivityEmployeeBinding

//    var EmpList = arrayListOf<D_employee>(
//        D_employee("1000", "홍길동", "생산1팀"),
//        D_employee("1010", "이순신", "생산2팀"),
//        D_employee("1020", "세종대왕", "생산3팀")
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate(layoutInflater)로 초기화 한다
        employeeBinding = ActivityEmployeeBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(employeeBinding.root)

        // W_work 에서 "사번"과"이름을 전달 받는 변수 --> 추후 "사용자" 검색에 사용(intent 사용)
        val value_1 = intent.getStringExtra("emp_id") //사번
        val value_2 = intent.getStringExtra("emp_name") //이름

        // db 조회를 위해 전달 받은 값을 잠시 저장 하는 장소로 사용
        employeeBinding.test1.setText(value_1)
        employeeBinding.test2.setText(value_2)

        // db 조회를 위해 전달 받은 값을 저장하는 장소를 보이지 않게 하고 위치/공간을 차지 하지 않게 함.
        employeeBinding.test1.visibility = View.GONE
        employeeBinding.test2.visibility = View.GONE

        get_data(value_1, value_2)

        // 리스트뷰의 특정 라인을 클릭하면 발생 하는 이벤트
        employeeBinding.listEmployee.setOnItemClickListener {parent, view, position, id ->
            // 리스트뷰의 선택 위치(라인?)를 D_employee에 대입하여 받음
            val selectItem = parent.getItemAtPosition(position) as D_employee

            employeeBinding.txtEmpEmpId.setText(selectItem.emp_id)
            employeeBinding.txtEmpEmpName.setText(selectItem.emp_name)
            employeeBinding.txtEmpDeptName.setText((selectItem.dept_name))

        }

        // 화면 아래 확인 버튼을 클릭하여 선택한 '사원번호', '사원명'을 원)액티비티인 W_work로 보냄
        employeeBinding.btnEmpConfirm.setOnClickListener        {
            val emp_id = employeeBinding.txtEmpEmpId.text.toString()
            val emp_name = employeeBinding.txtEmpEmpName.text.toString()
            val dept_name = employeeBinding.txtEmpDeptName.text.toString()

//            // 인텐트를 생성하고 값을 전달 함
//            val returnIntent = Intent()
//            returnIntent.putExtra("emp_id", emp_id)
//            returnIntent.putExtra("emp_name", emp_name)
//            returnIntent.putExtra("dept_name", dept_name)
//            setResult(RESULT_OK, returnIntent)
//            finish()

            val intent = Intent(this, W_work::class.java)
            intent.putExtra("emp_id", emp_id)
            intent.putExtra("emp_name", emp_name)
            intent.putExtra("dept_name", dept_name)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
   }
    private fun get_data(emp_id: String?, emp_name: String?) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.197:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val qrservice: S_employee = retrofit.create(S_employee::class.java)

        qrservice.requestEmpInfo(emp_id, emp_name).enqueue(object : Callback<List<D_employee>> { //받는 값이 Object 형식이면 Callback<D_production>
            override fun onFailure(call: Call<List<D_employee>>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@W_employee)
                dialog.setTitle("에러")
                dialog.setMessage(t.message)
                dialog.show()
            }

            override fun onResponse(call: Call<List<D_employee>>, response: Response<List<D_employee>>) { //받는 값이 Object 형식이면 Callback<D_production>
                if (response.isSuccessful && response.body() != null) {
                    val empList = response.body()!!
                    initializeViews(empList) // 리스트뷰에 데이터 설정
                } else {
                    // 오류 처리 코드
                    Log.d("Response", response.errorBody()?.string() ?: "No error body")
                    Toast.makeText(this@W_employee, "데이터 로드 실패", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initializeViews(result: List<D_employee>) {
        // List를 ArrayList로 변환
        val empArrayList = ArrayList(result)

        // ListView에 어댑터 설정
        val adapter = A_employee(this, empArrayList)
        employeeBinding.listEmployee.adapter = adapter
    }
}