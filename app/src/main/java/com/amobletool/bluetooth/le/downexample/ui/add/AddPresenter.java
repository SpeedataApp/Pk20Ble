package com.amobletool.bluetooth.le.downexample.ui.add;

import android.text.TextUtils;
import android.widget.Spinner;

import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.LiuCheng;
import com.amobletool.bluetooth.le.downexample.bean.Word;
import com.amobletool.bluetooth.le.downexample.bean.WordDao;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AddPresenter extends BasePresenterImpl<AddContract.View> implements AddContract.Presenter {


    //字体转成代码
    @Override
    public String word2Id(String str) {
        if (TextUtils.isEmpty(str)) {
            return "000000000000";
        }
        StringBuffer result = new StringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            String substring = str.substring(i, i + 1);
            Word unique = MyApp.getDaoInstant().getWordDao().queryBuilder()
                    .where(WordDao.Properties.Word.eq(substring)).unique();
            String id = unique.getId();
            result.append(id);
        }
        int add0 = 6 - length;
        for (int i = 0; i < add0; i++) {
            result.append("00");
        }
        return String.valueOf(result);
    }

    //获取固定标识
    @Override
    public String getGuDingBiaoShi(boolean c1, boolean c2, boolean c3) {
        int result = 0;
        if (c1) {
            result = result + 4;
        }
        if (c2) {
            result = result + 2;
        }
        if (c3) {
            result = result + 1;
        }
        return "0" + result;
    }

    //获取任务的发送代码
    @Override
    public String getRenWuCode(List<Spinner> spinnerList) {
        StringBuffer renWu = new StringBuffer();
        StringBuffer noRenWu = new StringBuffer();
        StringBuffer renWuName = new StringBuffer();
        List<String> renWuList = new ArrayList<>();
        List<String> renWuNameList = new ArrayList<>();

        for (int i = 0; i < spinnerList.size(); i++) {
            String select = spinnerList.get(i).getSelectedItem().toString();
            if (TextUtils.isEmpty(select)) {
                noRenWu.append("00");
                continue;
            }
            String[] split = select.split(":");
            if (renWuList.size() == 0) {
                renWuList.add(split[0]);
                renWuNameList.add(split[1]);
            } else {
                int j;
                for (j = 0; j < renWuList.size(); j++) {
                    if (split[0].equals(renWuList.get(j))) {
                        noRenWu.append("00");
                        break;
                    }
                }
                if (j == renWuList.size()) {
                    renWuList.add(split[0]);
                    renWuNameList.add(split[1]);
                }
            }
        }
        int parseInt = 0, parseInt2 = 0;
        if (renWuList.size()==0){
            return null;
        }
        for (int i = 0; i < renWuList.size(); i++) {
            renWu.append(renWuList.get(i));
            if (i == 0) {
                parseInt = Integer.parseInt(renWuList.get(i), 16);
            }
            if (i == renWuList.size() - 1) {
                parseInt2 = Integer.parseInt(renWuList.get(i), 16);
            }
        }
        for (int i = 0; i < renWuNameList.size(); i++) {
            if (i != renWuNameList.size() - 1) {
                renWuName.append(renWuNameList.get(i) + "\n");
            } else {
                renWuName.append(renWuNameList.get(i));
            }
        }
        int jiaoYan = parseInt + parseInt2;
        String jiaoYanStr = Integer.toHexString(jiaoYan);
        if (jiaoYanStr.length()==1){
            jiaoYanStr="0"+jiaoYanStr;
        }
        int length = renWuList.size() + 1;
        return renWu + "-" + noRenWu + "-0" + length + "-" + jiaoYanStr+"-"+renWuName;
    }

    //保存流程到pda数据库
    @Override
    public int saveLiuCheng(String id,String code,String name,String renWuCode,String renWuName) {
        try {
            LiuCheng liuCheng=new LiuCheng();
            liuCheng.setId(id);
            liuCheng.setCode(code);
            liuCheng.setName(name);
            liuCheng.setRenWuCode(renWuCode);
            liuCheng.setRenWuName(renWuName);
            MyApp.getDaoInstant().getLiuChengDao().insertOrReplace(liuCheng);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
