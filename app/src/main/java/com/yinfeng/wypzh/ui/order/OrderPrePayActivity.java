package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.CancelOrderParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.beecloud.BCPay;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.beecloud.entity.BCPayResult;
import cn.beecloud.entity.BCReqParams;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_DETAIL;
import static com.yinfeng.wypzh.http.common.ApiContents.CODE_DENY_CANCLE_REPEAT;

public class OrderPrePayActivity extends BaseActivity {
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView tvOrderId, tvOrderTime;
    private TextView tvServiceTime, tvServiceAddress, tvServiceAsk;
    private TextView tvServicePrice, tvcoupon, tvFinalPrice;
    private RelativeLayout rlzfb, rlwx;
    private ImageView ivzfbselect, ivwxselect;
    private Button btConfirmPay;

    private String orderId, orderName, orderTime;
    private String serviceTime, serviceAddress, serviceAsk;
    private String priceRaw, coupon, priceFinal;
    private boolean isZFB = true;
    OrderDetailBean orderDetailBean;
    private boolean isCanceling = false;
    private boolean isPaying = false;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("订单支付");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
//                cancelToPre();
                doCancelOrder();
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        tvOrderId = mRootView.findViewById(R.id.tvOrderId);
        tvOrderTime = mRootView.findViewById(R.id.tvOrderTime);
        tvServiceTime = mRootView.findViewById(R.id.tvServiceTime);
        tvServiceAddress = mRootView.findViewById(R.id.tvServiceAddress);
        tvServiceAsk = mRootView.findViewById(R.id.tvServiceAsk);
        tvServicePrice = mRootView.findViewById(R.id.tvServicePrice);
        tvcoupon = mRootView.findViewById(R.id.tvcoupon);
        tvFinalPrice = mRootView.findViewById(R.id.tvFinalPrice);
        rlzfb = mRootView.findViewById(R.id.rlzfb);
        rlwx = mRootView.findViewById(R.id.rlwx);
        ivzfbselect = mRootView.findViewById(R.id.ivzfbselect);
        ivwxselect = mRootView.findViewById(R.id.ivwxselect);
        btConfirmPay = mRootView.findViewById(R.id.btConfirmPay);

        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);

        resetPayType(isZFB);
        if (ContextUtils.isWeixinAvilible(this))
            initWxPay();
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
    }

    private void initWxPay() {
        String initInfo = BCPay.initWechatPay(this, Constants.BEECLOUD_WX_APPID);
        if (initInfo != null) {
            ToastUtil.getInstance().showShort(this, "微信初始化失败：" + initInfo);
        }
    }

    private void cancelToPre() {
        final MaterialDialog cancelDialog = DialogHelper.getMaterialDialog(this, "您确定要取消订单吗？");
        cancelDialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        cancelDialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        cancelDialog.dismiss();
                        doCancelOrder();
                    }
                }
        );
        cancelDialog.show();

    }

    @SuppressLint("CheckResult")
    private void doCancelOrder() {
        if (isCanceling) {
//            showLoadingDialog("取消订单...");
            return;
        }
        isCanceling = true;
//        showLoadingDialog("取消订单...");
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .concatMap(new Function<Long, Observable<Response<BaseBean<String>>>>() {
                    @Override
                    public Observable<Response<BaseBean<String>>> apply(Long aLong) throws Exception {
                        return OrderApi.getInstance().cancelOrder(getCancelOrderParam());
                    }
                })
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(OrderPrePayActivity.this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        isCanceling = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS || result.getCode() == CODE_DENY_CANCLE_REPEAT) {
//                            ToastUtil.getInstance().showShort(OrderPrePayActivity.this, "取消订单成功！");
//                            finish();
                        } else {
//                            ToastUtil.getInstance().showShort(OrderPrePayActivity.this, result.getMessage());
                        }
                        finish();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isCanceling = false;
                        hideLoadingDialog();
//                        checkNetValidAndToast();
                        finish();
                    }
                });
    }

    private CancelOrderParam getCancelOrderParam() {
        CancelOrderParam param = new CancelOrderParam();
        param.setId(orderId);
        param.setCancelType(ApiContents.USER_TYPE);
        param.setCancelReason("未支付");
        return param;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btConfirmPay).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
//                        Intent intent = new Intent(OrderPrePayActivity.this, OrderPaySuccessActivity.class);
//                        startActivity(intent);
//                        finish();
                        checkOrderStateBeforePay();

                    }
                });
        rlzfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isZFB = true;
                resetPayType(isZFB);
            }
        });
        rlwx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isZFB = false;
                resetPayType(isZFB);
            }
        });
    }

    private void checkOrderStateBeforePay() {
        if (TextUtils.isEmpty(orderId))
            return;
        if (isPaying) {
            showLoadingDialog("支付中...");
            return;
        }
        isPaying = true;
        showLoadingDialog("支付中...");
        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>() {
                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            if (bean != null && TextUtils.equals(bean.getState(), Constants.ORDER_STATE_SUBMIT)) {
                                doPay();
                            } else {
                                isPaying = false;
                                hideLoadingDialog();
                                showOrderAutoCancelDialog(bean.getState());
                            }
                        } else {
                            isPaying = false;
                            hideLoadingDialog();
                            ToastUtil.getInstance().showShort(OrderPrePayActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isPaying = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });

    }

    /**
     * 发起第三方支付
     */
    private void doPay() {
        if (isZFB) {
            if (ContextUtils.isZfbAvilable(this)) {
                doPayZFB();
            } else {
                isPaying = false;
                hideLoadingDialog();
                ToastUtil.getInstance().showShort(this, "支付宝未安装");
            }
        } else {
            if (ContextUtils.isWeixinAvilible(this)) {
                doPayWX();
            } else {
                isPaying = false;
                hideLoadingDialog();
                ToastUtil.getInstance().showShort(this, "微信未安装或者微信版本过低");
            }
        }
    }

    private void showOrderAutoCancelDialog(String orderState) {
        String orderStateTip = ContextUtils.getOrderStateTip(orderState);
        MaterialDialog orderStateDialog = DialogHelper.getMaterialDialogOneQuick(this, orderStateTip);
        orderStateDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                finish();
            }
        });
        orderStateDialog.show();
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
        priceRaw = ContextUtils.getPriceStrConvertFenToYuan(orderDetailBean.getServicePrice());
        priceFinal = ContextUtils.getPriceStrConvertFenToYuan(orderDetailBean.getPayPrice());
        coupon = getCouponStr(orderDetailBean.getCouponId());

        tvOrderId.setText(orderName);
        tvOrderTime.setText(orderTime);
        tvServiceTime.setText(serviceTime);
        tvServiceAddress.setText(serviceAddress);
        tvServiceAsk.setText(serviceAsk);
        tvServicePrice.setText(priceRaw);
        tvcoupon.setText(coupon);
        tvFinalPrice.setText(priceFinal);

    }

    private String getCouponStr(String couponId) {
        if (TextUtils.isEmpty(couponId))
            return "没有使用";
        return "已使用免单券";
    }

    private String getServiceTime(String startTime, String endTime) {
        String startYear = startTime.substring(0, 5);
        String endYear = endTime.substring(0, 5);
        if (TextUtils.equals(startYear, endYear)) {
            String temp = endTime.substring(5, endTime.length());
            return startTime + "——" + temp;
        } else {
            return startTime + "——" + endTime;
        }
    }

    private void resetPayType(boolean isZFB) {
        if (isZFB) {
            ivzfbselect.setImageResource(R.drawable.selected_oval);
            ivwxselect.setImageResource(R.drawable.unselect_oval);
        } else {
            ivzfbselect.setImageResource(R.drawable.unselect_oval);
            ivwxselect.setImageResource(R.drawable.selected_oval);
        }
    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(KEY_ORDER_DETAIL);
        if (orderDetailBean == null)
            finish();
    }

    public static void activityStart(Context context, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderPrePayActivity.class);
        intent.putExtra(KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressedSupport() {
//        cancelToPre();
        doCancelOrder();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_pre_pay;
    }


    BCCallback bcCallback = new BCCallback() {
        @Override
        public void done(BCResult bcResult) {
            isPaying = false;
            hideLoadingDialog();
            final BCPayResult bcPayResult = (BCPayResult) bcResult;
            //根据你自己的需求处理支付结果
            String result = bcPayResult.getResult();
            /*
              注意！
              所有支付渠道建议以服务端的状态金额为准，此处返回的RESULT_SUCCESS仅仅代表手机端支付成功
            */
            String toastMsg = "";
            if (result.equals(BCPayResult.RESULT_SUCCESS)) {
//                toastMsg = "用户支付成功";
//                checkPaySuccessState();
                goToNextPageSuccess();
                return;
            } else {
                isPaying = false;
                hideLoadingDialog();
                if (result.equals(BCPayResult.RESULT_CANCEL)) {
                    toastMsg = "用户取消支付";
                } else if (result.equals(BCPayResult.RESULT_FAIL)) {
                    toastMsg = "支付失败, 原因: " + bcPayResult.getErrCode() +
                            " # " + bcPayResult.getErrMsg() +
                            " # " + bcPayResult.getDetailInfo();
                } else if (result.equals(BCPayResult.RESULT_UNKNOWN)) {
                    //可能出现在支付宝8000返回状态
                    toastMsg = "订单状态未知";
                } else {
                    toastMsg = "invalid return";
                }
            }
            LogUtil.error("toastMsg : " + toastMsg);
            ToastUtil.getInstance().showLong(OrderPrePayActivity.this, toastMsg);
        }
    };

    private void checkPaySuccessState() {
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .concatMap(new Function<Long, Observable<Response<BaseBean<OrderDetailBean>>>>() {
                    @Override
                    public Observable<Response<BaseBean<OrderDetailBean>>> apply(Long aLong) throws Exception {
                        return OrderApi.getInstance().getOrderDetail(orderId);
                    }
                })
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(OrderPrePayActivity.this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>() {
                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                        } else {
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {

                    }
                });
    }

    private void goToNextPageSuccess() {
        OrderPaySuccessActivity.activityStart(OrderPrePayActivity.this, orderDetailBean);
        finish();
    }

    private void doPayZFB() {
        Map<String, String> mapOptional = new HashMap<String, String>();
        mapOptional.put("type", Constants.BEECLOUD_PAY_ORDER_COMMON);
        BCPay.PayParams aliParam = new BCPay.PayParams();
        aliParam.channelType = BCReqParams.BCChannelTypes.ALI_APP;
        aliParam.billTitle = "陪诊服务";
        aliParam.billTotalFee = orderDetailBean.getPayPrice();
        LogUtil.error("支付宝 要支付的钱是 " + orderDetailBean.getPayPrice());
        aliParam.billNum = orderId;
        aliParam.optional = mapOptional;
        BCPay.getInstance(this).reqPaymentAsync(
                aliParam, bcCallback);
    }


    private void doPayWX() {
        if (BCPay.isWXAppInstalledAndSupported() &&
                BCPay.isWXPaySupported()) {
            Map<String, String> mapOptional = new HashMap<String, String>();
            mapOptional.put("type", Constants.BEECLOUD_PAY_ORDER_COMMON);
            BCPay.PayParams payParams = new BCPay.PayParams();
            payParams.channelType = BCReqParams.BCChannelTypes.WX_APP;
            payParams.billTitle = "陪诊服务";   //订单标题
            payParams.billTotalFee = orderDetailBean.getPayPrice();    //订单金额(分)
            LogUtil.error("微信 要支付的钱是 " + orderDetailBean.getPayPrice());
            payParams.billNum = orderId;  //订单流水号
//                            payParams.couponId = "bbbf835d-f6b0-484f-bb6e-8e6082d4a35f";    // 优惠券ID
            payParams.optional = mapOptional;            //扩展参数(可以null)
            BCPay.getInstance(this).reqPaymentAsync(
                    payParams,
                    bcCallback);            //支付完成后回调入口

        } else {
            hideLoadingDialog();
            ToastUtil.getInstance().showShort(this, "您尚未安装微信或者安装的微信版本不支持");
        }
    }
}
