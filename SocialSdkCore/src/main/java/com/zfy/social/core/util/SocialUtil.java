package com.zfy.social.core.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.model.SocialBuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * CreateAt : 2018/12/21
 * Describe :
 *
 * @author chendong
 */
public class SocialUtil {

    public static final String TAG = "SocialSdk";

    public static boolean isAppCachePath(Context context, String path) {
        return path.contains(context.getPackageName());
    }

    public static boolean hasPermission(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
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
    public static boolean isAnyEmpty(String... strs) {
        for (String str : strs) {
            if (str == null || TextUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    public static Uri fromFile(Context context, File file) {
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static void e(String tag, String msg) {
        if (SocialSdk.getConfig().isDebug()) {
            Log.e(TAG + "|" + tag, msg);
        }
    }

    public static void t(String tag, Throwable throwable) {
        Log.e(TAG + "|" + tag, throwable.getMessage(), throwable);
    }

    public static void json(String tag, String json) {
        if (!SocialSdk.getConfig().isDebug()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (json == null || json.trim().length() == 0) {
            sb.append("json isEmpty => ").append(json);
        } else {
            try {
                json = json.trim();
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    sb.append(jsonObject.toString(2));
                } else if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    sb.append(jsonArray.toString(2));
                } else {
                    sb.append("json 格式错误 => ").append(json);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sb.append("json formatError => ").append(json);
            }
        }
        e(tag, sb.toString());
    }

    public static SocialBuildConfig parseBuildConfig() {
        try {
            Object inst = Class.forName("com.zfy.social.config.SocialBuildConfig").newInstance();
            String object2Json = JsonUtil.getObject2Json(inst);
            Log.e("chendong", object2Json);
            SocialBuildConfig buildConfig = JsonUtil.getObject(object2Json, SocialBuildConfig.class);
            if (buildConfig != null) {
                Log.e("chendong", buildConfig.toString());
            }
            return buildConfig;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
