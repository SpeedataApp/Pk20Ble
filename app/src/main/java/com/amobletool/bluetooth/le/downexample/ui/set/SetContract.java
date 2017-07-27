package com.amobletool.bluetooth.le.downexample.ui.set;

import android.app.Activity;

import com.amobletool.bluetooth.le.downexample.mvp.BasePresenter;
import com.amobletool.bluetooth.le.downexample.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SetContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        void setTime();
        void setBili(String s);
        void setZiKu(Activity activity);
    }
}
