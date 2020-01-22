package com.babypat;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.babypat.platform.HuaweiPlatform;
import com.zfy.social.core.SocialOptions;
import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.SocialValues;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginStateListener;
import com.zfy.social.core.listener.OnShareStateListener;
import com.zfy.social.core.manager.LoginManager;
import com.zfy.social.core.manager.ShareManager;
import com.zfy.social.core.model.LoginObj;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.model.ShareResult;
import com.zfy.social.core.util.SocialUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

;

public class TestActivity extends AppCompatActivity {

    public static final String TAG = TestActivity.class.getSimpleName();

    @BindView(R.id.switch_btn) Switch mSwitchBtn;
    @BindView(R.id.tv_info_display) TextView mInfoTv;
    @BindView(R.id.tab_ly) TabLayout mTabLayout;
    @BindView(R.id.code_iv) ImageView mCodeIv;

    private String localImagePath;
    private String netVideoPath;
    private String netMusicPath;
    private String localGifPath;
    private String targetUrl;
    private String localVideoPath;
    private String netImagePath;

    private ShareObj textObj;
    private ShareObj imageObj;
    private ShareObj netImageObj;
    private ShareObj imageGifObj;
    private ShareObj videoObj;
    private ShareObj videoLocalObj;
    private ShareObj musicObj;
    private ShareObj webObj;
    private ShareObj appObj;
    private OnShareStateListener mOnShareListener;
    private OnLoginStateListener mOnLoginListener;
    private String[] mPlatform;

    private boolean isInit = true;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_platform);
        mActivity = this;
        ButterKnife.bind(this);
        onInitDatas();
        onInitViews();
    }

    public void showMsg(String msg) {
        //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        updateDisplay(msg);
    }

    public void onInitDatas() {

        localImagePath = new File(Environment.getExternalStorageDirectory(), "test.jpg").getAbsolutePath();

        localGifPath = new File(Environment.getExternalStorageDirectory(), "3.gif").getAbsolutePath();
        netVideoPath = "http://7xtjec.com1.z0.glb.clouddn.com/export.mp4";
        netImagePath = "http://s3.hixd.com/129721.jpg";
        netMusicPath = "http://7xtjec.com1.z0.glb.clouddn.com/test_music.mp3";
        targetUrl = "https://mp.weixin.qq.com/s/Z7Kp_xstwOU7ipLNERRQdA";
        localVideoPath = new File(Environment.getExternalStorageDirectory(), "4.mp4").getAbsolutePath();

        initObj();
        mOnShareListener = new OnShareStateListener() {
            @Override
            public void onState(Activity activity, ShareResult result) {
                switch (result.state) {
                    case ShareResult.STATE_SUCCESS:
                        showMsg("分享成功");
                        break;
                    case ShareResult.STATE_FAIL:
                        SocialError e = result.error;
                        showMsg("分享失败  " + e.toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (e.getCode() == SocialError.CODE_STORAGE_READ_ERROR) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                            } else if (e.getCode() == SocialError.CODE_STORAGE_WRITE_ERROR) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                            }
                        }
                        break;
                    case ShareResult.STATE_CANCEL:
                        showMsg("分享取消");
                        break;
                }
            }
        };

        mOnLoginListener = new OnLoginStateListener() {
            @Override
            public void onState(Activity activity,LoginResult result) {
                switch (result.state) {
                    case LoginResult.STATE_SUCCESS:
                        Log.e(TAG, result.toString());
                        updateDisplay(result.toString());
                        break;
                    case LoginResult.STATE_FAIL:
                        showMsg("登录失败 " + result.error.toString());
                        break;
                    case LoginResult.STATE_CANCEL:
                        showMsg("登录取消");
                        break;
                    case LoginResult.STATE_WX_CODE_RECEIVE:
                        String wxCodePath = result.wxCodePath;
                        Bitmap bitmap = BitmapFactory.decodeFile(wxCodePath);
                        mCodeIv.setImageBitmap(bitmap);
                        showMsg("二维码已更新");
                        break;
                    case LoginResult.STATE_WX_CODE_SCANNED:
                        showMsg("用户已扫码");
                        break;
                }
            }

        };
    }

    private void initObj() {
        String share_url = "http://t1cdn.meicool.com/app/invitation/invited.html";
        String title = "邀请你一起来玩美酷直播!";
        String desc = "超级火爆的真人视频交友平台，微信登录更有好礼相送";
        String share_img = "http://t1img.oss-cn-shenzhen.aliyuncs.com/Application/Meiku/Static/image/default_faceuser.png";

        textObj = ShareObj.buildTextObj("分享文字", "summary");
        imageObj = ShareObj.buildImageObj(localImagePath);
        netImageObj = ShareObj.buildImageObj(netImagePath);
        imageGifObj = ShareObj.buildImageObj(localGifPath);
        appObj = ShareObj.buildAppObj("分享app", "summary", localImagePath, targetUrl);
        webObj = ShareObj.buildWebObj("分享web", "summary", "", targetUrl);
//        webObj = ShareObj.buildWebObj(title, desc, share_img, share_url);
        videoObj = ShareObj.buildVideoObj("分享视频", "summary", localImagePath, targetUrl, netVideoPath, 10);
        videoLocalObj = ShareObj.buildVideoObj("分享本地视频", "summary", localImagePath, targetUrl, localVideoPath, 0);

        musicObj = ShareObj.buildMusicObj("分享音乐", "summary", localImagePath, targetUrl, netMusicPath, 10);
    }

    public void onInitViews() {

        mPlatform = new String[]{"qq", "微信", "微博", "钉钉"};
        for (String s : mPlatform) {
            mTabLayout.addTab(mTabLayout.newTab().setText(s));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isInit) {
                    Toast.makeText(mActivity, "请先初始化", Toast.LENGTH_SHORT).show();
                    return;
                }
                clickPos = tab.getPosition();
                updateDisplay("切换到 " + mPlatform[clickPos]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private int clickPos = 0;


    @Target.LoginTarget
    public int getLoginTargetTo() {
        switch (clickPos) {
            case 0:
                return Target.LOGIN_QQ;
            case 1:
                return Target.LOGIN_WX;
            case 2:
                return Target.LOGIN_WB;
            case 3:
                return Target.LOGIN_WX;
            default:
                return Target.LOGIN_WX;
        }
    }

    @Target.ShareTarget
    public int getShareTargetTo() {
        switch (clickPos) {
            case 0:
                if (mSwitchBtn.isChecked())
                    return Target.SHARE_QQ_ZONE;
                else
                    return Target.SHARE_QQ_FRIENDS;
            case 1:
                if (mSwitchBtn.isChecked())
                    return Target.SHARE_WX_ZONE;
                return
                        Target.SHARE_WX_FRIENDS;
            case 2:
                return Target.SHARE_WB;
            case 3:
                return Target.SHARE_DD;
            default:
                return Target.SHARE_WX_FRIENDS;
        }
    }

    @OnClick({R.id.init_btn})
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.init_btn:
                isInit = true;
                initSocialSDKSample();
                break;
            default:
                break;
        }
    }

    // 图片，Gif分享 两种方式
    // openApi分享针对大图片相对慢一点,不会弹起新页面，优点是应用名称可以点亮，点击之后会跳转，申请高级权限后可以分享网络图片
    // 普通分享会弹起编辑页面，缺点是小尾巴不能点击
    @OnClick({
            R.id.btn_login,
            R.id.btn_share_video_local,
            R.id.btn_share_text,
            R.id.btn_share_img,
            R.id.btn_share_gif,
            R.id.btn_share_app,
            R.id.btn_share_web,
            R.id.btn_share_music,
            R.id.btn_share_video,
            R.id.btn_clear_token,
            R.id.clear_btn,
            R.id.btn_share_sms,
            R.id.btn_share_clipboard,
            R.id.btn_share_email,
            R.id.btn_share_net_img,
            R.id.huawei_btn,
            R.id.btn_login_scan,
    })
    public void clickBtn(View view) {
        if (!isInit) {
            Toast.makeText(this, "请先初始化", Toast.LENGTH_SHORT).show();
            return;
        }


        initObj();
        switch (view.getId()) {
            case R.id.btn_login_scan:
                LoginObj obj = new LoginObj();
                // 如果不设置，将会使用配置时设置的 secret
                obj.setAppSecret("0a3cb007291d0e59834ee3654f499171");
                obj.setNonceStr("3611cdc33b794c7c92a49ca45bdfab2d");
                obj.setTimestamp("1560416904");
                obj.setSignature("b28f69426f3b3874d89718c8ba792caa4a0a1bcc");
                // 如果不设置，将会使用 SocialValues.WX_SCOPE
                obj.setScope(SocialValues.WX_SCOPE);
                LoginManager.login(Target.LOGIN_WX_SCAN, obj, mOnLoginListener);
                break;
            case R.id.huawei_btn:
                LoginManager.login(HuaweiPlatform.LOGIN_HUAWEI, mOnLoginListener);
                break;
            case R.id.clear_btn:
                mInfoTv.setText("");
                break;
            case R.id.btn_clear_token:
                LoginManager.clearAllToken(mActivity);
                break;
            case R.id.btn_login:
                LoginManager.login(getLoginTargetTo(), mOnLoginListener);
                break;
            case R.id.btn_share_text:
                textObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), textObj, mOnShareListener);
                break;
            case R.id.btn_share_img:
                imageObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), imageObj, mOnShareListener);
                break;
            case R.id.btn_share_net_img:
                netImageObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), netImageObj, mOnShareListener);
                break;
            case R.id.btn_share_gif:
                imageGifObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), imageGifObj, mOnShareListener);
                break;
            case R.id.btn_share_app:
                appObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), appObj, mOnShareListener);
                break;
            case R.id.btn_share_web:
                webObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), webObj, mOnShareListener);
                break;
            case R.id.btn_share_music:
                musicObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), musicObj, mOnShareListener);
                break;
            case R.id.btn_share_video:
                videoObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), videoObj, mOnShareListener);
                break;
            case R.id.btn_share_video_local:
                videoLocalObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(getShareTargetTo(), videoLocalObj, mOnShareListener);
                break;
            case R.id.btn_share_sms:
                webObj.setSmsParams("13611301719", "说啥呢");
                ShareManager.share(Target.SHARE_SMS, webObj, mOnShareListener);
                break;
            case R.id.btn_share_clipboard:
                webObj.setClipboardParams("复制的内容");
                ShareManager.share(Target.SHARE_CLIPBOARD, webObj, mOnShareListener);
                break;
            case R.id.btn_share_email:
                webObj.setEMailParams("1101873740@qq.com", "主题", "内容");
                webObj.setWxMiniParams("51299u9**q31",SocialValues.WX_MINI_TYPE_RELEASE,"/page/path");
                ShareManager.share(Target.SHARE_EMAIL, webObj, mOnShareListener);
                break;
        }
    }


    private void initSocialSDKSample() {

        SocialOptions options = new SocialOptions.Builder2(this)
                // 调试模式，开启 log 输出
                .debug(true)
                // 加载缩略图失败时，降级使用资源图
                .failImgRes(R.mipmap.ic_launcher_new)
                // token 保留时间，单位小时，默认不保留
                .tokenExpiresHours(24)
                // 分享如果停留在第三放将会返回成功，默认返回失败
                .shareSuccessIfStay(true)
                // 微博 loading 窗颜色
                .wbProgressColor(Color.YELLOW)
                // 添加自定义的 json 解析
//                .jsonAdapter(new GsonJsonAdapter())
//                // 请求处理类，如果使用了微博的 openApi 分享，这个是必须的
//                .requestAdapter(new OkHttpRequestAdapter())
                // 添加新平台
                .addPlatform(new HuaweiPlatform.Factory())
                // 添加分享拦截器
                .addShareInterceptor((context, r, obj) -> {
                    obj.setSummary("被重新组装" + obj.getSummary());
                    return null;
                })
                // 构建
                .build();


        SocialSdk.init(getApplication(), new SocialOptions.Builder(this)
                // 开启调试
                .debug(true)
                // 加载缩略图失败时，降级使用资源图
                .failImgRes(R.mipmap.ic_launcher_new)
                .build());
    }


    private void initSocialSDK() {
        SocialOptions options = new SocialOptions.Builder(this)
                // 开启调试
                .debug(true)
                // 加载缩略图失败时，降级使用资源图
                .failImgRes(R.mipmap.ic_launcher_new)
                .build();

    }


    private void updateDisplay(String msg) {
        String trim = mInfoTv.getText().toString().trim();
        String result = trim + "\n\n" + mPlatform[clickPos] + (mSwitchBtn.isChecked() ? "空间" : "") + "==============>\n" + msg + "\n";
        mInfoTv.setText(result);
        SocialUtil.e(TAG, msg);
    }

}
