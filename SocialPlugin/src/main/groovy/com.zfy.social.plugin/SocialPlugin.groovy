package com.zfy.social.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.zfy.social.plugin.extension.ConfigExt
import com.zfy.social.plugin.extension.SocialExt
import com.zfy.social.plugin.lib.UtilX
import org.gradle.api.Plugin
import org.gradle.api.Project

class SocialPlugin implements Plugin<Project> {

    static SocialExt socialExt

    static boolean pluginDebug = true

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
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }
        final def android
        if (hasApp) {
            android = project.extensions.getByType(AppExtension)
        } else {
            android = project.extensions.getByType(LibraryExtension)
        }
        // 创建闭包，接收参数
        project.extensions.create("socialsdk", SocialExt.class)

        variants.all { variant ->
            SocialExt extension = project.socialsdk
            socialExt = extension
            pluginDebug = extension.debug
            // 打印配置信息
            printSocialConfig(extension)
            // qq 添加 qq_id
            addManifestPlaceholder(project, extension)
            // 追加依赖
            prepareDependencies(project, extension)
        }

        android.registerTransform(new ScanClassTransform())
        android.registerTransform(new SocialConfigTransform())
    }

    // 添加到 manifest placeholders
    private static void addManifestPlaceholder(Project project, SocialExt extension) {
        if (extension.qq.enable) {
            def addQqId = false
            project.android.buildTypes.all { buildType ->
                addQqId = true
                // log "添加 manifestPlaceholder, buildType => ${buildType}"
                buildType.manifestPlaceholders.qq_id = extension.qq.appId
            }
            project.android.productFlavors.all { flavor ->
                addQqId = true
                // log "添加 manifestPlaceholder, flavor => ${flavor}"
                flavor.manifestPlaceholders.qq_id = extension.qq.appId
            }
            if (!addQqId) {
                // log "添加 manifestPlaceholder, default config"
                project.android.defaultConfig.manifestPlaceholders.qq_id = extension.qq.appId
            }
        }
    }

    static String version(ConfigExt configExtension, def defaultVersion) {
        if (configExtension.version != null) {
            return configExtension.version
        }
        return defaultVersion
    }


    // 根据配置准备依赖信息
    static void prepareDependencies(Project project, SocialExt extension) {
        if (extension.local) {
            log "使用本地依赖，不使用远程依赖"
            return
        }
        def coreV = version(extension.core, "1.1.4")
        def wxV = version(extension.wx, "1.1.2")
        def qqV = version(extension.qq, "1.1.0")
        def wbV = version(extension.wb, "1.1.0")
        def ddV = version(extension.dd, "1.1.0")

        def coreLib = "com.zfy:social-sdk-core:${coreV}"
        def wxLib = "com.zfy:social-sdk-wx:${wxV}"
        def ddLib = "com.zfy:social-sdk-dd:${ddV}"
        def qqLib = "com.zfy:social-sdk-qq:${qqV}"
        def wbLib = "com.zfy:social-sdk-weibo:${wbV}"

        log '依赖追加 => 开始添加依赖'
        project.dependencies.add('implementation', coreLib)
        if (extension.useGson) {
            project.dependencies {
                implementation('com.zfy:social-sdk-x-json:1.0.1') {
                    exclude group: 'com.zfy', module: 'social-sdk-core'
                }
            }
        }
        if (extension.useOkHttp) {
            project.dependencies {
                implementation('com.zfy:social-sdk-x-http:1.0.1') {
                    exclude group: 'com.zfy', module: 'social-sdk-core'
                }
            }
        }
        if (extension.qq.enable) {
            log "依赖追加 => 添加 QQ 平台，appId = ${extension.qq.appId}"
            project.dependencies {
                implementation(qqLib) {
                    exclude group: 'com.zfy', module: 'social-sdk-core'
                }
            }
        }
        if (extension.dd.enable) {
            log "依赖追加 => 添加 钉钉 平台，appId = ${extension.dd.appId}"
            project.dependencies {
                implementation(ddLib) {
                    exclude group: 'com.zfy', module: 'social-sdk-core'
                }
            }
        }
        if (extension.wx.enable) {
            log "依赖追加 => 添加 微信 平台，appId = ${extension.wx.appId}, secret = ${extension.wx.appSecret}"
            project.dependencies {
                implementation(wxLib) {
                    exclude group: 'com.zfy', module: 'social-sdk-core'
                }
            }
        }
        if (extension.wb.enable) {
            log "依赖追加 => 添加 微博 平台，appId = ${extension.wb.appId}, url = ${extension.wb.url}"
            project.dependencies {
                implementation(wbLib) {
                    exclude group: 'com.zfy', module: 'social-sdk-core'
                }
            }
        }
    }

    // 打印配置信息
    private static void printSocialConfig(SocialExt extension) {

        log("config.wxEnable => ${extension.wx.enable}")
        log("config.wxAppId => ${extension.wx.appId}")
        log("config.wxAppSecret => ${extension.wx.appSecret}")
        log("config.wxOnlyAuthCode => ${extension.wx.onlyAuthCode}")

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
        if (pluginDebug) {
            UtilX.log("social: ${msg}")
        }
    }
}