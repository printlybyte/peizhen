package com.yinfeng.wypzh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.ViewPagerAdatper;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.ui.login.LoginActivity;
import com.yinfeng.wypzh.ui.login.SycCodeLoginActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.SFUtil;
import com.youth.banner.transformer.StackTransformer;

import java.util.ArrayList;
import java.util.List;


public class GuideActivity extends BaseActivity {

    ViewPager mViewPage;
    List<View> mViewList;
    LinearLayout mIn_ll;
    ImageView mOne_dot, mTwo_dot, mThree_dot, mFour_dot;
    ImageView mLight_dots;
    Button btNext;
    int mDistance;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mViewPage = mRootView.findViewById(R.id.in_viewpager);
        mIn_ll = mRootView.findViewById(R.id.in_ll);
        mLight_dots = findViewById(R.id.iv_light_dots);
    }

    @Override
    protected void setListener() {
    }

    private void toNext() {
        SFUtil.getInstance().setIsFirstOpenYes(this);
        Intent intent;
        if (ContextUtils.isLogin(GuideActivity.this)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, SycCodeLoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void initData() {
        mViewList = new ArrayList<View>();
        LayoutInflater lf = getLayoutInflater().from(GuideActivity.this);
        View view1 = lf.inflate(R.layout.we_indicator1, null);
        View view2 = lf.inflate(R.layout.we_indicator2, null);
        View view3 = lf.inflate(R.layout.we_indicator3, null);
        View view4 = lf.inflate(R.layout.we_indicator4, null);

        btNext = view4.findViewById(R.id.btNext);

        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        mViewList.add(view4);

        mViewPage.setAdapter(new ViewPagerAdatper(mViewList));

        addDots();
        moveDots();
//        mViewPage.setPageTransformer(true, new StackTransformer());

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toNext();
            }
        });
    }


    private void addDots() {
        mOne_dot = new ImageView(this);
        mOne_dot.setBackgroundResource(R.drawable.shape_oval_grey);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.dp6), (int) getResources().getDimension(R.dimen.dp6));
        layoutParams.setMargins(0, 0, 40, 0);
        mIn_ll.addView(mOne_dot, layoutParams);
        mTwo_dot = new ImageView(this);
        mTwo_dot.setBackgroundResource(R.drawable.shape_oval_grey);
        mIn_ll.addView(mTwo_dot, layoutParams);
        mThree_dot = new ImageView(this);
        mThree_dot.setBackgroundResource(R.drawable.shape_oval_grey);
        mIn_ll.addView(mThree_dot, layoutParams);
        mFour_dot = new ImageView(this);
        mFour_dot.setBackgroundResource(R.drawable.shape_oval_grey);
        mIn_ll.addView(mFour_dot, layoutParams);

        setClickListener();

    }

    private void setClickListener() {
        mOne_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPage.setCurrentItem(0);
            }
        });
        mTwo_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPage.setCurrentItem(1);
            }
        });
        mThree_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPage.setCurrentItem(2);
            }
        });
        mFour_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPage.setCurrentItem(3);
            }
        });
    }

    private void moveDots() {
        mLight_dots.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获得两个圆点之间的距离
                mDistance = mIn_ll.getChildAt(1).getLeft() - mIn_ll.getChildAt(0).getLeft();
                mLight_dots.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //页面滚动时小白点移动的距离，并通过setLayoutParams(params)不断更新其位置
                float leftMargin = mDistance * (position + positionOffset);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
                params.leftMargin = (int) leftMargin;
                mLight_dots.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
//                //页面跳转时，设置小圆点的margin
//                float leftMargin = mDistance * position;
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
//                params.leftMargin = (int) leftMargin;
//                mLight_dots.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_guide;
    }
}
