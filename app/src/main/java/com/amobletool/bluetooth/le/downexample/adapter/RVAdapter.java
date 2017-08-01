package com.amobletool.bluetooth.le.downexample.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.utils.DataManageUtils;

import java.util.List;

import xyz.reginer.baseadapter.BaseAdapterHelper;
import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * Created by 张明_ on 2017/7/14.
 */

public class RVAdapter extends CommonRvAdapter<Data> {

    public RVAdapter(Context context, int layoutResId, List data) {
        super(context, layoutResId, data);
    }


    @Override
    public void convert(BaseAdapterHelper helper, Data item, int position) {
        String barCode = DataManageUtils.convertHexToString(item.getBarCode());
        helper.setText(R.id.tv_barCode, barCode);
        String wangDian = "";
        String itemWangDian = item.getWangDian();
        if (!itemWangDian.equals("ffffffffffffffffffff")) {
            wangDian = DataManageUtils.convertHexToString(itemWangDian);
        }
        setUi(helper, wangDian, R.id.tv_wangdian, R.id.ll_wangdian);
        String zhu = "";
        if (!item.getZhu().equals("ffffffffffffffffffffffffffffffffffffffff")) {
            zhu = DataManageUtils.convertHexToString(item.getZhu());
        }
        setUi(helper, zhu, R.id.tv_zhu, R.id.ll_zhu);
        String zi = "";
        String itemZi = item.getZi();
        if (!itemZi.equals("ffffffffffffffffffffffffffffffffffffffff")) {
            zi = DataManageUtils.convertHexToString(itemZi);
        }
        setUi(helper, zi, R.id.tv_zi, R.id.ll_zi);
        String center = "";
        if (!item.getCenter().equals("ffffffffffffffffffff")) {
            center = DataManageUtils.convertHexToString(item.getCenter());
        }
        setUi(helper, center, R.id.tv_center, R.id.ll_center);
        String muDi = "";
        if (!item.getMuDi().equals("ffffffffffffffffffff")) {
            muDi = DataManageUtils.convertHexToString(item.getMuDi());
        }
        setUi(helper, muDi, R.id.tv_mudi, R.id.ll_mudi);
        //ffffffffffff
        String timeResult = "";
        if (!item.getTime().equals("ffffffffffff")) {
            String time = item.getTime();
            timeResult = "20" + time.substring(10, 12) + "-"
                    + time.substring(8, 10) + "-"
                    + time.substring(6, 8) + " "
                    + time.substring(4, 6) + ":"
                    + time.substring(2, 4) + ":"
                    + time.substring(0, 2);
        }
        setUi(helper, timeResult, R.id.tv_time, R.id.ll_time);

        String itemL = "";
        int l=0;
        if (!item.getL().equals("ffff")) {
            itemL = item.getL();
            l = Integer.parseInt(itemL, 16);
            setUi(helper, l + "毫米", R.id.tv_l, R.id.ll_l);
        }else {
            setUi(helper, "", R.id.tv_l, R.id.ll_l);
        }


        int w = 0;
        if (!item.getW().equals("ffff")) {
            w = Integer.parseInt(item.getW(), 16);
            setUi(helper, w + "毫米", R.id.tv_w, R.id.ll_w);
        }else {
            setUi(helper, "", R.id.tv_w, R.id.ll_w);
        }


        int h = 0;
        if (!item.getH().equals("ffff")) {
            h = Integer.parseInt(item.getH(), 16);
            setUi(helper, h + "毫米", R.id.tv_h, R.id.ll_h);
        }else {
            setUi(helper, "", R.id.tv_h, R.id.ll_h);
        }

//        byte[] hexString2Bytes = Utils.HexString2Bytes(item.getV());
//        int i = hexString2Bytes[0] << 24;
//        int i1 = hexString2Bytes[1] << 16;
//        int i2 = hexString2Bytes[2] << 8;
//        int i3 = hexString2Bytes[3];
//        int v = i + i1 + i2 + i3;
        String itemV = item.getV();
        int v = 0;
        if (!itemV.equals("ffffffff")) {
            v = Integer.parseInt(itemV, 16);
            setUi(helper, v  + "", R.id.tv_v, R.id.ll_v);
        }else {
            setUi(helper, "", R.id.tv_v, R.id.ll_v);
        }

        int g = 0;
        if (!item.getG().equals("ffff")) {
            g = Integer.parseInt(item.getG(), 16);
            setUi(helper, g+"", R.id.tv_g, R.id.ll_g);
        }else {
            setUi(helper, "", R.id.tv_g, R.id.ll_g);
        }
    }

    private void setUi(BaseAdapterHelper helper, String wangDian, int viewId, int ll) {
        wangDian = wangDian.replace("\u0000", "");
        if (TextUtils.isEmpty(wangDian) || wangDian.equals("")) {
            helper.setVisible(ll, false);
        } else {
            helper.setVisible(ll, true);
            helper.setText(viewId, wangDian);
        }
    }
}
