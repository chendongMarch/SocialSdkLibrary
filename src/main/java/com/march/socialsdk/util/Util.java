package com.march.socialsdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * CreateAt : 2016/12/6
 * Describe : 未归类的帮助方法
 *
 * @author chendong
 */

public class Util {

    public static final String TAG = Util.class.getSimpleName();

    public static boolean hasPermission(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    // 任何一个为空 返回true
    public static boolean isAnyEmpty(String... strings) {
        boolean isEmpty = false;
        for (String string : strings) {
            if (TextUtils.isEmpty(string)) {
                isEmpty = true;
                break;
            }
        }
        return isEmpty;
    }

    // app 是否安装
    public static boolean isAppInstall(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        boolean result = false;
        for (PackageInfo info : packages) {
            if (TextUtils.equals(info.packageName.toLowerCase(), pkgName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    // 根据包名，打开对应app
    public static boolean openApp(Context context, String pkgName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 获取 md5
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuilder strBuf = new StringBuilder();
            for (byte anEncryption : encryption) {
                if (Integer.toHexString(0xff & anEncryption).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & anEncryption));
                } else {
                    strBuf.append(Integer.toHexString(0xff & anEncryption));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

}
