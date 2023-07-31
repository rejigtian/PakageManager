package com.rejig.pakagemanager.utils;

import java.util.List;

/**
 * 权限申请回调
 * @author rejig
 * date 2021-08-10
 */
public interface PermissionCallback {

    /**
     * 有权限被授予时回调
     *
     * @param granted 请求成功的权限组
     * @param isAll   是否全部授予了
     * @param alreadyHas 本来就有权限
     */
    void hasPermission(List<String> granted, boolean isAll, boolean alreadyHas);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param denied 请求失败的权限组
     * @param quick  是否有某个权限被永久拒绝了
     */
    void noPermission(List<String> denied, boolean quick);
}
