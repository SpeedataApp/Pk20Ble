package com.amobletool.bluetooth.le.downexample.ui.set;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;

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

    private void initView(View view) {
        btn_setTime = (Button) view.findViewById(R.id.btn_setTime);
        btn_setTime.setOnClickListener(this);
        et_bili = (EditText) view.findViewById(R.id.et_bili);
        btn_bili = (Button) view.findViewById(R.id.btn_bili);
        btn_bili.setOnClickListener(this);
        btn_ziku = (Button) view.findViewById(R.id.btn_ziku);
        btn_ziku.setOnClickListener(this);
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
                mPresenter.setZiKu(getActivity());
                break;
        }
    }

}
