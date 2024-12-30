package com.example.dooch_wms_mobile_v12

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface S_department {

    @FormUrlEncoded
    @POST("fetch_dept_info.php") // 웹서버의 메인 다음에 나오는 주소
    fun requestDeptInfo(
        @Field("use") use: String?
    ) : Call<List<D_department>>

}