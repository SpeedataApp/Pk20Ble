package com.amobletool.bluetooth.le.downexample.ui.branch;


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

public class BranchActivity extends MVPBaseActivity<BranchContract.View, BranchPresenter> implements BranchContract.View, View.OnClickListener {

    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        initView();
    }

    private void initView() {
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);

        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn4:

                break;
            case R.id.btn5:

                break;
            case R.id.btn6:

                break;
            case R.id.btn7:

                break;
            case R.id.btn8:

                break;
        }
    }
}
