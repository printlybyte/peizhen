package com.yinfeng.wypzh.ui.order.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.widget.MsgView;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.OrderChildViewPageAdapter;
import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.widget.TopBar;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends BaseFragment {
    private TopBar mTopBar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private List<BaseFragment> mFragments;
    private String[] titles;
    private int mCurrentPosition = 0;

    private OrderChildViewPageAdapter mPageAdapter;

    public static OrderFragment newInstance() {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.hideTopBack();
        mTopBar.setTopCenterTxt("我的订单");
        mSlidingTabLayout = mRootView.findViewById(R.id.mSlidingTabLayout);

        mViewPager = mRootView.findViewById(R.id.mViewPager);
        initTitles();
        initFragments();
        mPageAdapter = new OrderChildViewPageAdapter(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(mPageAdapter);
        mSlidingTabLayout.setViewPager(mViewPager, titles);
        if (mCurrentPosition < mFragments.size())
            mSlidingTabLayout.setCurrentTab(mCurrentPosition);

    }

    private void initTitles() {
        titles = new String[5];
        titles[0] = "待接单";
        titles[1] = "待服务";
        titles[2] = "进行中";
        titles[3] = "待评价";
        titles[4] = "已结束";
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        BaseFragment fragment0 = OrderWaitReceiveFragment.newInstance();
        BaseFragment fragment1 = OrderWaitServiceFragment.newInstance();
        BaseFragment fragment2 = OrderServicingFragment.newInstance();
        BaseFragment fragment3 = OrderWaitCommentFragment.newInstance();
        BaseFragment fragment4 = OrderFinishFragment.newInstance();
        mFragments.add(fragment0);
        mFragments.add(fragment1);
        mFragments.add(fragment2);
        mFragments.add(fragment3);
        mFragments.add(fragment4);
    }

    public void setCurrentItemPosition(int position) {
        mCurrentPosition = position;
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_ORDER_REDPOINT_SHOW, mode = ThreadMode.MAIN)
    private void showOrderRedPoint(int position) {
        LogUtil.error("EventBus showOrderRedPoint orderId :" + position);
//        mSlidingTabLayout.showDot(position);
//        mSlidingTabLayout.showMsg(1, 1);
//        mSlidingTabLayout.setMsgMargin(position, 12, 12);

        if (mCurrentPosition == position)
            return;
        mSlidingTabLayout.setMsgMargin(position, 16, 8);
        TextView tv_tab_title = mSlidingTabLayout.getTitleView(position);
        View tabView = (View) tv_tab_title.getParent();
        MsgView msgView = (MsgView) tabView.findViewById(com.flyco.tablayout.R.id.rtv_msg_tip);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
        DisplayMetrics dm = msgView.getResources().getDisplayMetrics();
        msgView.setVisibility(View.VISIBLE);
        msgView.setStrokeWidth(0);
        msgView.setText("");
        lp.width = (int) (8 * dm.density);
        lp.height = (int) (8 * dm.density);
        msgView.setLayoutParams(lp);
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_ORDER_REDPOINT_HIDE, mode = ThreadMode.MAIN)
    private void hideOrderRedPoint(int position) {
        LogUtil.error("EventBus hideOrderRedPoint orderId :" + position);
            mSlidingTabLayout.hideMsg(position);
    }

    @Override
    protected void initData() {


    }

    @Override
    protected void setListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSlidingTabLayout.hideMsg(position);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (mCurrentPosition < mFragments.size()) {
            mSlidingTabLayout.setCurrentTab(mCurrentPosition);
            mSlidingTabLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSlidingTabLayout.hideMsg(mCurrentPosition);
                }
            }, 1000);
        }
        OrderUtil.startTimeTaskMain();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        OrderUtil.stopTimeTaskMain();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_order;
    }
}
