package bs

import javassist.ClassPool
import org.gradle.api.Project

class BuildSrcInject {

    private static final ClassPool sClassPool = ClassPool.getDefault()

    // https://github.com/zjw-swun/AppMethodTime/blob/master/BuildSrc/src/main/groovy/com.zjw.plugin/MyInject.groovy
    public static void inject(String path, Project project) {
        try {

            // 将当前路径加入 class pool, 否则找不到这个类
            sClassPool.appendClassPath(path)
            // 加入android.jar，不然找不到android相关的所有类
            sClassPool.appendClassPath(project.android.bootClasspath[0].toString())
            // 引入 Bundle 包
            // sClassPool.importPackage("android.os.Bundle")
            File dir = new File(path)
            if (dir.isDirectory()) {
                dir.eachFileRecurse { File file ->
                    def filePath = file.getAbsolutePath()
                    if (file.getName().equals("TestActivity.class")) {
                        // 获取 TestActivity Class
                        def ctClass = sClassPool.getCtClass("com.babypat.TestActivity")
                        

                        if (ctClass.isFrozen()) {
                            ctClass.defrost()
                        }
                        // 获取到 onCreate 方法
                        def ctMethod = ctClass.getDeclaredMethod("onCreate")
                        String insertBeforeStr = """ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
                                                """
                        ctMethod.insertBefore(insertBeforeStr)
                        ctClass.writeFile(path)
                        ctClass.detach()

                        println "chendong ${path}  ${filePath}"
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

    }

}