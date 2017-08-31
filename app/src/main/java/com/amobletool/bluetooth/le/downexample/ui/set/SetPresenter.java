package com.amobletool.bluetooth.le.downexample.ui.set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.SystemClock;

import com.amobletool.bluetooth.le.downexample.MsgEvent;
import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.YouSuData;
import com.amobletool.bluetooth.le.downexample.bean.ZiKuData;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;
import com.amobletool.bluetooth.le.downexample.utils.DataManageUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
        Date date = new Date(currentTimeMillis);
        String format = simpleDateFormat.format(date);
        String[] split = format.split(" ");
        String[] spHMS = split[1].split(":");
        String[] spYMD = split[0].split("-");
        String week = DataManageUtils.backWeek(split[2]);
        String year = spYMD[0].substring(2, 4);
        String jiaoYan = DataManageUtils.getJiaoYan(spHMS[2], year);
        final String result = "FF0C08" + spHMS[2] + spHMS[1] + spHMS[0] + week + spYMD[2]
                + spYMD[1] + year + "0000000000000000" + jiaoYan + "00";
        if (mNotifyCharacteristic3 != null) {
            MyApp.getInstance().writeCharacteristic3(result);
            MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                @Override
                public void getData(String data) {
                    seeResult(data, "0C");
                }
            });
        } else {
            EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
        }

    }

    //检验返回的数据
    private void seeResult(String data, String result) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", result);
        if (jiaoYanData==0){
            EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
        }else {
            EventBus.getDefault().post(new MsgEvent("Notification", "设置失败"));
        }
    }


    private volatile int count = 0;
    private volatile int i = 0;
    private volatile boolean isSend = false;

    private int seeZikuResult(String data, String s1, String s2,String zhiling) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", zhiling);
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            sendData(s1, s2);
            count++;
            return -1;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            count++;
            if (splitData[3].equals("B1")) {
                MyApp.getInstance().writeCharacteristic3(s1);
            } else if (splitData[3].equals("B2")) {
                MyApp.getInstance().writeCharacteristic3(s2);
            }
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
            if (parseInt > 60000) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
                return;
            }
            int high = parseInt >> 8 & 0xff;
            int low = parseInt >> 0 & 0xff;
            String highStr = Integer.toHexString(high);
            String lowStr = Integer.toHexString(low);
            String jiaoYan = DataManageUtils.getJiaoYan(highStr, lowStr);
            final String result = "FF0D03" + highStr + lowStr + "00000000000000000000000000" + jiaoYan + "00";
            if (mNotifyCharacteristic3 != null) {
                MyApp.getInstance().writeCharacteristic3(result);
                MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                    @Override
                    public void getData(String data) {
                        seeResult(data, "0D");
                    }
                });
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
            return;
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
                                    int result = seeZikuResult(data, ZiKuData.getData1(i), ZiKuData.getData2(i),"0B");
                                    if (result == 0) {
                                        count = 0;
                                        i++;
                                        if (i == 68) {
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
        final String result = "FF0E010000000000000000000000000000000000";
        if (mNotifyCharacteristic3 != null) {
            MyApp.getInstance().writeCharacteristic3(result);
            MyApp.getInstance().setGetBluetoothLeDataListener(new MyApp.getBluetoothLeDataListener() {
                @Override
                public void getData(String data) {
                    seeResult(data, "0E");
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
                                    int result = seeZikuResult(data, YouSuData.getData1(i), YouSuData.getData2(i),"10");
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
