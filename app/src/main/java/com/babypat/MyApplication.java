package com.babypat;

import android.app.Application;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.common.SocialConstants;
import com.march.socialsdk.model.SocialSdkConfig;

/**
 * CreateAt : 8/12/17
 * Describe :
 *
 * @author march
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSocialSDK();
    }

    private void initSocialSDK() {

        String qqAppId = getString(R.string.QQ_APPID);
        String wxAppId = getString(R.string.WEICHAT_APPID);
        String wxSecretKey = getString(R.string.WEICHAT_APPKEY);
        String sinaAppId = getString(R.string.SINA_APPKEY);

        SocialSdkConfig config = new SocialSdkConfig(this)
                // 配置qq
                .qq(qqAppId)
                // 配置wx
                .wechat(wxAppId, wxSecretKey)
                // 配置sina
                .sina(sinaAppId)
                // 配置Sina的RedirectUrl，有默认值，如果是官网默认的不需要设置
                .sinaRedirectUrl("http://open.manfenmm.com/bbpp/app/weibo/common.php")
                // 配置Sina授权scope,有默认值，默认值 all
                .sinaScope(SocialConstants.SCOPE);
        SocialSdk.init(config);
    }
}
