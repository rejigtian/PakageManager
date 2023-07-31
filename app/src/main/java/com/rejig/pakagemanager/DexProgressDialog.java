package com.rejig.pakagemanager;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rejig.pakagemanager.utils.FileUtil;
import com.rejig.pakagemanager.widget.BaseFullScreenDialog;
import com.rejig.pakagemanager.widget.ProgressView;

public class DexProgressDialog extends FrameLayout {
    private ProgressView progressView;
    private Dialog dialog;
    public DexProgressDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public DexProgressDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DexProgressDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dex_progress_dialog, this);
        progressView = findViewById(R.id.progress_view);
        progressView.setTitle("当前进度 0%");
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void analysisPkg(String filePath) {
        FileUtil.getProguardFiles(filePath, new FileUtil.ProgressCallback() {
            @Override
            public void onProgressChange(float progress) {
                progressView.setTitle("当前进度 "+(int)(progress*100)+"%");
                progressView.updateProgress(progress);
            }

            @Override
            public void finish() {
                if (dialog != null){
                    dialog.dismiss();
                    Toast.makeText(getContext(), "加载完成, 文件保存于"
                            + FileUtil.getSaveFolder(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(String message) {
                if (dialog != null){
                    dialog.dismiss();
                }
                Toast.makeText(getContext(), "加载失败" + message, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    public static void show(Context context, String filePath){
        DexProgressDialog dexProgressDialog = new DexProgressDialog(context);
        BaseFullScreenDialog dialog = new BaseFullScreenDialog(context, R.style.dialog_style_custom);
        dialog.setContentView(dexProgressDialog);
        dialog.init303();
        dialog.setCancelable(false);
        dialog.show();
        dexProgressDialog.analysisPkg(filePath);
        dexProgressDialog.setDialog(dialog);
    }
}
