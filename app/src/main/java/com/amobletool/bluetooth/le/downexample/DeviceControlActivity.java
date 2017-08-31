/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amobletool.bluetooth.le.downexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amobletool.bluetooth.le.R;
import com.amobletool.bluetooth.le.downexample.service.BluetoothLeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.amobletool.bluetooth.le.downexample.service.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.amobletool.bluetooth.le.downexample.service.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.amobletool.bluetooth.le.downexample.service.BluetoothLeService.ACTION_GATT_DISCONNECTED;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //在成功启动初始化时自动连接到设备。
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Toast.makeText(DeviceControlActivity.this, "ServiceDisconnected", Toast.LENGTH_SHORT).show();
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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                //将使原填充的菜单项无效，当用户再次访问菜单时，再次调用onCreateOptionsMenu(Menu menu)。
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //显示用户界面上所有受支持的服务和特性。
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    // 如果给定的GATT特性被选中，请检查支持的特性。这个示例演示“读”和“通知”功能。
    // 看到http://d.android.com/reference/android/bluetooth/BluetoothGatt.html的完成
    // 列表支持的特征特性。

    // 点击未知特性值
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            // If there is an active notification on a characteristic, clear
//                            // it first so it doesn't update the data field on the user interface.
//                            if (mNotifyCharacteristic != null) {
//                                mBluetoothLeService.setCharacteristicNotification(
//                                        mNotifyCharacteristic, true);
//                                mNotifyCharacteristic = null;
//                            }
//                            mBluetoothLeService.readCharacteristic(characteristic);
//                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mDataField.setText("No data");
                            mNotifyCharacteristic = characteristic;
//                            mBluetoothLeService.setCharacteristicNotification(
//                                    characteristic, true);
                            LayoutInflater layoutInflater=LayoutInflater
                                    .from(DeviceControlActivity.this);
                            View view = layoutInflater.inflate(R.layout.write_dialog, null);
                            final EditText et= (EditText) view.findViewById(R.id.et);
                            Button btn1= (Button) view.findViewById(R.id.btn_test1);
                            Button btn2= (Button) view.findViewById(R.id.btn_test2);
                            Button btn3= (Button) view.findViewById(R.id.btn_test3);
                            btn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    et.setText("FF0A0301011A1B00000000051500000000001A00");
                                }
                            });
                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    et.setText("FF0A04050104050F10110D050215000000001A00");
                                }
                            });
                            btn3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    et.setText("FF0A04090716170F10110D050215000000001A00");
                                }
                            });
                            new AlertDialog.Builder(DeviceControlActivity.this).setTitle("输入命令")
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setView(view)
                                    .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String input = et.getText().toString();
                                            if (mNotifyCharacteristic != null) {
                                                byte[] data = Utils.HexString2Bytes(input);//转十六进制
                                                //设置为WRITE_TYPE_NO_RESPONSE，这样速度会快
//                                                mNotifyCharacteristic.setWriteType
//                                                        (BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                                                mNotifyCharacteristic.setValue(data);
                                                mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic);
//                                                SystemClock.sleep(500);
//                                                mBluetoothLeService.readCharacteristic(characteristic);
                                            }
                                            try {//下面三句控制弹框的关闭

                                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");

                                                field.setAccessible(true);

                                                field.set(dialog, true);//true表示要关闭

                                            } catch (Exception e) {

                                                e.printStackTrace();

                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                        return true;
                    }
                    return false;
                }
            };
    private BluetoothGattCharacteristic mCharacteristicChar3;

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        EventBus.getDefault().register(this);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }else {
            mDataField.setText("no data");
        }
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent mEvent) {
        String type = mEvent.getType();
        String msg = (String) mEvent.getMsg();
        if (type.equals("onCharacteristicRead")) {
            mDataField.setText("readFailed:"+msg);
        }else {
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        }
    }
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        //0000fff3-0000-1000-8000-00805f9b34fb
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        mGattServicesList.setAdapter(gattServiceAdapter);


//        for (BluetoothGattService gattService : gattServices) {
//            uuid = gattService.getUuid().toString();
//            Log.d(TAG, "displayGattServices: " + uuid);
//            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                uuid = gattCharacteristic.getUuid().toString();
//                if (uuid.contains("fff4")) {
//                    Log.e("console", "2gatt Characteristic: " + uuid);
//                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
//                    mBluetoothLeService.readCharacteristic(gattCharacteristic);
//                }
//            }
//        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
