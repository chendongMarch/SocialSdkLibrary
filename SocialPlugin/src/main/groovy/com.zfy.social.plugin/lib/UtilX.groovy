package com.zfy.social.plugin.lib

import com.zfy.social.plugin.SocialPlugin

import java.util.function.Consumer

class UtilX {
    static def log(msg) {
        if (SocialPlugin.getSocialExt() != null && SocialPlugin.getSocialExt().debug) {
            println(msg)
        }
    }

    static def eachFile(File file, Consumer<File> fileConsumer) {
        file.eachFileRecurse { f ->
            fileConsumer.accept(f)
        }
    }
}