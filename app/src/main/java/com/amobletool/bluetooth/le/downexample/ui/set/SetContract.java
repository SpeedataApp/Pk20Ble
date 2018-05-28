package com.amobletool.bluetooth.le.downexample.ui.set;

import android.app.Activity;
import android.app.ProgressDialog;

import com.amobletool.bluetooth.le.downexample.mvp.BasePresenter;
import com.amobletool.bluetooth.le.downexample.mvp.BaseView;


public class SetContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        void setTime();
        void setBili(String s);
        void setZiKu(Activity activity,ProgressDialog progressDialog);
        void setClean();
        void setLogo(Activity activity,ProgressDialog progressDialog,String logo);
        void setWorkerName(Activity activity,ProgressDialog progressDialog,String name);
        void setCleanFlash();
        void setLeastBili(String s);
    }
}
