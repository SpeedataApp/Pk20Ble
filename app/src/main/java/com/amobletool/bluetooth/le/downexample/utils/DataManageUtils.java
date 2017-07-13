package com.amobletool.bluetooth.le.downexample.utils;

/**
 * Created by 张明_ on 2017/7/5.
 */

public class DataManageUtils {
    //获取网点代码
    public static byte[] getBranchCode(byte[] data) {
        byte b = data[3];
        if (b == (byte) 0xB1) {
            byte[] result = new byte[10];
            try {
                System.arraycopy(data, 4, result, 0, 10);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取中心条码
    public static byte[] getCenterCode(byte[] data, byte[] data2) {
        byte b = data[3];
        byte c = data2[0];
        if (b == (byte) 0xB1 && c == (byte) 0xB2) {
            byte[] result = new byte[10];
            try {
                System.arraycopy(data, 14, result, 0, 5);
                System.arraycopy(data2, 1, result, 5, 5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    public static int L = 1;
    public static int W = 3;
    public static int H = 5;
    public static int V = 7;
    public static int G = 9;

    //获取长宽高体积重量
    public static byte[] getLWHVG(byte[] data, int srcPos) {
        byte b = data[0];
        if (b == (byte) 0xB3) {
            byte[] result = new byte[2];
            try {
                System.arraycopy(data, srcPos, result, 0, 2);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取时间日期
    public static byte[] getTime(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB3) {
            byte[] result = new byte[6];
            try {
                System.arraycopy(data, 11, result, 0, 6);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取用途
    public static byte[] getUse(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB3) {
            byte[] result = new byte[1];
            try {
                System.arraycopy(data, 17, result, 0, 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取快递单号
    public static byte[] getExpressCode(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB4 && c == (byte) 0xB5) {
            byte[] result = new byte[20];
            try {
                System.arraycopy(data, 1, result, 0, 18);
                System.arraycopy(data2, 1, result, 18, 2);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取主单号
    public static byte[] getMainCode(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB5 && c == (byte) 0xB6) {
            byte[] result = new byte[20];
            try {
                System.arraycopy(data, 3, result, 0, 16);
                System.arraycopy(data2, 1, result, 16, 4);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取子单号
    public static byte[] getSonCode(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB6 && c == (byte) 0xB7) {
            byte[] result = new byte[20];
            try {
                System.arraycopy(data, 5, result, 0, 14);
                System.arraycopy(data2, 1, result, 16, 4);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }

    //获取标记
    public static byte[] getFlag(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB7) {
            byte[] result = new byte[1];
            try {
                System.arraycopy(data, 15, result, 0, 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
        return null;
    }
}
