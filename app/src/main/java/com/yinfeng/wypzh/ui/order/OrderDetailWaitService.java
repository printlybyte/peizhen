package com.yinfeng.wypzh.ui.order;

import android.Manifest;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.CancelOrderParam;
import com.yinfeng.wypzh.bean.order.OrderCancelReason;
import com.yinfeng.wypzh.bean.order.OrderCancelReasonList;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderStartServiceParam;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.CancelOrderDialog;
import com.yinfeng.wypzh.ui.dialog.CancelOrderDialogNew;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.utils.UmUtil;
import com.yinfeng.wypzh.widget.TopBar;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_DETAIL;

public class OrderDetailWaitService extends BaseActivity {
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView tvRemainTip, tvTimeRemain;
    private ImageView ivHeadIcon, ivPhone, ivMsg;
    private TextView tvName, tvPhone;
    private TextView tvOrderId, tvOrderTime, tvServiceAddress, tvServiceTime, tvServiceAsk, tvFinalPrice;
    private TextView tvCancelOrder, tvConfirmService;
    private OrderDetailBean orderDetailBean;
    private boolean isCancelOrdering = false;
    private boolean isStartServicing = false;
    private boolean isGetReasoning = false;

    AMapLocationClient mLocationClient;
    AMapLocationListener mLocationListener;
    AMapLocationClientOption mLocationOption;
    double latitude = -1;//获取纬度
    double longitude = -1;//获取经度
    private String address;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("接单成功-待服务");
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
                ContextUtils.callComplainPhone(OrderDetailWaitService.this);
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);

        tvRemainTip = mRootView.findViewById(R.id.tvRemainTip);
        tvTimeRemain = mRootView.findViewById(R.id.tvTimeRemain);

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

        tvCancelOrder = mRootView.findViewById(R.id.tvCancelOrder);
        tvConfirmService = mRootView.findViewById(R.id.tvConfirmService);

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

            resetTimeRemain(orderDetailBean.getMakeStartTime());

            String arriveState = orderDetailBean.getArrivedState();
            if (TextUtils.equals(arriveState, Constants.ORDER_WAIT_SERVICE_ARRIVE)) {
                tvConfirmService.setEnabled(true);
                tvConfirmService.setBackgroundColor(ContextCompat.getColor(this, R.color.c06b49b));
//                tvConfirmService.setTextColor(ContextCompat.getColor(this,android.R.color.white));
            } else {
                tvConfirmService.setEnabled(false);
                tvConfirmService.setBackgroundColor(ContextCompat.getColor(this, R.color.ce5e5e5e));
//                tvConfirmService.setTextColor(ContextCompat.getColor(this,android.R.color.white));
            }
        }
    }

    private void initLocationClient() {
        if (mLocationClient == null)
            mLocationClient = new AMapLocationClient(getApplicationContext());
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocationLatest(true);
            mLocationOption.setNeedAddress(true);
            mLocationClient.setLocationOption(mLocationOption);
        }
        if (mLocationListener == null) {
            mLocationListener = new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            //可在其中解析amapLocation获取相应内容。
                            latitude = aMapLocation.getLatitude();//获取纬度
                            longitude = aMapLocation.getLongitude();//获取经度
                            address = aMapLocation.getAddress();
                            LogUtil.error(" latitude " + latitude + "  longitude:" + longitude + " address :" + address);
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            LogUtil.error("AmapError", "location Error, ErrCode:"
                                    + aMapLocation.getErrorCode() + ", errInfo:"
                                    + aMapLocation.getErrorInfo());
                        }
                    }
                    mLocationClient.stopLocation();
                    doStartService();
                }
            };
            mLocationClient.setLocationListener(mLocationListener);
        }
    }

    private void resetTimeRemain(String startTime) {
        String remainTime = ContextUtils.getRemainTimeWaitService(startTime);
        if (TextUtils.isEmpty(remainTime)) {
            tvTimeRemain.setText("");
        } else {
            if (TextUtils.equals(remainTime, "已超时")) {
                tvRemainTip.setText(getResources().getString(R.string.orderwaitservice_time_out));
                tvTimeRemain.setText(remainTime);
                tvTimeRemain.setTextColor(Color.RED);
            } else {
                tvRemainTip.setText(getResources().getString(R.string.orderwaitservice_time_remain));
                tvTimeRemain.setText(remainTime);
                tvTimeRemain.setTextColor(ContextCompat.getColor(this, R.color.c06b49b));
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(tvCancelOrder).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        doGetCancelReason();
//                        showCancelOrderDialog();
                    }
                });
        RxView.clicks(tvConfirmService).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
//                        doStartService();
                        showLoadingDialog();
                        checkLocationPermission();
                    }
                });
        RxView.clicks(ivPhone).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String phoneNum = orderDetailBean.getWaiterPhone();
                        if (!TextUtils.isEmpty(phoneNum))
                            ContextUtils.callPhone(OrderDetailWaitService.this, phoneNum);
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

    @SuppressLint("CheckResult")
    private void checkLocationPermission() {
        RxPermissions rxPermission = new RxPermissions(OrderDetailWaitService.this);
        rxPermission
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            mLocationClient.startLocation();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            doStartService();
                        } else {
                            doStartService();
                        }
                    }
                });
    }

    private void goToPzyInfo() {
        WaiterInfo info = ContextUtils.getWaiterInfo(orderDetailBean);
        if (info != null)
            PzyDetailActivity.activityStart(this, info);
    }

    private void doGetCancelReason() {
        if (isGetReasoning) {
            showLoadingDialog();
            return;
        }
        isGetReasoning = true;
        OrderApi.getInstance().getCancelReasonList()
                .compose(RxSchedulers.<Response<BaseBean<OrderCancelReasonList>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderCancelReasonList>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderCancelReasonList>>() {
                    @Override
                    public void success(BaseBean<OrderCancelReasonList> result) {
                        isGetReasoning = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderCancelReasonList orderCancelReasonList = result.getResult();
                            if (orderCancelReasonList != null) {
                                List<OrderCancelReason> list = orderCancelReasonList.getList();
                                if (list != null && list.size() > 0) {
                                    showCancelOrderDialog(list);
                                }
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetReasoning = false;
                        hideLoadingDialog();
                    }
                });
    }

    private void showCancelOrderDialog(List<OrderCancelReason> list) {

        CancelOrderDialogNew cancelOrderDialog = DialogHelper.getCancelOrderDialogNew(this, list, new CancelOrderDialogNew.OnCancelOrderListener() {
            @Override
            public void confirmCancelOrder(String cancelReason) {
                doCancelOrder(cancelReason);
            }
        });
        cancelOrderDialog.setCancelable(false);
        cancelOrderDialog.setCanceledOnTouchOutside(false);
        cancelOrderDialog.show();
    }
//    private void showCancelOrderDialog() {
//
//        CancelOrderDialog cancelOrderDialog = DialogHelper.getCancelOrderDialog(this, new CancelOrderDialog.OnCancelOrderListener() {
//            @Override
//            public void confirmCancelOrder(String cancelReason) {
//                doCancelOrder(cancelReason);
//            }
//        });
//        cancelOrderDialog.setCancelable(false);
//        cancelOrderDialog.setCanceledOnTouchOutside(false);
//        cancelOrderDialog.show();
//    }

    private void doCancelOrder(String cancelReason) {
        if (isCancelOrdering) {
            showLoadingDialog("取消订单中...");
            return;
        }
        isCancelOrdering = true;
        showLoadingDialog("取消订单中...");
        CancelOrderParam cancelOrderParam = new CancelOrderParam();
        cancelOrderParam.setId(orderDetailBean.getId());
        cancelOrderParam.setCancelType(ApiContents.USER_TYPE);
        cancelOrderParam.setCancelReason(cancelReason);
        OrderApi.getInstance().cancelOrder(cancelOrderParam)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        isCancelOrdering = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(OrderDetailWaitService.this, "订单取消成功");
                            OrderUtil.deleteOrderWaitService(orderDetailBean.getId());
                            finish();
                        } else {
                            ToastUtil.getInstance().showShort(OrderDetailWaitService.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isCancelOrdering = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void doStartService() {
        if (isStartServicing) {
            showLoadingDialog();
            return;
        }
        isStartServicing = true;
//        showLoadingDialog();
        OrderStartServiceParam param = new OrderStartServiceParam(orderDetailBean.getId());
        param.setServiceLat(String.valueOf(latitude));
        param.setServiceLon(String.valueOf(longitude));
        param.setServicePosition(address);
        OrderApi.getInstance().startService(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        hideLoadingDialog();
                        isStartServicing = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UmUtil.removeNotification(OrderDetailWaitService.this, UmUtil.getPushNotifId(orderDetailBean.getId()));
                            OrderUtil.addOrderServicing(orderDetailBean.getId());
                            OrderUtil.deleteOrderWaitService(orderDetailBean.getId());
                            RedPointUtil.showOrderDot(2);
                            RedPointUtil.showBottomDot(1);
                            finish();
                        } else {
                            ToastUtil.getInstance().showLong(OrderDetailWaitService.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isStartServicing = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    @Override
    protected void initData() {
        initLocationClient();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_detail_wait_service;
    }

    public static void activityStart(Context context, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderDetailWaitService.class);
        intent.putExtra(KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(KEY_ORDER_DETAIL);
        if (orderDetailBean == null) {
            finish();
        }
    }

    //主页面的定时器发送的刷新倒计时的命令
    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_TIME_TASK_ORDER_REFRESH, mode = ThreadMode.MAIN)
    private void refreshTimeRemain(String defaultContent) {
        LogUtil.error("EventBus refreshTimeRemain  ");
        resetTimeRemain(orderDetailBean.getMakeStartTime());
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_HAS_ARRIVED, mode = ThreadMode.MAIN)
    private void waiterHasArrived(String orderId) {
        LogUtil.error("EventBus waiterHasArrived  ");
        if (orderDetailBean != null && !TextUtils.isEmpty(orderId) && TextUtils.equals(orderId, orderDetailBean.getId())) {
            tvConfirmService.setEnabled(true);
            tvConfirmService.setBackgroundColor(ContextCompat.getColor(this, R.color.c06b49b));
            showHasArrivedDialog();
        }
    }

    private void showHasArrivedDialog() {
        DialogHelper.getHasArrivedDialog(this).show();
    }
}
