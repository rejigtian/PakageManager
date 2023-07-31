package com.rejig.pakagemanager;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.rejig.pakagemanager.utils.AppUtil;
import com.rejig.pakagemanager.utils.FileUtil;
import com.rejig.pakagemanager.utils.PermissionCallback;
import com.rejig.pakagemanager.utils.PermissionRequester;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQ_CODE_FILE_CHOOSE = 1000;
    private Button getPkgBtn;
    private Button copyBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_main);
        getPkgBtn = findViewById(R.id.get_pkg_btn);
        copyBtn = findViewById(R.id.copy_save_file_btn);
        setListener();
    }


    private void setListener() {
        getPkgBtn.setOnClickListener(v -> PermissionRequester.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .permission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestDialogTip("为了本地文件，需要开启文件权限。")
                .request(new PermissionCallback() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll, boolean alreadyHas) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");//无类型限制
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, REQ_CODE_FILE_CHOOSE);
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        Toast.makeText(MainActivity.this, "您拒绝了授权", Toast.LENGTH_LONG).show();
                    }
                }));

        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("data", FileUtil.getSaveFolder().getAbsolutePath());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "复制成功", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAssignFolder(File file){
        if(file == null || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(AppUtil.getApplication(), AppUtil.getApplication().getApplicationContext().getPackageName(), file);
        intent.setDataAndType(uri, "file/*");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_CODE_FILE_CHOOSE == requestCode){
            if (data != null){
                Uri uri = data.getData();
                if (uri == null) return;
                Toast.makeText(this, "文件较大时加载速度较慢，请耐心等待～", Toast.LENGTH_LONG).show();
                DexProgressDialog.show(this, FileUtil.getPathFromUri(this, uri));
            }
        }
    }
}
