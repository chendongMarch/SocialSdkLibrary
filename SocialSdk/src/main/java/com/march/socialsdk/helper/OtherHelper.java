package com.march.socialsdk.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.Closeable;
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

public class OtherHelper {

    public static final String TAG = OtherHelper.class.getSimpleName();

    public static Long String2Long(String str) {
        Long data = 0L;
        try {
            data = Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    public static boolean isEmpty(String... strings) {
        boolean isEmpty = false;
        for (String string : strings) {
            if (TextUtils.isEmpty(string)) {
                isEmpty = true;
                break;
            }
        }
        return isEmpty;
    }


    public static boolean isAppInstall(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }
//      try {
//          context.getPackageManager().getPackageInfo(pkgName,PackageManager.GET_RESOLVED_FILTER)
//      }catch (Exception e){
//          e.printStackTrace();
//      }
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


    public static void closeStream(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


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
