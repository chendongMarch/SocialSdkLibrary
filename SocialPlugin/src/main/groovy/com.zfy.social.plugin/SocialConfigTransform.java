package com.zfy.social.plugin;

import com.zfy.social.plugin.lib.AbstractTransform;
import com.zfy.social.plugin.lib.UtilX;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.function.Function;


/**
 * CreateAt : 2020-01-20
 * Describe :
 *
 * @author chendong
 */
public class SocialConfigTransform extends AbstractTransform {

    @Override
    public Function<ClassWriter, ClassVisitor> onEachClassFile(String name) {
        UtilX.log("social transform result " + name);
        return SocialClassVisitorImpl.factory;
    }


    @Override
    protected boolean isAttentionFile(String name) {
        return super.isAttentionFile(name) && name.startsWith("com/zfy/social/core/SocialOptions$Builder.class");
    }

}
