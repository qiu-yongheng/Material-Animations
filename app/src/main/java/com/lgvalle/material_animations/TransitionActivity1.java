package com.lgvalle.material_animations;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.View;

import com.lgvalle.material_animations.databinding.ActivityTransition1Binding;

public class TransitionActivity1 extends BaseDetailActivity {
    private Sample sample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindData();
        setupWindowAnimations();
        setupLayout();
        setupToolbar();
    }

    /**
     * 初始化界面bind
     */
    private void bindData() {
        ActivityTransition1Binding binding = DataBindingUtil.setContentView(this, R.layout.activity_transition1);
        sample = (Sample) getIntent().getExtras().getSerializable(EXTRA_SAMPLE);
        binding.setTransition1Sample(sample);
    }

    /**
     * 淡入淡出
     * 设置A-->B的动画, 在B中设置
     * 表示B中控件展示的动画
     */
    private void setupWindowAnimations() {
        Visibility enterTransition = buildEnterTransition();
        getWindow().setEnterTransition(enterTransition);
    }

    /**
     * item的点击事件, 显示不同的界面切换效果
     */
    private void setupLayout() {
        /** 1. 显示代码中设置的切换动画 */
        findViewById(R.id.sample1_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransitionActivity1.this, TransitionActivity2.class);
                i.putExtra(EXTRA_SAMPLE, sample);
                i.putExtra(EXTRA_TYPE, TYPE_PROGRAMMATICALLY);
                // 打开界面
                transitionTo(i);
            }
        });
        /** 2. 显示xml中设置的切换动画 */
        findViewById(R.id.sample1_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransitionActivity1.this, TransitionActivity2.class);
                i.putExtra(EXTRA_SAMPLE, sample);
                i.putExtra(EXTRA_TYPE, TYPE_XML);
                transitionTo(i);
            }
        });
        /** 3.  */
        findViewById(R.id.sample1_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransitionActivity1.this, TransitionActivity3.class);
                i.putExtra(EXTRA_SAMPLE, sample);
                i.putExtra(EXTRA_TYPE, TYPE_PROGRAMMATICALLY);
                transitionTo(i);
            }
        });
        /** 4.  */
        findViewById(R.id.sample1_button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransitionActivity1.this, TransitionActivity3.class);
                i.putExtra(EXTRA_SAMPLE, sample);
                i.putExtra(EXTRA_TYPE, TYPE_XML);
                transitionTo(i);
            }
        });
        /** 5. 设置设置平移动画退出界面 */
        findViewById(R.id.sample1_button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Visibility returnTransition = buildReturnTransition();
                // 设置返回使用平移动画
                getWindow().setReturnTransition(returnTransition);

                finishAfterTransition();
            }
        });
        /** 6. 不指定退出动画, 默认使用进入动画(渐变) */
        findViewById(R.id.sample1_button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * If no return transition is defined Android will use reversed enter transition
                 * In this case, return transition will be a reversed Slide (defined in buildEnterTransition)
                 * 如果没有设置返回动画, Android默认使用buildEnterTransition的动画效果
                 */
                finishAfterTransition();
            }
        });
    }

    /**
     * 创建进入的动画 A -> B , int B
     * 渐变动画
     * @return
     */
    private Visibility buildEnterTransition() {
        Fade enterTransition = new Fade();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        // 这个视图将不会受到进入过渡动画的影响
        enterTransition.excludeTarget(R.id.square_red, true);
        return enterTransition;
    }

    /**
     * 创建界面返回上一个界面时的动画 A -> B , int A
     * 平移动画
     * @return
     */
    private Visibility buildReturnTransition() {
        Visibility enterTransition = new Slide();
//        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        enterTransition.setDuration(5000);
        return enterTransition;
    }
}
