package com.amobletool.bluetooth.le;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class AmoComActivity extends Activity {
    private final static String TAG = "DeviceScanActivity";// DeviceScanActivity.class.getSimpleName();
    private final String ACTION_NAME_RSSI = "AMOMCU_RSSI"; // 其他文件广播的定义必须一致
    private final String ACTION_CONNECT = "AMOMCU_CONNECT"; // 其他文件广播的定义必须一致
    private static final byte[] succeed_cmd = {(byte) 0xAA, (byte) 0xBB, 0X01, 0x00, 0x00, (byte) 0xBC};
    private static final byte[] err_cmd = {(byte) 0xAA, (byte) 0xBB, 0X01, 0x00, 0x00, (byte) 0xBB};

    static Handler mHandler = new Handler();

    // 根据rssi 值计算距离， 只是参考作用， 不准确---amomcu
    static final int rssibufferSize = 10;
    int[] rssibuffer = new int[rssibufferSize];
    int rssibufferIndex = 0;
    boolean rssiUsedFalg = false;
    private static byte scanLength;
    private static String longScanDatas;
    private static byte first;
    private static ListView listView;
    static MyAdapter myAdapter;
    private static String scanDats;
    private static infoEntity infoEntity;
    private TextView tvRssi;
    private static int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_datas_act);
        registerBoradcastReceiver();
        listView = (ListView) findViewById(R.id.lv_showdatas);
        tvRssi = (TextView) findViewById(R.id.tv_rssi);
        myAdapter = new MyAdapter(this);

        listView.setAdapter(myAdapter);
//        infoEntity = new infoEntity();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String mac_addr = bundle.getString("mac_addr");
        String char_uuid = bundle.getString("char_uuid");
        TextView tv_mac_addr = (TextView) this
                .findViewById(R.id.textview_mac_addr);
        TextView tv_char_uuid = (TextView) this
                .findViewById(R.id.textview_char_uuid);

        tv_mac_addr.setText(mac_addr);
        tv_char_uuid.setText(char_uuid);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mPosition = listView.getLastVisiblePosition();
//                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog alertDialog = new AlertDialog.Builder(AmoComActivity.this).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.dialog_layout);
                TextView tv_title = (TextView) window.findViewById(R.id.tv_dialog_title);
                tv_title.setText("详细信息");
                TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
                tv_message.setText("扫描数据："+infoEntity.getScanDatas()+"\n长度："+infoEntity.getL()+"mm\n宽度："+
     +infoEntity.getW()+"mm\n高度："+infoEntity.getH()+"mm\n重量："+infoEntity.getG()+"kg\n");
            }
        });

    }

    private static void showdatas(int num, String datas, int l, int w, int h, int g) {
        infoEntity = new infoEntity(num, datas, l, w, h, g);
        myAdapter.addDatas(infoEntity);
//        myAdapter.notifyDataSetChanged();
        listView.setSelection(mPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        w=0;
    }

    /**
     * 接收 rssi 的广播（信号强度
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ACTION_NAME_RSSI)) {
                int rssi = intent.getIntExtra("RSSI", 0);
                int rssi_avg = 0;

                if (true) {
                    rssibuffer[rssibufferIndex] = rssi;
                    rssibufferIndex++;

                    if (rssibufferIndex == rssibufferSize)
                        rssiUsedFalg = true;
                    rssibufferIndex = rssibufferIndex % rssibufferSize;

                    if (rssiUsedFalg == true) {
                        int rssi_sum = 0;
                        for (int i = 0; i < rssibufferSize; i++) {
                            rssi_sum += rssibuffer[i];
                        }
                        rssi_avg = rssi_sum / rssibufferSize;
                    }
                }
                tvRssi.setText(rssi_avg + "dbm");
            } else if (action.equals(ACTION_CONNECT)) {
                int status = intent.getIntExtra("CONNECT_STATUC", 0);
                if (status == 0) {
                    getActionBar().setTitle("已断开连接");
                    finish();
                } else {
                    getActionBar().setTitle("已连接");
                }
            }
        }
    };

    // 注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME_RSSI);
        myIntentFilter.addAction(ACTION_CONNECT);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    /**
     * 分包发送 每包最大18个字节
     *
     * @param bytes 发送的命令
     */
    private static void Send_Bytes(byte bytes[]) {
        // 分包发送 每包最大18个字节
        int total_len = bytes.length;
        int cur_pos = 0;
        int i = 0;
        while (cur_pos < total_len) {
            int lenSub = 0;
            if (total_len - cur_pos > 18)
                lenSub = 18;
            else
                lenSub = total_len - cur_pos;

            byte[] bytes_sub = new byte[lenSub];

            for (i = 0; i < lenSub; i++) {
                bytes_sub[i] = bytes[cur_pos + i];
            }
            cur_pos += lenSub;
            DeviceScanActivity.writeChar6_in_bytes(bytes_sub);//发送 命令
            // 延时 一会
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static int w=0;
    /**
     * 解析数据
     *
     * @param bytes 下位机传回的原始数据
     */
    private static void parseData(byte[] bytes) {
        int flag = 1;
        int length = bytes.length;


        outer:for (int i = 0; i < length; i++) {
            if (bytes[i] == -86 && bytes[length - 1] == 0) {
                if (bytes[i + 1] == 10) {  //扫头数据
                    if (flag == 2) {
                        flag = 1;
                        break outer;
                    }

                    scanLength = (byte) (bytes[2] - 1);
                    byte[] scanByte = new byte[scanLength];
                    if (scanLength > 15) {
                        byte[] longScanByte = new byte[16];
                        first = bytes[3];
                        System.arraycopy(bytes, i + 3, longScanByte, 0, 16);
                        longScanDatas = Utils.byteArrayToAscii(longScanByte);
                        break outer;
                    }
                    System.arraycopy(bytes, i + 3, scanByte, 0, scanLength);
                    byte scanCheck = bytes[length - 2];
                    byte sum = (byte) (scanByte[0] + scanByte[scanByte.length - 1]);
                    if (sum == scanCheck) {
                        Send_Bytes(succeed_cmd);
                        scanDats = Utils.byteArrayToAscii(scanByte);
//                        Text_Recv.append(" scan" + scanDats + "\n");

                        break outer;
                    } else {
                        Send_Bytes(err_cmd);
                        break outer;
                    }
                } else { //长宽高
                    byte[] lByte = new byte[2];
                    byte[] wByte = new byte[2];
                    byte[] hByte = new byte[2];
                    byte[] gByte = new byte[2];

                    if (bytes[1] == 11) {
                        if (flag == 2) {
                            flag = 1;
                            break outer;
                        }
                        flag++;
                        byte swhLength = (byte) (bytes[2] - 1);
                        byte[] lwhByte = new byte[swhLength];
                        System.arraycopy(bytes, i + 3, lwhByte, 0, swhLength);
                        byte lwhCheck = bytes[length - 2];
                        byte sum = (byte) (lwhByte[0] + lwhByte[lwhByte.length - 1]);
                        if (sum == lwhCheck) {
                            Send_Bytes(succeed_cmd);
                            System.arraycopy(lwhByte, 0, lByte, 0, lByte.length);
                            System.arraycopy(lwhByte, 2, wByte, 0, wByte.length);
                            System.arraycopy(lwhByte, 4, hByte, 0, hByte.length);
                            System.arraycopy(lwhByte, 6, gByte, 0, gByte.length);
//                            Text_Recv.append("长:" + Utils.byteArrayToInt(lByte) + "宽：" + Utils.byteArrayToInt(wByte)
//                                    + "高：" + Utils.byteArrayToInt(hByte) + "重：" + Utils.byteArrayToInt(gByte) + "\n");
//                            infoEntity.setScanDatas(scanDats);
//                            infoEntity.setNum(w++);
//                            infoEntity.setL(Utils.byteArrayToInt(lByte));
//                            infoEntity.setW(Utils.byteArrayToInt(wByte));
//                            infoEntity.setH(Utils.byteArrayToInt(hByte));
//                            infoEntity.setG(Utils.byteArrayToInt(gByte));
//                            myAdapter.addDatas(infoEntity);
//        myAdapter.notifyDataSetChanged();
//                            listView.setSelection(mPosition);
                            showdatas(w++,scanDats, Utils.byteArrayToInt(lByte), Utils.byteArrayToInt(wByte), Utils.byteArrayToInt(hByte)
                                    , Utils.byteArrayToInt(gByte));
                            break outer;
                        } else {
                            Send_Bytes(err_cmd);
                            break outer;
                        }
                    }
                }
            } else if (bytes[i] != -86 && bytes[length - 1] == 0) {//长度大于十五的扫头数据

                byte[] longScanByte2 = new byte[scanLength - 16];
                System.arraycopy(bytes, 0, longScanByte2, 0, longScanByte2.length);
                byte sum = (byte) (longScanByte2[longScanByte2.length - 1] + first);
                byte check = bytes[length - 2];
                if (check == sum) {
                    Send_Bytes(succeed_cmd);
                    String s = Utils.byteArrayToAscii(longScanByte2);
                    scanDats = longScanDatas + s;
                    break outer;
                } else {
                    Send_Bytes(err_cmd);
                    break outer;
                }
            } else {
                Send_Bytes(err_cmd);
                break outer;
            }
        }

    }

    /**
     * 接受原始数据
     *
     * @param str
     * @param data
     * @param uuid
     */
    public static synchronized void char6_display(String str, final byte[] data,
                                                  String uuid) {
        Log.i(TAG, "char6_display str = " + str);
        Log.e("ddddddddddd", "char6_display: " + Arrays.toString(data));

        if (uuid.equals(DeviceScanActivity.UUID_HERATRATE)) {

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String TimeStr = formatter.format(curDate);
            byte[] ht = new byte[str.length()];
            // System.arraycopy(ht, Totol_Send_bytes,
            // Utils.hexStringToBytes(str), 0, str.length());

            String DisplayStr = "[" + TimeStr + "] " + "HeartRate : " + data[0]
                    + "=" + data[1];
            // Text_Recv.append(DisplayStr + "\r\n");
        } else if (uuid.equals(DeviceScanActivity.UUID_TEMPERATURE)) {
            // 温度测量

            byte[] midbytes = str.getBytes();
            String HexStr = Utils.bytesToHexString(midbytes);
            // Text_Recv.append(HexStr);
        } else if (uuid.equals(DeviceScanActivity.UUID_CHAR6)) { // amomcu 的串口透传

            SimpleDateFormat formatter = new SimpleDateFormat(
                    "HH:mm:ss ");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String TimeStr = formatter.format(curDate);

            String DisplayStr = "[" + TimeStr + "] " + str;
            String HexStr = Utils.bytesToHexString(data);

        }

        mHandler.post(new Runnable() {
            @Override
            public synchronized void run() {
                parseData(data);
            }
        });
    }

    public synchronized static String GetLastData() {
        String string = "";
        return string;
    }

    /**
     * @param send
     * @param recv
     */
    public synchronized static void update_display_send_recv_info(int send,
                                                                  int recv) {
        String info1 = String.format("发送%4d,接收%4d [字节]", send, recv);
    }
}