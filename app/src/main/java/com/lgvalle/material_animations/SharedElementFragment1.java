package com.lgvalle.material_animations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by lgvalle on 05/09/15.
 */
public class SharedElementFragment1 extends Fragment {

    private static final String EXTRA_SAMPLE = "sample";

    /**
     * 创建fragment对象, 并绑定对应的数据, 以后可以取出
     * @param sample
     * @return
     */
    public static SharedElementFragment1 newInstance(Sample sample) {

        Bundle args = new Bundle();

        args.putSerializable(EXTRA_SAMPLE, sample);
        SharedElementFragment1 fragment = new SharedElementFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sharedelement_fragment1, container, false);
        // 获取传递过来的数据
        final Sample sample = (Sample) getArguments().getSerializable(EXTRA_SAMPLE);

        final ImageView squareBlue = (ImageView) view.findViewById(R.id.square_blue);
        // 设置图片填充色
        DrawableCompat.setTint(squareBlue.getDrawable(), sample.color);

        //
        view.findViewById(R.id.sample2_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNextFragment(sample, squareBlue, false);
            }
        });

        //
        view.findViewById(R.id.sample2_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNextFragment(sample, squareBlue, true);
            }
        });

        return view;
    }

    /**
     * 切换fragment
     * @param sample
     * @param squareBlue
     * @param overlap
     */
    private void addNextFragment(Sample sample, ImageView squareBlue, boolean overlap) {
        // 1. 创建fragment
        SharedElementFragment2 sharedElementFragment2 = SharedElementFragment2.newInstance(sample);
        // 2. 创建平移动画
        Slide slideTransition = new Slide(Gravity.RIGHT);
        slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        // 3. 共享元素动画
        ChangeBounds changeBoundsTransition = new ChangeBounds();
        changeBoundsTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        // 4. 设置fragment进入动画
        sharedElementFragment2.setEnterTransition(slideTransition);
        // 5. 设置动画是否有重叠部分
        sharedElementFragment2.setAllowEnterTransitionOverlap(overlap);
        sharedElementFragment2.setAllowReturnTransitionOverlap(overlap);
        // 6. 设置共享元素动画
        sharedElementFragment2.setSharedElementEnterTransition(changeBoundsTransition);

        getFragmentManager().beginTransaction()
                .replace(R.id.sample2_content, sharedElementFragment2)
                .addToBackStack(null)
                .addSharedElement(squareBlue, getString(R.string.square_blue_name)) // 添加共享元素
                .commit();
    }

}
