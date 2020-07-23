package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.OrderConfirmCompleteParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.RenewParam;
import com.yinfeng.wypzh.bean.order.RenewResult;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailBean;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailResult;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.RenewOrderDialog;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_DETAIL;
import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_ID;

public class OrderDetailServicing extends BaseActivity {

    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView tvRemainTip, tvTimeRemain;
    private ImageView ivHeadIcon, ivPhone, ivMsg;
    private TextView tvName, tvPhone;
    private TextView tvOrderId, tvOrderTime, tvServiceAddress, tvServiceTime, tvServiceAsk, tvFinalPrice;
    private TextView tvRenewOrder, tvConfirmComplete;
    private OrderDetailBean orderDetailBean;
    private boolean isConfirmCompleting = false;
    private boolean isRenewing = false;
    private ServiceOptionDetailBean serviceOptionDetailBean;
    private boolean isGetServiceDetailing = false;
    private String orderId;


    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("服务进行中");
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
                ContextUtils.callComplainPhone(OrderDetailServicing.this);
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);

        tvTimeRemain = mRootView.findViewById(R.id.tvTimeRemain);
        tvRemainTip = mRootView.findViewById(R.id.tvRemainTip);

        ivHeadIcon = mRootView.findViewById(R.id.ivHeadIcon);
        ivPhone = mRootView.findViewById(R.id.ivPhone);
        ivMsg = mRootView.findViewById(R.id.ivMsg);
        tvName = mRootView.findViewById(R.id.tvName);
        tvPhone = mRootView.findViewById(R.id.tvPhone);

        tvOrderId = mRootView.findViewById(R.id.tvOrderId);
        tvOrderTime = mRootView.findViewById(R.id.tvOrderTime);
        tvServiceTime = mRootView.findViewById(R.id.tvServiceTime);
        tvServiceAddress = mRootView.findViewById(R.id.tvServiceAddress);
        tvServiceAsk = mRootView.findViewById(R.id.tvServiceAsk);
        tvFinalPrice = mRootView.findViewById(R.id.tvFinalPrice);

        tvRenewOrder = mRootView.findViewById(R.id.tvRenewOrder);
        tvConfirmComplete = mRootView.findViewById(R.id.tvConfirmComplete);
        resetView();
    }

    private void resetView() {

        if (orderDetailBean != null) {
//            String profile = orderDetailBean.getProfile();
            String name = orderDetailBean.getWaiterName();
            String waiterPhone = "联系电话:" + orderDetailBean.getWaiterPhone();

            String orderId = "订单号: " + orderDetailBean.getCode();
            String orderTime = "下单时间：" + orderDetailBean.getCreateTime();
            String serviceTime = ContextUtils.getServiceTime(orderDetailBean.getMakeStartTime(), orderDetailBean.getMakeEndTime());
            String serviceAddress = orderDetailBean.getHospitalName() + " " + orderDetailBean.getDepartmentName();
            String serviceAsk = orderDetailBean.getRemark();
            String finalPrice = ContextUtils.getPriceStrConvertFenToYuan(orderDetailBean.getPayPrice());

            String waiterInfoStr = orderDetailBean.getWaiter();
            if (!TextUtils.isEmpty(waiterInfoStr)) {
                try {
                    WaiterInfo waiterInfo = new Gson().fromJson(waiterInfoStr, WaiterInfo.class);
                    if (waiterInfo != null) {
                        String profile = waiterInfo.getProfile();
                        ImageUtil.getInstance().loadImgCircle(this, profile, ivHeadIcon);
                    }
                } catch (Exception e) {
                }
            }
//            ImageUtil.getInstance().loadImgCircle(this, profile, ivHeadIcon);

            tvName.setText(name);
            tvPhone.setText(waiterPhone);
            tvOrderId.setText(orderId);
            tvOrderTime.setText(orderTime);
            tvServiceTime.setText(serviceTime);
            tvServiceAddress.setText(serviceAddress);
            tvServiceAsk.setText(serviceAsk);
            tvFinalPrice.setText(finalPrice);
            resetTimeRemain(orderDetailBean.getEndTime());
        }
    }

    private void resetTimeRemain(String endTime) {
        String remainTime = ContextUtils.getRemainTimeServicing(endTime);
        if (TextUtils.isEmpty(remainTime)) {
            tvTimeRemain.setText("");
        } else {
            if (TextUtils.equals(remainTime, "已超时")) {
                tvRemainTip.setText(getResources().getString(R.string.orderserving_time_out));
                tvTimeRemain.setText(remainTime);
                tvTimeRemain.setTextColor(Color.RED);
            } else {
                tvRemainTip.setText(getResources().getString(R.string.orderserving_time_remain));
                tvTimeRemain.setText(remainTime);
                tvTimeRemain.setTextColor(ContextCompat.getColor(this, R.color.c06b49b));
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(tvConfirmComplete).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        showConfirmCompleteDialog();
                    }
                });
        RxView.clicks(tvRenewOrder).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        doGetServiceDetail();
                    }
                });
        RxView.clicks(ivPhone).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String phoneNum = orderDetailBean.getWaiterPhone();
                        if (!TextUtils.isEmpty(phoneNum))
                            ContextUtils.callPhone(OrderDetailServicing.this, phoneNum);
                    }
                });
        RxView.clicks(ivMsg).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String account = orderDetailBean.getWaiterId();
                        if (!TextUtils.isEmpty(account))
                            openMsg(account);
//                            requestIMessagePermissions(account);
                    }
                });
        RxView.clicks(ivHeadIcon).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        goToPzyInfo();
                    }
                });
    }

    private void goToPzyInfo() {
        WaiterInfo info = ContextUtils.getWaiterInfo(orderDetailBean);
        if (info != null)
            PzyDetailActivity.activityStart(this, info);
    }

    private void showConfirmCompleteDialog() {
        final MaterialDialog confirmDialog = DialogHelper.getMaterialDialog(OrderDetailServicing.this, "确认服务已完成?");
        confirmDialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        confirmDialog.dismiss();
                    }
                }
                , new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        confirmDialog.dismiss();
                        doConfirmComplete();
                    }
                }
        );
        confirmDialog.show();
    }

    private void doConfirmComplete() {
        if (isConfirmCompleting) {
            showLoadingDialog();
            return;
        }
        isConfirmCompleting = true;
        showLoadingDialog();
        OrderConfirmCompleteParam param = new OrderConfirmCompleteParam(orderDetailBean.getId());
        OrderApi.getInstance().confirmComplete(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        hideLoadingDialog();
                        isConfirmCompleting = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderUtil.addOrderWaitComment(orderDetailBean.getId());
                            OrderUtil.deleteOrderServicing(orderDetailBean.getId());
                            RedPointUtil.showOrderDot(3);
                            RedPointUtil.showBottomDot(1);
                            finish();
                        } else {
                            ToastUtil.getInstance().showLong(OrderDetailServicing.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isConfirmCompleting = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void showRenewOrderDialog() {
        RenewOrderDialog renewOrderDialog = DialogHelper.getRenewOrderDialog(this, serviceOptionDetailBean, new RenewOrderDialog.OnConfirmRenewOrderListener() {
            @Override
            public void confirmRenew(int time, int price) {
                doRenewOrder(time, price);
            }
        });
        renewOrderDialog.show();
    }

    private void doGetServiceDetail() {
        if (isGetServiceDetailing) {
            showLoadingDialog();
            return;
        }
        isGetServiceDetailing = true;
        showLoadingDialog();
        OrderApi.getInstance().getServiceOptionDetail("")
                .compose(RxSchedulers.<Response<ServiceOptionDetailResult>>applySchedulers())
                .compose(this.<Response<ServiceOptionDetailResult>>bindToLife())
                .subscribe(new BaseObserver<ServiceOptionDetailResult>() {
                    @Override
                    public void success(ServiceOptionDetailResult result) {
                        isGetServiceDetailing = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            serviceOptionDetailBean = result.getResult();
                            showRenewOrderDialog();
                        } else {
                            ToastUtil.getInstance().showShort(OrderDetailServicing.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetServiceDetailing = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void doRenewOrder(final int time, int price) {
        if (isRenewing) {
            showLoadingDialog();
            return;
        }
        isRenewing = true;
        showLoadingDialog();

        RenewParam param = new RenewParam();
        param.setOrderId(orderDetailBean.getId());
        param.setProductId(orderDetailBean.getProductId());
        param.setWaiterId(orderDetailBean.getWaiterId());
        param.setExpire(String.valueOf(time));
        param.setPayPrice(price);
        OrderApi.getInstance().submitOrderRenew(param)
                .compose(RxSchedulers.<Response<BaseBean<RenewResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<RenewResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<RenewResult>>() {
                    @Override
                    public void success(BaseBean<RenewResult> result) {
                        isRenewing = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            RenewResult renewResult = result.getResult();
                            renewResult.setRenewTime(time);
                            OrderRenewPrePay.activityStart(OrderDetailServicing.this, renewResult, orderDetailBean);
                        } else {
                            ToastUtil.getInstance().showLong(OrderDetailServicing.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isRenewing = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_detail_servicing;
    }

    public static void activityStart(Context context, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderDetailServicing.class);
        intent.putExtra(KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    public static void activityStart(Context context, String id) {
        Intent intent = new Intent(context, OrderDetailServicing.class);
        intent.putExtra(KEY_ORDER_ID, id);
        context.startActivity(intent);
    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(KEY_ORDER_DETAIL);
        if (orderDetailBean == null) {
            orderId = getIntent().getStringExtra(KEY_ORDER_ID);
            if (TextUtils.isEmpty(orderId)) {
                finish();
            } else {
                doGetOrderDetail();
            }

        }
    }

    private void doGetOrderDetail() {
        showLoadingDialog();
        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>() {
                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            orderDetailBean = bean;
                            resetView();
                        } else {
                            showRetryDialog(result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        showRetryDialog(errorMsg);
                    }
                });
    }

    private void showRetryDialog(String content) {
        if (TextUtils.isEmpty(content))
            content = "获取异常,重试";
        MaterialDialog dialog = DialogHelper.getMaterialDialogOneQuick(OrderDetailServicing.this, content);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                doGetOrderDetail();
            }
        });
        dialog.show();
    }

    //主页面的定时器发送的刷新倒计时的命令
    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_TIME_TASK_ORDER_REFRESH, mode = ThreadMode.MAIN)
    private void refreshTimeRemain(String defaultContent) {
        LogUtil.error("EventBus refreshTimeRemain  ");
        resetTimeRemain(orderDetailBean.getEndTime());
    }
}
