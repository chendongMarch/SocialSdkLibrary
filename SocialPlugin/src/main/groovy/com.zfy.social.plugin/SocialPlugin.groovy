package com.zfy.social.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class SocialPlugin implements Plugin<Project> {
    void apply(Project project) {
        // 插件位置
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }
        // 变体
        final def variants
        if (hasApp) {
            log "当前位置为 App Module"
            variants = project.android.applicationVariants
        } else {
            log "当前位置为 Library Module"
            variants = project.android.libraryVariants
        }

        // android {}
        final def android
        if (hasApp) {
            android = project.extensions.getByType(AppExtension)
        } else {
            android = project.extensions.getByType(LibraryExtension)
        }

        // 创建闭包，接收参数
        project.extensions.create("social", SocialExtension.class)
        // 给 android {} 添加一个转换
        android.registerTransform(new SocialConfigTransform(project))


        variants.all { variant ->

            def variantData = variant.variantData
            def scope = variantData.scope
            SocialExtension extension = project.social

            // 打印编译信息
            log "variantData = ${variantData} ${scope}"
            log "当前调试模式 debugable = ${variant.buildType.isDebuggable()}"
            log "当前构建类型 buildType = ${variant.buildType.name}"
            // 打印配置信息
            printSocialConfig(extension)
            // qq 添加 qq_id
            addManifestPlaceholder(project, extension)
            // 追加依赖
            prepareDependencies(project, extension)
            // 添加自动生成代码的任务
            appendGenerateSocialConfigTask(project, scope, variant, extension)

        }

        project.task('readSocialConfig').doLast {
            log "欢迎使用 SocialSDK Plugin"
            printSocialConfig(project.social)
        }
    }

    private static void addManifestPlaceholder(Project project, SocialExtension extension) {
        if (extension.qq.enable) {
            project.android.defaultConfig.manifestPlaceholders.qq_id = extension.qq.appId
            project.android.buildTypes.all { buildType ->
                buildType.manifestPlaceholders.qq_id = extension.qq.appId
            }
            project.android.productFlavors.all { flavor ->
                flavor.manifestPlaceholders.qq_id = extension.qq.appId
            }
            log "为 qq 添加 manifest holders，添加之后 => "
            log(project.android.defaultConfig.manifestPlaceholders)
        }
    }

    // 追加自动生成 Java 类的 task
    private static void appendGenerateSocialConfigTask(Project project,
                                                       scope, variant,
                                                       SocialExtension extension) {
        def generateSocialConfigTaskName = scope.getTaskName("social", "generateBuildConfigTask")
        def generateSocialConfigTask = project.task(generateSocialConfigTaskName)
        // 设置task要执行的任务
        generateSocialConfigTask.doLast {
            createConfigJava(variant, extension)
        }
        // 设置 task 依赖于生成BuildConfig 的 task，然后在生成 BuildConfig 后生成我们的类
        String generateBuildConfigTaskName = variant.getVariantData().getScope().getGenerateBuildConfigTask().name
        def generateBuildConfigTask = project.tasks.getByName(generateBuildConfigTaskName)
        if (generateBuildConfigTask) {
            generateSocialConfigTask.dependsOn generateBuildConfigTask
            generateBuildConfigTask.finalizedBy generateSocialConfigTask
        }
    }

    // 根据配置准备依赖信息
    static void prepareDependencies(Project project, SocialExtension extension) {
        def local = false
        def coreLib = local ? 'project(":SocialSdkCore")' : 'com.zfy:social-sdk-core:0.0.2'
        def wxLib = local ? 'project(":SocialSdkWx")  ' : 'com.zfy:social-sdk-wx:0.0.1'
        def ddLib = local ? 'project(":SocialSdkDd")  ' : 'com.zfy:social-sdk-dd:0.0.1'
        def qqLib = local ? 'project(":SocialSdkQq")  ' : 'com.zfy:social-sdk-qq:0.0.1'
        def wbLib = local ? 'project(":SocialSdkWb")  ' : 'com.zfy:social-sdk-weibo:0.0.1'

        log '依赖追加 =》 开始添加依赖'
        project.dependencies.add('implementation', coreLib)
        if (extension.qq.enable) {
            log "依赖追加 => 添加 QQ 平台，appId = ${extension.qq.appId}"
            project.dependencies.add('implementation', qqLib)
        }
        if (extension.dd.enable) {
            log "依赖追加 => 添加 钉钉 平台，appId = ${extension.dd.appId}"
            project.dependencies.add('implementation', ddLib)
        }
        if (extension.wx.enable) {
            log "依赖追加 => 添加 微信 平台，appId = ${extension.wx.appId}, secret = ${extension.wx.appSecret}"
            project.dependencies.add('implementation', wxLib)
        }
        if (extension.wb.enable) {
            log "依赖追加 => 添加 微博 平台，appId = ${extension.wb.appId}, url = ${extension.wb.url}"
            project.dependencies.add('implementation', wbLib)
        }
    }

    // 打印配置信息
    private static void printSocialConfig(SocialExtension extension) {
        log("config.debug => ${extension.debug}")
        log("config.tokenExpireHour => ${extension.tokenExpireHour}")

        log("config.wxEnable => ${extension.wx.enable}")
        log("config.wxAppId => ${extension.wx.appId}")
        log("config.wxAppSecret => ${extension.wx.appSecret}")

        log("config.qqEnable => ${extension.qq.enable}")
        log("config.qqAppId => ${extension.qq.appId}")

        log("config.ddEnable => ${extension.dd.enable}")
        log("config.ddAppId => ${extension.dd.appId}")

        log("config.wbEnable => ${extension.wb.enable}")
        log("config.wbAppId => ${extension.wb.appId}")
        log("config.wbUrl => ${extension.wb.url}")
    }

    // 日志打印
    private static void log(msg) {
        println "social: ${msg}"
    }

    // 生成配置代码
    private static void createConfigJava(def variant, SocialExtension extension) {
        def content = """
package com.zfy.social.config;
/**
 * Automatically generated file. DO NOT MODIFY
 * 
 * @author chendong
 */
public class SocialBuildConfig {
    
    // 是否开启调试模式
    public final boolean debug = ${extension.debug};
   
    // 开启微信平台
    public final boolean wxEnable = ${extension.wx.enable};
    // 微信 appId
    public final String wxAppId = "${extension.wx.appId}";
    // 微信 secret 登录使用
    public final String wxAppSecret = "${extension.wx.appSecret}";
    
    // 开启 QQ 平台
    public final boolean qqEnable = ${extension.qq.enable};
    // qq appId
    public final String qqAppId = "${extension.qq.appId}";
    
    // 开启 DD 平台
    public final boolean ddEnable = ${extension.dd.enable};
    // dd appId
    public final String ddAppId = "${extension.dd.appId}";
    
    // 开启 Wb 平台
    public final boolean wbEnable = ${extension.wb.enable};
    // wb appId
    public final String wbAppId = "${extension.wb.appId}";
    // wb redirect url
    public final String wbRedirectUrl = "${extension.wb.url}";
    
    // 设置 token 有效期，有效期内不会重新获取 token
    // 默认一天，如下设置为 12 小时
    // 设置为0，将不会做持久化存储，每次获取最新的
    public final long tokenExpireHour = ${extension.tokenExpireHour};
}
"""
        File outputDir = variant.getVariantData().getScope().getBuildConfigSourceOutputDir()
        def dir = new File(outputDir.absolutePath + "/com/zfy/social/config")
        dir.mkdirs()
        def javaFile = new File(dir, "SocialBuildConfig.java")
        println "social: outputDir ${dir.absolutePath}"
        javaFile.write(content, 'UTF-8')
    }

}