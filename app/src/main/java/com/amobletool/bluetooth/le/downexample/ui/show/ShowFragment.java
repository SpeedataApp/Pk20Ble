package com.amobletool.bluetooth.le.downexample.ui.show;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.MsgEvent;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.adapter.RVAdapter;
import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ShowFragment extends MVPBaseFragment<ShowContract.View, ShowPresenter>
        implements ShowContract.View ,CommonRvAdapter.OnItemClickListener{

    private RecyclerView rv_content;
    private List<Data> datas;

    @Override
    public int getLayout() {
        return R.layout.fragment_show;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = this.getView();
        initView(view);
        EventBus.getDefault().register(this);
        datas = MyApp.getDaoInstant().getDataDao().loadAll();
        initRV();
    }

    private void initView(View view) {
        rv_content= (RecyclerView) view.findViewById(R.id.rv_content);
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
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Msg(MsgEvent msgEvent){
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("Save6DataSuccess".equals(type)){
            closeFragment();
            openFragment(new ShowFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
