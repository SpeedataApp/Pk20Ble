package com.amobletool.bluetooth.le.downexample.ui.set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.amobletool.bluetooth.le.downexample.MyApp;
import com.amobletool.bluetooth.le.downexample.bean.MsgEvent;
import com.amobletool.bluetooth.le.downexample.mvp.BasePresenterImpl;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import speedata.com.blelib.bean.ZiKuData;
import speedata.com.blelib.utils.PK20Utils;

import static com.amobletool.bluetooth.le.downexample.MyApp.cn;
import static com.amobletool.bluetooth.le.downexample.MyApp.mNotifyCharacteristic3;


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
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
            }

        }

    }

    //检验返回的数据
    private void seeResult(int jiaoYanData) {
        if (jiaoYanData == 0) {
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "成功"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Success"));
            }
        } else {
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "失败"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Failed"));
            }
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
            checkBackData = PK20Utils.checkSetLOGOBackData(data);
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

    private int checkNameResult(String data, String s1, String s2) {
        int checkBackData = PK20Utils.checkSetNameBackData(data);
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
                if (cn) {
                    EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
                } else {
                    EventBus.getDefault().post(new MsgEvent("Notification", "Please enter an effective ratio."));
                }
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
                if (cn) {
                    EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
                } else {
                    EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please enter an effective ratio."));
            }
        }
    }


    //设置字库
    @Override
    public void setZiKu(final Activity activity, final ProgressDialog progressDialog) {
        Log.d("ZM", "setZiKu: 开始");
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
                                        if (cn) {
                                            EventBus.getDefault().post(new MsgEvent("Notification", "失败次数过多，请检查设备状态"));
                                        } else {
                                            EventBus.getDefault().post(new MsgEvent("Notification", "Too many failures, please check the status of the equipment."));
                                        }
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
                                            if (cn) {
                                                EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
                                            } else {
                                                EventBus.getDefault().post(new MsgEvent("Notification", "Success"));
                                            }
                                            return;
                                        }
                                        sendData(ZiKuData.getData1(i), ZiKuData.getData2(i));
                                        Log.d("ZM", "run: " + i);
                                    }
                                }
                            }).start();

                        }
                    });
                }
            }).start();

        } else {
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
            }
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
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
            }
        }
    }


    @Override
    public void setLogo(final Activity activity, final ProgressDialog progressDialog, String logo) {
        final List<String> logoData = PK20Utils.getLOGOData(activity, logo);
        count = 0;
        i = 0;
        if (mNotifyCharacteristic3 != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendData(logoData.get(0), logoData.get(1));
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
                                        if (cn) {
                                            EventBus.getDefault().post(new MsgEvent("Notification", "失败次数过多，请检查设备状态"));
                                        } else {
                                            EventBus.getDefault().post(new MsgEvent("Notification", "Too many failures, please check the status of the equipment."));
                                        }
                                        return;
                                    }
                                    int result = checkMoreResult(data, logoData.get(i), logoData.get(i + 1), false);
                                    if (result == 0) {
                                        count = 0;
                                        i = i + 2;
                                        if (i == logoData.size()) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.cancel();
                                                }
                                            });
                                            if (cn) {
                                                EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
                                            } else {
                                                EventBus.getDefault().post(new MsgEvent("Notification", "Success"));
                                            }
                                            return;
                                        }
                                        sendData(logoData.get(i), logoData.get(i + 1));
                                    }
                                }
                            }).start();

                        }
                    });
                }
            }).start();

        } else {
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
            }
            progressDialog.cancel();
        }
    }

    /**
     * 设置操作员姓名
     *
     * @param activity
     * @param progressDialog
     * @param name
     */
    @Override
    public void setWorkerName(final Activity activity, final ProgressDialog progressDialog, String name) {
        final List<String> nameData = PK20Utils.getNameSetData(name);
        List<String> nameDianZhen = PK20Utils.getNameDianZhen(activity, name);
        nameData.addAll(nameDianZhen);
        count = 0;
        i = 0;
        if (mNotifyCharacteristic3 != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendData(nameData.get(0), nameData.get(1));
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
                                        if (cn) {
                                            EventBus.getDefault().post(new MsgEvent("Notification", "失败次数过多，请检查设备状态"));
                                        } else {
                                            EventBus.getDefault().post(new MsgEvent("Notification", "Too many failures, please check the status of the equipment."));
                                        }
                                        return;
                                    }
                                    int result = checkNameResult(data, nameData.get(i), nameData.get(i + 1));
                                    if (result == 0) {
                                        count = 0;
                                        i = i + 2;
                                        if (i == nameData.size()) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.cancel();
                                                }
                                            });
                                            if (cn) {
                                                EventBus.getDefault().post(new MsgEvent("Notification", "设置成功"));
                                            } else {
                                                EventBus.getDefault().post(new MsgEvent("Notification", "Success"));
                                            }
                                            return;
                                        }
                                        sendData(nameData.get(i), nameData.get(i + 1));
                                    }
                                }
                            }).start();

                        }
                    });
                }
            }).start();

        } else {
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
            }
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
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
            }
        }
    }

    @Override
    public void setLeastBili(String s) {
        try {
            int parseInt = Integer.parseInt(s);
            if (parseInt > 10000) {
                if (cn) {
                    EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
                } else {
                    EventBus.getDefault().post(new MsgEvent("Notification", "Please enter an effective ratio."));
                }
                return;
            }
            String setRatioData = PK20Utils.getSetLeastRatioData(parseInt);
            if (TextUtils.isEmpty(setRatioData)) {
                if (cn) {
                    EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
                } else {
                    EventBus.getDefault().post(new MsgEvent("Notification", "Please enter an effective ratio."));
                }
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
                if (cn) {
                    EventBus.getDefault().post(new MsgEvent("Notification", "请连接设备"));
                } else {
                    EventBus.getDefault().post(new MsgEvent("Notification", "Please connect the equipment"));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            if (cn) {
                EventBus.getDefault().post(new MsgEvent("Notification", "请输入有效比例"));
            } else {
                EventBus.getDefault().post(new MsgEvent("Notification", "Please enter an effective ratio."));
            }
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
