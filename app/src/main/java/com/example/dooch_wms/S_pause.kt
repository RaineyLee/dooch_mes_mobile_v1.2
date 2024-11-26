package com.example.dooch_wms

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface S_pause {

    @FormUrlEncoded
    @POST("input_stop_reason.php") // 웹서버의 메인 다음에 나오는 주소
    fun inputstopreason(
        @Field("order_id") order_id: String?,
        @Field("item_id") item_id: String?,
        @Field("item_name") item_name: String?,
        @Field("batch_id") batch_id: String?,
        @Field("stop_reason") stop_reason: String?
    ) : Call<D_msg> // 받아서 Message 클래스로 전달

}