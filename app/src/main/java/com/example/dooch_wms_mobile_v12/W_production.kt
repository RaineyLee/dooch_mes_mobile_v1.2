package com.example.dooch_wms_mobile_v12

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms_mobile_v12.databinding.ActivityProductionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class W_production : AppCompatActivity() {
    private lateinit var productionBinding: ActivityProductionBinding

    // Intent로 주고 받는 변수 선언
    var dept_id : String? = ""
    var dept_name : String? = ""

    // DB 조회를 위한 변수 설정
    var r_dept_id : String? = ""
    var r_dept_name : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate(layoutInflater)로 초기화 한다
        productionBinding = ActivityProductionBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(productionBinding.root)

        // W_work 에서 전달 받는 변수
        // "생산오더" 검색에 사용(intent 사용)
        dept_id = intent.getStringExtra("dept_id")
        dept_name = intent.getStringExtra("dept_name")

        // Spinner 설정
        // Spinner 입력값 가져 오기
        val dept_list = resources.getStringArray(R.array.teams)
        val status_list = resources.getStringArray(R.array.status)

        // Spinner 어댑터 설정 & Spinner에 adapter 적용
        val dept_adapter = ArrayAdapter(this, R.layout.spinner_item, dept_list)
        dept_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        val status_adapter = ArrayAdapter(this, R.layout.spinner_item, status_list)
        status_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        productionBinding.prodDeptSpinner.adapter = dept_adapter
        productionBinding.prodStausSpinner.adapter = status_adapter

        // Intent로 받은 값을 Spinner에 적용(초기에 적용. 수정은 가능)
        val position = dept_list.indexOf(dept_name)
        if(position != -1){
            productionBinding.prodDeptSpinner.setSelection(position)
        }else{
            Toast.makeText(this, "해당값이 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // Dept 스피너 클릭시 이벤트 설정
        productionBinding.prodDeptSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                r_dept_name = parent?.getItemAtPosition(position).toString()

                when (r_dept_name){
                    "생산팀-1파트" -> r_dept_id = 1100.toString()
                    "생산팀-2파트" -> r_dept_id = 1200.toString()
                    "생산팀-3파트" -> r_dept_id = 1300.toString()
                    "생산팀-4파트" -> r_dept_id = 1400.toString()
                    else -> r_dept_id = 1410.toString()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Status 스피너 클릭시 이벤트 설정
        productionBinding.prodStausSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val status = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }



        // 리스트뷰의 특정 라인을 클릭하면 발생 하는 이벤트
        productionBinding.listProdOrder.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // 리스트뷰의 선택 위치(라인?)를 D_employee에 대입하여 받음
                val selectItem = parent.getItemAtPosition(position) as D_production
                productionBinding.txtProdOrderId.setText(selectItem.order_id)
                productionBinding.txtProdId.setText(selectItem.item_id)
                productionBinding.txtProdName.setText(selectItem.item_name)
                productionBinding.txtProdQty.setText(selectItem.item_qty)
                productionBinding.txtProdStatus.setText(selectItem.status)
            }


        productionBinding.prodBtnSearch.setOnClickListener {
            var status: String = productionBinding.prodStausSpinner.selectedItem.toString()

            if(r_dept_id == "부서선택"){
                r_dept_id = "%%"
            }else{
                r_dept_id
            }

            if(status == "상태선택"){
                status = "%%"
            }else{
                status
            }

            selectProdOrder(r_dept_id.toString(), status)
            Log.d("전달값", dept_id + status)
        }

        // 화면 아래 확인 버튼을 클릭하여 선택한 '생산오더', '제품명', '수량' 을 원)액티비티인 W_work로 보냄
        productionBinding.btnProdConfirm.setOnClickListener {
            val order_id = productionBinding.txtProdOrderId.text.toString()
            val prod_id = productionBinding.txtProdId.text.toString()
            val prod_name = productionBinding.txtProdName.text.toString()
            val prod_qty = productionBinding.txtProdQty.text.toString() + " 개"
            val status = productionBinding.txtProdStatus.text.toString()


            // 인텐트를 생성하고 값을 전달 함
            val intent = Intent(this, W_work::class.java)
            //생산오더를 조회 하고 값을 W_work 화면에 전달하는 변수
            intent.putExtra("order_id", order_id)
            intent.putExtra("prod_id", prod_id)
            intent.putExtra("prod_name", prod_name)
            intent.putExtra("prod_qty", prod_qty)
            intent.putExtra("status", status)
            //W_work 화면에서 전달 받은 값을 돌려주는 변수
            intent.putExtra("dept_id", dept_id)
            intent.putExtra("dept_name", dept_name)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
//
//            val returnIntent = Intent()
//            returnIntent.putExtra("order_id", order_id)
//            returnIntent.putExtra("prod_id", prod_id)
//            returnIntent.putExtra("prod_name", prod_name)
//            returnIntent.putExtra("prod_qty", prod_qty)
//            setResult(RESULT_OK, returnIntent)
//            finish()
        }
    }
        private fun selectProdOrder(dept_id: String, status: String){
            // 레트로핏 설정
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.197:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val prodservice: S_production = retrofit.create(S_production::class.java)

            prodservice.selectProdInfo(dept_id, status).enqueue(object : Callback<List<D_production>> { //받는 값이 List 형식이면 Callback<List<D_production>>
                override fun onFailure(call: Call<List<D_production>>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@W_production)
                    dialog.setTitle("결과값 오류")
                    dialog.setMessage(t.message)
                    dialog.show()
                }
                override fun onResponse(call: Call<List<D_production>>, response: Response<List<D_production>>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
                    if (response.isSuccessful) {
                        val prodInfo = response.body()!!
                        initializeViews_2(prodInfo)
                    } else {
                        Toast.makeText(this@W_production,"해당값이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

    private fun initializeViews_2(result: List<D_production>) {
        // List를 ArrayList로 변환
        val prodArrayList = ArrayList(result)

        // ListView에 어댑터 설정
        val adapter = A_production(this, prodArrayList)
        productionBinding.listProdOrder.adapter = adapter
    }


        private fun initializeViews(result: List<D_production>) {
            // List를 ArrayList로 변환
            val empArrayList = ArrayList(result)

            // ListView에 어댑터 설정
            val adapter = A_production(this, empArrayList)
            productionBinding.listProdOrder.adapter = adapter
        }

    }