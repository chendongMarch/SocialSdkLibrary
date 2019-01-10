package bs

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildSrcPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('testPlugin').doLast {
            println "buildSrcLog Hello gradle plugin in src"
        }

        // 插件位置
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("buildSrcLog 'android' or 'android-library' plugin required.")
        }
        // 变体
        final def variants
        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }

        project.extensions.create('testbuildsrc', BuildSrcExtension)

        def android = project.extensions.getByType(AppExtension)
        def classTransform = new BuildSrcTransform(project);
        android.registerTransform(classTransform);

         variants.all { variant ->
            println "buildSrcLog chendong ${project.testbuildsrc.message}"
        }

    }
}