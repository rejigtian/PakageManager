package com.rejig.pakagemanager.utils;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;

public class AnimatorUtil {

    /**
     * 快速构造一个常见的属性动画
     * @param isCountUp 是否增量计数，若为true则从0到1计数
     * @param repeatCount 重复次数
     * @param mills 动画时常
     * @param interpolator 插值器
     * @return 构造好的 {@link ValueAnimator}
     */
    public static ValueAnimator getAnimator(boolean isCountUp, int repeatCount, int mills, TimeInterpolator interpolator){
        ValueAnimator animator = new ValueAnimator();
        animator.setInterpolator(interpolator);
        if (isCountUp){
            animator.setFloatValues(0, 1);
        } else {
            animator.setFloatValues(1, 0);
        }
        animator.setRepeatCount(repeatCount);
        animator.setDuration(mills);
        return animator;
    }
}
