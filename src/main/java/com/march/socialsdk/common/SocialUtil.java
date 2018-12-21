package com.march.socialsdk.common;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.march.socialsdk.SocialSdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * CreateAt : 2018/12/21
 * Describe :
 *
 * @author chendong
 */
public class SocialUtil {

    public static final String TAG = "SocialSdk";

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
}
