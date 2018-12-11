package com.xfhy.photowall.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by feiyang on 2018/12/11 10:11
 * Description : MD5加密工具
 */
public class MD5Util {
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            //出错时  使用key的hashCode
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }
}
