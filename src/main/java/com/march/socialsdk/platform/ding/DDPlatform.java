package com.march.socialsdk.platform.ding;

import android.app.Activity;
import android.content.Context;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.march.socialsdk.model.ShareObj;
import com.march.socialsdk.platform.AbsPlatform;

/**
 * CreateAt : 2018/2/11
 * Describe :
 *
 * @author chendong
 */
public class DDPlatform extends AbsPlatform {


    private final IDDShareApi mDdShareApi;

    public DDPlatform(Context context, String appId, String appName) {
        super(context, appId, appName);
        mDdShareApi = DDShareApiFactory.createDDShareApi(context, appId, false);
    }

    @Override
    public void onNewIntent(Activity activity) {
        super.onNewIntent(activity);
        mDdShareApi.handleIntent(activity.getIntent(), (IDDAPIEventHandler) activity);
    }

    @Override
    public void onResponse(Object resp) {
        super.onResponse(resp);
        BaseResp baseResp = (BaseResp) resp;
        int errCode = baseResp.mErrCode;
        switch (errCode){
            case BaseResp.ErrCode.ERR_OK:
                break;
        }
    }

    @Override
    protected void shareOpenApp(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareText(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareImage(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareApp(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareWeb(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareMusic(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareVideo(int shareTarget, Activity activity, ShareObj obj) {

    }

    @Override
    protected void shareVoice(int shareTarget, Activity activity, ShareObj obj) {

    }
}
