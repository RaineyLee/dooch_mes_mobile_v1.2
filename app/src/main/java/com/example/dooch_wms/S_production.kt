package com.example.dooch_wms

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface S_production {
    @FormUrlEncoded
    @POST("fetch_production_info.php") // 웹서버의 메인 다음에 나오는 주소
    fun requestProdInfo(
        @Field("order_id") order_id: String?
//        @Field("code") code:String
    ) : Call<D_production> // 받아서 Login 클래스로 전달

    @FormUrlEncoded
    @POST("start_production_info.php") // 웹서버의 메인 다음에 나오는 주소
    fun startProdInfo(
        @Field("order_id") order_id: String?,
        @Field("emp_id") emp_id: String?,
        @Field("emp_name") emp_name: String?,
        @Field("status") status: String?,
        @Field("s_time") s_time: String?,
        @Field("c_time") c_time: String?
    ) : Call<D_msg> // 받아서 Message 클래스로 전달

    @FormUrlEncoded
    @POST("end_production_info.php") // 웹서버의 메인 다음에 나오는 주소
    fun endProdInfo(
        @Field("order_id") order_id: String?,
        @Field("end_time") end_time: String?,
        @Field("working_time") working_time: String?,
        @Field("pause_time") pause_time: String?,
        @Field("status") status: String?
    ) : Call<D_msg> // 받아서 Message 클래스로 전달

    @FormUrlEncoded
    @POST("pause_production_info.php") // 웹서버의 메인 다음에 나오는 주소
    fun pauseProdInfo(
        @Field("order_id") order_id: String?,
        @Field("working_time") working_time: String?,
        @Field("status") status: String?
    ) : Call<D_msg> // 받아서 Message 클래스로 전달
}