package com.yinfeng.wypzh.ui.order.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseFragment;

/**
 * @author Asen
 */
public class OrderBlankFragment extends BaseFragment {
    public static OrderBlankFragment newInstance() {
        Bundle args = new Bundle();
        OrderBlankFragment fragment = new OrderBlankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText("尚未开发，敬请期待！");
//        LottieAnimationView animationView = (LottieAnimationView) view.findViewById(R.id.animation_view);
//        animationView.setAnimation("lottiejsonfile/loading_emptypage_withimags.json", LottieAnimationView.CacheStrategy.Weak);
//        animationView.setAnimation("lottiejsonfile/loading_emptypage.json", LottieAnimationView.CacheStrategy.Weak);
//        animationView.setImageAssetsFolder("images/");//assets目录下的子目录，存放动画所需的图片
//        animationView.loop(true);
//        animationView.setScale(0.3f);
//        animationView.playAnimation();//播放动画

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_order_blank;
    }
}
