package com.amobletool.bluetooth.le.downexample.ui.allocate;


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

public class AllocateActivity extends MVPBaseActivity<AllocateContract.View, AllocatePresenter> implements AllocateContract.View, View.OnClickListener {

    private Button btn9;
    private Button btn10;
    private Button btn11;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocate);
        initView();
    }

    private void initView() {
        btn9 = (Button) findViewById(R.id.btn9);
        btn10 = (Button) findViewById(R.id.btn10);
        btn11 = (Button) findViewById(R.id.btn11);

        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn9:

                break;
            case R.id.btn10:

                break;
            case R.id.btn11:

                break;
        }
    }
}
