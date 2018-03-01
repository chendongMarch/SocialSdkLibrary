package com.march.socialsdk.manager;

/**
 * CreateAt : 2018/3/1
 * Describe :
 *
 * @author chendong
 */
public class IManager {

    public static final int INVALID_PARAM = -1;

    public static final int ACTION_TYPE_LOGIN = 0;
    public static final int ACTION_TYPE_SHARE = 1;

    public static final String KEY_SHARE_MEDIA_OBJ = "KEY_SHARE_MEDIA_OBJ"; // media obj key
    public static final String KEY_ACTION_TYPE = "KEY_ACTION_TYPE"; // action type

    public static final String KEY_SHARE_TARGET = "KEY_SHARE_TARGET"; // share target
    public static final String KEY_LOGIN_TARGET = "KEY_LOGIN_TARGET"; // login target

}
