package com.amobletool.bluetooth.le.downexample.ui.add;

import android.text.TextUtils;

import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.Word;
import com.amobletool.bluetooth.le.downexample.bean.WordDao;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class AddPresenter extends BasePresenterImpl<AddContract.View> implements AddContract.Presenter{


    //字体转成代码
    @Override
    public String word2Id(String str) {
        if (TextUtils.isEmpty(str)){
            return "000000000000";
        }
        StringBuffer result=new StringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            String substring = str.substring(i, i + 1);
            Word unique = MyApp.getDaoInstant().getWordDao().queryBuilder()
                    .where(WordDao.Properties.Word.eq(substring)).unique();
            String id = unique.getId();
            result.append(id);
        }
        int add0=6-length;
        for (int i = 0; i < add0; i++) {
            result.append("00");
        }
        return String.valueOf(result);
    }

    //获取固定标识
    @Override
    public String getGuDingBiaoShi(boolean c1, boolean c2, boolean c3) {
        int result=0;
        if (c1){
            result=result+4;
        }
        if (c2){
            result=result+2;
        }
        if (c3){
            result=result+1;
        }
        return "0"+result;
    }
}
