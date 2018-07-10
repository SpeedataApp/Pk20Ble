package com.amobletool.bluetooth.le.downexample.ui.menu;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseActivity;
import com.amobletool.bluetooth.le.downexample.ui.add.AddActivity;
import com.amobletool.bluetooth.le.downexample.ui.assign.AssignFragment;
import com.amobletool.bluetooth.le.downexample.ui.set.SetFragment;
import com.amobletool.bluetooth.le.downexample.ui.show.ShowFragment;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import speedata.com.blelib.bean.LWHData;


public class MenuActivity extends MVPBaseActivity<MenuContract.View, MenuPresenter> implements MenuContract.View, View.OnClickListener {

    private TextView device_name;
    private TextView device_address;
    //    private ToggleButton btn_serviceStatus;
    private LinearLayout ivOn;
    private FrameLayout frame_main;
    private LinearLayout ll;
    private TextView add;
    private TextView start;
    private TextView show;
    private String whichFragment = "";
    private TextView set;
    private TextView mTvL;
    private TextView mTvW;
    private TextView mTvH;
    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        EventBus.getDefault().register(this);
        permission();

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

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
                ivOn.setVisibility(View.VISIBLE);
            } else {
                ll.setVisibility(View.GONE);
                Log.d("ZM_connect", "隐藏连接按键");
                ivOn.setVisibility(View.GONE);
            }
//            btn_serviceStatus.setChecked(result);
            Log.d("ZM_connect", "" + result);

        } else if ("Notification".equals(type)) {
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("Save6Data".equals(type)) {
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("Save6DataSuccess".equals(type)) {
            MyApp.getInstance().writeCharacteristic6("AA0A020100000000000000000000000000000200");
            Log.d("ZM", "接收完成: " + System.currentTimeMillis());
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("LWHData".equals(type)) {
            LWHData lwhData = (LWHData) msg;
            mTvH.setText("H:" + lwhData.H);
            mTvL.setText("L:" + lwhData.L);
            mTvW.setText("W:" + lwhData.W);
        } else if ("KP".equals(type)) {
            boolean isShow = (boolean) msg;
            if (isShow) {
                kProgressHUD.show();
            } else {
                kProgressHUD.dismiss();
            }

        }

    }


    private void initView() {
        device_name = (TextView) findViewById(R.id.device_name);
        device_address = (TextView) findViewById(R.id.device_address);
//        btn_serviceStatus = (ToggleButton) findViewById(btn_serviceStatus);
        frame_main = (FrameLayout) findViewById(R.id.frame_main);
        ivOn = (LinearLayout) findViewById(R.id.iv_on);
        ivOn.setOnClickListener(this);

//        btn_serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    MyApp.getInstance().connect();
//                    Log.d("ZM_connect", "点击了连接");
//                } else {
//                    MyApp.getInstance().wantDisconnectBle();
//                    MyApp.getInstance().disconnect();
//                    ll.setVisibility(View.GONE);
//                    Log.d("ZM_connect", "点击了断开");
//                }
//            }
//        });

        ll = (LinearLayout) findViewById(R.id.ll);
        add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(this);
        start = (TextView) findViewById(R.id.start);
        start.setOnClickListener(this);
        show = (TextView) findViewById(R.id.show);
        show.setOnClickListener(this);
        set = (TextView) findViewById(R.id.set);
        set.setOnClickListener(this);


        mTvL = (TextView) findViewById(R.id.tv_l);
        mTvW = (TextView) findViewById(R.id.tv_w);
        mTvH = (TextView) findViewById(R.id.tv_h);


        boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
        if (cn) {
            kProgressHUD = KProgressHUD.create(getApplicationContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("重连中...")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
        } else {
            kProgressHUD = KProgressHUD.create(getApplicationContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Reconnection...")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
        }
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
            case R.id.iv_on:
                closeBle();
                break;
        }
    }

    private void closeBle() {
        MyApp.getInstance().wantDisconnectBle();
        MyApp.getInstance().disconnect();
        ll.setVisibility(View.GONE);
        Log.d("ZM_connect", "点击了断开");
        ivOn.setVisibility(View.GONE);
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
                    boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
                    if (cn) {
                        Toast.makeText(getApplicationContext(), "再次点击返回退出", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Press the exit again", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        closeBle();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void permission() {
        AndPermission.with(MenuActivity.this)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.BLUETOOTH
                        , Manifest.permission.BLUETOOTH_ADMIN
                        , Manifest.permission.INTERNET)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MenuActivity.this, rationale).show();
                    }
                }).start();
    }

    PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MenuActivity.this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(MenuActivity.this, 300).show();
            }
        }
    };
}
