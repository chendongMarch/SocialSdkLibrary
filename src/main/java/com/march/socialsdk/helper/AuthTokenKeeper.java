package com.march.socialsdk.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.march.socialsdk.R;
import com.march.socialsdk.manager.LoginManager;
import com.march.socialsdk.model.token.QQAccessToken;
import com.march.socialsdk.model.token.SinaAccessToken;
import com.march.socialsdk.model.token.WeChatAccessToken;
import com.march.socialsdk.platform.Target;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * CreateAt : 2016/12/6
 * Describe : 授权token存储管理
 *
 * @author chendong
 */

public class AuthTokenKeeper {

    public static final String TOKEN_STORE = "TOKEN_STORE";

    public static final String WECHAT_TOKEN_KEY = "WECHAT_TOKEN_KEY";
    public static final String SINA_TOKEN_KEY   = "SINA_TOKEN_KEY";
    public static final String QQ_TOKEN_KEY     = "QQ_TOKEN_KEY";

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(TOKEN_STORE + context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public static WeChatAccessToken getWxToken(Context context) {
        SharedPreferences sp = getSp(context);
        return GsonHelper.getObject(sp.getString(WECHAT_TOKEN_KEY, null), WeChatAccessToken.class);
    }

    public static void saveWxToken(Context context, WeChatAccessToken wxResponse) {
        SharedPreferences sp = getSp(context);
        String tokenJson = GsonHelper.getObject2Json(wxResponse);
        sp.edit().putString(WECHAT_TOKEN_KEY, tokenJson).apply();
    }

    public static QQAccessToken getQQToken(Context context) {
        SharedPreferences sp = getSp(context);
        return GsonHelper.getObject(sp.getString(QQ_TOKEN_KEY, null), QQAccessToken.class);
    }

    public static void saveQQToken(Context context, QQAccessToken qqAccessToken) {
        SharedPreferences sp = getSp(context);
        String tokenJson = GsonHelper.getObject2Json(qqAccessToken);
        sp.edit().putString(QQ_TOKEN_KEY, tokenJson).apply();
    }

    public static Oauth2AccessToken getWbToken(Context context) {
        SharedPreferences sp = getSp(context);
        return GsonHelper.getObject(sp.getString(SINA_TOKEN_KEY, null), Oauth2AccessToken.class);
    }

    public static void saveWbToken(Context context, Oauth2AccessToken token) {
        SharedPreferences sp = getSp(context);
        String tokenJson = GsonHelper.getObject2Json(new SinaAccessToken(token));
        sp.edit().putString(SINA_TOKEN_KEY, tokenJson).apply();
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
