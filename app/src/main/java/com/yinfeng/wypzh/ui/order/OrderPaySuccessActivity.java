package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_DETAIL;
import static com.yinfeng.wypzh.ui.MainActivity.KEY_NEED_SWITCH_POSITION;

public class OrderPaySuccessActivity extends BaseActivity {
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView tvOrderId, tvOrderTime;
    private TextView tvServiceTime, tvServiceAddress, tvServiceAsk;
    private TextView tvFinalPrice;
    private Button btComplete;

    private String orderId, orderTime;
    private String serviceTime, serviceAddress, serviceAsk;
    private String orderName;
    private String finalPrice;

    OrderDetailBean orderDetailBean;


    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("支付成功");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
//                toMainActvity();
                goToOrderList();
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);

        tvOrderId = mRootView.findViewById(R.id.tvOrderId);
        tvOrderTime = mRootView.findViewById(R.id.tvOrderTime);
        tvServiceTime = mRootView.findViewById(R.id.tvServiceTime);
        tvServiceAddress = mRootView.findViewById(R.id.tvServiceAddress);
        tvServiceAsk = mRootView.findViewById(R.id.tvServiceAsk);
        tvFinalPrice = mRootView.findViewById(R.id.tvFinalPrice);
        btComplete = mRootView.findViewById(R.id.btComplete);
    }


    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btComplete).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        toWaitService();
                    }
                });
    }

    @Override
    protected void initData() {
        getIntentData();

        orderId = orderDetailBean.getId();
        orderName = "订单号：" + orderDetailBean.getCode();
        orderTime = "下单时间：" + orderDetailBean.getCreateTime();
        serviceTime = getServiceTime(orderDetailBean.getMakeStartTime(), orderDetailBean.getMakeEndTime());
        serviceAddress = orderDetailBean.getHospitalName() + " " + orderDetailBean.getDepartmentName();
        serviceAsk = orderDetailBean.getRemark();
        finalPrice = ContextUtils.getPriceStrConvertFenToYuan(orderDetailBean.getPayPrice());
//        priceRaw = "￥" + orderDetailBean.getServicePrice();
//        priceFinal = "￥" + orderDetailBean.getPayPrice();
//        coupon = getCouponStr(orderDetailBean.getCouponId());

        tvOrderId.setText(orderName);
        tvOrderTime.setText(orderTime);
        tvServiceTime.setText(serviceTime);
        tvServiceAddress.setText(serviceAddress);
        tvServiceAsk.setText(serviceAsk);
        tvFinalPrice.setText(finalPrice);
//        tvServicePrice.setText(priceRaw);
//        tvcoupon.setText(coupon);
//        tvFinalPrice.setText(priceFinal);
    }

    private String getServiceTime(String startTime, String endTime) {
        String startYear = startTime.substring(0, 5);
        String endYear = endTime.substring(0, 5);
        if (TextUtils.equals(startYear, endYear)) {
            String temp = endTime.substring(5, endTime.length());
            return startTime + "—" + temp;
        } else {
            return startTime + "—" + endTime;
        }
    }

    private void toMainActvity() {
//        Intent intent = new Intent(OrderPaySuccessActivity.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra(KEY_NEED_SWITCH_POSITION, 1);
//        startActivity(intent);
//        finish();

    }

    private void toWaitService() {
        OrderWaitReceiveActivity.activityStart(this, orderDetailBean);
        finish();
    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(KEY_ORDER_DETAIL);
        if (orderDetailBean == null)
            finish();
        OrderUtil.addOrderWaitReceive(orderDetailBean.getId());
        RedPointUtil.showOrderDot(0);
        RedPointUtil.showBottomDot(1);
        SFUtil.getInstance().addOrderLoopWaitReceive(this, orderDetailBean.getId());
        OrderUtil.startLoopWaitReceive();
    }

    public static void activityStart(Context context, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderPaySuccessActivity.class);
        intent.putExtra(KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressedSupport() {
////        toMainActvity();
//        finish();
        goToOrderList();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_pay_success;
    }

    private void goToOrderList() {
        OrderUtil.toOrderListFragment(this, 0);
        finish();
    }

}
