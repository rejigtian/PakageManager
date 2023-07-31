package com.rejig.pakagemanager.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rejig.pakagemanager.R;
import com.rejig.pakagemanager.utils.AnimatorUtil;
import com.rejig.pakagemanager.utils.ScreenUtil;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressView extends ConstraintLayout {
    private ProgressBar progressBar;
    private ConstraintLayout progressLay;
    private ValueAnimator valueAnimator;
    private ImageView progressIv;
    private TextView titleTv;
    private boolean smoothGrow = false;
    private int progress = 0;
    private Timer timer;
    private final static int TIME_INTERVAL = 200;

    public ProgressView(@NonNull Context context) {
        super(context);
        init();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProgressView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.progress_view, this);
        progressBar = findViewById(R.id.progress_bar);
        progressLay = findViewById(R.id.progress_cover_lay);
        progressIv = findViewById(R.id.progress_iv);
        titleTv = findViewById(R.id.title_tv);
        titleTv.setVisibility(GONE);
        progressLay.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), ScreenUtil.dip2px(9));
            }
        });
        progressLay.setClipToOutline(true);
    }

    public void setTitle(String title){
        if (title != null && title.length() > 0){
            titleTv.setVisibility(VISIBLE);
            titleTv.setText(title);
        }
    }

    public void setSmoothGrow(boolean smoothGrow) {
        this.smoothGrow = smoothGrow;
    }

    public void updateProgress(float progress){
        this.progress = (int) (progress * 100);
        if (smoothGrow){
            if (timer != null) {
                return;
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateProgressSmooth();
                }
            }, TIME_INTERVAL);
        } else {
            progressBar.setProgress(this.progress);
        }
        if ((progress * 100) < 1) {
            progressLay.setVisibility(View.INVISIBLE);
        } else {
            progressLay.setVisibility(View.VISIBLE);
            progressLay.getLayoutParams().width = (int) (ScreenUtil.dip2px(272) * progress);
        }
        progressAnimateOn();
    }

    private void updateProgressSmooth() {
        int currentProgress = progressBar.getProgress();
        if (currentProgress < progress){
            currentProgress++;
            progressBar.setProgress(currentProgress);
        }
    }

    private void progressAnimateOn() {
        if (valueAnimator == null) {
            int transWidth = ScreenUtil.dip2px((float) 18.5);
            valueAnimator = AnimatorUtil.getAnimator(true, ValueAnimator.INFINITE, 500, new LinearInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                float ratio = (float) animation.getAnimatedValue();
                progressIv.setTranslationX(transWidth * ratio);
            });
            valueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDetachedFromWindow();
    }
}
