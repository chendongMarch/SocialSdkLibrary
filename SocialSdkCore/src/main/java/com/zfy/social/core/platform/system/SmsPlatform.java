package com.zfy.social.core.platform.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.platform.IPlatform;
import com.zfy.social.core.platform.PlatformFactory;
import com.zfy.social.core.util.SocialUtil;

/**
 * CreateAt : 2018/12/26
 * Describe :
 *
 * @author chendong
 */
public class SmsPlatform extends SystemPlatform {

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            return new SmsPlatform(context, null, null, target);
        }

        @Override
        public int getTarget() {
            return Target.PLATFORM_WX;
        }
    }

    private SmsPlatform(Context context, String appId, String appName, int target) {
        super(context, appId, appName, target);
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        if (obj.isSms()) {
            String smsPhone = SocialUtil.notNull(obj.getSmsPhone());
            String smsBody = SocialUtil.notNull(obj.getSmsBody());
            if (TextUtils.isEmpty(smsPhone)) {
                mOnShareListener.onFailure(SocialError.make("手机号为空"));
                return;
            }
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + smsPhone));
            intent.putExtra("sms_body", smsBody);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            mOnShareListener.onSuccess(shareTarget);
        }
    }
}
