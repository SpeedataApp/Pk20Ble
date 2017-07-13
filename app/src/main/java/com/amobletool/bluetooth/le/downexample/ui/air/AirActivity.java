package com.amobletool.bluetooth.le.downexample.ui.air;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AirActivity extends MVPBaseActivity<AirContract.View, AirPresenter> implements AirContract.View, View.OnClickListener {

    private Button btn12;
    private Button btn13;
    private Button btn14;
    private Button btn15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        initView();
    }

    private void initView() {
        btn12 = (Button) findViewById(R.id.btn12);
        btn13 = (Button) findViewById(R.id.btn13);
        btn14 = (Button) findViewById(R.id.btn14);
        btn15 = (Button) findViewById(R.id.btn15);

        btn12.setOnClickListener(this);
        btn13.setOnClickListener(this);
        btn14.setOnClickListener(this);
        btn15.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn12:

                break;
            case R.id.btn13:

                break;
            case R.id.btn14:

                break;
            case R.id.btn15:

                break;
        }
    }
}
