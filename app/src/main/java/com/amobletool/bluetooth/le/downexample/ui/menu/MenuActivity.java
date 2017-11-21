package com.amobletool.bluetooth.le.downexample.ui.menu;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseActivity;
import com.amobletool.bluetooth.le.downexample.ui.add.AddActivity;
import com.amobletool.bluetooth.le.downexample.ui.assign.AssignFragment;
import com.amobletool.bluetooth.le.downexample.ui.set.SetFragment;
import com.amobletool.bluetooth.le.downexample.ui.show.ShowFragment;

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
    private TextView show;
    private String whichFragment = "";
    private TextView set;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        EventBus.getDefault().register(this);
        initView();
        openFragment(new ShowFragment());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(whichFragment)) {
            closeFragment();
            if ("show".equals(whichFragment)) {
                openFragment(new ShowFragment());
            } else if ("assign".equals(whichFragment)) {
                openFragment(new AssignFragment());
            } else {
                openFragment(new SetFragment());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(MsgEvent msgEvent) {
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("ServiceConnectedStatus".equals(type)) {
            boolean result = (boolean) msg;
            Log.d("ZM_connect", "First:" + result);

            if (result) {
                ll.setVisibility(View.VISIBLE);
                Log.d("ZM_connect", "显示连接按键");
                device_address.setText("Address：" + MyApp.address);
                device_name.setText("Name：" + MyApp.name);
            } else {
                ll.setVisibility(View.GONE);
                Log.d("ZM_connect", "隐藏连接按键");
            }
            btn_serviceStatus.setChecked(result);
            Log.d("ZM_connect", "" + result);

        } else if ("Notification".equals(type)) {
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("Save6Data".equals(type)) {
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("Save6DataSuccess".equals(type)) {
            MyApp.getInstance().writeCharacteristic6("AA0A020100000000000000000000000000000200");
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        }
//        else {
//            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
//        }

    }


    private void initView() {
        device_name = (TextView) findViewById(R.id.device_name);
        device_address = (TextView) findViewById(R.id.device_address);
        btn_serviceStatus = (ToggleButton) findViewById(R.id.btn_serviceStatus);
        frame_main = (FrameLayout) findViewById(R.id.frame_main);

        btn_serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MyApp.getInstance().connect();
                    Log.d("ZM_connect", "点击了连接");
                } else {
                    MyApp.getInstance().disconnect();
                    EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", false));
                    Log.d("ZM_connect", "点击了断开");
                }
            }
        });

        ll = (LinearLayout) findViewById(R.id.ll);
        add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(this);
        start = (TextView) findViewById(R.id.start);
        start.setOnClickListener(this);
        show = (TextView) findViewById(R.id.show);
        show.setOnClickListener(this);
        set = (TextView) findViewById(R.id.set);
        set.setOnClickListener(this);
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
            case R.id.show:
                changeShowImage();
                break;
            case R.id.set:
                changeSetImage();
                break;
        }
    }


    private void changeShowImage() {
        Drawable showBlue = getResources().getDrawable(R.drawable.show);
        showBlue.setBounds(0, 0, showBlue.getMinimumWidth(), showBlue.getMinimumHeight());
        show.setCompoundDrawables(null, showBlue, null, null);
        Drawable startBlack = getResources().getDrawable(R.drawable.start_black);
        startBlack.setBounds(0, 0, startBlack.getMinimumWidth(), startBlack.getMinimumHeight());
        start.setCompoundDrawables(null, startBlack, null, null);
        Drawable setBlack = getResources().getDrawable(R.drawable.set_black);
        setBlack.setBounds(0, 0, setBlack.getMinimumWidth(), setBlack.getMinimumHeight());
        set.setCompoundDrawables(null, setBlack, null, null);

        closeFragment();
        openFragment(new ShowFragment());
        whichFragment = "show";
    }

    private void changeStartImage() {
        Drawable showBlack = getResources().getDrawable(R.drawable.show_black);
        showBlack.setBounds(0, 0, showBlack.getMinimumWidth(), showBlack.getMinimumHeight());
        show.setCompoundDrawables(null, showBlack, null, null);
        Drawable startBlue = getResources().getDrawable(R.drawable.start);
        startBlue.setBounds(0, 0, startBlue.getMinimumWidth(), startBlue.getMinimumHeight());
        start.setCompoundDrawables(null, startBlue, null, null);
        Drawable setBlack = getResources().getDrawable(R.drawable.set_black);
        setBlack.setBounds(0, 0, setBlack.getMinimumWidth(), setBlack.getMinimumHeight());
        set.setCompoundDrawables(null, setBlack, null, null);

        closeFragment();
        openFragment(new AssignFragment());
        whichFragment = "assign";
    }

    private void changeAddImage() {
//        Drawable addBlue = getResources().getDrawable(R.drawable.add);
//        addBlue.setBounds(0, 0, addBlue.getMinimumWidth(), addBlue.getMinimumHeight());
//        add.setCompoundDrawables(null, addBlue, null, null);
//        Drawable startBlack = getResources().getDrawable(R.drawable.start_black);
//        startBlack.setBounds(0, 0, startBlack.getMinimumWidth(), startBlack.getMinimumHeight());
//        start.setCompoundDrawables(null, startBlack, null, null);

//        closeFragment();
        openAct(this, AddActivity.class);
    }

    private void changeSetImage() {
        Drawable showBlack = getResources().getDrawable(R.drawable.show_black);
        showBlack.setBounds(0, 0, showBlack.getMinimumWidth(), showBlack.getMinimumHeight());
        show.setCompoundDrawables(null, showBlack, null, null);
        Drawable startBlack = getResources().getDrawable(R.drawable.start_black);
        startBlack.setBounds(0, 0, startBlack.getMinimumWidth(), startBlack.getMinimumHeight());
        start.setCompoundDrawables(null, startBlack, null, null);
        Drawable setBlue = getResources().getDrawable(R.drawable.set);
        setBlue.setBounds(0, 0, setBlue.getMinimumWidth(), setBlue.getMinimumHeight());
        set.setCompoundDrawables(null, setBlue, null, null);

        closeFragment();
        openFragment(new SetFragment());
        whichFragment = "set";
    }

    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    Toast.makeText(MenuActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
