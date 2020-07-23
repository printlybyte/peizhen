package com.yinfeng.wypzh.ui.order.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.OrderWaitServiceAdapter;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderInfo;
import com.yinfeng.wypzh.bean.order.OrderStartServiceParam;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.order.OrderDetailWaitService;
import com.yinfeng.wypzh.ui.order.PzyDetailActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.utils.UmUtil;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Asen
 */
public class OrderWaitServiceFragment extends BaseOrderChildFragment {

    private OrderWaitServiceAdapter mAdapter;
    private boolean isStartServicing = false;
    private String orderIdStartService;
    private int positionStartService = -1;
    AMapLocationClient mLocationClient;
    AMapLocationListener mLocationListener;
    AMapLocationClientOption mLocationOption;
    double latitude = -1;//获取纬度
    double longitude = -1;//获取经度
    private String address;

    public static OrderWaitServiceFragment newInstance() {
        Bundle args = new Bundle();
        OrderWaitServiceFragment fragment = new OrderWaitServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void assembleData() {
        mList = new ArrayList<>();
        mAdapter = new OrderWaitServiceAdapter(mList);
        initLocationClient();
    }

    @Override
    protected String initOrderState() {
        return Constants.ORDER_STATE_TAKE;
    }

    @Override
    protected String initCommentState() {
        return "";
    }

    @Override
    protected CharSequence initEmptyViewTip() {
        return "暂无订单";
    }

    @Override
    protected void setListener() {
        super.setListener();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.llItem:
                        goToDetail(position);
                        break;
                    case R.id.btConfirmStartService:
                        if (TextUtils.equals(mAdapter.getData().get(position).getArrivedState(), Constants.ORDER_WAIT_SERVICE_ARRIVE))
                            showStartServiceDialog(position, mAdapter.getData().get(position).getId());
                        break;
                    case R.id.ivPhone:
                        String phoneNum = mAdapter.getData().get(position).getWaiterPhone();
                        callPhone(phoneNum);
                        break;
                    case R.id.ivMsg:
                        String account = mAdapter.getData().get(position).getWaiterId();
                        openMsg(account);
//                        requestPermissions(account);
                        break;
                    case R.id.ivHeadIcon:
                        goToPzyInfo(position);
                        break;
                }
            }
        });
    }

    private void initLocationClient() {
        if (mLocationClient == null)
            mLocationClient = new AMapLocationClient(getActivity());
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
//                    hideLoadingDialog();
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            //可在其中解析amapLocation获取相应内容。
                            latitude = aMapLocation.getLatitude();//获取纬度
                            longitude = aMapLocation.getLongitude();//获取经度
                            address = aMapLocation.getAddress();
                            LogUtil.error(" latitude " + latitude + "  longitude:" + longitude);
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


    private void showStartServiceDialog(final int position, final String id) {
        final MaterialDialog dialog = DialogHelper.getMaterialDialog(getActivity(), "确认开始服务?");
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }
                , new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
//                        doStartService(position, id);
                        positionStartService = position;
                        orderIdStartService = id;
                        showLoadingDialog();
                        checkLocationPermission();
                    }
                });
        dialog.show();
    }

    @SuppressLint("CheckResult")
    private void checkLocationPermission() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
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


    private void goToDetail(int position) {
        OrderDetailWaitService.activityStart(getActivity(), mAdapter.getItem(position));
    }

    private void goToPzyInfo(int position) {
        OrderDetailBean bean = mAdapter.getData().get(position);
        WaiterInfo info = ContextUtils.getWaiterInfo(bean);
        if (info != null)
            PzyDetailActivity.activityStart(getActivity(), info);
    }


    private void callPhone(String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum))
            ContextUtils.callPhone(getActivity(), phoneNum);
    }

    private void doStartService() {
        if (isStartServicing) {
            showLoadingDialog();
            return;
        }
        isStartServicing = true;
        showLoadingDialog();
        OrderStartServiceParam param = new OrderStartServiceParam(orderIdStartService);
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
                            UmUtil.removeNotification(getActivity(), UmUtil.getPushNotifId(orderIdStartService));
                            OrderUtil.addOrderServicing(orderIdStartService);
                            RedPointUtil.showOrderDot(2);
//                            RedPointUtil.showBottomDot(1);
                            if (mAdapter.getData().size() > positionStartService && TextUtils.equals(mAdapter.getData().get(positionStartService).getId(), orderIdStartService)) {
                                if (mAdapter.getData().size() == 1) {
                                    setEmptyViewNoData();
                                }
                                mAdapter.remove(positionStartService);
                                positionStartService = -1;
                            }
                        } else {
                            ToastUtil.getInstance().showLong(getActivity(), result.getMessage());
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
//    private void doStartService(int position, final String id) {
//        if (isStartServicing) {
//            showLoadingDialog();
//            return;
//        }
//        isStartServicing = true;
//        orderIdStartService = id;
//        positionStartService = position;
//        showLoadingDialog();
//        OrderStartServiceParam param = new OrderStartServiceParam(id);
//        OrderApi.getInstance().startService(param)
//                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
//                .compose(this.<Response<BaseBean<String>>>bindToLife())
//                .subscribe(new BaseObserver<BaseBean<String>>() {
//                    @Override
//                    public void success(BaseBean<String> result) {
//                        hideLoadingDialog();
//                        isStartServicing = false;
//                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
//                            UmUtil.removeNotification(getActivity(), UmUtil.getPushNotifId(id));
//                            OrderUtil.addOrderServicing(orderIdStartService);
//                            RedPointUtil.showOrderDot(2);
////                            RedPointUtil.showBottomDot(1);
//                            if (mAdapter.getData().size() > positionStartService && TextUtils.equals(mAdapter.getData().get(positionStartService).getId(), orderIdStartService)) {
//                                if (mAdapter.getData().size() == 1) {
//                                    setEmptyViewNoData();
//                                }
//                                mAdapter.remove(positionStartService);
//                                positionStartService = -1;
//                            }
//                        } else {
//                            ToastUtil.getInstance().showLong(getActivity(), result.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void fail(int httpCode, int errCode, String errorMsg) {
//                        hideLoadingDialog();
//                        isStartServicing = false;
//                        checkNetValidAndToast(httpCode, errCode, errorMsg);
//                    }
//                });
//    }


    //刷新增加 有人接单
    //刷新删除 take 下取消订单 or take->service 双方到达 开始服务
    @Subscriber(tag = Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_SERVICE, mode = ThreadMode.MAIN)
    private void refreshOrderListWaitService(String orderId) {
        LogUtil.error("EventBus refreshOrderListWaitService orderId :" + orderId);
        if (hasInit)
            doRefresh();
    }

    //主页面的定时器发送的刷新倒计时的命令
    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_TIME_TASK_ORDER_REFRESH, mode = ThreadMode.MAIN)
    private void refreshTimeRemain(String defaultContent) {
        LogUtil.error("EventBus refreshTimeRemain  ");
        if (hasInit)
            mAdapter.notifyDataSetChanged();
    }
}
