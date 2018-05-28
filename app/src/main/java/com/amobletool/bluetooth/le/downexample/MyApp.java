package com.amobletool.bluetooth.le.downexample;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amobletool.bluetooth.le.downexample.bean.DaoMaster;
import com.amobletool.bluetooth.le.downexample.bean.DaoSession;
import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.bean.Word;
import com.amobletool.bluetooth.le.downexample.bean.WordDao;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import speedata.com.blelib.base.BaseBleApplication;
import speedata.com.blelib.bean.LWHData;
import speedata.com.blelib.bean.PK20Data;
import speedata.com.blelib.service.BluetoothLeService;

import static speedata.com.blelib.service.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static speedata.com.blelib.service.BluetoothLeService.ACTION_GATT_CONNECTED;
import static speedata.com.blelib.service.BluetoothLeService.ACTION_GATT_DISCONNECTED;

/**
 * Created by 张明_ on 2017/7/10.
 */

public class MyApp extends BaseBleApplication {

    private static MyApp m_application; // 单例
    public ArrayList<Activity> aList = new ArrayList<>();
    public static String address = "";
    public static String name = "";
    //greendao
    private static DaoSession daoSession;
    public static boolean cn;

    private void setupDatabase() {
        //创建数据库
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "pk20.db", null);
        //获得可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获得数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获得dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_application = this;
        setupDatabase();
//        boolean haveWord = SharedXmlUtil.getInstance(this).read("haveWord", false);
//        if (!haveWord) {
//            //创建字库
//            makeWordKu();
//            SharedXmlUtil.getInstance(this).write("haveWord", true);
//        }
        cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
    }


    public static MyApp getInstance() {
        return m_application;
    }


    /**
     * TODO(将所有已创建的Activity加入aList集合中)
     */
    public void addActivity(Activity activity) {

        if (!aList.contains(activity)) {
            aList.add(activity);
        }
    }

    /**
     * TODO(将aList集合中已存在的Activity移除)
     */
    public void deleteActivity(Activity activity) {
        if (compare(activity)) {
            aList.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    private boolean compare(Activity ch) {
        boolean flag = false;
        if (aList.contains(ch))
            flag = true;
        return flag;
    }

    public void getDeviceName(BluetoothDevice device) {
        address = device.getAddress();
        name = device.getName();
        bindServiceAndRegisterReceiver(device);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.处理由服务触发的各种事件。
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接到GATT服务器
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.与GATT服务器断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.发现了GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    //从设备接收数据。这可能是阅读的结果或通知操作。
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                EventBus.getDefault().post(new MsgEvent("KP", false));
                boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
                if (cn) {
                    Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Connection", Toast.LENGTH_LONG).show();
                }
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", true));
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", false));
                boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
                if (cn) {
                    Toast.makeText(getApplicationContext(), "已断开", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_LONG).show();
                }

                Log.d("ZM_connect", "application里面的断开连接");
                if (wantDisconnect) {
                    unregisterReceiver(mGattUpdateReceiver);
                } else {
                    EventBus.getDefault().post(new MsgEvent("KP", true));
                }
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (TextUtils.isEmpty(data)) {
                    LWHData lwh = intent.getParcelableExtra(BluetoothLeService.NOTIFICATION_DATA_LWH);
                    if (lwh != null) {
                        EventBus.getDefault().post(new MsgEvent("LWHData", lwh));
                    } else {
                        String dataERR = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_ERR);
                        if (TextUtils.isEmpty(dataERR)) {
                            PK20Data mPK20Data = intent.getParcelableExtra(BluetoothLeService.NOTIFICATION_DATA);
                            Data mData = new Data();
                            mData.setWangDian(mPK20Data.wangDian);
                            mData.setCenter(mPK20Data.center);
                            mData.setMuDi(mPK20Data.muDi);
                            mData.setLiuCheng(mPK20Data.liuCheng);
                            mData.setL(mPK20Data.L);
                            mData.setW(mPK20Data.W);
                            mData.setH(mPK20Data.H);
                            mData.setG(mPK20Data.G);
                            mData.setV(mPK20Data.V);
                            mData.setTime(mPK20Data.time);
                            mData.setBarCode(mPK20Data.barCode);
                            mData.setZhu(mPK20Data.zhu);
                            mData.setZi(mPK20Data.zi);
                            mData.setBiaoJi(mPK20Data.biaoJi);
                            mData.setBiaoshi(mPK20Data.biaoshi);
                            mData.setMac(mPK20Data.mac);
                            mData.setName(mPK20Data.name);
                            MyApp.getDaoInstant().getDataDao().insertOrReplace(mData);
                            boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
                            if (cn) {
                                EventBus.getDefault().post(new MsgEvent("Save6DataSuccess", "数据存储成功"));
                            } else {
                                EventBus.getDefault().post(new MsgEvent("Save6DataSuccess", "Data storage success"));
                            }
                        } else {
                            EventBus.getDefault().post(new MsgEvent("Save6Data", dataERR));
                        }
                    }

                }
            }
        }
    };


    //创建字库
    private void makeWordKu() {
        final String[] idStr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
                "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
                "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F",
                "40", "41", "42", "43", "44", "45"};
        final String[] wordStr = {"上", "级", "地", "网", "点", "代", "新", "目", "的", "实", "际", "重", "量",
                "低", "体", "积", "测", "快", "件", "并", "发", "中", "心", "连", "接", "揽", "收", "和",
                "成", "功", "失", "败", "稍", "等", "传", "蓝", "牙", "秤", "条", "码", "清", "除", "保",
                "存", "充", "电", "长", "宽", "高", "请", "主", "子", "单", "号", "从", "称", "扫", "描",
                "到", "已", "提", "取", "未", "全", "部", "数", "据", "时", "间"};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WordDao wordDao = getDaoInstant().getWordDao();
                    for (int i = 0; i < idStr.length; i++) {
                        Word word = new Word();
                        word.setId(idStr[i]);
                        word.setWord(wordStr[i]);
                        wordDao.insertOrReplace(word);
                    }
                    EventBus.getDefault().post(new MsgEvent("", "字库添加成功"));
                } catch (Exception e) {
                    EventBus.getDefault().post(new MsgEvent("", "字库添加失败"));
                }
            }
        }).start();
    }
}
