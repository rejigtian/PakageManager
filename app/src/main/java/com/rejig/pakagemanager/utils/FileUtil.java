package com.rejig.pakagemanager.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.test.internal.runner.ClassPathScanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import dalvik.system.DexFile;

/**
 * 文件夹操作的工具类
 * @author rejig
 * date 2021-08-10
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    public static final float UNPACK_APK_PRO = 0.1f;

    /**
     * 在文件后面追加写入
     * @param fullFilePath 文件完整路径
     * @param content 写入内容
     */
    public static void writeFileAppend(String fullFilePath, String content) {
        BufferedWriter out = null;
        try {
            if (content == null) content = "";
            File file = new File(fullFilePath);
            if (file.getParentFile() == null) return;
            file.getParentFile().mkdirs();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(content);
            out.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File getSaveFolder(){
        return AppUtil.getApplication().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
    }

    public static void getProguardFiles(String filePath, ProgressCallback progressCallback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            @SuppressLint("VisibleForTests")
            ClassPathScanner classPathScanner = new ClassPathScanner(filePath);
            try {
                long lastCallTime = System.currentTimeMillis();
                handler.postDelayed(() ->virtualProgressCall(handler, progressCallback,  0.01f),1000);
                @SuppressLint("VisibleForTests")
                Set<String> classSet = classPathScanner.getClassPathEntries(new ClassPathScanner.AcceptAllFilter());
                handler.removeCallbacksAndMessages(null);
                HashSet<String> pkgSet = new HashSet<>();
                int index = 0;
                int totalSize = classSet.size() + pkgSet.size();
                for (String str : classSet){
                    int lastIndex = str.lastIndexOf(".");
                    if (lastIndex == -1) {
                        lastIndex = str.length();
                    }
                    FileUtil.writeFileAppend(getSaveFolder()+"/class_name.txt", str);
                    str = str.substring(0, lastIndex);
                    int nextIndex = str.lastIndexOf(".");
                    if (nextIndex == str.length() - 2){
                        str = str.substring(0, lastIndex);
                    }
                    str ="-keep class " + str + ".**{*;}";

                    pkgSet.add(str);
                    Log.e(TAG, "onClick: " + str );
                    index ++;
                    totalSize = classSet.size() + pkgSet.size();
                    lastCallTime = checkNeedCallback(progressCallback, lastCallTime, (float) index, totalSize);
                }
                Set<String> sortSet = new TreeSet<>((o1, o2) -> o2.compareTo(o1));
                sortSet.addAll(pkgSet);
                for (String str : sortSet){
                    Log.e(TAG, "onClick: " + str );
                    FileUtil.writeFileAppend(getSaveFolder()+"/pkg_name.txt", str);
                    index ++;
                    lastCallTime = checkNeedCallback(progressCallback, lastCallTime, (float) index, totalSize);
                }
                ThreadUtil.runOnUiThread(progressCallback::finish);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void virtualProgressCall(Handler handler, ProgressCallback progressCallback,final float progress) {
        progressCallback.onProgressChange(progress);
        if (progress < UNPACK_APK_PRO) {
            handler.postDelayed(() ->virtualProgressCall(handler, progressCallback, progress + 0.01f),1000);
        }
    }

    private static long checkNeedCallback(ProgressCallback progressCallback, long lastCallTime, float index, int totalSize) {
        float progress;
        if (System.currentTimeMillis() - lastCallTime > 200){
            progress = index / totalSize;
            lastCallTime = System.currentTimeMillis();
            ThreadUtil.runOnUiThread(() -> progressCallback.onProgressChange(progress * (1-UNPACK_APK_PRO) + UNPACK_APK_PRO));
        }
        return lastCallTime;
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是Android 4.4之後的版本，而且屬於文件URI
            final String authority = uri.getAuthority();
            // 判斷Authority是否為本地端檔案所使用的
            if ("com.android.externalstorage.documents".equals(authority)) {
                // 外部儲存空間
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                if ("primary".equals(type)) {
                    return Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(divide[1]);
                } else {
                    return "/storage/".concat(type).concat("/").concat(divide[1]);
                }
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                // 下載目錄
                final String docId = DocumentsContract.getDocumentId(uri);
                if (docId.startsWith("raw:")) {
                    return docId.replaceFirst("raw:", "");
                }
                final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                return queryAbsolutePath(context, downloadUri);
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // 圖片、影音檔案
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                Uri mediaUri;
                if ("image".equals(type)) {
                    mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    return null;
                }
                mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
                return queryAbsolutePath(context, mediaUri);
            }
        } else {
            // 如果是一般的URI
            final String scheme = uri.getScheme();
            String path = null;
            if ("content".equals(scheme)) {
                // 內容URI
                path = queryAbsolutePath(context, uri);
            } else if ("file".equals(scheme)) {
                // 檔案URI
                path = uri.getPath();
            }
            return path;
        }
        return null;
    }

    public static String queryAbsolutePath(final Context context, final Uri uri) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static void getProguardFilesNew(String filePath, ProgressCallback progressCallback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                Set<String> classSet = new HashSet<>();
                DexFile dexFile = new DexFile(filePath);
                try {
                    Enumeration<String> classNames = dexFile.entries();
                    while (classNames.hasMoreElements()) {
                        String className = classNames.nextElement();
                        classSet.add(className);
                    }
                } finally {
                    dexFile.close();
                }
                long lastCallTime = System.currentTimeMillis();
                handler.postDelayed(() ->virtualProgressCall(handler, progressCallback,  0.01f),1000);
                handler.removeCallbacksAndMessages(null);
                Set<String> pkgSet = new LinkedHashSet<>();
                int index = 0;
                int totalSize = classSet.size() + pkgSet.size();
                for (String str : classSet){
                    int lastIndex = str.lastIndexOf(".");
                    FileUtil.writeFileAppend(getSaveFolder()+"/class_name.txt", str);
                    str = str.substring(0, lastIndex);
                    int nextIndex = str.lastIndexOf(".");
                    if (nextIndex == str.length() - 2){
                        str = str.substring(0, lastIndex);
                    }
                    str ="-keep class " + str + ".**{*;}";
                    pkgSet.add(str);
                    Log.e(TAG, "onClick: " + str );
                    index ++;
                    totalSize = classSet.size() + pkgSet.size();
                    lastCallTime = checkNeedCallback(progressCallback, lastCallTime, (float) index, totalSize);
                }
                for (String str : pkgSet){
                    Log.e(TAG, "onClick: " + str );
                    FileUtil.writeFileAppend(getSaveFolder()+"/pkg_name.txt", str);
                    index ++;
                    lastCallTime = checkNeedCallback(progressCallback, lastCallTime, (float) index, totalSize);
                }
                ThreadUtil.runOnUiThread(progressCallback::finish);
            } catch (Exception e) {
                e.printStackTrace();
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressCallback !=null) {
                            progressCallback.onFail(e.getMessage());
                        }
                    }
                });
            }
        }).start();
    }



    public interface ProgressCallback{
        void onProgressChange(float progress);
        void finish();
        void onFail(String message);
    }
}
