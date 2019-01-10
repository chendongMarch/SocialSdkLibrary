package bs

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildSrcPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('testPlugin').doLast {
            println "chendong Hello gradle plugin in src"
        }

        project.extensions.create('testbuildsrc', BuildSrcExtension)

        project.android.applicationVariants.all { variant ->
            println "chendong ${project.testbuildsrc.message}"
        }

    }
}