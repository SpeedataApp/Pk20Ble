package com.amobletool.bluetooth.le.downexample.ui.show;

import android.util.Log;

import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.interfaces.PK20Service;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.amobletool.bluetooth.le.downexample.MyApp.cn;


public class ShowPresenter extends BasePresenterImpl<ShowContract.View> implements ShowContract.Presenter {
    public final String BASE_URL = "http://218.247.237.138:8078";

    //public final String BASE_URL = "http://192.168.155.2:8080";
    @Override
    public void upIntent(final List<Data> datas) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PK20Service p = retrofit.create(PK20Service.class);
        List<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            HashMap<String, String> paramsMap = new LinkedHashMap<>();
            String biaoshi = datas.get(i).getBiaoshi();
            if ("00".equals(biaoshi)) {
                paramsMap.put("Length", datas.get(i).getL().replace("毫米", ""));  //从集合取出数据，放入JSONObject里面 JSONObject对象和map差不多用法,以键和值形式存储数据
                paramsMap.put("Height", datas.get(i).getH().replace("毫米", ""));
                paramsMap.put("Width", datas.get(i).getW().replace("毫米", ""));
                paramsMap.put("longLength", "");
            } else {
                paramsMap.put("Length", "");  //从集合取出数据，放入JSONObject里面 JSONObject对象和map差不多用法,以键和值形式存储数据
                paramsMap.put("Height", "");
                paramsMap.put("Width", "");
                paramsMap.put("longLength", datas.get(i).getL().replace("毫米", ""));
            }
            paramsMap.put("BillNo", datas.get(i).getBarCode());
            paramsMap.put("IsOk", "1");
            paramsMap.put("Uid", datas.get(i).getBarCode());
            paramsMap.put("Weight", datas.get(i).getV());
            paramsMap.put("CreateTime", datas.get(i).getTime());
            paramsMap.put("address", datas.get(i).getWangDian());
            paramsMap.put("scanner", datas.get(i).getName());
            paramsMap.put("deviceCode", datas.get(i).getMac());
            list.add(paramsMap);
        }
        Gson gson = new Gson();
        String strEntity = gson.toJson(list);
//        String strEntity = "[{\"length\":\"960\",\"height\":\"982\",\"width\":\"965\",\"billNo\":\"201711275001\",\"isOk\":\"1\",\"uid\":\"201711275001\",\"weight\":\"0\"},{\"length\":\"990\",\"height\":\"1027\",\"width\":\"1026\",\"billNo\":\"201711275002\",\"isOk\":\"1\",\"uid\":\"201711275002\",\"weight\":\"\"}]";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Log.i("zm", "转换成json字符串: " + strEntity);
        Call<ResponseBody> insertbill = p.getInsertbill(requestBody);
        insertbill.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    Log.d("ZM", "onResponse: " + result);
                    if ("{\"Message\":\"Success\",\"Result\":0}".equals(result)) {
                        MyApp.getDaoInstant().getDataDao().deleteInTx(datas);
                        if (mView != null) {
                            if (cn) {
                                mView.showToast("上传成功！");
                            } else {
                                mView.showToast("Upload successful！");
                            }

                        }
                    } else {
                        mView.onlyShowToast(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("ZM", "onFailure: " + t.toString());
                if (mView != null) {
                    if (cn) {
                        mView.showToast("上传失败：" + t.toString());
                    } else {
                        mView.showToast("Fail to upload：" + t.toString());
                    }

                }
            }
        });
    }


    private String changeArrayDateToJson(Data datas) {  //把一个集合转换成json格式的字符串
        JSONObject object2 = new JSONObject();//一个user对象，使用一个JSONObject对象来装
        try {
            object2.put("length", datas.getL());  //从集合取出数据，放入JSONObject里面 JSONObject对象和map差不多用法,以键和值形式存储数据
            object2.put("width", datas.getW());
            object2.put("height", datas.getH());
            object2.put("billNo", datas.getBarCode());
            object2.put("isOk", "1");
            object2.put("uid", datas.getBarCode());
            object2.put("weight", datas.getG());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = object2.toString(); //把JSONObject转换成json格式的字符串
        Log.i("zm", "转换成json字符串: " + jsonString);
        return jsonString;
    }
}
