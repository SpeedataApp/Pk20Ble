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

package com.amobletool.bluetooth.le.downexample.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amobletool.bluetooth.le.downexample.MsgEvent;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.SampleGattAttributes;
import com.amobletool.bluetooth.le.downexample.Utils;
import com.amobletool.bluetooth.le.downexample.bean.Data;
import com.amobletool.bluetooth.le.downexample.utils.DataManageUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String ACTION_NOTIFICATION_DATA = "com.example.bluetooth.le.ACTION_NOTIFICATION_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
//                mBluetoothGatt.discoverServices();
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
                EventBus.getDefault().post(new MsgEvent("", "ACTION_GATT_CONNECTED"));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
                EventBus.getDefault().post(new MsgEvent("", "ACTION_GATT_DISCONNECTED"));
            }
        }

        //discoverServices 搜索连接设备所支持的service。
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        //readCharacteristic 读取指定的characteristic。
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            } else {
                EventBus.getDefault().post(new MsgEvent("onCharacteristicRead", status + ""));
            }
        }

        //setCharacteristicNotification 设置当指定characteristic值变化时，发出通知。
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//            EventBus.getDefault().post(new MsgEvent("Notification", "setCharacteristicNotification"));
        }

        //写入回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Handler handler = new Handler(Looper.getMainLooper());
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(BluetoothLeService.this, "写入成功", Toast.LENGTH_SHORT).show();
//                    }
//                });

                setCharacteristicNotification(characteristic, true);
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BluetoothLeService.this, "写入失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BluetoothLeService.this, "没有权限", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    private List<byte[]> mByteList = new ArrayList<>();

    public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            sendBroadcast(intent);
        } else if ("0000fff6-0000-1000-8000-00805f9b34fb".equals(characteristic.getUuid().toString())) {
            final byte[] data = characteristic.getValue();
            if (data[3] == (byte) 0xB1 && data[0] == (byte) 0xAA && data[1] == (byte) 0x0A) {
                mByteList.clear();
            }
            mByteList.add(data);
            if (mByteList.size() == 7) {
                final List<byte[]> mByteNewList = new ArrayList<>();
                mByteNewList.addAll(mByteList);
                mByteList.clear();
                byte[] bytes0 = mByteNewList.get(0);
                for (int i = 0; i < mByteNewList.size(); i++) {
                    String bytesToHexString = Utils.bytesToHexString(mByteNewList.get(i));
                    Log.d("PK20", "broadcastUpdate: " + bytesToHexString);
                }
                int jiaoYan6 = DataManageUtils.jiaoYan6(mByteNewList.get(0), mByteNewList.get(6));
                if (jiaoYan6 != 0) {
//                    characteristic.setValue("AA01010000000000000000000000000000000000");
//                    wirteCharacteristic(characteristic);
                    EventBus.getDefault().post(new MsgEvent("Save6Data", "信道6数据有误"));
                    return;
                }
                StringBuffer stringBuffer = new StringBuffer();
                if (bytes0[3] != (byte) 0xB1) {
                    stringBuffer.append("B1");
                }
                if (mByteNewList.get(1)[0] != (byte) 0xB2) {
                    stringBuffer.append("B2");
                }
                if (mByteNewList.get(2)[0] != (byte) 0xB3) {
                    stringBuffer.append("B3");
                }
                if (mByteNewList.get(3)[0] != (byte) 0xB4) {
                    stringBuffer.append("B4");
                }
                if (mByteNewList.get(4)[0] != (byte) 0xB5) {
                    stringBuffer.append("B5");
                }
                if (mByteNewList.get(5)[0] != (byte) 0xB6) {
                    stringBuffer.append("B6");
                }
                if (mByteNewList.get(6)[0] != (byte) 0xB7) {
                    stringBuffer.append("B7");
                }
                if (TextUtils.isEmpty(stringBuffer.toString())) {
                    mThread m = new mThread(mByteNewList, characteristic);
                    m.start();
                } else {
                    String toString = stringBuffer.toString();
                    int length = toString().length() / 2 + 1;
                    StringBuffer zero = new StringBuffer();
                    for (int i = 0; i < 15 - length + 1; i++) {
                        zero.append("00");
                    }
                    String jiaoYan = DataManageUtils.getJiaoYan(toString.substring(0, 2)
                            , toString.substring(toString.length() - 2, toString.length()));
                    zero.append(jiaoYan);
//                    characteristic.setValue("AA020"+length+toString+zero+"00");
//                    wirteCharacteristic(characteristic);
                    EventBus.getDefault().post(new MsgEvent("Save6Data", "信道6数据部分重发"));
                }
            }
        } else {
            // For all other profiles, writes the data formatted in HEX.
            // 对于所有其他配置文件，用十六进制格式编写数据。
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, stringBuilder.toString());
                sendBroadcast(intent);
            }
        }
    }


    class mThread extends Thread {
        List<byte[]> mByteNewList;
        BluetoothGattCharacteristic characteristic;

        public mThread(List<byte[]> mByteNewList, BluetoothGattCharacteristic characteristic) {
            this.mByteNewList = mByteNewList;
            this.characteristic = characteristic;
        }

        @Override
        public void run() {
            Data mData = new Data();
            String branchCode = DataManageUtils.getBranchCode(mByteNewList.get(0));
            mData.setWangDian(branchCode);
            String centerCode = DataManageUtils.getCenterCode(mByteNewList.get(0), mByteNewList.get(1));
            mData.setCenter(centerCode);
            String muDiCode = DataManageUtils.getMuDiCode(mByteNewList.get(1));
            mData.setMuDi(muDiCode);
            String use = DataManageUtils.getUse(mByteNewList.get(1));
            mData.setLiuCheng(use);
            String l = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.L);
            mData.setL(l);
            String w = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.W);
            mData.setW(w);
            String h = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.H);
            mData.setH(h);
            String g = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.G);
            mData.setG(g);
            String v = DataManageUtils.getV(mByteNewList.get(2));
            mData.setV(v);
            String time = DataManageUtils.getTime(mByteNewList.get(2));
            mData.setTime(time);
            String expressCode = DataManageUtils.getExpressCode(mByteNewList.get(3), mByteNewList.get(4));
            mData.setBarCode(expressCode);
            String mainCode = DataManageUtils.getMainCode(mByteNewList.get(4), mByteNewList.get(5));
            mData.setZhu(mainCode);
            String sonCode = DataManageUtils.getSonCode(mByteNewList.get(5), mByteNewList.get(6));
            mData.setZi(sonCode);
            String flag = DataManageUtils.getFlag(mByteNewList.get(6));
            mData.setBiaoJi(flag);
            MyApp.getDaoInstant().getDataDao().insertOrReplace(mData);
            EventBus.getDefault().post(new MsgEvent("Save6DataSuccess", "一条数据存储成功"));
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * 连接到在蓝牙LE设备上托管的GATT服务器。
     *
     * @param address The device address of the destination device.目标设备的设备地址。
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.以前连接设备。尝试重新连接。
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.我们想要直接连接到设备上，所以我们设置了自动连接 参数为false。
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.在使用了一个可使用的设备之后，应用程序必须调用这个方法来确保资源的使用。
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     * 启用或禁用给定特性的通知。
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
//        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
//        for (BluetoothGattDescriptor dp : descriptors) {
//            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(dp);
//        }
        boolean isEnableNotification = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (isEnableNotification) {
            List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
            if (descriptorList != null && descriptorList.size() > 0) {
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
        // This is specific to Heart Rate Measurement.这是特定于心率测量的。
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     * 检索连接设备上支持的GATT服务的列表
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
