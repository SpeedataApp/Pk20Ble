package com.amobletool.bluetooth.le.downexample.ui.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseActivity;
import com.amobletool.bluetooth.le.downexample.ui.air.AirActivity;
import com.amobletool.bluetooth.le.downexample.ui.allocate.AllocateActivity;
import com.amobletool.bluetooth.le.downexample.ui.branch.BranchActivity;
import com.amobletool.bluetooth.le.downexample.ui.courier.CourierActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                openAct(this, CourierActivity.class);
                break;
            case R.id.btn2:
                openAct(this, BranchActivity.class);
                break;
            case R.id.btn3:
                openAct(this, AllocateActivity.class);
                break;
            case R.id.btn4:
                openAct(this, AirActivity.class);
                break;
        }
    }
}
