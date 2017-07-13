package com.amobletool.bluetooth.le.downexample.ui.menu;

import com.amobletool.bluetooth.le.downexample.mvp.BasePresenter;
import com.amobletool.bluetooth.le.downexample.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MenuContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        void toggleOnCheckedChangeListener(boolean isCheck);
    }
}
