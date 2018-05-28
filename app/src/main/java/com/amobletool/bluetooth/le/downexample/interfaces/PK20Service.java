package com.amobletool.bluetooth.le.downexample.interfaces;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by 张明_ on 2017/11/24.
 */

public interface PK20Service {
    //上传pk20数据
//    @FormUrlEncoded
//    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("pk20/dws/insertbills")
//    @POST("jeeplatform/dws/insertbills")
    Call<ResponseBody> getInsertbill(@Body RequestBody route);
}
