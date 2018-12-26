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
            return new SmsPlatform();
        }

        @Override
        public int getTarget() {
            return Target.PLATFORM_WX;
        }
    }

    private SmsPlatform() {
        super(null, null);
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
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("smsto:" + smsPhone), "vnd.android-dir/mms-sms");
            intent.putExtra("sms_body", smsBody);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            mOnShareListener.onSuccess();
        }
    }
}
