package com.zfy.social.core;

import android.util.SparseArray;

import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.adapter.IRequestAdapter;
import com.zfy.social.core.adapter.impl.DefaultRequestAdapter;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
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
        addPlatform(new EmailPlatform.Factory());
        addPlatform(new SmsPlatform.Factory());
        addPlatform(new ClipboardPlatform.Factory());
        if (sSocialOptions.isDdEnable()) {
            addPlatform(Target.PLATFORM_DD, "com.zfy.social.dd.DDPlatform$Factory");
        }
        if (sSocialOptions.isWxEnable()) {
            addPlatform(Target.PLATFORM_WX, "com.zfy.social.wx.WxPlatform$Factory");
        }
        if (sSocialOptions.isWbEnable()) {
            addPlatform(Target.PLATFORM_WB, "com.zfy.social.wb.WbPlatform$Factory");
        }
        if (sSocialOptions.isQqEnable()) {
            addPlatform(Target.PLATFORM_QQ, "com.zfy.social.qq.QQPlatform$Factory");
        }
    }

    public static void addPlatform(PlatformFactory factory) {
        sPlatformFactories.append(factory.getPlatformTarget(), factory);
    }

    private static void addPlatform(int target, String factoryClazz) {
        try {
            Object instance = Class.forName(factoryClazz).newInstance();
            if (instance instanceof PlatformFactory) {
                SocialUtil.e("chendong", "注册平台 " + target + " ," + instance.getClass().getName());
                addPlatform((PlatformFactory) instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public static SparseArray<PlatformFactory> getPlatformFactories() {
        return sPlatformFactories;
    }
}
