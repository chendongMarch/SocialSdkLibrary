package com.zfy.social.plugin;

import com.zfy.social.plugin.extension.Settings;
import com.zfy.social.plugin.lib.AbstractClassVisitor;
import com.zfy.social.plugin.lib.AbstractTransform;
import com.zfy.social.plugin.lib.TransformX;
import com.zfy.social.plugin.lib.UtilX;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


/**
 * CreateAt : 2020-01-20
 * Describe :
 *
 * @author chendong
 */
public class ScanClassTransform extends AbstractTransform {

    private Set<String> platformClassList;
    private Factory classVisitorFactory;

    public ScanClassTransform() {
        platformClassList = Settings.platformClassList = new HashSet<>();
        classVisitorFactory = new Factory();
    }

    @Override
    protected Function<ClassWriter, ClassVisitor> onEachClassFile(String name) {
        UtilX.log("scan transform result " + name);
        return classVisitorFactory;
    }

    @Override
    protected boolean isAttentionFile(String name) {
        return super.isAttentionFile(name)
                && !name.startsWith("android")
                && ! name.startsWith("com/google")
                && ! name.startsWith("com/squareup")
                && !name.startsWith("com/tencent")
                && !name.startsWith("com/sina")
                && !name.startsWith("com/android/dingtalk")
                && !name.startsWith("bolts")
                && !name.startsWith("butterknife")
                && !name.startsWith("okio")
                && ! name.startsWith("okhttp");
    }

    class Factory implements Function<ClassWriter, ClassVisitor> {
        @Override
        public ClassVisitor apply(ClassWriter classWriter) {
            return new ScanClassVisitor((classWriter));
        }
    }

    class ScanClassVisitor extends AbstractClassVisitor {

        public ScanClassVisitor(ClassVisitor classVisitor) {
            super(classVisitor);
        }

        @Override
        public MethodVisitor watch(MethodVisitor visitor, ClassInfo classInfo, MethodInfo methodInfo) {
            if (TransformX.isSubClass(classInfo.superName,
                    classInfo.interfaces,
                    "com/zfy/social/core/platform/PlatformFactory")) {
                platformClassList.add(classInfo.name);
            }
            return super.watch(visitor, classInfo, methodInfo);
        }
    }
}
