package com.zfy.social.core;

import android.content.Context;
import android.util.SparseArray;

import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.adapter.IRequestAdapter;
import com.zfy.social.core.adapter.impl.DefaultRequestAdapter;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.platform.system.ClipboardPlatform;
import com.zfy.social.core.platform.system.EmailPlatform;
import com.zfy.social.core.platform.system.SmsPlatform;
import com.zfy.social.core.util.SocialUtil;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class SocialSdk {

    private static SocialOptions sSocialOptions;

    static IJsonAdapter sJsonAdapter;
    static IRequestAdapter sRequestAdapter;

    private static SparseArray<PlatformFactory> sPlatformFactories;

    public static SocialOptions getConfig() {
        if (sSocialOptions == null) {
            throw SocialError.make(SocialError.CODE_SDK_INIT_ERROR);
        }
        return sSocialOptions;
    }

    public static void init(SocialOptions config) {
        sSocialOptions = config;
        // 自动注册平台
        sPlatformFactories = new SparseArray<>();
        // 系统平台
        sPlatformFactories.append(Target.PLATFORM_EMAIL, new EmailPlatform.Factory());
        sPlatformFactories.append(Target.PLATFORM_SMS, new SmsPlatform.Factory());
        sPlatformFactories.append(Target.PLATFORM_CLIPBOARD, new ClipboardPlatform.Factory());
        if (sSocialOptions.isDdEnable()) {
            registerPlatform(Target.PLATFORM_DD, "com.zfy.social.dd.DDPlatform$Factory");
        }
        if (sSocialOptions.isWxEnable()) {
            registerPlatform(Target.PLATFORM_WX, "com.zfy.social.wx.WxPlatform$Factory");
        }
        if (sSocialOptions.isWbEnable()) {
            registerPlatform(Target.PLATFORM_WB, "com.zfy.social.wb.WbPlatform$Factory");
        }
        if (sSocialOptions.isQqEnable()) {
            registerPlatform(Target.PLATFORM_QQ, "com.zfy.social.qq.QQPlatform$Factory");
        }
    }

    private static void registerPlatform(int target, String factoryClazz) {
        try {
            Object instance = Class.forName(factoryClazz).newInstance();
            if (instance instanceof PlatformFactory) {
                SocialUtil.e("chendong", "注册平台 " + target + " ," + instance.getClass().getName());
                sPlatformFactories.append(target, (PlatformFactory) instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static IPlatform getPlatform(Context context, int target) {
        PlatformFactory platformFactory = sPlatformFactories.get(target);
        if (platformFactory != null) {
            return platformFactory.create(context, target);
        }
        return null;
    }

    public static IRequestAdapter getRequestAdapter() {
        if (sRequestAdapter == null) {
            sRequestAdapter = new DefaultRequestAdapter();
        }
        return sRequestAdapter;
    }


    public static IJsonAdapter getJsonAdapter() {
        return sJsonAdapter;
    }

}
