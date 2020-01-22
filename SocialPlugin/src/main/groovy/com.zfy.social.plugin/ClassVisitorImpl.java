package com.zfy.social.plugin;

import com.zfy.social.plugin.extension.SocialExt;
import com.zfy.social.plugin.lib.TransformX;

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
public class ClassVisitorImpl extends ClassVisitor implements Opcodes {


    public static Factory factory = new Factory();

    static class Factory implements Function<ClassWriter, ClassVisitor> {
        @Override
        public ClassVisitor apply(ClassWriter classWriter) {
            return new ClassVisitorImpl(classWriter);
        }
    }

    private String className;
    private boolean isTargetClass;

    public ClassVisitorImpl(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;

//        System.out.println("ConfigInjectVisitor visit version = " + version
//                + ", access = " + access
//                + ", name = " + name
//                + ", signature  = " + signature
//                + ", superName = " + superName
//                + ", interfaces = " + Arrays.toString(interfaces));

        if (TransformX.isSubClass(superName, interfaces, "com/zfy/social/core/platform/AbsPlatform")) {
            System.out.println("是平台类的子类");
//            isTargetClass = true;
        }

        if ("com/zfy/social/core/util/AsmUtil".equals(name)) {
            isTargetClass = true;
            System.out.println("找到  AsmUtil");
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);

//        System.out.println("ConfigInjectVisitor visitMethod "
//                + ", access = " + access
//                + ", name = " + name
//                + ", desc = " + desc
//                + ", signature  = " + signature
//                + ", exceptions = " + exceptions);

        if (isTargetClass && "updateSocialOptions".equals(name)) {
            System.out.println("找到  AsmUtil updateSocialOptions");
            SocialConfigMethodVisitorImpl visitor = new SocialConfigMethodVisitorImpl(methodVisitor);
            return visitor;
        }

        return methodVisitor;
    }


    @Override
    public void visitEnd() {
        super.visitEnd();
        // System.out.println("ConfigInjectVisitor visitEnd ");
    }

    static class SocialPlatformMethodVisitorImpl extends MethodVisitor {

        public SocialPlatformMethodVisitorImpl(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitCode() {

            mv.visitFieldInsn(Opcodes.GETSTATIC, "com/zfy/social/core/util/AsmUtil", "TAG", "Ljava/lang/String;");
            mv.visitLdcInsn("register platform com.zfy.social.wx.WxPlatform$Factory");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn("com.zfy.social.wx.WxPlatform$Factory");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/zfy/social/core/_SocialSdk", "registerPlatform", "(Ljava/lang/String;)V", false);

            super.visitCode();
        }
    }

    static class SocialConfigMethodVisitorImpl extends MethodVisitor {

        public SocialConfigMethodVisitorImpl(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitCode() {
            mv.visitLdcInsn("SocialSdk");
            SocialExt socialExt = SocialPlugin.getSocialExt();
            mv.visitLdcInsn("start init social sdk options " + socialExt.toString());
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);

            if (socialExt.wx.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(socialExt.wx.appId);
                mv.visitLdcInsn(socialExt.wx.appSecret);
                mv.visitInsn(socialExt.wx.onlyAuthCode ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/zfy/social/core/SocialOptions$Builder", "wx", "(Ljava/lang/String;Ljava/lang/String;Z)Lcom/zfy/social/core/SocialOptions$Builder;", false);
                mv.visitInsn(Opcodes.POP);
            }

            if (socialExt.qq.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(socialExt.qq.appId);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/zfy/social/core/SocialOptions$Builder", "qq", "(Ljava/lang/String;)Lcom/zfy/social/core/SocialOptions$Builder;", false);
                mv.visitInsn(Opcodes.POP);
            }

            if (socialExt.wb.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(socialExt.wb.appId);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/zfy/social/core/SocialOptions$Builder", "wb", "(Ljava/lang/String;)Lcom/zfy/social/core/SocialOptions$Builder;", false);
                mv.visitInsn(Opcodes.POP);
            }
            if (socialExt.dd.enable) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(socialExt.dd.appId);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/zfy/social/core/SocialOptions$Builder", "dd", "(Ljava/lang/String;)Lcom/zfy/social/core/SocialOptions$Builder;", false);
                mv.visitInsn(Opcodes.POP);
            }

            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            //判断RETURN
            if (opcode == Opcodes.RETURN) {
                //在这里插入代码
            }
            super.visitInsn(opcode);
        }
    }
}
