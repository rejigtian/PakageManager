package com.rejig.pakagemanager.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rejig.pakagemanager.R;

import java.lang.reflect.Field;

/**
 * 权限申请弹窗，支持部分UI自定义
 *
 * @author rejig
 * date 2021-08-10
 */
public class PermissionDialog extends DialogFragment {
    private static final long SHOW_TIME_INTERVAL = 500;
    private static long lastShowTime = 0;

    private static final String KEY_CONTENT = "content";
    private static final String KEY_OK = "ok_tip";
    private static final String KEY_CANCEL = "cancel_tip";

    private TextView cancelBtn;
    private TextView okBtn;
    private TextView contentTv;

    private OnClickListener okClickListener;
    private OnClickListener cancelClickListener;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            fragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.permission_view, container);
        cancelBtn = v.findViewById(R.id.cancel_tv);
        okBtn = v.findViewById(R.id.ok_tv);
        contentTv = v.findViewById(R.id.content_tv);
        initViews();
        initEvent();
        return v;
    }

    private void initViews() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        String content = bundle.getString(KEY_CONTENT);
        String ok = bundle.getString(KEY_OK, "确认");
        String cancel = bundle.getString(KEY_CANCEL);

        contentTv.setText(content);
        okBtn.setText(ok);
        if (TextUtils.isEmpty(cancel)) {
            cancelBtn.setVisibility(View.GONE);
        } else {
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText(cancel);
        }
    }

    private void initEvent() {
        okBtn.setOnClickListener(v -> {
            if (okClickListener != null) {
                okClickListener.onClick(PermissionDialog.this, true);
            }
        });
        cancelBtn.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.onClick(PermissionDialog.this, false);
            }
        });
    }

    public void showIfNeed(FragmentManager fragmentManager, String tag) {
        long div = System.currentTimeMillis() - lastShowTime;
        if (div < SHOW_TIME_INTERVAL && div > 0) {
            return;
        }
        lastShowTime = System.currentTimeMillis();
        showAllowingStateLoss(fragmentManager, tag);
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        try {
            Field mDismissed = DialogFragment.class.getDeclaredField("mDismissed");
            mDismissed.setAccessible(true);
            mDismissed.set(this, false);
            Field mShownByMe = DialogFragment.class.getDeclaredField("mShownByMe");
            mShownByMe.setAccessible(true);
            mShownByMe.set(this, true);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                show(manager, tag);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void setCancelClickListener(OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public void setOkClickListener(OnClickListener okClickListener) {
        this.okClickListener = okClickListener;
    }

    public interface OnClickListener {
        void onClick(DialogFragment dialog, boolean fromOkBtn);
    }

    public static PermissionDialog newDialog(String contentTip, String okBtn, String cancelBtn) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CONTENT, contentTip);
        bundle.putString(KEY_OK, okBtn);
        bundle.putString(KEY_CANCEL, cancelBtn);
        PermissionDialog dialog = new PermissionDialog();
        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat_Dialog);
        dialog.setCancelable(false);
        dialog.setArguments(bundle);
        return dialog;
    }
}
