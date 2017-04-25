package com.lgvalle.material_animations;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Sample> samples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        setupSamples();
        setupToolbar();
        setupLayout();
    }

    /**
     * 设置界面切换动画
     * 跳转到其他界面
     * 从其他界面返回
     * 平移动画
     */
    private void setupWindowAnimations() {
        // Re-enter transition is executed when returning to this activity
        Slide slideTransition = new Slide();
        // 设置平移方向
        slideTransition.setSlideEdge(Gravity.LEFT);
        // 设置动画时间
        slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
    }

    /**
     * 创建数据用来演示
     * getResources().getColor(R.color.color_name) 在Android6.0以后已经不再支持, 可以使用以下的方法
     * ContextCompat.getColor(): support v4包下的方法, 向下兼容所有的api
     */
    private void setupSamples() {
        samples = Arrays.asList(
                new Sample(ContextCompat.getColor(this, R.color.sample_red), "Transitions"),
                new Sample(ContextCompat.getColor(this, R.color.sample_blue), "Shared Elements"),
                new Sample(ContextCompat.getColor(this, R.color.sample_green), "View animations"),
                new Sample(ContextCompat.getColor(this, R.color.sample_yellow), "Circular Reveal Animation")
        );
    }

    /**
     * 设置toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 布局中已经使用自定义的标题, 隐藏系统的标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * 设置列表显示
     * item的点击事件在adapter中处理
     */
    private void setupLayout() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sample_list);
        // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        // 设置item线性排列
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 创建adapter, 绑定数据
        SamplesRecyclerAdapter samplesRecyclerAdapter = new SamplesRecyclerAdapter(this, samples);
        // 绑定adapter
        recyclerView.setAdapter(samplesRecyclerAdapter);
    }
}
