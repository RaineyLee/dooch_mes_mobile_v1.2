package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms.databinding.ActivityEmployeeBinding
import com.example.dooch_wms.databinding.ActivityMainBinding

class W_employee : ComponentActivity() {
    private lateinit var employeeBinding: ActivityEmployeeBinding

    var EmpList = arrayListOf<D_employee>(
        D_employee("1000", "홍길동"),
        D_employee("1010", "이순신"),
        D_employee("1020", "세종대왕")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate(layoutInflater)로 초기화 한다
        employeeBinding = ActivityEmployeeBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(employeeBinding.root)
        // W_work 에서 "사번"과"이름을 전달 받는 변수 --> 추후 "사용자" 검색에 사용
        val value_1 = intent.getStringExtra("key_1") //사번
        val value_2 = intent.getStringExtra("key_2") //이름

        employeeBinding.test1.setText(value_1)
        employeeBinding.test2.setText(value_2)

        val Adapter = A_employee(this,EmpList)
        employeeBinding.listEmployee.adapter = Adapter

        employeeBinding.listEmployee.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val selectItem = parent.getItemAtPosition(position) as D_employee

            employeeBinding.txtEmpEmpId.setText(selectItem.id)
            employeeBinding.txtEmpEmpName.setText(selectItem.name)

        }

    }
}