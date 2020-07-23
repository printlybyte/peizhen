package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class OrderCommentSuccessActivity extends BaseActivity {
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private Button btShare;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("评价成功");
        mTopBar.setTopRightTxt("投诉");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mTopBar.setTopBarRightTxtListener(new TopBar.TopBarRightTextCickListener() {
            @Override
            public void topRightTxtClick() {
                ContextUtils.callComplainPhone(OrderCommentSuccessActivity.this);
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        btShare = mRootView.findViewById(R.id.btShare);

    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btShare).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        goToSharePage();
                    }
                });
    }

    private void goToSharePage() {
        Intent intent = new Intent(this, ShareOrderCompleteActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_comment_success;
    }

}
