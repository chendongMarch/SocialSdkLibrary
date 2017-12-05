package com.march.socialsdk.adapter.impl;

import com.march.socialsdk.adapter.IImageConvertAdapter;
import com.march.socialsdk.helper.FileHelper;
import com.march.socialsdk.helper.PlatformLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * CreateAt : 2017/12/5
 * Describe : 内置网络图片自动下载
 *
 * @author chendong
 */
public class DefImageConvertAdapter implements IImageConvertAdapter {

    @Override
    public File convertImage(String url) {
        try {
            return saveInputStreamToFile(FileHelper.downloadFileSync(url), url);
        } catch (IOException e) {
            PlatformLog.t(e);
        }
        return null;
    }

    private File saveInputStreamToFile(InputStream is, String url) {
        File file = new File(FileHelper.mapUrl2LocalPath(System.currentTimeMillis() + url));
        if (file.exists())
            return file;
        return FileHelper.saveStreamToFile(file, is);
    }
}
