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

package com.amobletool.bluetooth.le.downexample.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class Utils {

	private static HashMap<Integer, String> serviceTypes = new HashMap();
	static {
		// Sample Services.
		serviceTypes.put(BluetoothGattService.SERVICE_TYPE_PRIMARY, "PRIMARY");
		serviceTypes.put(BluetoothGattService.SERVICE_TYPE_SECONDARY,
				"SECONDARY");
	}

	public static String getServiceType(int type) {
		return serviceTypes.get(type);
	}

	// -------------------------------------------
	private static HashMap<Integer, String> charPermissions = new HashMap();
	static {
		charPermissions.put(0, "UNKNOW");
		charPermissions
				.put(BluetoothGattCharacteristic.PERMISSION_READ, "READ");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED,
				"READ_ENCRYPTED");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM,
				"READ_ENCRYPTED_MITM");
		charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE,
				"WRITE");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED,
				"WRITE_ENCRYPTED");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM,
				"WRITE_ENCRYPTED_MITM");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED,
				"WRITE_SIGNED");
		charPermissions.put(
				BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM,
				"WRITE_SIGNED_MITM");
	}

	public static String getCharPermission(int permission) {
		return getHashMapValue(charPermissions, permission);
	}

	// -------------------------------------------
	private static HashMap<Integer, String> charProperties = new HashMap();
	static {

		charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST,
				"BROADCAST");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS,
				"EXTENDED_PROPS");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE,
				"INDICATE");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY,
				"NOTIFY");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE,
				"SIGNED_WRITE");
		charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
		charProperties.put(
				BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
				"WRITE_NO_RESPONSE");
	}

	public static String getCharPropertie(int property) {
		return getHashMapValue(charProperties, property);
	}

	// --------------------------------------------------------------------------
	private static HashMap<Integer, String> descPermissions = new HashMap();
	static {
		descPermissions.put(0, "UNKNOW");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ, "READ");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED,
				"READ_ENCRYPTED");
		descPermissions.put(
				BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM,
				"READ_ENCRYPTED_MITM");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE, "WRITE");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED,
				"WRITE_ENCRYPTED");
		descPermissions.put(
				BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM,
				"WRITE_ENCRYPTED_MITM");
		descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED,
				"WRITE_SIGNED");
		descPermissions.put(
				BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM,
				"WRITE_SIGNED_MITM");
	}

	public static String getDescPermission(int property) {
		return getHashMapValue(descPermissions, property);
	}

	private static String getHashMapValue(HashMap<Integer, String> hashMap,
                                          int number) {
		String result = hashMap.get(number);
		if (TextUtils.isEmpty(result)) {
			List<Integer> numbers = getElement(number);
			result = "";
			for (int i = 0; i < numbers.size(); i++) {
				result += hashMap.get(numbers.get(i)) + "|";
			}
		}
		return result;
	}

	/**
	 * 位运算结果的反推函数10 -> 2 | 8;
	 */
	static private List<Integer> getElement(int number) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < 32; i++) {
			int b = 1 << i;
			if ((number & b) > 0)
				result.add(b);
		}

		return result;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
    /**
     * byte[]->ascii String {0x71,0x72,0x73,0x41,0x42}->"qrsAB"
     *
     * @param cmds
     * @return
     */
    public static String byteArrayToAscii(byte[] cmds) {
        int tRecvCount = cmds.length;
        StringBuffer tStringBuf = new StringBuffer();
        String nRcvString;
        char[] tChars = new char[tRecvCount];
        for (int i = 0; i < tRecvCount; i++) {
            tChars[i] = (char) cmds[i];
        }
        tStringBuf.append(tChars);
        nRcvString = tStringBuf.toString(); // nRcvString从tBytes转成了String类型的"123"
        return nRcvString;
    }

	public static String bytesToString(byte[] src) {
		// byte[] ת string
		String res = new String(src);
		return res;
	}
	/**
	 * byte[]转int
	 *
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < bytes.length; i++) {
			int shift = (bytes.length - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}
	// HexString����>byte
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}
	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 *
	 * @param temp String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String temp) {
//		src.replace("\\s","");
		String src = temp.replace(" ", "");
		System.out.println(" src= " + src);
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 *
	 * @param src0 byte
	 * @param src1 byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		try {
			byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
					.byteValue();
			_b0 = (byte) (_b0 << 4);
			byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
					.byteValue();
			byte ret = (byte) (_b0 ^ _b1);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String b2hexs(byte[] b, int length) {
		String ret = "";

		for (int i = 0; i < length; ++i) {
			String hex = Integer.toHexString(b[i] & 255);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}

			ret = ret + hex.toUpperCase();
		}

		return ret;
	}
}
