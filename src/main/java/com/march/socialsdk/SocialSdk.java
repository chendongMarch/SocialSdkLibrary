package com.march.socialsdk;

import com.march.socialsdk.adapter.IJsonAdapter;
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

    public static SocialSdkConfig getConfig() {
        return sSocialSdkConfig;
    }

    public static void init(SocialSdkConfig config) {
        sSocialSdkConfig = config;
    }

    public static IJsonAdapter getJsonAdapter() {
        return sJsonAdapter;
    }

    public static void addJsonAdapter(IJsonAdapter jsonAdapter) {
        sJsonAdapter = jsonAdapter;
    }
}
