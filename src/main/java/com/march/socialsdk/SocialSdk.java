package com.march.socialsdk;

import android.app.Activity;
import android.util.SparseArray;

import com.march.socialsdk.adapter.IJsonAdapter;
import com.march.socialsdk.adapter.IRequestAdapter;
import com.march.socialsdk.adapter.impl.RequestAdapterImpl;
import com.march.socialsdk.model.SocialSdkConfig;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.PlatformCreator;
import com.march.socialsdk.platform.Target;
import com.march.socialsdk.platform.ding.DDPlatform;
import com.march.socialsdk.platform.qq.QQPlatform;
import com.march.socialsdk.platform.wechat.WxPlatform;
import com.march.socialsdk.platform.weibo.WbPlatform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class SocialSdk {

    private static SocialSdkConfig              sSocialSdkConfig;
    private static IJsonAdapter                 sJsonAdapter;
    private static IRequestAdapter              sRequestAdapter;
    private static SparseArray<PlatformCreator> sPlatformCreatorMap;
    private static ExecutorService              sExecutorService;

    public static SocialSdkConfig getConfig() {
        if (sSocialSdkConfig == null) {
            throw new IllegalStateException("invoke SocialSdk.init() first please");
        }
        return sSocialSdkConfig;
    }

    public static void init(SocialSdkConfig config) {
        sSocialSdkConfig = config;
        sPlatformCreatorMap = new SparseArray<>();
        registerPlatform(new QQPlatform.Creator(), Target.LOGIN_QQ, Target.SHARE_QQ_FRIENDS, Target.SHARE_QQ_ZONE);
        registerPlatform(new WxPlatform.Creator(), Target.LOGIN_WX, Target.SHARE_WX_FAVORITE, Target.SHARE_WX_ZONE, Target.SHARE_WX_FRIENDS);
        registerPlatform(new WbPlatform.Creator(), Target.LOGIN_WB, Target.SHARE_WB);
        registerPlatform(new DDPlatform.Creator(), Target.SHARE_DD);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Platform 注册
    ///////////////////////////////////////////////////////////////////////////

    private static void registerPlatform(PlatformCreator creator, int... targets) {
        for (int target : targets) {
            sPlatformCreatorMap.put(target, creator);
        }
    }

    public static IPlatform getPlatform(Activity context, int target) {
        PlatformCreator creator = sPlatformCreatorMap.get(target);
        if (creator != null) {
            return creator.create(context, target);
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // JsonAdapter
    ///////////////////////////////////////////////////////////////////////////

    public static IJsonAdapter getJsonAdapter() {
        if (sJsonAdapter == null) {
            throw new IllegalStateException("为了不引入其他的json解析依赖，特地将这部分放出去，必须添加一个对应的 json 解析工具，参考代码 sample/GsonJsonAdapter.java");
        }
        return sJsonAdapter;
    }

    public static void setJsonAdapter(IJsonAdapter jsonAdapter) {
        sJsonAdapter = jsonAdapter;
    }

    ///////////////////////////////////////////////////////////////////////////
    // RequestAdapter
    ///////////////////////////////////////////////////////////////////////////

    public static IRequestAdapter getRequestAdapter() {
        if (sRequestAdapter == null) {
            sRequestAdapter = new RequestAdapterImpl();
        }
        return sRequestAdapter;
    }

    public static void setRequestAdapter(IRequestAdapter requestAdapter) {
        sRequestAdapter = requestAdapter;
    }

    public static ExecutorService getExecutorService() {
        if (sExecutorService == null) {
            sExecutorService = Executors.newCachedThreadPool();
        }
        return sExecutorService;
    }
}
