package com.rejig.pakagemanager.utils;


import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

/**
 * @author rejig
 * date 2021-08-10
 */
public class PermissionOption {
    FragmentActivity activity;
    ArrayList<String> permissions = new ArrayList<>();
    String requestTip = "";
    String okTip = "确认";
    String cancelTip = "取消";
}
