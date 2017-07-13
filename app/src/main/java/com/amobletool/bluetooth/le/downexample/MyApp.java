package com.amobletool.bluetooth.le.downexample;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.amobletool.bluetooth.le.downexample.bean.DaoMaster;
import com.amobletool.bluetooth.le.downexample.bean.DaoSession;
import com.amobletool.bluetooth.le.downexample.bean.Word;
import com.amobletool.bluetooth.le.downexample.bean.WordDao;
import com.amobletool.bluetooth.le.downexample.utils.SharedXmlUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.amobletool.bluetooth.le.downexample.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.amobletool.bluetooth.le.downexample.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.amobletool.bluetooth.le.downexample.BluetoothLeService.ACTION_GATT_DISCONNECTED;

/**
 * Created by 张明_ on 2017/7/10.
 */

public class MyApp extends Application {

    private static final String TAG = "pk20";
    private static MyApp m_application; // 单例
    public ArrayList<Activity> aList = new ArrayList<>();
    public static String address = "";
    public static String name = "";
    private BluetoothLeService mBluetoothLeService = null;
    private BluetoothGattCharacteristic mNotifyCharacteristic3 = null;
    private BluetoothGattCharacteristic mNotifyCharacteristic6 = null;
    //greendao
    private static DaoSession daoSession;

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
        boolean haveWord = SharedXmlUtil.getInstance(this).read("haveWord", false);
        if (!haveWord) {
            //创建字库
            makeWordKu();
            SharedXmlUtil.getInstance(this).write("haveWord", true);
        }

    }


    public static MyApp getInstance() {
        return m_application;
    }

    public void writeCharacteristic3(String s) {
        if (mNotifyCharacteristic3 != null) {
            byte[] data = Utils.HexString2Bytes(s);//转十六进制
            mNotifyCharacteristic3.setValue(data);
            mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic3);
        }
    }

    public void writeCharacteristic6(String s) {
        if (mNotifyCharacteristic6 != null) {
            byte[] data = Utils.HexString2Bytes(s);//转十六进制
            mNotifyCharacteristic6.setValue(data);
            mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic6);
        }
    }

    public void readCharacteristic6() {
        if (mNotifyCharacteristic6 != null) {
            mBluetoothLeService.readCharacteristic(mNotifyCharacteristic6);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mBluetoothLeService = null;
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

    public void connect() {
        mBluetoothLeService.connect(address);
    }

    public void disconnect() {
        mBluetoothLeService.disconnect();
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
        Intent gattServiceIntent = new Intent(MyApp.this, BluetoothLeService.class);
        this.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Toast.makeText(MyApp.this, "Unable to initialize Bluetooth", Toast.LENGTH_SHORT).show();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //在成功启动初始化时自动连接到设备。
            boolean connect = mBluetoothLeService.connect(address);
            if (connect) {
                Toast.makeText(MyApp.this, "ServiceConnected success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyApp.this, "ServiceConnected failed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Toast.makeText(MyApp.this, "ServiceDisconnected", Toast.LENGTH_SHORT).show();
        }
    };

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
                Toast.makeText(MyApp.this, "connection success", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", true));
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                unregisterReceiver(mGattUpdateReceiver);
                unbindService(mServiceConnection);
                Toast.makeText(MyApp.this, "disconnection", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", false));
                mNotifyCharacteristic3 = null;
                mNotifyCharacteristic6 = null;
                mBluetoothLeService.close();
                mBluetoothLeService = null;
                address = null;
                name = null;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //显示用户界面上所有受支持的服务和特性。
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                sendBluetoothLeData(data);
            }
        }
    };


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            Toast.makeText(MyApp.this, "no service", Toast.LENGTH_SHORT).show();
            return;
        }
        String uuid = null;
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.d(TAG, "displayGattServices: " + uuid);
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equals("0000fff3-0000-1000-8000-00805f9b34fb")) {
                    mNotifyCharacteristic3 = gattCharacteristic;
                } else if (uuid.equals("0000fff6-0000-1000-8000-00805f9b34fb")) {
                    mNotifyCharacteristic6 = gattCharacteristic;
                }

            }
        }
    }

    public interface getBluetoothLeDataListener {
        void getData(String data);
    }

    private getBluetoothLeDataListener listener;

    public void setGetBluetoothLeDataListener(getBluetoothLeDataListener listener) {
        this.listener = listener;
    }

    private void sendBluetoothLeData(String data) {
        if (listener != null) {
            listener.getData(data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    //创建字库
    private void makeWordKu() {
        final String[] idStr = {"01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
                "10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
                "20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
                "30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E"};
        final String[] wordStr = {"上", "级", "地", "网", "点", "代", "新", "目", "的", "实", "际", "重", "量",
                "低", "体", "积", "测", "快", "件", "并", "发", "中", "心", "连", "接", "揽", "收", "和",
                "成", "功", "失", "败", "稍", "等", "传", "蓝", "牙", "秤", "条", "码", "清", "除", "保",
                "存", "充", "电", "长", "宽", "高", "请", "主", "子", "单", "号", "从", "称", "扫", "描",
                "到", "已", "提", "取"};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WordDao wordDao=getDaoInstant().getWordDao();
                    for (int i = 0; i < idStr.length; i++) {
                        Word word=new Word();
                        word.setId(idStr[i]);
                        word.setWord(wordStr[i]);
                        wordDao.insertOrReplace(word);
                    }
                    EventBus.getDefault().post(new MsgEvent("","字库添加成功"));
                } catch (Exception e) {
                    EventBus.getDefault().post(new MsgEvent("","字库添加失败"));
                }
            }
        }).start();
    }
}
