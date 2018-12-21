package com.babypat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.march.socialsdk.adapter.IJsonAdapter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

/**
 * CreateAt : 2017/11/25
 * Describe : 默认使用 gson 转换
 *
 * @author chendong
 */
public class GsonJsonAdapter implements IJsonAdapter {

    @Override
    public <T> T toObj(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.getTime());
            }
        }).setDateFormat(DateFormat.LONG);
        return gsonBuilder.create().toJson(object);
    }
}
