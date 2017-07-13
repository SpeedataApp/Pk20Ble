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

    //10进制转16进制
    public static String IntToHex(int n) {
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15)
                ch[nIndex] = 'F';
            else if (k == 14)
                ch[nIndex] = 'E';
            else if (k == 13)
                ch[nIndex] = 'D';
            else if (k == 12)
                ch[nIndex] = 'C';
            else if (k == 11)
                ch[nIndex] = 'B';
            else if (k == 10)
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char) ('0' + k);
            nIndex++;
            if (m == 0)
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    //16进制转10进制
    public static int HexToInt(String strHex) {
        int nResult = 0;
        if (!IsHex(strHex))
            return nResult;
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (GetHex(ch) * GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //计算16进制对应的数值
    public static int GetHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return (int) (ch - '0');
        if (ch >= 'a' && ch <= 'f')
            return (int) (ch - 'a' + 10);
        if (ch >= 'A' && ch <= 'F')
            return (int) (ch - 'A' + 10);
        throw new Exception("error param");
    }

    //计算幂
    public static int GetPower(int nValue, int nCount) throws Exception {
        if (nCount < 0)
            throw new Exception("nCount can't small than 1!");
        if (nCount == 0)
            return 1;
        int nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }

    //判断是否是16进制数
    public static boolean IsHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))
                continue;
            return false;
        }
        return true;
    }
}
