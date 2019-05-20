package com.zfy.social.core;

import android.util.SparseArray;

import com.zfy.social.core.adapter.IJsonAdapter;
import com.zfy.social.core.adapter.IRequestAdapter;
import com.zfy.social.core.adapter.impl.DefaultRequestAdapter;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.ShareInterceptor;
import com.zfy.social.core.manager.ShareManager;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.platform.system.ClipboardPlatform;
import com.zfy.social.core.platform.system.EmailPlatform;
import com.zfy.social.core.platform.system.SmsPlatform;
import com.zfy.social.core.util.SocialUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class SocialSdk {

    public static final String TAG = SocialSdk.class.getSimpleName();

    // 配置项
    private static SocialOptions sSocialOptions;
    // Json 解析 Adapter
    static IJsonAdapter sJsonAdapter;
    // 请求 Adapter
    static IRequestAdapter sRequestAdapter;
    // 分享截断
    static List<ShareInterceptor> sShareInterceptors;
    // platform factory
    private static SparseArray<PlatformFactory> sPlatformFactories;

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
        sShareInterceptors = new ArrayList<>();
        sShareInterceptors.add(new ShareManager.ImgInterceptor());
        List<ShareInterceptor> interceptors = config.getShareInterceptors();
        if (interceptors != null) {
            sShareInterceptors.addAll(interceptors);
        }
    }

    // 添加 platform
    public static void addPlatform(PlatformFactory factory) {
        sPlatformFactories.append(factory.getPlatformTarget(), factory);
    }

    private static void addPlatform(int target, String factoryClazz) {
        try {
            Object instance = Class.forName(factoryClazz).newInstance();
            if (instance instanceof PlatformFactory) {
                SocialUtil.e(TAG, "注册平台 " + target + " ," + instance.getClass().getName());
                addPlatform((PlatformFactory) instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 获取配置项
    public static SocialOptions opts() {
        if (sSocialOptions == null) {
            throw SocialError.make(SocialError.CODE_SDK_INIT_ERROR);
        }
        return sSocialOptions;
    }

    // 获取网络请求 adapter
    public static IRequestAdapter getRequestAdapter() {
        if (sRequestAdapter == null) {
            sRequestAdapter = new DefaultRequestAdapter();
        }
        return sRequestAdapter;
    }

    // 获取 json 解析 adapter
    public static IJsonAdapter getJsonAdapter() {
        return sJsonAdapter;
    }

    // 获取构建工厂
    public static SparseArray<PlatformFactory> getPlatformFactories() {
        return sPlatformFactories;
    }

    public static List<ShareInterceptor> getShareInterceptors() {
        return sShareInterceptors;
    }
}
