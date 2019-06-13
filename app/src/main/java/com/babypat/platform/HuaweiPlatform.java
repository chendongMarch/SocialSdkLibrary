package com.babypat.platform;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.model.LoginObj;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.AbsPlatform;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;

/**
 * CreateAt : 2018/12/27
 * Describe :
 *
 * @author chendong
 */
public class HuaweiPlatform extends AbsPlatform {

    public static final int PLATFORM_HUAWEI = 107;

    public static final int LOGIN_HUAWEI = 203;

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            return new HuaweiPlatform(context, null, null, target);
        }

        @Override
        public int getPlatformTarget() {
            return PLATFORM_HUAWEI;
        }

        @Override
        public boolean checkShareTarget(int shareTarget) {
            return false;
        }

        @Override
        public boolean checkLoginTarget(int loginTarget) {
            return loginTarget == LOGIN_HUAWEI;
        }
    }

    public HuaweiPlatform(Context context, String appId, String appName, int target) {
        super(context, appId, appName, target);
    }

    @Override
    protected void dispatchShare(Activity activity, int i, ShareObj shareObj) {

    }

    @Override
    public Class getUIKitClazz() {
        return null;
    }

    @Override
    public boolean isInstall(Context context) {
        return true;
    }

    @Override
    public void login(Activity act, int target, LoginObj obj, OnLoginStateListener listener) {
        Toast.makeText(act, "模拟扩展新平台，华为登录成功", Toast.LENGTH_LONG).show();
        listener.onState(act, LoginResult.successOf(LOGIN_HUAWEI, null, null));
    }


}
