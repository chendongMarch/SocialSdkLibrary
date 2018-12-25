package com.babypat;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.zfy.social.core.SocialOptions;
import com.zfy.social.core.SocialSdk;
import com.zfy.social.core.common.Target;
import com.zfy.social.core.exception.SocialError;
import com.zfy.social.core.listener.OnLoginListener;
import com.zfy.social.core.listener.OnShareListener;
import com.zfy.social.core.manager.LoginManager;
import com.zfy.social.core.manager.ShareManager;
import com.zfy.social.core.model.LoginResult;
import com.zfy.social.core.model.ShareObj;
import com.zfy.social.core.util.SocialUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestPlatformActivity extends AppCompatActivity {

    public static final String TAG = TestPlatformActivity.class.getSimpleName();

    @BindView(R.id.switch_btn) Switch mSwitchBtn;
    @BindView(R.id.tv_info_display) TextView mInfoTv;
    @BindView(R.id.tab_ly) TabLayout mTabLayout;
    @BindView(R.id.sv_content) ScrollView mScrollView;

    private String localImagePath;
    private String netVideoPath;
    private String netMusicPath;
    private String localGifPath;
    private String targetUrl;
    private String localVideoPath;
    private String netImagePath;

    private ShareObj textObj;
    private ShareObj imageObj;
    private ShareObj imageGifObj;
    private ShareObj videoObj;
    private ShareObj videoLocalObj;
    private ShareObj musicObj;
    private ShareObj webObj;
    private ShareObj appObj;
    private OnShareListener mOnShareListener;
    private OnLoginListener mOnLoginListener;
    private String[] mPlatform;

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

    public void log(Object o) {
        Log.e("TestPlatformActivity", o.toString());
    }

    public void showMsg(String msg) {
        //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        updateDisplay(msg);
    }

    public void onInitDatas() {

        localImagePath = new File(Environment.getExternalStorageDirectory(), "test.jpg").getAbsolutePath();

        localGifPath = new File(Environment.getExternalStorageDirectory(), "3.gif").getAbsolutePath();
        netVideoPath = "http://7xtjec.com1.z0.glb.clouddn.com/export.mp4";
        netImagePath = "http://thirdwx.qlogo.cn/mmopen/vi_32/52eZlEWhZMnBsTShxYoLAux0QCj7qx4QpcptCfBYwO65FDI7rYucAlQgplrgk8NFMZWiaMh8GhnUsIFr3JgFOKw/132";
        netMusicPath = "http://7xtjec.com1.z0.glb.clouddn.com/test_music.mp3";
        targetUrl = "https://mp.weixin.qq.com/s/Z7Kp_xstwOU7ipLNERRQdA";
        localVideoPath = new File(Environment.getExternalStorageDirectory(), "4.mp4").getAbsolutePath();

        initObj();
        mOnShareListener = new OnShareListener() {
            @Override
            public void onStart(int shareTarget, ShareObj obj) {

            }

            @Override
            public ShareObj onPrepareInBackground(int shareTarget, ShareObj obj) throws Exception {
                return null;
            }

            @Override
            public void onSuccess() {
                showMsg("分享成功");
            }

            @Override
            public void onFailure(SocialError e) {
                showMsg("分享失败  " + e.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (e.getCode() == SocialError.CODE_STORAGE_READ_ERROR) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else if (e.getCode() == SocialError.CODE_STORAGE_WRITE_ERROR) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    }
                }
            }

            @Override
            public void onCancel() {
                showMsg("分享取消");
            }
        };

        mOnLoginListener = new OnLoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, loginResult.toString());
                updateDisplay(loginResult.toString());
            }

            @Override
            public void onCancel() {
                showMsg("登录取消");
            }

            @Override
            public void onFailure(SocialError e) {
                showMsg("登录失败 " + e.toString());
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
        imageGifObj = ShareObj.buildImageObj(localGifPath);
        appObj = ShareObj.buildAppObj("分享app", "summary", localImagePath, targetUrl);
        webObj = ShareObj.buildWebObj("分享web", "summary", localImagePath, targetUrl);
//        webObj = ShareObj.buildWebObj(title, desc, share_img, share_url);
        videoObj = ShareObj.buildVideoObj("分享视频", "summary", localImagePath, targetUrl, netVideoPath, 10);
        videoLocalObj = ShareObj.buildVideoObj("分享本地视频", "summary", localVideoPath);

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

    // 图片，Gif分享 两种方式
    // openApi分享针对大图片相对慢一点,不会弹起新页面，优点是应用名称可以点亮，点击之后会跳转，申请高级权限后可以分享网络图片
    // 普通分享会弹起编辑页面，缺点是小尾巴不能点击
    @OnClick({
            R.id.init_btn,
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
            R.id.clear_btn})
    public void clickBtn(View view) {
        initObj();
        switch (view.getId()) {
            case R.id.clear_btn:
                mInfoTv.setText("");
                break;
            case R.id.btn_clear_token:
                LoginManager.clearAllToken(mActivity);
                break;
            case R.id.btn_login:
                LoginManager.login(mActivity, getLoginTargetTo(), mOnLoginListener);
                break;
            case R.id.btn_share_text:
                textObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), textObj, mOnShareListener);
                break;
            case R.id.btn_share_img:
                imageObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), imageObj, mOnShareListener);
                break;
            case R.id.btn_share_gif:
                imageGifObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), imageGifObj, mOnShareListener);
                break;
            case R.id.btn_share_app:
                appObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), appObj, mOnShareListener);
                break;
            case R.id.btn_share_web:
                webObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), webObj, mOnShareListener);
                break;
            case R.id.btn_share_music:
                musicObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), musicObj, mOnShareListener);
                break;
            case R.id.btn_share_video:
                videoObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), videoObj, mOnShareListener);
                break;
            case R.id.btn_share_video_local:
                videoLocalObj.setSummary(System.currentTimeMillis() + " [http://www.ibbpp.com]");
                ShareManager.share(mActivity, getShareTargetTo(), videoLocalObj, mOnShareListener);
                break;
            case R.id.init_btn:
                initSocialSDKSample();
                break;
        }
    }


    private void initSocialSDKSample() {
        SocialOptions options = new SocialOptions.Builder(this)
                // 调试模式，开启 log 输出
                .debug(true)
                // 加载缩略图失败时，降级使用资源图
                .failImgRes(R.mipmap.ic_launcher_new)
                // 添加自定义的 json 解析
                .jsonAdapter(new GsonJsonAdapter())
                // 请求处理类，如果使用了微博的 openApi 分享，这个是必须的
                .requestAdapter(new OkHttpRequestAdapter())
                // 构建
                .build();
        // 初始化
        SocialSdk.init(options);
    }


    private void initSocialSDK() {

        String qqAppId = getString(R.string.QQ_APP_ID);
        String wxAppId = getString(R.string.WX_APP_ID);
        String wxSecretKey = getString(R.string.WX_SECRET_KEY);
        String wbAppId = getString(R.string.SINA_APP_ID);
        String ddAppId = getString(R.string.DD_APP_ID);

        SocialOptions options = new SocialOptions.Builder(this)
                // 开启调试
                .debug(true)

                // 添加自定义的 json 解析
                .jsonAdapter(new GsonJsonAdapter())
                // 请求处理类，如果使用了微博的 openApi 分享，这个是必须的
                .requestAdapter(new OkHttpRequestAdapter())

                // 加载缩略图失败时，降级使用资源图
                .failImgRes(R.mipmap.ic_launcher_new)
                // 设置 token 有效期，单位小时，默认 24
                .tokenExpiresHours(12)

                // 配置钉钉
                .dd(ddAppId)
                // 配置qq
                .qq(qqAppId)
                // 配置wx, 第三个参数是是否只返回 code
                .wx(wxAppId, wxSecretKey, false)
                // 配置wb
                .wb(wbAppId, "http://open.manfenmm.com/bbpp/app/weibo/common.php")

                .build();
        // 👮 添加 config 数据，必须
        SocialSdk.init(options);
    }


    private void updateDisplay(String msg) {
        String trim = mInfoTv.getText().toString().trim();
        String result = trim + "\n\n" + mPlatform[clickPos] + (mSwitchBtn.isChecked() ? "空间" : "") + "==============>\n" + msg + "\n";
        mInfoTv.setText(result);
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        SocialUtil.e(TAG, msg);
    }

}
