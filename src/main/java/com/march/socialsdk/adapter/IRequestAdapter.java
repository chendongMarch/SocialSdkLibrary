package com.march.socialsdk.adapter;

import java.io.File;

/**
 * CreateAt : 2017/12/8
 * Describe : 请求的 adapter
 *
 * @author chendong
 */
public interface IRequestAdapter {

    File getFile(String url);

    String getJson(String url);
}
