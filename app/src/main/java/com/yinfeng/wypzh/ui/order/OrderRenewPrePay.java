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

import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.RenewResult;
import com.yinfeng.wypzh.utils.ContextUtils;
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
import io.reactivex.functions.Consumer;

public class OrderRenewPrePay extends BaseActivity {
    public static final String KEY_RENEW_RESULT = "key_renew_result";

    private RenewResult renewResult;
    private OrderDetailBean orderDetailBean;

    private SmartRefreshLayout mSmartRefreshLayout;
    private TopBar mTopBar;
    private TextView tvOrderID, tvRenewTime, tvRenewPrice;
    private RelativeLayout rlzfb, rlwx;
    private ImageView ivzfbselect, ivwxselect;
    private Button btConfirmPay;
    private boolean isZFB = true;
    private boolean isPaying = false;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("支付订单");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });
        tvOrderID = mRootView.findViewById(R.id.tvOrderID);
        tvRenewTime = mRootView.findViewById(R.id.tvRenewTime);
        tvRenewPrice = mRootView.findViewById(R.id.tvRenewPrice);
        rlzfb = mRootView.findViewById(R.id.rlzfb);
        rlwx = mRootView.findViewById(R.id.rlwx);
        ivzfbselect = mRootView.findViewById(R.id.ivzfbselect);
        ivwxselect = mRootView.findViewById(R.id.ivwxselect);
        btConfirmPay = mRootView.findViewById(R.id.btConfirmPay);

        resetPayType(isZFB);
        if (ContextUtils.isWeixinAvilible(this))
            initWxPay();
    }

    private void initWxPay() {
        String initInfo = BCPay.initWechatPay(this, Constants.BEECLOUD_WX_APPID);
        if (initInfo != null) {
            ToastUtil.getInstance().showShort(this, "微信初始化失败：" + initInfo);
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

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btConfirmPay).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        doPay();
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
                if (result.equals(BCPayResult.RESULT_CANCEL)) {
                    if (!isZFB) {
                        finish();
                        return;
                    }
                    toastMsg = "用户取消支付";
                } else if (result.equals(BCPayResult.RESULT_FAIL)) {
                    if (bcPayResult != null && !TextUtils.isEmpty(bcPayResult.getDetailInfo())) {
                        int errCode = bcPayResult.getErrCode();
                        String detailReason = bcPayResult.getDetailInfo();
                        String repeatReason = "商户订单号重复";
                        if (errCode == 7 && detailReason.contains(repeatReason)) {
                            finish();
                            return;
                        }
                        toastMsg = "支付失败, 原因: " + errCode +
                                " # " + bcPayResult.getErrMsg() +
                                " # " + detailReason;
                    }

                } else if (result.equals(BCPayResult.RESULT_UNKNOWN)) {
                    //可能出现在支付宝8000返回状态
                    toastMsg = "订单状态未知";
                } else {
                    toastMsg = "invalid return";
                }
            }
            LogUtil.error("toastMsg : " + toastMsg);
            ToastUtil.getInstance().showLong(OrderRenewPrePay.this, toastMsg);
        }
    };

    /**
     * 发起第三方支付
     */
    private void doPay() {
        if (isPaying) {
            showLoadingDialog();
            return;
        }
        isPaying = true;
        showLoadingDialog();
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

    private void doPayZFB() {
        Map<String, String> mapOptional = new HashMap<String, String>();
        mapOptional.put("type", Constants.BEECLOUD_PAY_ORDER_RENEW);
        BCPay.PayParams aliParam = new BCPay.PayParams();
        aliParam.channelType = BCReqParams.BCChannelTypes.ALI_APP;
        aliParam.billTitle = "陪诊延时服务";
        aliParam.billTotalFee = renewResult.getRenewPrice();
        LogUtil.error("支付宝 要支付的钱是 " + renewResult.getRenewPrice());
        aliParam.billNum = renewResult.getRenewId();
        aliParam.optional = mapOptional;
        BCPay.getInstance(this).reqPaymentAsync(
                aliParam, bcCallback);
    }


    private void doPayWX() {
        if (BCPay.isWXAppInstalledAndSupported() &&
                BCPay.isWXPaySupported()) {
            Map<String, String> mapOptional = new HashMap<String, String>();
            mapOptional.put("type", Constants.BEECLOUD_PAY_ORDER_RENEW);
            BCPay.PayParams payParams = new BCPay.PayParams();
            payParams.channelType = BCReqParams.BCChannelTypes.WX_APP;
            payParams.billTitle = "陪诊延时服务";   //订单标题
            payParams.billTotalFee = renewResult.getRenewPrice();    //订单金额(分)
            LogUtil.error("微信 要支付的钱是 " + renewResult.getRenewPrice());
            payParams.billNum = renewResult.getRenewId();  //订单流水号
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

    private void goToNextPageSuccess() {
        OrderRenewPaySuccess.activityStart(this, renewResult, orderDetailBean);
        finish();
    }

    @Override
    protected void initData() {
        if (renewResult != null) {
//            String orderId = renewResult.getRenewId();
            String orderId = orderDetailBean.getCode();
            int price = renewResult.getRenewPrice();
            String showPrice = ContextUtils.getPriceStrConvertFenToYuan(price);
            int time = renewResult.getRenewTime();
            String showTime = time + "分钟";

            tvOrderID.setText(orderId);
            tvRenewPrice.setText(showPrice);
            tvRenewTime.setText(showTime);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_renew_pre_pay;
    }

    public static void activityStart(Context context, RenewResult renewResult, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderRenewPrePay.class);
        intent.putExtra(KEY_RENEW_RESULT, renewResult);
        intent.putExtra(Constants.KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    private void getIntentData() {
        renewResult = (RenewResult) getIntent().getSerializableExtra(KEY_RENEW_RESULT);
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(Constants.KEY_ORDER_DETAIL);
        if (renewResult == null || orderDetailBean == null)
            finish();
    }
}
