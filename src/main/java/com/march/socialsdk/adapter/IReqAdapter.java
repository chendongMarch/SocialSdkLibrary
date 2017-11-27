package com.march.socialsdk.adapter;

import java.util.Map;

/**
 * CreateAt : 2017/11/25
 * Describe :
 *
 * @author chendong
 */
public interface IReqAdapter {

    interface OnResultListener {
        void onSuccess(String result);

        void onFailure(Exception exception);
    }

    void sendRequest(String url,Map<String,String> params,OnResultListener listener);
}
