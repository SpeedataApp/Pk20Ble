package com.amobletool.bluetooth.le.downexample.ui.menu;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.MsgEvent;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseActivity;
import com.amobletool.bluetooth.le.downexample.ui.add.AddActivity;
import com.amobletool.bluetooth.le.downexample.ui.main.MainFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MenuActivity extends MVPBaseActivity<MenuContract.View, MenuPresenter> implements MenuContract.View, View.OnClickListener {

    private TextView device_name;
    private TextView device_address;
    private ToggleButton btn_serviceStatus;
    private FrameLayout frame_main;
    private LinearLayout ll;
    private TextView add;
    private TextView start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        EventBus.getDefault().register(this);
        initView();
        openFragment(new MainFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(MsgEvent msgEvent) {
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("ServiceConnectedStatus".equals(type)) {
            boolean result = (boolean) msg;
            btn_serviceStatus.setChecked(result);
            if (result) {
                ll.setVisibility(View.VISIBLE);
                device_address.setText("Address：" + MyApp.address);
                device_name.setText("Name：" + MyApp.name);
            } else {
                ll.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        }

    }


    private void initView() {
        device_name = (TextView) findViewById(R.id.device_name);
        device_address = (TextView) findViewById(R.id.device_address);
        btn_serviceStatus = (ToggleButton) findViewById(R.id.btn_serviceStatus);
        frame_main = (FrameLayout) findViewById(R.id.frame_main);

        btn_serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.toggleOnCheckedChangeListener(isChecked);
            }
        });

        ll = (LinearLayout) findViewById(R.id.ll);
        add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(this);
        start = (TextView) findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                changeAddImage();
                break;
            case R.id.start:
                changeStartImage();
                break;
        }
    }

    private void changeStartImage() {
        Drawable addBlack = getResources().getDrawable(R.drawable.add_black);
        addBlack.setBounds(0, 0, addBlack.getMinimumWidth(), addBlack.getMinimumHeight());
        add.setCompoundDrawables(null, addBlack, null, null);
        Drawable startBlue = getResources().getDrawable(R.drawable.start);
        startBlue.setBounds(0, 0, startBlue.getMinimumWidth(), startBlue.getMinimumHeight());
        start.setCompoundDrawables(null, startBlue, null, null);
    }

    private void changeAddImage() {
        Drawable addBlue = getResources().getDrawable(R.drawable.add);
        addBlue.setBounds(0, 0, addBlue.getMinimumWidth(), addBlue.getMinimumHeight());
        add.setCompoundDrawables(null, addBlue, null, null);
        Drawable startBlack = getResources().getDrawable(R.drawable.start_black);
        startBlack.setBounds(0, 0, startBlack.getMinimumWidth(), startBlack.getMinimumHeight());
        start.setCompoundDrawables(null, startBlack, null, null);

        openAct(this, AddActivity.class);
    }
}