package com.zfy.social.core.model.token;

import android.content.Context;
import android.content.SharedPreferences;

import com.zfy.social.core.SocialOptions;
import com.zfy.social.core._SocialSdk;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.util.JsonUtil;
import com.zfy.social.core.util.SocialUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreateAt : 2017/5/21
 * Describe : token基类
 *
 * @author chendong
 */
public abstract class AccessToken {

    private String openid;//授权用户唯一标识。
    private String unionid;
    private String access_token;//接口调用凭证
    private long   expires_in;//access_token接口调用凭证超时时间，单位（秒）。

    public boolean isValid() {
        if (getLoginTarget() == Target.LOGIN_WX) {
            return getAccess_token() != null && getUnionid() != null;
        } else
            return getAccess_token() != null && getOpenid() != null;
    }

    public String getSocialId() {
        if (getLoginTarget() == Target.LOGIN_WX) {
            return unionid;
        } else
            return openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public abstract int getLoginTarget();


    @Override
    public String toString() {
        return "BaseAccessToken{" +
                "openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }


    //// 静态 token 存取

    private static final String TOKEN_STORE = "TOKEN_STORE";

    public static final String KEY_TIME = "_KEY_TIME";
    public static final String KEY_TOKEN = "_KEY_TOKEN";

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(TOKEN_STORE + context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static <T> T getToken(final Context context, final int target, final Class<T> tokenClazz) {
        SocialOptions opts = _SocialSdk.getInst().opts();
        if (opts.getTokenExpiresHoursMs() <= 0) {
            return null;
        }
        int platformTarget = SocialUtil.mapPlatformTarget(target);
        SharedPreferences sp = getSp(context);
        long time = sp.getLong(platformTarget + KEY_TIME, -1);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < opts.getTokenExpiresHoursMs()) {
            T object = JsonUtil.getObject(sp.getString(platformTarget + KEY_TOKEN, null), tokenClazz);
            return object;
        } else {
            return null;
        }
    }


    private static ExecutorService sService;

    public static void saveToken(final Context context, final int target, final Object token) {
        SocialOptions opts = _SocialSdk.getInst().opts();
        if (opts.getTokenExpiresHoursMs() <= 0) {
            return;
        }
        if (sService == null) {
            sService = Executors.newSingleThreadExecutor();
        }
        sService.execute(() -> {
            try {
                int platformTarget = SocialUtil.mapPlatformTarget(target);
                SharedPreferences sp = getSp(context);
                if (token == null) {
                    sp.edit().putString(platformTarget + KEY_TOKEN, "").apply();
                    sp.edit().putLong(platformTarget + KEY_TIME, 0).apply();
                    return;
                }
                String tokenJson = JsonUtil.getObject2Json(token);
                sp.edit().putString(platformTarget + KEY_TOKEN, tokenJson).apply();
                sp.edit().putLong(platformTarget + KEY_TIME, System.currentTimeMillis()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 清理平台 token
    public static void clearToken(Context context, int target) {
        saveToken(context, target, null);
    }
}
