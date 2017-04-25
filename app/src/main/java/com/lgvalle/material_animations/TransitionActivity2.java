package com.lgvalle.material_animations;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import com.lgvalle.material_animations.databinding.ActivityTransition2Binding;

public class TransitionActivity2 extends BaseDetailActivity {

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindData();
        setupWindowAnimations();
        setupLayout();
        setupToolbar();
    }

    /**
     * bind初始化界面
     */
    private void bindData() {
        ActivityTransition2Binding binding = DataBindingUtil.setContentView(this, R.layout.activity_transition2);
        Sample sample = (Sample) getIntent().getExtras().getSerializable(EXTRA_SAMPLE);
        // 获取要显示的动画类型
        type = getIntent().getExtras().getInt(EXTRA_TYPE);
        // 给界面绑定数据
        binding.setTransition2Sample(sample);
    }

    /**
     * 根据类型设置动画是代码实现还是xml实现
     * 设置界面进入的动画
     */
    private void setupWindowAnimations() {
        Transition transition;
        if (type == TYPE_PROGRAMMATICALLY) {
            transition = buildEnterTransition();
        }  else {
            transition = TransitionInflater.from(this).inflateTransition(R.transition.explode);
        }
        getWindow().setEnterTransition(transition);
    }

    /**
     * 在显示动画后, 结束界面
     */
    private void setupLayout() {
        findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
    }

    /**
     * 爆炸动画: 控件往中间移动
     * @return
     */
    private Transition buildEnterTransition() {
        Explode enterTransition = new Explode();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }

}
