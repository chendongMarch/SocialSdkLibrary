package com.zfy.social.plugin.lib;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

/**
 * CreateAt : 2020-01-20
 * Describe : 访问 class 工具
 *
 * @author chendong
 */
public class AbstractClassVisitor extends ClassVisitor implements Opcodes, ScanClassMethodVisitorWatcher {

    public static class ClassInfo {
        public int version;
        public int access;
        public String name;
        public String signature;
        public String superName;
        public String[] interfaces;

        @Override
        public String toString() {
            return "ClassInfo{" +
                    "version=" + version +
                    ", access=" + access +
                    ", name='" + name + '\'' +
                    ", signature='" + signature + '\'' +
                    ", superName='" + superName + '\'' +
                    ", interfaces=" + Arrays.toString(interfaces) +
                    '}';
        }
    }


    public static class MethodInfo {
        public int access;
        public String name;
        public String desc;
        public String signature;
        public String[] exceptions;

        @Override
        public String toString() {
            return "MethodInfo{" +
                    "access=" + access +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", signature='" + signature + '\'' +
                    ", exceptions=" + Arrays.toString(exceptions) +
                    '}';
        }
    }

    private ScanClassMethodVisitorWatcher methodVisitorWatcher = this;
    private ClassInfo classInfo = new ClassInfo();
    private MethodInfo methodInfo = new MethodInfo();

    public AbstractClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    public void setMethodVisitorWatcher(ScanClassMethodVisitorWatcher methodVisitorWatcher) {
        this.methodVisitorWatcher = methodVisitorWatcher;
    }

    @Override
    public MethodVisitor watch(MethodVisitor visitor, ClassInfo classInfo, MethodInfo methodInfo) {
        return null;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        classInfo.version = version;
        classInfo.access = access;
        classInfo.name = name;
        classInfo.signature = signature;
        classInfo.superName = superName;
        classInfo.interfaces = interfaces;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        methodInfo.access = access;
        methodInfo.name = name;
        methodInfo.desc = desc;
        methodInfo.signature = signature;
        methodInfo.exceptions = exceptions;
        if (methodVisitorWatcher != null) {
            MethodVisitor visitor = methodVisitorWatcher.watch(methodVisitor, classInfo, methodInfo);
            if (visitor != null) {
                return visitor;
            }
        }
        return methodVisitor;
    }


    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
