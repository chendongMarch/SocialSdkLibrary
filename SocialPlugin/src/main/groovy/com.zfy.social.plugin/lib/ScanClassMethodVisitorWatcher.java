package com.zfy.social.plugin.lib;

import com.zfy.social.plugin.lib.AbstractClassVisitor;

import org.objectweb.asm.MethodVisitor;

/**
 * CreateAt : 2020-01-21
 * Describe :
 *
 * @author chendong
 */
public interface ScanClassMethodVisitorWatcher {
    MethodVisitor watch(MethodVisitor visitor, AbstractClassVisitor.ClassInfo classInfo, AbstractClassVisitor.MethodInfo methodInfo);
}
