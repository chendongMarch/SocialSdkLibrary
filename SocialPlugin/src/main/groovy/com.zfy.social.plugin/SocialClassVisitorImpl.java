package com.zfy.social.plugin;

import com.zfy.social.plugin.extension.ConfigExt;
import com.zfy.social.plugin.extension.Settings;
import com.zfy.social.plugin.extension.SocialExt;
import com.zfy.social.plugin.lib.AbstractClassVisitor;
import com.zfy.social.plugin.lib.TransformX;
import com.zfy.social.plugin.lib.UtilX;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Function;

/**
 * CreateAt : 2020-01-20
 * Describe : 访问 class 工具
 *
 * @author chendong
 */
public class SocialClassVisitorImpl extends AbstractClassVisitor {

    public static Factory factory = new Factory();

    public SocialClassVisitorImpl(ClassVisitor classVisitor) {
        super(classVisitor);
    }

    static class Factory implements Function<ClassWriter, ClassVisitor> {
        @Override
        public ClassVisitor apply(ClassWriter classWriter) {
            return new SocialClassVisitorImpl(classWriter);
        }
    }

    @Override
    public MethodVisitor watch(MethodVisitor visitor, ClassInfo classInfo, MethodInfo methodInfo) {
        if ("com/zfy/social/core/SocialOptions$Builder".equals(classInfo.name)) {
            if ("initConfigByAsm".equals(methodInfo.name)) {
                UtilX.log("找到 SocialOptions$Builder initConfigByAsm");
                return new SocialMethodVisitorImpl(visitor);
            }
        }
        return super.watch(visitor, classInfo, methodInfo);
    }

    static class SocialMethodVisitorImpl extends MethodVisitor {

        public SocialMethodVisitorImpl(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitCode() {
            SocialExt socialExt = SocialPlugin.getSocialExt();

            ConfigExt wx = socialExt.wx;
            if (wx.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wxEnable", "Z");


                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(wx.appId);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wxAppId", "Ljava/lang/String;");


                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(wx.appSecret);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wxSecretKey", "Ljava/lang/String;");


                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(wx.onlyAuthCode ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wxOnlyAuthCode", "Z");
            }

            ConfigExt qq = socialExt.qq;
            if (qq.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "qqEnable", "Z");


                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(qq.appId);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "qqAppId", "Ljava/lang/String;");
            }

            ConfigExt wb = socialExt.wb;
            if (wb.enable) {

                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wbEnable", "Z");


                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(wb.appId);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wbAppId", "Ljava/lang/String;");


                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(wb.url);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wbRedirectUrl", "Ljava/lang/String;");
            }

            ConfigExt dd = socialExt.dd;
            if (dd.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "ddEnable", "Z");

                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(dd.appId);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "ddAppId", "Ljava/lang/String;");
            }


            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(TransformX.toAsmBool(socialExt.shareSuccessIfStay));
            mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "shareSuccessIfStay", "Z");


            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitIntInsn(Opcodes.BIPUSH, socialExt.tokenExpiresHours);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "tokenExpiresHours", "I");


            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(TransformX.toAsmBool(socialExt.useGson));
            mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "useGson", "Z");


            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(TransformX.toAsmBool(socialExt.useOkHttp));
            mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "useOkHttp", "Z");


            if (socialExt.appName != null) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(socialExt.appName);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "appName", "Ljava/lang/String;");
            }


            if (socialExt.color != null) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(socialExt.color);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/graphics/Color", "parseColor", "(Ljava/lang/String;)I", false);
                mv.visitFieldInsn(Opcodes.PUTFIELD, "com/zfy/social/core/SocialOptions$Builder", "wbProgressColor", "I");
            }


            for (String name : Settings.platformClassList) {
                String pkgClassPath = TransformX.toPkgClassPath(name);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, "com/zfy/social/core/SocialOptions$Builder", "factoryClassList", "Ljava/util/Set;");
                mv.visitLdcInsn(pkgClassPath);
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true);
                mv.visitInsn(Opcodes.POP);
            }

            super.visitCode();

            UtilX.log("结束 visit code");
        }
    }
}
