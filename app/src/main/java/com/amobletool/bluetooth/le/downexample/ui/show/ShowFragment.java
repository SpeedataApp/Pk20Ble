package com.amobletool.bluetooth.le.downexample.ui.show;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.adapter.RVAdapter;
import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import xyz.reginer.baseadapter.CommonRvAdapter;

import static com.amobletool.bluetooth.le.downexample.MyApp.cn;


public class ShowFragment extends MVPBaseFragment<ShowContract.View, ShowPresenter>
        implements ShowContract.View, CommonRvAdapter.OnItemClickListener {

    private RecyclerView rv_content;
    private List<Data> datas;
    private TextView tv_countNum;
    private Button btn_upIntent;
    private KProgressHUD kProgressHUD;

    @Override
    public int getLayout() {
        return R.layout.fragment_show;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = this.getView();
        initView(view);
        datas = MyApp.getDaoInstant().getDataDao().loadAll();
        tv_countNum.setText(datas.size() + "");
        initRV();
    }

    private void initView(View view) {
        rv_content = (RecyclerView) view.findViewById(R.id.rv_content);
        tv_countNum = (TextView) view.findViewById(R.id.tv_countNum);
        btn_upIntent = (Button) view.findViewById(R.id.btn_upIntent);
        btn_upIntent.setClickable(true);
        btn_upIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datas.size() == 0) {
                    if (cn) {
                        Toast.makeText(getActivity(), "无数据上传", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Numerous uploaded", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }
                btn_upIntent.setClickable(false);
                kProgressHUD = KProgressHUD.create(getActivity())
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                mPresenter.upIntent(datas);
            }
        });
    }

    private RVAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    private void initRV() {
        mAdapter = new RVAdapter(getActivity(), R.layout.info_show, datas);
        rv_content.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layoutManager.setReverseLayout(true);//列表翻转
        rv_content.setLayoutManager(layoutManager);
        rv_content.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Msg(MsgEvent msgEvent) {
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("Save6DataSuccess".equals(type)) {
            closeFragment();
            openFragment(new ShowFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }

        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
            closeFragment();
            openFragment(new ShowFragment());
        }
    }

    @Override
    public void onlyShowToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }

        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
    }
}
