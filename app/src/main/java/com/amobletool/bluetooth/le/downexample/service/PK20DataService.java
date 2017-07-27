package com.amobletool.bluetooth.le.downexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amobletool.bluetooth.le.downexample.MyApp;

import static com.amobletool.bluetooth.le.downexample.MyApp.mNotifyCharacteristic6;

/**
 * Created by 张明_ on 2017/7/19.
 */

public class PK20DataService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mNotifyCharacteristic6 != null) {
            MyApp.getInstance().setCharacteristicNotification6(mNotifyCharacteristic6,true);
        }
    }
}
