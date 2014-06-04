package com.rapidftr.utils;

import android.util.Base64;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

@Implements(Base64.class)
public class ShadowBase64 {
    @RealObject
    private Base64 base64;

    @Implementation
    public static String encodeToString(byte[] bytes, int mode) {
        if (bytes == null)
            return "";
        StringBuffer result = new StringBuffer(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            appendHex(result, bytes[i]);
        }
        return result.toString();

    }

    @Implementation
    public static byte[] decode(String value, int mode) {
        int len = value.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(value.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;

    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
