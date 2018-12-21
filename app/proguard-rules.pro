# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/liulj/Downloads/soft/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#这里com.xiaomi.mipushdemo.DemoMessageRreceiver改成app中定义的完整类名
-keep class com.babypat.mipush.MiPushReceiver {*;}
-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses

#反馈的混淆,来自网页 http://baichuan.taobao.com/docs/doc.htm?spm=a3c0d.7629140.0.0.dRQAtA&treeId=118&articleId=105686&docType=1
-keep class com.alibaba.sdk.android.feedback.impl.FeedbackServiceImpl {*;}
-keep class com.alibaba.sdk.android.feedback.impl.FeedbackAPI {*;}
-keep class com.alibaba.sdk.android.feedback.util.IWxCallback {*;}
-keep class com.alibaba.sdk.android.feedback.util.IUnreadCountCallback{*;}
-keep class com.alibaba.sdk.android.feedback.FeedbackService{*;}
-keep public class com.alibaba.mtl.log.model.LogField {public *;}
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep class com.ta.utdid2.device.**{*;}

-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**

# kiwi的混淆
-dontwarn com.kiwi.**
-keep public class com.kiwi.**{*;}


#百度
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**