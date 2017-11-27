package com.march.socialsdk.adapter;


/**
 * CreateAt : 2017/11/25
 * Describe : json 转换
 *
 * @author chendong
 */
public interface IJsonAdapter {

    <T> T toObj(String jsonString, Class<T> cls);

    String toJson(Object object);
}
