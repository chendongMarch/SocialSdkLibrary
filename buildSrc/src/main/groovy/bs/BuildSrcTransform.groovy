package bs

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

class BuildSrcTransform extends Transform {

    Project project

    BuildSrcTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "BuildSrcTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(@NonNull TransformInvocation transformInvocation) {
        println "buildSrcLog chendong buildSrc transform SocialConfigTransform"
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            // 对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                BuildSrcInject.inject(directoryInput.file.absolutePath, project)
                // 文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
                // println "buildSrcLog dir source => ${directoryInput.file} ,desc => ${dest}"
            }
            // 对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->

                // jar文件一般是第三方依赖库jar文件

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
                // println "buildSrcLog source => ${jarInput.file} ,desc => ${dest}"
            }
        }
    }
}
