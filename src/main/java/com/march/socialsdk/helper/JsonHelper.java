package com.march.socialsdk.helper;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.adapter.IJsonAdapter;

/**
 * CreateAt : 2016/12/3
 * Describe : 使用外部注入的 json 转换类，减轻类库的依赖
 *
 * @author chendong
 */

public class JsonHelper {

    public static <T> T getObject(String jsonString, Class<T> cls) {
        IJsonAdapter jsonAdapter = SocialSdk.getJsonAdapter();
        if (jsonAdapter != null) {
            try {
                return jsonAdapter.toObj(jsonString, cls);
            } catch (Exception e) {
                e.printStackTrace();
                PlatformLog.e("JsonHelper", e.getMessage());
            }
        }
        return null;
    }

    public static String getObject2Json(Object object) {
        IJsonAdapter jsonAdapter = SocialSdk.getJsonAdapter();
        if (jsonAdapter != null) {
            return jsonAdapter.toJson(object);
        }
        return null;
    }
}
