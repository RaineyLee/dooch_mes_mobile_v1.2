package com.example.dooch_wms_mobile_v12

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.dooch_wms_mobile_v12.databinding.ActivityProductionBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class W_production : AppCompatActivity() {
    private lateinit var productionBinding: ActivityProductionBinding

//    var ProdList = arrayListOf<D_production>(
//        D_production("1000", "입형다단 펌프", "1"),
//        D_production("1010", "회형다단 펌프", "2"),
//        D_production("1020", "소방용 펌프", "3")
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate(layoutInflater)로 초기화 한다
        productionBinding = ActivityProductionBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(productionBinding.root)

        // W_work 에서 전달 받는 변수
        // "생산오더" 검색에 사용(intent 사용)
        val order_id = intent.getStringExtra("order_id") //생산오더
        // 사원번호, 사원명을 돌려 주기위해 받는 변수
        val emp_id = intent.getStringExtra("emp_id")
        val emp_name = intent.getStringExtra("emp_name")

        productionBinding.txtProdEmpId.text = emp_id.toString()
        productionBinding.txtProdEmpName.text = emp_name.toString()

        // 20241006 화면변경으로 인해 사용 안 함.
//        // db 조회를 위해 전달 받은 값을 잠시 저장 하는 장소로 사용
//        productionBinding.txtProdTest1.setText(order_id)

        get_data("29309")

        // 리스트뷰의 특정 라인을 클릭하면 발생 하는 이벤트
        productionBinding.listProdOrder.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // 리스트뷰의 선택 위치(라인?)를 D_employee에 대입하여 받음
                val selectItem = parent.getItemAtPosition(position) as D_production
                productionBinding.txtProdOrderId.setText(selectItem.order_id)
                productionBinding.txtProdId.setText(selectItem.item_id)
                productionBinding.txtProdName.setText(selectItem.item_name)
                productionBinding.txtProdQty.setText(selectItem.item_qty)

            }

        // 화면 아래 확인 버튼을 클릭하여 선택한 '생산오더', '제품명', '수량' 을 원)액티비티인 W_work로 보냄
        productionBinding.btnProdConfirm.setOnClickListener {
            val order_id = productionBinding.txtProdOrderId.text.toString()
            val prod_id = productionBinding.txtProdId.text.toString()
            val prod_name = productionBinding.txtProdName.text.toString()
            val prod_qty = productionBinding.txtProdQty.text.toString()

            val r_emp_id = productionBinding.txtProdEmpId.text.toString()
            val r_emp_name = productionBinding.txtProdEmpName.text.toString()

            // 인텐트를 생성하고 값을 전달 함
            val intent = Intent(this, W_work::class.java)
            //생산오더를 조회 하고 값을 W_work 화면에 전달하는 변수
            intent.putExtra("order_id", order_id)
            intent.putExtra("prod_id", prod_id)
            intent.putExtra("prod_name", prod_name)
            intent.putExtra("prod_qty", prod_qty)
            //W_work 화면에서 전달 받은 값을 돌려주는 변수
            intent.putExtra("r_emp_id", r_emp_id)
            intent.putExtra("r_emp_name", r_emp_name)
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

        private fun get_data(order_id: String?) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.197:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

//            val prodservice: S_production = retrofit.create(S_production::class.java)
//
//            prodservice.requestProdInfo(order_id).enqueue(object : Callback<D_production> { //받는 값이 List 형식이면 Callback<List<D_production>>
//                override fun onFailure(call: Call<D_production>, t: Throwable) {
//                    val dialog = AlertDialog.Builder(this@W_work)
//                    dialog.setTitle("에러")
//                    dialog.setMessage(t.message)
//                    dialog.show()
//                }
//                override fun onResponse(call: Call<D_production>, response: Response<D_production>) {  //받는 값이 List 형식이면 Callback<List<D_production>>
//                    if (response.isSuccessful && response.body() != null) {
//                        val prodList = response.body()!!
//                        // body로 받은 값을 할당 하는 코드
//                        workBinding.txtWorkOrderId.setText(prodList.order_id.toString())
//                        workBinding.txtWorkProdId.text = prodList.item_id.toString()
//                        workBinding.txtWorkProdName.text = prodList.item_name.toString()
//                        workBinding.txtWorkProdQty.text = prodList.item_qty.toString()
//                    } else {
//                        // 오류 처리 코드
//                        Log.d("Response", response.errorBody()?.string() ?: "No error body")
//                        Toast.makeText(this@W_work, "유효한 생산오더가 아닙니다. 생산오더를 다시 확인해 주세요.", Toast.LENGTH_LONG).show()
//                    }
//                }
//            })
        }

        private fun initializeViews(result: List<D_production>) {
            // List를 ArrayList로 변환
            val empArrayList = ArrayList(result)

            // ListView에 어댑터 설정
            val adapter = A_production(this, empArrayList)
            productionBinding.listProdOrder.adapter = adapter
        }

    }