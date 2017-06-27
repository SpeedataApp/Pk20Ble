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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.amobletool.bluetooth.le.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.amobletool.bluetooth.le.downexample.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.amobletool.bluetooth.le.downexample.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.amobletool.bluetooth.le.downexample.BluetoothLeService.ACTION_GATT_DISCONNECTED;


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
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                            final EditText et = new EditText(DeviceControlActivity.this);

                            new AlertDialog.Builder(DeviceControlActivity.this).setTitle("输入命令")
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setView(et)
                                    .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String input = et.getText().toString();
                                            if (mNotifyCharacteristic != null) {
                                                byte[] data= Utils.HexString2Bytes(input);//转十六进制
                                                mNotifyCharacteristic.setValue(data);
                                                mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic);
                                            }
                                            try {//下面三句控制弹框的关闭

                                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");

                                                field.setAccessible(true);

                                                field.set(dialog,true);//true表示要关闭

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
        switch(item.getItemId()) {
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
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);


        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.d(TAG, "displayGattServices: "+uuid);
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.contains("fff4")) {
                    Log.e("console", "2gatt Characteristic: " + uuid);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic);
                }
            }
        }
    }

    // Demonstrates how to iterate through the supported GATT
// Services/Characteristics.
// In this sample, we populate the data structure that is bound to the
// ExpandableListView
// on the UI.
//    private void displayGattServices(List gattServices) {
//        if (gattServices == null)
//            return;
//        String uuid = null;
//        ArrayList gattServiceData = new ArrayList<>();
//        ArrayList gattCharacteristicData = new ArrayList<>();
//
//        mGattCharacteristics = new ArrayList<>();
//
//// Loops through available GATT Services.
//        for (BluetoothGattService gattService : gattServices) {
//            HashMap<String, String> currentServiceData = new HashMap<>();
//            uuid = gattService.getUuid().toString();
//            if (uuid.contains("ba11f08c-5f14-0b0d-1080")) {//服务的uuid
//                //System.out.println("this gattService UUID is:" + gattService.getUuid().toString());
//                currentServiceData.put(LIST_NAME, "Service_OX100");
//                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
//                ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
//                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//                ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();
//
//                // Loops through available Characteristics.
//                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                    charas.add(gattCharacteristic);
//                    HashMap<String, String> currentCharaData = new HashMap<>();
//                    uuid = gattCharacteristic.getUuid().toString();
//                    if (uuid.toLowerCase().contains("cd01")) {
//                        currentCharaData.put(LIST_NAME, "cd01");
//                    } else if (uuid.toLowerCase().contains("cd02")) {
//                        currentCharaData.put(LIST_NAME, "cd02");
//                    } else if (uuid.toLowerCase().contains("cd03")) {
//                        currentCharaData.put(LIST_NAME, "cd03");
//                    } else if (uuid.toLowerCase().contains("cd04")) {
//                        currentCharaData.put(LIST_NAME, "cd04");
//                    } else {
//                        currentCharaData.put(LIST_NAME, "write");
//                    }
//
//                    currentCharaData.put(LIST_UUID, uuid);
//                    gattCharacteristicGroupData.add(currentCharaData);
//                }
//
//                mGattCharacteristics.add(charas);
//
//                gattCharacteristicData.add(gattCharacteristicGroupData);
//
//                mCharacteristicChar3 = gattService.getCharacteristic(UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb"));
//
//                //System.out.println("=======================Set Notification==========================");
//                // 开始顺序监听，第一个：CD01
//                mBluetoothLeService.setCharacteristicNotification(mCharacteristicCD01, true);
//            }
//        }
//    }static public void writeChar3_in_bytes(byte bytes[]) {
        // byte[] writeValue = new byte[1];
//        Log.i(TAG, "gattCharacteristic_char3 = " + mCharacteristicChar3);
//        if (mCharacteristicChar3 != null) {
//            boolean bRet = mCharacteristicChar3.setValue(bytes);
//            mBLE.writeCharacteristic(mCharacteristicChar3);
//        }
//    }
//    public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {
//
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//
//        mBluetoothGatt.writeCharacteristic(characteristic);
//    }
    static BluetoothGattCharacteristic gattCharacteristic_char3 = null;
    static BluetoothGattCharacteristic gattCharacteristic_char6 = null;
    static BluetoothGattCharacteristic gattCharacteristic_keydata = null;
    public static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR1 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR2 = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR3 = "0000fff3-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR4 = "0000fff4-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR5 = "0000fff5-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR6 = "0000fff6-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR7 = "0000fff7-0000-1000-8000-00805f9b34fb";
    public static String UUID_HERATRATE = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String UUID_TEMPERATURE = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String UUID_0XFFA6 = "0000ffa6-0000-1000-8000-00805f9b34fb";
    public static String UUID_0XFFA3 = "0000ffa6-0000-1000-8000-00805f9b34fb";
//    private void displayGattServices(List<BluetoothGattService> gattServices) {
//        if (gattServices == null)
//            return;
//        BluetoothGattCharacteristic Characteristic_cur = null;
//
//        for (BluetoothGattService gattService : gattServices) {
//            // -----Service的字段信息----//
//            // -----Characteristics的字段信�?----//
//            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());
//
//
//                byte[] data = gattCharacteristic.getValue();
//                if (data != null && data.length > 0) {
//                    Log.e(TAG, "---->char value:" + new String(data));
//                }
//
//
//                if (gattCharacteristic.getUuid().toString().equals(UUID_CHAR3)) {
//                    // 把char1 保存起来�?以方便后面读写数据时使用
//                    gattCharacteristic_char3 = gattCharacteristic;
//                    Characteristic_cur = gattCharacteristic;
//                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
//                    Log.i(TAG, "+++++++++UUID_CHAR6");
//                }
////                if (gattCharacteristic.getUuid().toString().equals(UUID_CHAR6)) {
////                    // 把char1 保存起来�?以方便后面读写数据时使用
////                    gattCharacteristic_char6 = gattCharacteristic;
////                    Characteristic_cur = gattCharacteristic;
////                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
////                    Log.i(TAG, "+++++++++UUID_CHAR6");
////                }
//
//
//
////UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
//                if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
//                    // 把heartrate 保存起来�?以方便后面读写数据时使用
//                    //测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
//                    gattCharacteristic_keydata = gattCharacteristic;
//                    Characteristic_cur = gattCharacteristic;
//                    // 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
//                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
//                    Log.i(TAG, "+++++++++UUID_KEY_DATA");
//                }
//
//                // -----Descriptors的字段信�?----//
//                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
//                        .getDescriptors();
//                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
//                    byte[] desData = gattDescriptor.getValue();
//                    if (desData != null && desData.length > 0) {
//                        Log.e(TAG, "-------->desc value:" + new String(desData));
//                    }
//                }
//            }
//        }
//
//    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
