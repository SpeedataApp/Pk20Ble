package com.amobletool.bluetooth.le.downexample.ui.assign;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.adapter.CommonAdapter;
import com.amobletool.bluetooth.le.downexample.adapter.ViewHolder;
import com.amobletool.bluetooth.le.downexample.bean.LiuCheng;
import com.amobletool.bluetooth.le.downexample.bean.LiuChengDao;
import com.amobletool.bluetooth.le.downexample.mvp.MVPBaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import speedata.com.blelib.utils.DataManageUtils;

import static com.amobletool.bluetooth.le.downexample.MyApp.mNotifyCharacteristic3;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AssignFragment extends MVPBaseFragment<AssignContract.View, AssignPresenter> implements
        AssignContract.View, AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {

    private ListView rv_content;
    //    private Button btn_commit;
    private CommonAdapter<LiuCheng> commonAdapter;
    private List<LiuCheng> liuChengs;

    @Override
    public int getLayout() {
        return R.layout.fragment_assign;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = this.getView();
        initView(view);
        liuChengs = MyApp.getDaoInstant().getLiuChengDao().loadAll();
        for (int i = 0; i < liuChengs.size(); i++) {
            liuChengs.get(i).setIsCheck(null);
        }
        initListView();
    }

    private void initView(View view) {
        rv_content = (ListView) view.findViewById(R.id.rv_content);
//        btn_commit = (Button) view.findViewById(btn_commit);
//        btn_commit.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initListView() {
        commonAdapter = new CommonAdapter<LiuCheng>(getActivity(), liuChengs, R.layout.item_info) {
            @Override
            public void convert(ViewHolder helper, LiuCheng item, int position) {
                helper.setText(R.id.tv_id, item.getId());
                helper.setText(R.id.tv_name, item.getName());
                helper.setText(R.id.tv_renwu, item.getRenWuName());
            }
        };
        rv_content.setAdapter(commonAdapter);
        rv_content.setOnItemClickListener(this);
        rv_content.setOnItemLongClickListener(this);
    }


    List<String> idList = new ArrayList<>();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("下达《" + liuChengs.get(position).getName() + "》流程");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mNotifyCharacteristic3 == null) {
                    EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
                    return;
                }
                LiuCheng unique = MyApp.getDaoInstant().getLiuChengDao().queryBuilder()
                        .where(LiuChengDao.Properties.Id.eq(liuChengs.get(position).getId())).unique();
                final String renWuCode = unique.getCode();
                MyApp.getInstance().writeCharacteristic3(renWuCode);
                MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                    @Override
                    public void getData(String data) {
                        int ff = DataManageUtils.jiaoYanData(data, "FF", "0A");
                        if (ff == 0) {
                            String[] split = data.split(" ");
                            String zhilingCount = split[4];
                            if ("01".equals(zhilingCount)) {
                                Toast.makeText(getActivity(), "下达失败,指令已达上限", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "下达成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "下达失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case btn_commit:
//                if (mNotifyCharacteristic3 == null) {
//                    EventBus.getDefault().post(new MsgEvent("Notification","请连接设备"));
//                    return;
//                }
//                for (int i = 0; i < idList.size(); i++) {
//                    Log.d("PK20", "btn_commit onClick: " + idList.get(i));
//                    LiuCheng unique = MyApp.getDaoInstant().getLiuChengDao().queryBuilder()
//                            .where(LiuChengDao.Properties.Id.eq(idList.get(i))).unique();
//                    String renWuCode = unique.getCode();
//                    MyApp.getInstance().writeCharacteristic3(renWuCode);
//                    MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
//                        @Override
//                        public void getData(String data) {
//                            Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                break;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        idList.clear();
        liuChengs.clear();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("删除《" + liuChengs.get(position).getName() + "》流程");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LiuCheng unique = MyApp.getDaoInstant().getLiuChengDao().queryBuilder()
                        .where(LiuChengDao.Properties.Id.eq(liuChengs.get(position).getId())).unique();
                MyApp.getDaoInstant().getLiuChengDao().delete(unique);
                openFragment(new AssignFragment());
                closeFragment();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return true;
    }
}
