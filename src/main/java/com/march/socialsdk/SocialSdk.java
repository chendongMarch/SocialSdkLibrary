package com.march.socialsdk;

import com.march.socialsdk.adapter.IJsonAdapter;
import com.march.socialsdk.adapter.IRequestAdapter;
import com.march.socialsdk.adapter.impl.RequestAdapterImpl;
import com.march.socialsdk.model.SocialSdkConfig;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class SocialSdk {

    private static SocialSdkConfig sSocialSdkConfig;
    private static IJsonAdapter sJsonAdapter;
    private static IRequestAdapter sRequestAdapter;

    public static SocialSdkConfig getConfig() {
        if (sSocialSdkConfig == null) {
            throw new IllegalStateException("invoke SocialSdk.init() first please");
        }
        return sSocialSdkConfig;
    }

    public static void init(SocialSdkConfig config) {
        sSocialSdkConfig = config;
    }

    public static IJsonAdapter getJsonAdapter() {
        if (sJsonAdapter == null) {
            throw new IllegalStateException("为了不引入其他的json解析依赖，特地将这部分放出去，必须添加一个对应的 json 解析工具，参考代码 sample/GsonJsonAdapter.java");
        }
        return sJsonAdapter;
    }

    public static void setJsonAdapter(IJsonAdapter jsonAdapter) {
        sJsonAdapter = jsonAdapter;
    }

    public static IRequestAdapter getRequestAdapter() {
        if (sRequestAdapter == null) {
            sRequestAdapter = new RequestAdapterImpl();
        }
        return sRequestAdapter;
    }

    public static void setRequestAdapter(IRequestAdapter requestAdapter) {
        sRequestAdapter = requestAdapter;
    }
}
