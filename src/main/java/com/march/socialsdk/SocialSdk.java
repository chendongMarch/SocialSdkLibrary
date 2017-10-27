package com.march.socialsdk;

import com.march.socialsdk.model.SocialSdkConfig;

/**
 * CreateAt : 2017/5/19
 * Describe : SocialSdk
 *
 * @author chendong
 */
public class SocialSdk {

    private static SocialSdkConfig sSocialSdkConfig;

    public static SocialSdkConfig getConfig() {
        return sSocialSdkConfig;
    }

    public static void init(SocialSdkConfig config) {
        sSocialSdkConfig = config;
    }
}
