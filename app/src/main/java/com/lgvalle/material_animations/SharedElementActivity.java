package com.lgvalle.material_animations;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.Gravity;

import com.lgvalle.material_animations.databinding.ActivitySharedelementBinding;

/**
 * 共享title, icon元素
 */
public class SharedElementActivity extends BaseDetailActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sample sample = (Sample) getIntent().getExtras().getSerializable(EXTRA_SAMPLE);
        bindData(sample);
        setupWindowAnimations();
        setupLayout(sample);
        setupToolbar();
    }

    /**
     * bind初始化界面
     * @param sample
     */
    private void bindData(Sample sample) {
        ActivitySharedelementBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sharedelement);
        // 绑定数据
        binding.setSharedSample(sample);
    }

    /**
     * 设置界面进入的切换动画
     */
    private void setupWindowAnimations() {
        // 我们没有兴趣定义一个新的输入转换。 相反，我们更改默认转换持续时间
        getWindow().getEnterTransition().setDuration(getResources().getInteger(R.integer.anim_duration_long));
    }

    /**
     * 左边平移动画
     *
     * @param sample
     */
    private void setupLayout(Sample sample) {
        // 动画始终从左边开始
        Slide slideTransition = new Slide(Gravity.LEFT);
        slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        // 创建Fragment并定义其中的一些过渡
        SharedElementFragment1 sharedElementFragment1 = SharedElementFragment1.newInstance(sample);
        // 设置重新进入的动画(左边出来)
        sharedElementFragment1.setReenterTransition(slideTransition);
        // 设置离开的动画(回到左边)
        sharedElementFragment1.setExitTransition(slideTransition);
        // 设置元素进入的动画
        sharedElementFragment1.setSharedElementEnterTransition(new ChangeBounds());

        // 显示fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample2_content, sharedElementFragment1)
                .commit();
    }
}
