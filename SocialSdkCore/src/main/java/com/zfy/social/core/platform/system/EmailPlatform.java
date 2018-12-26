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
public class EmailPlatform extends SystemPlatform {

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            return new EmailPlatform();
        }

        @Override
        public int getTarget() {
            return Target.PLATFORM_WX;
        }
    }

    private EmailPlatform() {
        super(null, null);
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        if (obj.isEMail()) {
            String mailAddress = SocialUtil.notNull(obj.geteMailAddress());
            String mailSubject = SocialUtil.notNull(obj.geteMailSubject());
            String mailBody = SocialUtil.notNull(obj.geteMailBody());

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + mailAddress));
            intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
            intent.putExtra(Intent.EXTRA_TEXT, mailBody);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            mOnShareListener.onSuccess();
        }
    }
}
