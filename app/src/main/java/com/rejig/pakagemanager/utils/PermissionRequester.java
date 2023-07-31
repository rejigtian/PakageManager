package com.rejig.pakagemanager.utils;


import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rejig
 * date 2021-08-10
 */
public class PermissionRequester {
    private final PermissionOption option = new PermissionOption();

    private PermissionRequester() { }

    public static PermissionRequester with(FragmentActivity activity) {
        PermissionRequester permission = new PermissionRequester();
        permission.option.activity = activity;
        return permission;
    }

    public PermissionRequester permission(String... permissions) {
        option.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public PermissionRequester requestDialogTip(String tip) {
        option.requestTip = tip;
        return this;
    }

    /**
     * 该方法需要弹出对话框会导致所调用的 activity 出现 onResume。
     * @param callback 权限申请回调
     */
    public void request(PermissionCallback callback) {
        if (requesting()) {
            return;
        }

        if (option.activity == null) {
            Log.e("WPPermission", "activity should not be null");
            return;
        }

        if (option.activity.isFinishing() || option.activity.isDestroyed()) {
            Log.e("WPPermission", "activity destroyed");
            return;
        }

        final InterceptCallback cb = new InterceptCallback(option.activity, callback);

        ArrayList<String> failPermissions = PermissionUtil.getFailPermissions(option.activity, option.permissions);

        if (failPermissions.size() == 0) {
            cb.hasPermission(option.permissions, true, true);
        } else {
            if (!TextUtils.isEmpty(option.requestTip)) {
                Log.e("WPPermission", "show tip dialog");
                PermissionDialog dialog = PermissionDialog.newDialog(option.requestTip,
                        option.okTip, option.cancelTip);
                dialog.setOkClickListener((dialog1, fromOkBtn) -> {
                    PermissionFragment.newInstance(option).prepareRequest(option.activity, cb);
                    dialog1.dismiss();
                });
                dialog.setCancelClickListener((dialog12, fromOkBtn) -> {
                    cb.noPermission(option.permissions, false);
                    dialog12.dismiss();
                });
                dialog.showIfNeed(option.activity.getSupportFragmentManager(), option.permissions.toString());
            } else {
                //申请没有授予过的权限
                PermissionFragment.newInstance(option).prepareRequest(option.activity, cb);
            }
        }
    }

    private boolean requesting() {
        FragmentActivity activity = option.activity;
        if (activity == null) {
            return false;
        }
        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(activity.getClass().getName());
        Fragment dialogFragment = fm.findFragmentByTag(option.permissions.toString());
        Log.e("WPPermission", "requesting: " + fragment + "\t" + dialogFragment);
        return fragment != null || dialogFragment != null;
    }

    private static class InterceptCallback implements PermissionCallback {
        PermissionCallback callback;
        Context context;

        InterceptCallback(Context context, PermissionCallback callback) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        public void hasPermission(List<String> granted, boolean isAll, boolean alreadyHas ) {
            if (granted.contains(Manifest.permission.RECORD_AUDIO)) {
                if (!PermissionUtil.hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
                    //某些特殊手机在给了录音权限之后还是不能录音。这里更进一步检查。
                    noPermission(granted, true);
                    return;
                }
            }
            callback.hasPermission(granted, isAll, alreadyHas);
        }

        @Override
        public void noPermission(List<String> denied, boolean quick) {
            callback.noPermission(denied, quick);
        }
    }
}
