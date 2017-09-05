package com.amobletool.bluetooth.le.downexample.ui.add;


import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseActivity;
import com.amobletool.bluetooth.le.downexample.utils.KeyboardUtil;
import com.amobletool.bluetooth.le.downexample.utils.SharedXmlUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AddActivity extends MVPBaseActivity<AddContract.View, AddPresenter> implements AddContract.View, View.OnClickListener {

    private EditText name;
    private KeyboardView keyboardView;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private Spinner sp1;
    private Spinner sp2;
    private Spinner sp3;
    private Spinner sp4;
    private Spinner sp5;
    private Spinner sp6;
    private Button commit;
    private List<Spinner> spinnerList = null;
    private Spinner sp7;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add);
        initView();
        getSpList();
    }

    private void initView() {
        final Context context = this;
        getActionBar().setTitle("流程添加");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        name = (EditText) findViewById(R.id.name);
        name.setInputType(InputType.TYPE_NULL);
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_view);
        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(keyboardView, context, name).showKeyboard();
                return false;
            }
        });
        cb1 = (CheckBox) findViewById(R.id.cb1);
        cb1.setOnClickListener(this);
        cb2 = (CheckBox) findViewById(R.id.cb2);
        cb2.setOnClickListener(this);
        cb3 = (CheckBox) findViewById(R.id.cb3);
        cb3.setOnClickListener(this);
        sp1 = (Spinner) findViewById(R.id.sp1);
        sp2 = (Spinner) findViewById(R.id.sp2);
        sp3 = (Spinner) findViewById(R.id.sp3);
        sp4 = (Spinner) findViewById(R.id.sp4);
        sp5 = (Spinner) findViewById(R.id.sp5);
        sp6 = (Spinner) findViewById(R.id.sp6);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
        sp7 = (Spinner) findViewById(R.id.sp7);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:

                String word2Id = mPresenter.word2Id(name.getText().toString());
                boolean checked1 = cb1.isChecked();
                boolean checked2 = cb2.isChecked();
                boolean checked3 = cb3.isChecked();
                String guDingBiaoShi = mPresenter.getGuDingBiaoShi(checked1, checked2, checked3);
                String renWuCode = mPresenter.getRenWuCode(spinnerList);
                if (TextUtils.isEmpty(renWuCode)){
                    Toast.makeText(this, "请选择任务！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] renWuSplit = renWuCode.split("-");
                int qingJingCode = SharedXmlUtil.getInstance(this).read("QingJingCode", 0);
                qingJingCode++;
                String qingJingStr = Integer.toHexString(qingJingCode);
                if (qingJingStr.length() == 1) {
                    qingJingStr = "0" + qingJingStr;
                }
                SharedXmlUtil.getInstance(this).write("QingJingCode", qingJingCode);
                String result = "FF0A" + renWuSplit[2] + qingJingStr + guDingBiaoShi
                        + word2Id + renWuSplit[0] + renWuSplit[1] + renWuSplit[3] + "00";
                int saveLiuCheng = mPresenter.saveLiuCheng(qingJingStr, result, name.getText().toString()
                        , renWuSplit[0], renWuSplit[4]);
                if (saveLiuCheng == 0) {
                    Toast.makeText(this, "添加成功！", Toast.LENGTH_SHORT).show();
                    this.finish();
                }else {
                    Toast.makeText(this, "添加失败！", Toast.LENGTH_SHORT).show();
                }

                Log.d("pk20", "commit onClick: " + word2Id + "-" + renWuCode + "-" + guDingBiaoShi);
                break;
        }
    }

    public void getSpList() {
        spinnerList = new ArrayList<>();
        spinnerList.add(sp1);
        spinnerList.add(sp2);
        spinnerList.add(sp3);
        spinnerList.add(sp4);
        spinnerList.add(sp5);
        spinnerList.add(sp6);
        spinnerList.add(sp7);
    }

    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    Toast.makeText(AddActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
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
