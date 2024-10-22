package com.example.dooch_wms

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.activity.ComponentActivity
import com.example.dooch_wms.databinding.ActivityProductionBinding

class W_production : ComponentActivity() {
    private lateinit var productionBinding: ActivityProductionBinding

    var EmpList = arrayListOf<D_production>(
        D_production("1000", "입형다단 펌프", "1"),
        D_production("1010", "회형다단 펌프", "2"),
        D_production("1020", "소방용 펌프", "3")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate(layoutInflater)로 초기화 한다
        productionBinding = ActivityProductionBinding.inflate(layoutInflater)

        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(productionBinding.root)

        // W_work 에서 "사번"과"이름을 전달 받는 변수 --> 추후 "사용자" 검색에 사용(intent 사용)
        val value_1 = intent.getStringExtra("emp_id") //사번
        val value_2 = intent.getStringExtra("emp_name") //이름

        // db 조회를 위해 전달 받은 값을 잠시 저장 하는 장소로 사용
        employeeBinding.test1.setText(value_1)
        employeeBinding.test2.setText(value_2)

        // 리스트뷰 아답터 생성
        val Adapter = A_employee(this, EmpList) // 아답터 클래스의 EmpList: ArrayList<D_employee> 형식을 따라감
        // 리스트뷰에 아답터 접목
        employeeBinding.listEmployee.adapter = Adapter

        // 리스트뷰의 특정 라인을 클릭하면 발생 하는 이벤트
        employeeBinding.listEmployee.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 리스트뷰의 선택 위치(라인?)를 D_employee에 대입하여 받음
            val selectItem = parent.getItemAtPosition(position) as D_employee

            employeeBinding.txtEmpEmpId.setText(selectItem.id)
            employeeBinding.txtEmpEmpName.setText(selectItem.name)
            employeeBinding.txtEmpDeptName.setText((selectItem.dept_name))

        }

        // 화면 아래 확인 버튼을 클릭하여 선택한 '사원번호', '사원명'을 원)액티비티인 W_work로 보냄
        employeeBinding.btnEmpConfirm.setOnClickListener {
            val emp_id = employeeBinding.txtEmpEmpId.text.toString()
            val emp_name = employeeBinding.txtEmpEmpName.text.toString()

            // 인텐트를 생성하고 값을 전달 함
            val returnIntent = Intent()
            returnIntent.putExtra("return_1", emp_id)
            returnIntent.putExtra("return_2", emp_name)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

    }
}