package com.amobletool.bluetooth.le.downexample.ui.menu;

import android.util.Log;

import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;

import org.greenrobot.eventbus.EventBus;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MenuPresenter extends BasePresenterImpl<MenuContract.View> implements MenuContract.Presenter{

    @Override
    public void toggleOnCheckedChangeListener(boolean isCheck) {

    }
}
