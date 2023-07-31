package com.rejig.pakagemanager.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.rejig.pakagemanager.utils.ContextUtil;
import com.rejig.pakagemanager.utils.FullScreenUtil;
import com.rejig.pakagemanager.utils.ScreenUtil;


public class BaseFullScreenDialog extends Dialog {
    private static final float WIDTH_FACTOR = 0.8f;
    private static final float HEIGHT_FACTOR = 0.5f;

    private final Context mContext;
    public BaseFullScreenDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public BaseFullScreenDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public void init() {
        Window dialogWindow = getWindow();
        if (dialogWindow == null) return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = getDialogWidth();
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void init303() {
        Window dialogWindow = getWindow();
        if (dialogWindow == null) return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtil.dip2px(303);
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void initLandlordDialog() {
        Window dialogWindow = getWindow();
        if (dialogWindow == null) return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtil.dip2px(360);
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void initFullWidth() {
        Window dialogWindow = getWindow();
        if (dialogWindow == null) return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtil.getScreenWidth();
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void initListDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = getDialogWidth();
        lp.height = getDialogHeight();
        window.setAttributes(lp);
    }

    public static int getDialogWidth() {
        return (int) (ScreenUtil.getScreenWidth() * WIDTH_FACTOR);
    }

    public static int getDialogHeight() {
        return (int) (ScreenUtil.getScreenHeight() * HEIGHT_FACTOR);
    }

    public void initBottomDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    public void initFullBottomDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    public void initFullScreenBottomDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        FullScreenUtil.setFullScreenWithStatusBar(window);
        window.setAttributes(lp);
    }

    public void initCenterDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    public void initFullScreenDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    public void initFullCenterDialog() {
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(window);
    }

    @Override
    public void show() {
        try {
            Activity activity = ContextUtil.getActivityFromContext(mContext);
            if (activity != null && activity.isFinishing()) return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.show();
    }

    @Override
    public void dismiss() {
        try {
            Activity activity = ContextUtil.getActivityFromContext(mContext);
            if (activity != null && activity.isFinishing()) return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dismiss();
    }
}
