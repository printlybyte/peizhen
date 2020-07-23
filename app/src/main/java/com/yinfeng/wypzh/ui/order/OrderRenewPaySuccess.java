package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.RenewResult;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_DETAIL;

public class OrderRenewPaySuccess extends BaseActivity {
    private RenewResult renewResult;
    private OrderDetailBean orderDetailBean;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TopBar mTopBar;
    private Button btConfirm;
    private ImageView ivHeadIcon, ivMsg, ivPhone;
    private TextView tvName, tvPhone;
    private TextView tvDesc;
    private String waiterPhone;
    private String waitAccount;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("服务中");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                OrderUtil.toOrderListFragment(OrderRenewPaySuccess.this, 2);
                finish();
            }
        });

        ivHeadIcon = mRootView.findViewById(R.id.ivHeadIcon);
        ivMsg = mRootView.findViewById(R.id.ivMsg);
        ivPhone = mRootView.findViewById(R.id.ivPhone);
        tvName = mRootView.findViewById(R.id.tvName);
        tvPhone = mRootView.findViewById(R.id.tvPhone);
        tvDesc = mRootView.findViewById(R.id.tvDesc);
        btConfirm = mRootView.findViewById(R.id.btConfirm);

    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btConfirm).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        OrderUtil.toOrderListFragment(OrderRenewPaySuccess.this, 2);
                        finish();
                    }
                });
        RxView.clicks(ivMsg).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (!TextUtils.isEmpty(waitAccount))
                            openMsg(waitAccount);
//                            requestIMessagePermissions(waitAccount);
                    }
                });
        RxView.clicks(ivPhone).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (!TextUtils.isEmpty(waiterPhone))
                            ContextUtils.callPhone(OrderRenewPaySuccess.this, waiterPhone);
                    }
                });
    }

    @Override
    protected void initData() {
        if (orderDetailBean != null) {
            OrderUtil.addOrderServicing(orderDetailBean.getId());
            String profile = orderDetailBean.getProfile();
            String waitName = orderDetailBean.getWaiterName();
            waiterPhone = orderDetailBean.getWaiterPhone();
            waitAccount = orderDetailBean.getWaiterId();

            ImageUtil.getInstance().loadImgCircle(this, profile, ivHeadIcon);
            tvName.setText(waitName);
            tvPhone.setText(waiterPhone);
            String desc = "说明：如陪诊员在20分钟内未能确认延时请求\n" +
                    "订单将默认该服务结束，进入订单支付环节。";
            tvDesc.setText(desc);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_renew_pay_success;
    }

    public static void activityStart(Context context, RenewResult renewResult, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderRenewPaySuccess.class);
        intent.putExtra(OrderRenewPrePay.KEY_RENEW_RESULT, renewResult);
        intent.putExtra(KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(KEY_ORDER_DETAIL);
        renewResult = (RenewResult) getIntent().getSerializableExtra(OrderRenewPrePay.KEY_RENEW_RESULT);
        if (orderDetailBean == null || renewResult == null)
            finish();
        if (renewResult != null) {
            SFUtil.getInstance().addOrderLoopRenewReply(this, renewResult.getRenewId());
            OrderUtil.startLoopRenewReply();
        }
    }

    @Override
    public void onBackPressedSupport() {
        OrderUtil.toOrderListFragment(OrderRenewPaySuccess.this, 2);
        finish();
    }
}
