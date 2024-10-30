package com.example.dooch_wms

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.dooch_wms.databinding.ActivityProductionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class W_production : ComponentActivity() {
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

        // W_work 에서 "생산오더 ID"를 전달 받는 변수 --> 추후 "생산오더" 검색에 사용(intent 사용)
        val order_id = intent.getStringExtra("order_id") //생산오더

        // db 조회를 위해 전달 받은 값을 잠시 저장 하는 장소로 사용
        productionBinding.txtProdTest1.setText(order_id)

        get_data(order_id)

        // 리스트뷰의 특정 라인을 클릭하면 발생 하는 이벤트
        productionBinding.listProdOrder.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // 리스트뷰의 선택 위치(라인?)를 D_employee에 대입하여 받음
                val selectItem = parent.getItemAtPosition(position) as D_production

                productionBinding.txtProdId.setText(selectItem.id)
                productionBinding.txtProdName.setText(selectItem.name)
                productionBinding.txtProdQty.setText((selectItem.qty))

            }

        // 화면 아래 확인 버튼을 클릭하여 선택한 '생산오더', '제품명', '수량' 을 원)액티비티인 W_work로 보냄
        productionBinding.btnProdConfirm.setOnClickListener {
            val prod_id = productionBinding.txtProdId.text.toString()
            val prod_name = productionBinding.txtProdName.text.toString()
            val prod_qty = productionBinding.txtProdQty.text.toString() + " EA"

            // 인텐트를 생성하고 값을 전달 함
            val returnIntent = Intent()
            returnIntent.putExtra("prod_id", prod_id)
            returnIntent.putExtra("prod_name", prod_name)
            returnIntent.putExtra("prod_qty", prod_qty)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

        private fun get_data(order_id: String?) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.197:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val qrservice: S_production = retrofit.create(S_production::class.java)

            qrservice.requestEmpInfo(order_id).enqueue(object : Callback<List<D_production>> {
                override fun onFailure(call: Call<List<D_production>>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@W_production)
                    dialog.setTitle("에러")
                    dialog.setMessage(t.message)
                    dialog.show()
                }

                override fun onResponse(call: Call<List<D_production>>, response: Response<List<D_production>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val empList = response.body()!!
                        initializeViews(empList) // 리스트뷰에 데이터 설정
                    } else {
                        // 오류 처리 코드
                        Log.d("Response", response.errorBody()?.string() ?: "No error body")
                        Toast.makeText(this@W_production, "데이터 로드 실패", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

        private fun initializeViews(result: List<D_production>) {
            // List를 ArrayList로 변환
            val empArrayList = ArrayList(result)

            // ListView에 어댑터 설정
            val adapter = A_employee(this, empArrayList)
            productionBinding.listProdOrder.adapter = adapter
        }

    }
}