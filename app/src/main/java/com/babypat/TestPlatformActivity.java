package com.babypat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.march.dev.app.activity.BaseActivity;
import com.march.dev.widget.TitleBarView;
import com.march.socialsdk.exception.SocialException;
import com.march.socialsdk.listener.OnLoginListener;
import com.march.socialsdk.listener.OnShareListener;
import com.march.socialsdk.listener.impl.SimpleShareListener;
import com.march.socialsdk.manager.LoginManager;
import com.march.socialsdk.manager.ShareManager;
import com.march.socialsdk.model.LoginResult;
import com.march.socialsdk.model.ShareMediaObj;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class TestPlatformActivity extends BaseActivity {

    public static final String TAG = TestPlatformActivity.class.getSimpleName();

    @BindView(R.id.switch_btn)      Switch   mSwitchBtn;
    @BindView(R.id.tv_info_display) TextView mInfoTv;

    private String localImagePath;
    private String netVideoPath;
    private String netMusicPath;
    private String localGifPath;
    private String targetUrl;
    private String localVideoPath;
    private String netImagePath;

    private Context  mContext;
    private Activity mActivity;

    private ShareMediaObj   textObj;
    private ShareMediaObj   imageObj;
    private ShareMediaObj   imageGifObj;
    private ShareMediaObj   videoObj;
    private ShareMediaObj   musicObj;
    private ShareMediaObj   webObj;
    private ShareMediaObj   appObj;
    private ShareMediaObj   voiceObj;
    private OnShareListener mOnShareListener;
    private OnLoginListener mOnLoginListener;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_platform;
    }

    public void log(Object o) {
        Log.e("TestPlatformActivity", o.toString());
    }

    public void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void onInitDatas() {

        localImagePath = new File(Environment.getExternalStorageDirectory(), "1.jpg").getAbsolutePath();
        localVideoPath = new File(Environment.getExternalStorageDirectory(), "video.mp4").getAbsolutePath();
        localGifPath = new File(Environment.getExternalStorageDirectory(), "3.gif").getAbsolutePath();
        netVideoPath = "http://7xtjec.com1.z0.glb.clouddn.com/export.mp4";
        netImagePath = "http://7xtjec.com1.z0.glb.clouddn.com/token.png";
        netMusicPath = "http://7xtjec.com1.z0.glb.clouddn.com/test_music.mp3";
        netMusicPath = "http://mp3.haoduoge.com/sSocialSdkConfig/2017-05-19/1495207225.mp3";
        targetUrl = "http://bbs.csdn.net/topics/391545021";

        textObj = ShareMediaObj.buildTextObj("分享文字", "summary");
        imageObj = ShareMediaObj.buildImageObj(localImagePath);
        imageGifObj = ShareMediaObj.buildImageObj(localGifPath);
        appObj = ShareMediaObj.buildAppObj("分享app", "summary", localImagePath, targetUrl);
        webObj = ShareMediaObj.buildWebObj("分享web", "summary", localImagePath, targetUrl);
        videoObj = ShareMediaObj.buildVideoObj("分享视频", "summary", localImagePath, targetUrl, netVideoPath, 10);
        musicObj = ShareMediaObj.buildMusicObj("分享音乐", "summary", localImagePath, targetUrl, netMusicPath, 10);
        voiceObj = ShareMediaObj.buildVoiceObj("分享声音", "summary", localImagePath, targetUrl, netMusicPath, 10);


        mOnShareListener = new SimpleShareListener() {
            @Override
            public void onSuccess() {
                toast("分享成功");
            }

            @Override
            public void onFailure(SocialException e) {
                toast("分享失败  " + e.toString());
            }

            @Override
            public void onCancel() {
                toast("分享取消");
            }
        };

        mOnLoginListener = new OnLoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onLoginSucceed(LoginResult loginResult) {
                Log.e(TAG, loginResult.toString());
            }

            @Override
            public void onCancel() {
                toast("登录取消");
            }

            @Override
            public void onFailure(SocialException e) {
                toast("登录失败 " + e.toString());
            }
        };
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mTitleBarView.setText(TitleBarView.CENTER, "测试三方");
    }

    private int clickPos = 0;

    @OnClick({R.id.btn_check_qq, R.id.btn_check_wb, R.id.btn_check_wx})
    public void clickPlatform(View view) {
        switch (view.getId()) {
            case R.id.btn_check_qq:
                clickPos = 0;
                mInfoTv.setText("qq");
                break;
            case R.id.btn_check_wx:
                clickPos = 1;
                mInfoTv.setText("微信");
                break;
            case R.id.btn_check_wb:
                clickPos = 2;
                mInfoTv.setText("微博");
                break;
        }
    }

    @LoginManager.LoginTarget
    public int getLoginTargetTo() {
        switch (clickPos) {
            case 0:
                return LoginManager.TARGET_QQ;
            case 1:
                return LoginManager.TARGET_WECHAT;
            default:
                return LoginManager.TARGET_SINA;
        }
    }

    @ShareManager.ShareTargetType
    public int getShareTargetTo() {
        switch (clickPos) {
            case 0:
                if (mSwitchBtn.isChecked())
                    return ShareManager.TARGET_QQ_ZONE;
                else
                    return ShareManager.TARGET_QQ_FRIENDS;
            case 1:
                if (mSwitchBtn.isChecked())
                    return ShareManager.TARGET_WECHAT_ZONE;
                return
                        ShareManager.TARGET_WECHAT_FRIENDS;
            default:
                return ShareManager.TARGET_SINA;
        }
    }

    // 图片，Gif分享 两种方式
    // openApi分享针对大图片相对慢一点,不会弹起新页面，优点是应用名称可以点亮，点击之后会跳转，申请高级权限后可以分享网络图片
    // 普通分享会弹起编辑页面，缺点是小尾巴不能点击
    @OnClick({R.id.btn_login,
                     R.id.btn_share_text,
                     R.id.btn_share_img,
                     R.id.btn_share_gif,
                     R.id.btn_share_app,
                     R.id.btn_share_web,
                     R.id.btn_share_music,
                     R.id.btn_share_video,
                     R.id.btn_share_voice})
    public void clickBtn(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                LoginManager.login(mActivity, getLoginTargetTo(), mOnLoginListener);
                break;
            case R.id.btn_share_text:
                textObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), textObj, mOnShareListener);
                break;
            case R.id.btn_share_img:
                imageObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), imageObj, mOnShareListener);
                break;
            case R.id.btn_share_gif:
                imageGifObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), imageGifObj, mOnShareListener);
                break;
            case R.id.btn_share_app:
                appObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), appObj, mOnShareListener);
                break;
            case R.id.btn_share_web:
                webObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), webObj, mOnShareListener);
                break;
            case R.id.btn_share_music:
                musicObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), musicObj, mOnShareListener);
                break;
            case R.id.btn_share_video:
                videoObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), videoObj, mOnShareListener);
                break;
            case R.id.btn_share_voice:
                voiceObj.setSummary(System.currentTimeMillis() + "");
                ShareManager.share(mActivity, getShareTargetTo(), voiceObj, mOnShareListener);
                break;
        }
    }
}
