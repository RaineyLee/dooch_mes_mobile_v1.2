package com.example.dooch_wms

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface S_employee {
    @FormUrlEncoded
    @POST("fetch_emp_info.php") // 웹서버의 메인 다음에 나오는 주소
    fun requestEmpInfo(
        @Field("emp_id") emp_id: String?,
        @Field("emp_name") emp_name: String?
//        @Field("code") code:String
    ) : Call<List<D_employee>> // 받아서 Login 클래스로 전달

//    @FormUrlEncoded
//    @POST("www/doo_item_info.php") // 웹서버의 메인 다음에 나오는 주소
//    fun requestItemLoc(
//        @Field("item_id") item_id: String?
////        @Field("item_loc") item_loc: String?
////        @Field("code") code:String
//    ) : Call<D_ItemData> // 받아서 Login 클래스로 전달
}