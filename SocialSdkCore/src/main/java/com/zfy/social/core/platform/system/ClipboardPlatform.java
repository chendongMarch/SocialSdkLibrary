package com.zfy.social.core.platform.system;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

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
public class ClipboardPlatform extends SystemPlatform {

    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            return new ClipboardPlatform();
        }

        @Override
        public int getTarget() {
            return Target.PLATFORM_WX;
        }
    }

    private ClipboardPlatform() {
        super(null, null);
    }

    @Override
    protected void dispatchShare(Activity activity, int shareTarget, ShareObj obj) {
        if (obj.isClipboard()) {
            String copyContent = SocialUtil.notNull(obj.getCopyContent());
            ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData textCd = ClipData.newPlainText("text", copyContent);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(textCd);
            } else {
                mOnShareListener.onFailure(SocialError.make("ClipboardManager null"));
                return;
            }
            mOnShareListener.onSuccess();
        }
    }


}
