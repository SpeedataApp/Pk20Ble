package com.amobletool.bluetooth.le.downexample.ui.add;


import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
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
//                for (int i = 0; i < spinnerList.size(); i++) {
//                    int itemID = Integer.parseInt(spinnerList.get(i).getSelectedItem());
//                    String hexString = Integer.toHexString(itemID);
//                }
                Toast.makeText(this, word2Id, Toast.LENGTH_SHORT).show();
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
}
