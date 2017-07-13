package com.amobletool.bluetooth.le.downexample.ui.add;

import android.widget.Spinner;

import com.amobletool.bluetooth.le.downexample.mvp.BasePresenter;
import com.amobletool.bluetooth.le.downexample.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class AddContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        String word2Id(String str);
        String getGuDingBiaoShi(boolean c1,boolean c2,boolean c3);
        String getRenWuCode(List<Spinner> spinnerList);
    }
}
