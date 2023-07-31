package com.rejig.pakagemanager.utils;

import android.os.Handler;
import android.os.Looper;

import com.rejig.pakagemanager.Thread.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by geeksammao on 24/10/2017.
 */

public class ThreadUtil {
    private static final ExecutorService pool = Executors.newFixedThreadPool(1, new NamedThreadFactory("ThreadUtil"));
    private static final ExecutorService cacheThread = Executors.newCachedThreadPool(new NamedThreadFactory("ThreadUtil"));
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public static void runOnUiThreadDelay(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    public static void runOnChildThread(Runnable runnable) {
        pool.submit(runnable);
    }

    /**
     *  替代每次new Thread
     *  有新任务直接执行，回收超时时间60s
     *  @param runnable runnable
     */
    public static void runInOtherThread(Runnable runnable) {
        cacheThread.submit(runnable);
    }

}
