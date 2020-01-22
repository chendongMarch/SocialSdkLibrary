package com.zfy.social.plugin.lib;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;


/**
 * CreateAt : 2020-01-20
 * Describe :
 *
 * @author chendong
 */
public abstract class AbstractTransform extends Transform {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }


    protected abstract Function<ClassWriter, ClassVisitor> onEachClassFile(String name);

    protected boolean isAttentionFile(String name) {
        return !TransformX.isNotAttentionClass(name);
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        UtilX.log("start transform");
        long startTime = System.currentTimeMillis();
        // 获取输入
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        final TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        // 删除之前的输出
        if (outputProvider != null) {
            outputProvider.deleteAll();
        }
        for (TransformInput transformInput : inputs) {
            for (DirectoryInput directoryInput : transformInput.getDirectoryInputs()) {
                onEachDirectory(directoryInput, outputProvider);
            }
            for (JarInput jarInput : transformInput.getJarInputs()) {
                onEachJar(jarInput, outputProvider);
            }
        }
        long endTime = System.currentTimeMillis();
        UtilX.log("end transform cost time " + (endTime - startTime) / 1000 + " s");
    }


    // 遍历每文件夹
    private void onEachDirectory(DirectoryInput directoryInput, TransformOutputProvider provider) {
        if (directoryInput.getFile().isDirectory()) {
            UtilX.eachFile(directoryInput.getFile(), new Consumer<File>() {
                @Override
                public void accept(File file) {
                    if (file.isFile() && isAttentionFile(file.getName())) {
                        try {
                            byte[] bytes = ResourceGroovyMethods.getBytes(file);
                            byte[] code = TransformX.visitClass(bytes, onEachClassFile(file.getName()));
                            FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsolutePath() + File.separator + file.getName());
                            fos.write(code);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (provider != null) {
            File file = provider.getContentLocation(directoryInput.getName(),
                    directoryInput.getContentTypes(),
                    directoryInput.getScopes(),
                    Format.DIRECTORY);
            try {
                FileUtils.copyDirectory(directoryInput.getFile(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 遍历所有 jar
    private void onEachJar(JarInput input, TransformOutputProvider provider) {
        File file = input.getFile();
        if (!file.getAbsolutePath().endsWith("jar")) {
            return;
        }
        String jarName = input.getName();
        String md5Name = DigestUtils.md5Hex(file.getAbsolutePath());
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4);
        }
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            File tmpFile = new File(file.getParent() + File.separator + "classes_temp.jar");
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile));
            //用于保存
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                // 插桩class
                byte[] bytes = IOUtils.toByteArray(inputStream);
                if (isAttentionFile(entryName)) {
                    // class文件处理
                    jarOutputStream.putNextEntry(zipEntry);
                    byte[] code = TransformX.visitClass(bytes, onEachClassFile(entryName));
                    jarOutputStream.write(code);
                } else {
                    jarOutputStream.putNextEntry(zipEntry);
                    jarOutputStream.write(bytes);
                }
                jarOutputStream.closeEntry();
            }
            // 结束
            jarOutputStream.close();
            jarFile.close();
            File dest = provider.getContentLocation(jarName + md5Name,
                    input.getContentTypes(), input.getScopes(), Format.JAR);
            FileUtils.copyFile(tmpFile, dest);
            tmpFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
