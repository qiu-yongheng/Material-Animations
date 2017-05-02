package com.lgvalle.material_animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lgvalle.material_animations.databinding.ActivityRevealBinding;


public class RevealActivity extends BaseDetailActivity implements View.OnTouchListener {
    private static final int DELAY = 100;
    private RelativeLayout bgViewGroup;
    private Toolbar toolbar;
    private Interpolator interpolator;
    private TextView body;
    private View btnRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindData();
        setupWindowAnimations();
        setupLayout();
        setupToolbar();
    }

    /**
     * bind
     */
    private void bindData() {
        ActivityRevealBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_reveal);
        Sample sample = (Sample) getIntent().getExtras().getSerializable(EXTRA_SAMPLE);
        binding.setReveal1Sample(sample);
    }

    /**
     * 设置动画
     */
    private void setupWindowAnimations() {
        // 插值器 ()
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        /**
         * 1. 共享元素: changeBounds
         * 2. 隐藏共享元素
         * 3.
         */
        setupEnterAnimations();
        setupExitAnimations();
    }

    /**
     * 设置进入的动画
     */
    private void setupEnterAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
        // 设置元素的动画
        getWindow().setSharedElementEnterTransition(transition);
        // 添加动画执行的监听
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            /**
             * 界面切换的动画结束后的回调
             * @param transition
             */
            @Override
            public void onTransitionEnd(Transition transition) {
                // 在这里删除侦听器是非常重要的，因为在退出时再次向后执行共享元素转换。 如果我们不删除监听器，这个代码将被再次触发
                transition.removeListener(this);
                // 隐藏共享元素小圆圈
                hideTarget();
                // 设置toolbar动画
                animateRevealShow(toolbar);
                // 设置按钮顺序进入, 延迟100nm执行
                animateButtonsIn();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    /**
     * 设置退出界面的动画
     */
    private void setupExitAnimations() {
        // 渐变
        Fade returnTransition = new Fade();
        // 设置退出界面的动画
        getWindow().setReturnTransition(returnTransition);
        // 设置动画时间
        returnTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        // 设置动画延迟开始时间
        returnTransition.setStartDelay(getResources().getInteger(R.integer.anim_duration_medium));
        // 监听动画执行
        returnTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                // 移除监听, 不然会重复执行
                transition.removeListener(this);
                // 设置按钮退出动画
                animateButtonsOut();
                // 当开始执行退出动画时, fragment设置自定义动画
                animateRevealHide(bgViewGroup);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    /**
     *
     */
    private void setupLayout() {
        bgViewGroup = (RelativeLayout) findViewById(R.id.reveal_root);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        body = ((TextView) findViewById(R.id.sample_body));
        View btnGreen = findViewById(R.id.square_green);
        btnRed = findViewById(R.id.square_red);
        View btnBlue = findViewById(R.id.square_blue);

        /** 1. 设置点击绿色色块, 改变背景(从中间开始, 至全屏) */
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealGreen();
            }
        });
        /** 2. 设置点击红色色块, 改变背景() */
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealRed();
            }
        });
        /** 3. 设置点击蓝色色块, 改变背景 */
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealBlue();
            }
        });
        /** 4. 设置点击黄色色块, 改变背景 */
        findViewById(R.id.square_yellow).setOnTouchListener(this);
    }

    /**
     * 改变蓝色背景
     */
    private void revealBlue() {
        // 1. 隐藏按钮
        animateButtonsOut();
        // 2. 动画从x = width/2, y = 0 开始
        Animator anim = animateRevealColorFromCoordinates(bgViewGroup, R.color.sample_blue, bgViewGroup.getWidth() / 2, 0);
        // 3. 监听动画执行
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 执行结束后, 显示按钮
                animateButtonsIn();
            }
        });
        body.setText(R.string.reveal_body4);
        body.setTextColor(ContextCompat.getColor(this, R.color.theme_blue_background));
    }

    /**
     * 先设置一个动画集, 色块移动到中间, 再改变背景
     */
    private void revealRed() {
        // 获取控件参数
        final ViewGroup.LayoutParams originalParams = btnRed.getLayoutParams();
        // 从xml中获取动画集
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
        // 监听动画执行
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                // 改变背景(从中间开始, 至全屏)
                animateRevealColor(bgViewGroup, R.color.sample_red);
                body.setText(R.string.reveal_body3);
                body.setTextColor(ContextCompat.getColor(RevealActivity.this, R.color.theme_red_background));
                // 还原色块原来的位置
                btnRed.setLayoutParams(originalParams);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        //
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        // 改变按钮位置到屏幕中间
        btnRed.setLayoutParams(layoutParams);
        // 执行动画
        TransitionManager.beginDelayedTransition(bgViewGroup, transition);
    }

    /**
     *
     * @param x
     * @param y
     */
    private void revealYellow(float x, float y) {
        // 执行动画改变背景
        animateRevealColorFromCoordinates(bgViewGroup, R.color.sample_yellow, (int) x, (int) y);
        body.setText(R.string.reveal_body1);
        body.setTextColor(ContextCompat.getColor(this, R.color.theme_yellow_background));
    }

    /**
     * 设置改变背景的动画
     */
    private void revealGreen() {
        animateRevealColor(bgViewGroup, R.color.sample_green);
        body.setText(R.string.reveal_body2);
        body.setTextColor(ContextCompat.getColor(this, R.color.theme_green_background));
    }

    /**
     * 隐藏分享元素: 小圆圈
     */
    private void hideTarget() {
        findViewById(R.id.shared_target).setVisibility(View.GONE);
    }

    /**
     * 进入界面时, 设置界面中的布局顺序进入
     */
    private void animateButtonsIn() {
        for (int i = 0; i < bgViewGroup.getChildCount(); i++) {
            // 获取父容器中的子控件
            View child = bgViewGroup.getChildAt(i);
            child.animate()
                    .setStartDelay(100 + i * DELAY) // 设置动画延迟执行
                    .setInterpolator(interpolator) // 设置插值器
                    .alpha(1) // 透明度为1, 可见
                    .scaleX(1) // 缩放1, 最大
                    .scaleY(1);
        }
    }

    /**
     * 退出界面时, 设置界面中的布局顺序退出
     */
    private void animateButtonsOut() {
        for (int i = 0; i < bgViewGroup.getChildCount(); i++) {
            // 获取父容器中的子控件
            View child = bgViewGroup.getChildAt(i);
            child.animate()
                    .setStartDelay(i) // 设置动画延迟执行
                    .setInterpolator(interpolator) // 设置插值器
                    .alpha(0) // 透明度0, 不可见
                    .scaleX(0f) // 缩放0, 最小
                    .scaleY(0f);
        }
    }

    /**
     *
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (view.getId() == R.id.square_yellow) {
                revealYellow(motionEvent.getRawX(), motionEvent.getRawY());
            }
        }
        return false;
    }

    /**
     * 设置显示toolbar的动画
     * @param viewRoot toolbar
     */
    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        /**
         * view 操作的视图
         * centerX 动画开始的中心点X
         * centerY 动画开始的中心点Y
         * startRadius 动画开始半径
         * startRadius 动画结束半径
         */
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius/2);
        // 这个可以不用写, 效果一样
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    /**
     * 设置改变背景的动画
     * @param viewRoot
     * @param color
     */
    private void animateRevealColor(ViewGroup viewRoot, @ColorRes int color) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        animateRevealColorFromCoordinates(viewRoot, color, cx, cy);
    }

    /**
     * 点击下面的色块时, 改变背景颜色, 设置改变背景的动画
     * @param viewRoot
     * @param color
     * @param x
     * @param y
     * @return
     */
    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        // 根据长宽计算斜边
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());
        // 从界面中心开始动画, 动画结束半径为屏幕斜边
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        // 设置控件背景
        viewRoot.setBackgroundColor(ContextCompat.getColor(this, color));
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        // 设置在动画开始与介绍的地方速率改变比较慢，在中间的时候加速
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

    /**
     * 设置界面退出时, 指定控件执行的动画
     * @param viewRoot
     */
    private void animateRevealHide(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = viewRoot.getWidth();

        /**
         * 创建动画
         * 参数一: 要设置动画的控件(fragment)
         * 参数二: 动画开始的x轴
         * 参数三: 动画开始的y轴
         * 参数四: 动画开始的半径(控件的宽)
         * 参数五: 动画结束的半径
         */
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 动画结束后, 隐藏控件
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        anim.start();
    }
}
