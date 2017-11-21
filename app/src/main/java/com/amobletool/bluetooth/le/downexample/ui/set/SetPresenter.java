package com.amobletool.bluetooth.le.downexample.ui.set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.SystemClock;
import android.text.TextUtils;

import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import speedata.com.blelib.bean.SpeedataData;
import speedata.com.blelib.bean.YouSuData;
import speedata.com.blelib.bean.ZiKuData;
import speedata.com.blelib.utils.PK20Utils;

import static com.amobletool.bluetooth.le.downexample.MyApp.mNotifyCharacteristic3;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SetPresenter extends BasePresenterImpl<SetContract.View> implements SetContract.Presenter {

    private Timer timer;

    @Override
    public void setTime() {
        long currentTimeMillis = System.currentTimeMillis();
        String setTimeData = PK20Utils.getSetTimeData(currentTimeMillis);
        if (mNotifyCharacteristic3 != null) {
            MyApp.getInstance().writeCharacteristic3(setTimeData);
            MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                @Override
                public void getData(String data) {
                    int checkSetTimeBackData = PK20Utils.checkSetTimeBackData(data);
                    seeResult(checkSetTimeBackData);
                }
            });
        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
        }

    }

    //检验返回的数据
    private void seeResult(int jiaoYanData) {
        if (jiaoYanData == 0) {
            EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "设置失败"));
        }
    }


    private volatile int count = 0;
    private volatile int i = 0;
    private volatile boolean isSend = false;

    private int checkMoreResult(String data, String s1, String s2, boolean isZiku) {
        int checkBackData = 0;
        if (isZiku) {
            checkBackData = PK20Utils.checkSetZiKuBackData(data);
        } else {
            checkBackData = PK20Utils.checkSetNameBackData(data);
        }
        count++;
        if (checkBackData == -1 || checkBackData == -2) {
            sendData(s1, s2);
            return -1;
        } else if (checkBackData == -4) {
            MyApp.getInstance().writeCharacteristic3(s1);
            return -1;
        } else if (checkBackData == -5) {
            MyApp.getInstance().writeCharacteristic3(s2);
            return -1;
        } else {
            return 0;
        }
    }

    //设置体积重比例
    @Override
    public void setBili(String s) {
        try {
            int parseInt = Integer.parseInt(s);
            String setRatioData = PK20Utils.getSetRatioData(parseInt);
            if (TextUtils.isEmpty(setRatioData)) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
                return;
            }
            if (mNotifyCharacteristic3 != null) {
                MyApp.getInstance().writeCharacteristic3(setRatioData);
                MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                    @Override
                    public void getData(String data) {
                        int checkSetRatioBackData = PK20Utils.checkSetRatioBackData(data);
                        seeResult(checkSetRatioBackData);
                    }
                });
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
        }
    }


    //设置字库
    @Override
    public void setZiKu(final Activity activity, final ProgressDialog progressDialog) {
        count = 0;
        i = 0;
        if (mNotifyCharacteristic3 != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendData(ZiKuData.getData1(0), ZiKuData.getData2(0));
                    MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                        @Override
                        public void getData(final String data) {
//                            timer.cancel();
                            isSend = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(50);
                                    if (count > 5) {
                                        EventBus.getDefault().post(new MsgEvent("Notification"
                                                , "失败次数过多，请检查设备状态"));
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.cancel();
                                            }
                                        });
                                        return;
                                    }
                                    int result = checkMoreResult(data, ZiKuData.getData1(i),
                                            ZiKuData.getData2(i), true);
                                    if (result == 0) {
                                        count = 0;
                                        i++;
                                        if (i == 69) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.cancel();
                                                }
                                            });
                                            EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
                                            return;
                                        }
                                        sendData(ZiKuData.getData1(i), ZiKuData.getData2(i));
                                    }
                                }
                            }).start();

                        }
                    });
                }
            }).start();

        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            progressDialog.cancel();
        }
    }

    @Override
    public void setClean() {
        String cleanData = PK20Utils.getCleanData();
        if (mNotifyCharacteristic3 != null) {
            MyApp.getInstance().writeCharacteristic3(cleanData);
            MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                @Override
                public void getData(String data) {
                    int checkCleanBackData = PK20Utils.checkCleanBackData(data);
                    seeResult(checkCleanBackData);
                }
            });
        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
        }
    }

    @Override
    public void setYousu(final Activity activity, final ProgressDialog progressDialog) {
        count = 0;
        i = 0;
        if (mNotifyCharacteristic3 != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendData(YouSuData.getData1(0), YouSuData.getData2(0));
                    MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                        @Override
                        public void getData(final String data) {
                            isSend = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(50);
                                    if (count > 5) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.cancel();
                                            }
                                        });
                                        EventBus.getDefault().post(new MsgEvent("Notification"
                                                , "失败次数过多，请检查设备状态"));
                                        return;
                                    }
                                    int result = checkMoreResult(data, YouSuData.getData1(i),
                                            YouSuData.getData2(i), false);
                                    if (result == 0) {
                                        count = 0;
                                        i++;
                                        if (i == 4) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.cancel();
                                                }
                                            });
                                            EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
                                            return;
                                        }
                                        sendData(YouSuData.getData1(i), YouSuData.getData2(i));
                                    }
                                }
                            }).start();

                        }
                    });
                }
            }).start();

        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            progressDialog.cancel();
        }
    }

    @Override
    public void setSpeedata(final Activity activity, final ProgressDialog progressDialog) {
        count = 0;
        i = 0;
        if (mNotifyCharacteristic3 != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendData(SpeedataData.getData1(0), SpeedataData.getData2(0));
                    MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                        @Override
                        public void getData(final String data) {
                            isSend = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(50);
                                    if (count > 5) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.cancel();
                                            }
                                        });
                                        EventBus.getDefault().post(new MsgEvent("Notification"
                                                , "失败次数过多，请检查设备状态"));
                                        return;
                                    }
                                    int result = checkMoreResult(data, SpeedataData.getData1(i),
                                            SpeedataData.getData2(i), false);
                                    if (result == 0) {
                                        count = 0;
                                        i++;
                                        if (i == 4) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.cancel();
                                                }
                                            });
                                            EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
                                            return;
                                        }
                                        sendData(SpeedataData.getData1(i), SpeedataData.getData2(i));
                                    }
                                }
                            }).start();

                        }
                    });
                }
            }).start();

        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            progressDialog.cancel();
        }
    }

    @Override
    public void setCleanFlash() {
        String cleanData = PK20Utils.getCleanFlashData();
        if (mNotifyCharacteristic3 != null) {
            MyApp.getInstance().writeCharacteristic3(cleanData);
            MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                @Override
                public void getData(String data) {
                    int checkCleanFlashBackData = PK20Utils.checkCleanFlashBackData(data);
                    seeResult(checkCleanFlashBackData);
                }
            });
        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
        }
    }

    @Override
    public void setLeastBili(String s) {
        try {
            int parseInt = Integer.parseInt(s);
            String setRatioData = PK20Utils.getSetLeastRatioData(parseInt);
            if (TextUtils.isEmpty(setRatioData)) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
                return;
            }
            if (mNotifyCharacteristic3 != null) {
                MyApp.getInstance().writeCharacteristic3(setRatioData);
                MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                    @Override
                    public void getData(String data) {
                        int checkSetRatioBackData = PK20Utils.checkSetLeastRatioBackData(data);
                        seeResult(checkSetRatioBackData);
                    }
                });
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
        }
    }

    private void sendData(String s1, String s2) {
        MyApp.getInstance().writeCharacteristic3(s1);
        SystemClock.sleep(300);
        MyApp.getInstance().writeCharacteristic3(s2);
//        timer = new Timer();
//        mTimerTask mTimerTask = new mTimerTask(s1, s2);
//        timer.schedule(mTimerTask, 2000, 2000);
    }


    class mTimerTask extends TimerTask {
        String data1;
        String data2;

        public mTimerTask(String s, String s2) {
            data1 = s;
            data2 = s2;
        }

        @Override
        public void run() {
            if (mNotifyCharacteristic3 != null && !isSend) {
                sendData(data1, data2);
            }
        }
    }
}
