package com.march.socialsdk.helper;

import com.march.socialsdk.model.ShareObj;

import java.io.IOException;

/**
 * CreateAt : 2017/5/22
 * Describe : ShareObj 辅助类
 *
 * @author chendong
 */
public class ShareObjHelper {

    /**
     * 检测缩略图文件是不是存在
     *
     * @param obj ShareObj
     * @return 是否可用
     */
    public static boolean checkThumbImagePathValid(ShareObj obj) {
        if (CommonHelper.isAnyEmpty(obj.getThumbImagePath()))
            return false;
        String thumbImagePath = obj.getThumbImagePath();
        // 文件是不是存在
        if (FileHelper.isExist(thumbImagePath))
            return true;
        String localPath = FileHelper.mapUrl2LocalPath(thumbImagePath);
        // 映射后文件是否存在
        if (FileHelper.isExist(localPath)) {
            obj.setThumbImagePath(localPath);
            return true;
        }
        return false;
    }

    /**
     * 下载 ShareObj 中的 thumbImage
     *
     * @param obj ShareObj
     * @throws IOException io
     */
    public static void prepareThumbImagePath(ShareObj obj) throws IOException {
        if (checkThumbImagePathValid(obj))
            return;
        if (!FileHelper.isHttpPath(obj.getThumbImagePath()))
            throw new IllegalArgumentException("本地文件不存在，又不是网络路径");
        String thumbImagePath = obj.getThumbImagePath();
        String localPath = FileHelper.mapUrl2LocalPath(thumbImagePath);
        FileHelper.downloadFileSync(thumbImagePath, localPath);
        obj.setThumbImagePath(localPath);
    }
}
