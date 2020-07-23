package com.yinfeng.wypzh.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.widget.TopBar;

public class AboutAppActivity extends BaseActivity {


    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView tvOne, tvTwo, tvThree;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("关于陪诊");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);

        tvOne = mRootView.findViewById(R.id.tvOne);
        tvTwo = mRootView.findViewById(R.id.tvTwo);
        tvThree = mRootView.findViewById(R.id.tvThree);

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        String one = "无忧陪诊APP，\n" +
                "是一个提供专业就医陪诊服务的网上服务平台。\n" +
                "用户可以通过手机APP进行线上下单，\n" +
                "由专业陪诊人员接单并提供陪诊服务。\n";
        String two = "平台提供的陪诊服务包括：\n" +
                "预约挂号排队，就诊陪护取药等服务。\n";
        String three = "平台切实解决了\n" +
                "老人、儿童、孕妇、工作忙碌的年轻人及\n" +
                "异地就医人群的无人陪护、\n" +
                "盲目奔波等问题，\n" +
                "可最大程度缩短患者的就医时间，\n" +
                "为患者就医提供更多的便利。";
        tvOne.setText(one);
        tvTwo.setText(two);
        tvThree.setText(three);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_about_app;
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, AboutAppActivity.class);
        context.startActivity(intent);
    }
}
