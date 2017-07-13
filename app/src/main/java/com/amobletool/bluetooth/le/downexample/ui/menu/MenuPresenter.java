package com.amobletool.bluetooth.le.downexample.ui.menu;

import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MenuPresenter extends BasePresenterImpl<MenuContract.View> implements MenuContract.Presenter{

    @Override
    public void toggleOnCheckedChangeListener(boolean isCheck) {
        if (isCheck){
            MyApp.getInstance().connect();
        }else {
            MyApp.getInstance().disconnect();
        }
    }
}
