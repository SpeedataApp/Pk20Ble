package com.amobletool.bluetooth.le;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lenovo-pc on 2017/5/8.
 */

public class MyAdapter extends BaseAdapter {

    List<infoEntity> adapterDatas = new ArrayList<infoEntity>();

    protected LayoutInflater layoutInflater = null;

    public MyAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void addDatas(infoEntity t) {
        if (t != null) {
//            Collections.reverse(adapterDatas);
            adapterDatas.add(t);
        }
    }

    public void addDatas(List<infoEntity> lists) {
        if (lists != null) {
            Collections.reverse(adapterDatas);
            adapterDatas.addAll(lists);
        }
    }

    public void addDatas(List<infoEntity> lists, boolean isClear) {
        if (isClear) {
            adapterDatas.clear();
        }
        adapterDatas.addAll(lists);
    }

    public void removeDatas(int index) {
        if (index >= 0) {
            if (adapterDatas.size() > 0) {
                adapterDatas.remove(index);
            }
        }
    }

    public void removeDatas() {
        if (adapterDatas != null && adapterDatas.size() > 0) {
            adapterDatas.clear();
        }
    }

    public List<infoEntity> getAllDatasFromAdapter() {
        return adapterDatas;
    }

    @Override
    public int getCount() {
        if (adapterDatas != null) {
            return adapterDatas.size();
        }
        return 0;
    }


    @Override
    public infoEntity getItem(int position) {
        return adapterDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View views, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(
                R.layout.list_item_layout, null);
        TextView tvScandatas = (TextView) view.findViewById(R.id.tv_scan_datas);
        TextView tvL = (TextView) view.findViewById(R.id.tv_l);
        TextView tvW = (TextView) view.findViewById(R.id.tv_w);
        TextView tvH = (TextView) view.findViewById(R.id.tv_h);
        TextView tvG = (TextView) view.findViewById(R.id.tv_g);
        TextView tvID = (TextView) view.findViewById(R.id.tv_id);
        infoEntity infoEntity = getItem(i);
        //设置数据倒叙
        tvID.setText(infoEntity.getNum()+"");
        tvScandatas.setText(infoEntity.getScanDatas());
        tvL.setText(infoEntity.getL() + "");
        tvW.setText(infoEntity.getW() + "");
        tvH.setText(infoEntity.getH() + "");
        tvG.setText(infoEntity.getG() + "");
        // 获取要适配的当前项
        return view;

    }

}
