package com.zfy.social.plugin.lib;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.function.Function;

/**
 * CreateAt : 2020-01-20
 * Describe :
 *
 * @author chendong
 */
public class TransformX {

    /**
     * 去除资源等 class 文件
     *
     * @param name class name
     * @return 是否是需要关注的 class
     */
    public static boolean isNotAttentionClass(String name) {
        return !name.endsWith(".class")
                || name.startsWith("R$")
                || "R.class".equals(name)
                || "BuildConfig.class".equals(name);
    }

    /**
     * @param superName  父类
     * @param interfaces 接口
     * @param target     目标类
     * @return 判断是否是某个类/接口的子类
     */
    public static boolean isSubClass(String superName, String[] interfaces, String target) {
        if (superName.equals(target)) {
            return true;
        }
        for (String anInterface : interfaces) {
            if (anInterface.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用一个 class visitor 进入 class 内部
     *
     * @param bytes bytes
     * @return 输出的 bytes
     */
    public static byte[] visitClass(byte[] bytes, Function<ClassWriter, ClassVisitor> mapper) {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        classReader.accept(mapper.apply(classWriter), ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    public static String toByteClassPath(String path) {
        return path.replace(".", "/");
    }

    public static String toPkgClassPath(String path) {
        return path.replace("/", ".");
    }

    public static int toAsmBool(boolean value) {
        return value ? Opcodes.ICONST_1 : Opcodes.ICONST_0;
    }
}
