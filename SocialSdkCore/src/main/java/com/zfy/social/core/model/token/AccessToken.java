package com.zfy.social.core.model.token;

import android.content.Context;
import android.content.SharedPreferences;

import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.util.JsonUtil;

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

    public static final String WECHAT_TOKEN_KEY = "WECHAT_TOKEN_KEY";
    public static final String SINA_TOKEN_KEY   = "SINA_TOKEN_KEY";
    public static final String QQ_TOKEN_KEY     = "QQ_TOKEN_KEY";
    public static final String KEY_TIME = "_KEY_TIME";

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(TOKEN_STORE + context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static <T> T getToken(Context context, String key, Class<T> tokenClazz) {
        if (SocialSdk.getConfig().getTokenExpiresHoursMs() <= 0) {
            return null;
        }
        SharedPreferences sp = getSp(context);
        long time = sp.getLong(key + KEY_TIME, -1);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < SocialSdk.getConfig().getTokenExpiresHoursMs()) {
            return JsonUtil.getObject(sp.getString(key, null), tokenClazz);
        } else {
            return null;
        }
    }

    public static void saveToken(final Context context, final String key, final Object token) {
        if (SocialSdk.getConfig().getTokenExpiresHoursMs() <= 0) {
            return;
        }
        SocialSdk.getExecutorService().execute(() -> {
            try {
                SharedPreferences sp = getSp(context);
                String tokenJson = JsonUtil.getObject2Json(token);
                sp.edit().putString(key, tokenJson).apply();
                sp.edit().putLong(key + KEY_TIME, System.currentTimeMillis()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    // 清理平台 token
    public static void clearToken(Context context, @Target.LoginTarget int platform) {
        String key = null;
        switch (platform) {
            case Target.LOGIN_QQ:
                key = QQ_TOKEN_KEY;
                break;
            case Target.LOGIN_WB:
                key = SINA_TOKEN_KEY;
                break;
            case Target.LOGIN_WX:
                key = WECHAT_TOKEN_KEY;
                break;
        }
        if (key != null) {
            SharedPreferences.Editor edit = getSp(context).edit();
            edit.remove(key).apply();
        }
    }
}
