package com.march.socialsdk;

import android.content.Context;
import android.util.SparseArray;

import com.march.socialsdk.adapter.IJsonAdapter;
import com.march.socialsdk.adapter.IRequestAdapter;
import com.march.socialsdk.adapter.impl.DefaultRequestAdapter;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.platform.IPlatform;
import com.march.socialsdk.platform.PlatformCreator;
import com.march.socialsdk.platform.Target;

import java.util.List;
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
        actionRegisterPlatform();
    }


    ///////////////////////////////////////////////////////////////////////////
    // Platform 注册
    ///////////////////////////////////////////////////////////////////////////

    private static void actionRegisterPlatform() {
        if (sPlatformCreatorMap == null) {
            sPlatformCreatorMap = new SparseArray<>();
        }
        Target.Mapping[] mappings = {
                new Target.Mapping(Target.PLATFORM_QQ, SocialConstants.QQ_CREATOR),
                new Target.Mapping(Target.PLATFORM_WX, SocialConstants.WX_CREATOR),
                new Target.Mapping(Target.PLATFORM_WB, SocialConstants.WB_CREATOR),
                new Target.Mapping(Target.PLATFORM_DD, SocialConstants.DD_CREATOR),
        };
        List<Integer> disablePlatforms = sSocialSdkConfig.getDisablePlatforms();
        for (Target.Mapping mapping : mappings) {
            if (!disablePlatforms.contains(mapping.platform)) {
                PlatformCreator creator = makeCreator(mapping.creator);
                if (creator != null) {
                    sPlatformCreatorMap.put(mapping.platform, creator);
                }

            }
        }
    }

    private static PlatformCreator makeCreator(String clazz) {
        try {
            return (PlatformCreator) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static IPlatform getPlatform(Context context, int target) {
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
            sRequestAdapter = new DefaultRequestAdapter();
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
