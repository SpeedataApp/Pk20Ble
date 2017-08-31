package com.amobletool.bluetooth.le.downexample.ui.set;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SetFragment extends MVPBaseFragment<SetContract.View, SetPresenter>
        implements SetContract.View, View.OnClickListener {

    private Button btn_setTime;
    private EditText et_bili;
    private Button btn_bili;
    private Button btn_ziku;
    private Button btn_clean;
    private Button btn_yousu;
    private ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.fragment_set;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        initView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventMain(MsgEvent msgEvent){
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("ServiceConnectedStatus".equals(type)) {
            boolean result = (boolean) msg;
            if (!result){
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        btn_setTime = (Button) view.findViewById(R.id.btn_setTime);
        btn_setTime.setOnClickListener(this);
        et_bili = (EditText) view.findViewById(R.id.et_bili);
        btn_bili = (Button) view.findViewById(R.id.btn_bili);
        btn_bili.setOnClickListener(this);
        btn_ziku = (Button) view.findViewById(R.id.btn_ziku);
        btn_ziku.setOnClickListener(this);
        btn_clean = (Button) view.findViewById(R.id.btn_clean);
        btn_clean.setOnClickListener(this);
        btn_yousu = (Button) view.findViewById(R.id.btn_yousu);
        btn_yousu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setTime:
                mPresenter.setTime();
                break;
            case R.id.btn_bili:
                mPresenter.setBili(et_bili.getText().toString());
                break;
            case R.id.btn_ziku:
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("设置字库中...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                mPresenter.setZiKu(getActivity(), progressDialog);
                break;
            case R.id.btn_clean:
                mPresenter.setClean();
                break;
            case R.id.btn_yousu:
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("设置新公司名称中...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                mPresenter.setYousu(getActivity(),progressDialog);
                break;
        }
    }

}
