package com.amobletool.bluetooth.le.downexample.ui.main;


import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MainFragment extends MVPBaseFragment<MainContract.View, MainPresenter> implements MainContract.View {

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }
}
