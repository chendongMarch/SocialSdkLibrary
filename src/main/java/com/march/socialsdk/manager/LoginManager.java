package com.march.socialsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.utils.TokenStoreUtils;
import com.march.socialsdk.utils.LogUtils;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.uikit.ActionActivity;

/**
 * CreateAt : 2017/5/19
 * Describe : 登陆管理类，使用该类进行登陆操作
 *
 * @author chendong
 */
public class LoginManager extends BaseManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    private static OnLoginListener sOnLoginListener;

    /**
     * 开始登陆，供外面使用
     *
     * @param context       context
     * @param loginTarget   登陆类型
     * @param loginListener 登陆监听
     */
    public static void login(Context context, @Target.LoginTarget int loginTarget, OnLoginListener loginListener) {
        sOnLoginListener = loginListener;
        buildPlatform(context, loginTarget);
        if (!getCurrentPlatform().isInstall()) {
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
            LogUtils.e(TAG, "_actionLogin actionType无效");
            return;
        }
        if (actionType != ACTION_TYPE_LOGIN) {
            return;
        }
        if (loginTarget == INVALID_PARAM) {
            LogUtils.e(TAG, "shareTargetType无效");
            return;
        }
        if (sOnLoginListener == null) {
            LogUtils.e(TAG, "请设置 OnLoginListener");
        }
        if (getCurrentPlatform() == null) {
            return;
        }
        getCurrentPlatform().login(activity, getOnLoginListenerWrap(activity));
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

    public static void clearAllToken(Context context) {
        TokenStoreUtils.clearToken(context, Target.LOGIN_QQ);
        TokenStoreUtils.clearToken(context, Target.LOGIN_WX);
        TokenStoreUtils.clearToken(context, Target.LOGIN_WB);
    }

    public static void clearToken(Context context, @Target.LoginTarget int loginTarget) {
        TokenStoreUtils.clearToken(context, loginTarget);
    }
}
