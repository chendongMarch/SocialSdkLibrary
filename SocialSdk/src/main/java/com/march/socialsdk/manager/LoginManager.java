package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.helper.AuthTokenKeeper;
import com.march.socialsdk.helper.PlatformLog;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.uikit.ActionActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CreateAt : 2017/5/19
 * Describe : 登陆管理类，使用该类进行登陆操作
 *
 * @author chendong
 */
public class LoginManager extends BaseManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    public static final int TARGET_QQ     = 0x21;
    public static final int TARGET_WECHAT = 0x22;
    public static final int TARGET_SINA   = 0x23;

    private static OnLoginListener sOnLoginListener;

    @IntDef({TARGET_QQ, TARGET_WECHAT, TARGET_SINA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginTarget {

    }

    /**
     * 开始登陆，供外面使用
     *
     * @param context       context
     * @param loginTarget   登陆类型
     * @param loginListener 登陆监听
     */
    public static void login(Context context, @LoginTarget int loginTarget, OnLoginListener loginListener) {


        sOnLoginListener = loginListener;
        buildPlatform(context, loginTarget);
        if (!getPlatform().isInstall()) {
            loginListener.onFailure(new SocialException(SocialException.CODE_NOT_INSTALL));
            return;
        }
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_LOGIN);
        intent.putExtra(KEY_LOGIN_TARGET, loginTarget);
        context.startActivity(intent);
        if (context instanceof Activity)
            ((Activity) context).overridePendingTransition(0, 0);
    }


    /**
     * 激活登陆
     *
     * @param activity activity
     */
    public static void _actionLogin(final Activity activity) {
        Intent intent = activity.getIntent();
        int actionType = intent.getIntExtra(KEY_ACTION_TYPE, INVALID_PARAM);
        int loginTarget = intent.getIntExtra(KEY_LOGIN_TARGET, INVALID_PARAM);
        if (actionType == INVALID_PARAM) {
            PlatformLog.e(TAG, "actionType无效");
            return;
        }
        if (actionType != ACTION_TYPE_LOGIN) return;
        if (loginTarget == INVALID_PARAM) {
            PlatformLog.e(TAG, "shareTargetType无效");
            return;
        }
        if (sOnLoginListener == null) {
            PlatformLog.e(TAG, "请设置 OnLoginListener");
        }
        if (getPlatform() == null) {
            return;
        }
        getPlatform().login(activity, getOnLoginListenerWrap(activity));
    }


    private static OnLoginListener getOnLoginListenerWrap(final Activity activity) {
        return new OnLoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onLoginSucceed(LoginResult loginResult) {
                sOnLoginListener.onLoginSucceed(loginResult);
                finishProcess(activity);
            }

            @Override
            public void onCancel() {
                sOnLoginListener.onCancel();
                finishProcess(activity);
            }

            @Override
            public void onFailure(SocialException e) {
                sOnLoginListener.onFailure(e);
                finishProcess(activity);
            }
        };
    }

    public void clearAllToken(Context context){
        AuthTokenKeeper.clearToken(context,TARGET_QQ);
        AuthTokenKeeper.clearToken(context,TARGET_WECHAT);
        AuthTokenKeeper.clearToken(context,TARGET_SINA);
    }

    public void clearToken(Context context,@LoginTarget int loginTarget){
        AuthTokenKeeper.clearToken(context,loginTarget);
    }
}
