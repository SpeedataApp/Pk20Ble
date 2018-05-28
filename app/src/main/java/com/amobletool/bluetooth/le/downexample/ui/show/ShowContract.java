package com.amobletool.bluetooth.le.downexample.ui.show;

import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenter;
import com.amobletool.bluetooth.le.downexample.mvp.BaseView;

import java.util.List;


public class ShowContract {
    interface View extends BaseView {
        void showToast(String msg);
        void onlyShowToast(String msg);
    }

    interface  Presenter extends BasePresenter<View> {
        void upIntent(List<Data> datas);
    }
}
