package com.yinfeng.wypzh.ui.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.OrderServicingAdapter;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.OrderConfirmCompleteParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderInfo;
import com.yinfeng.wypzh.bean.order.OrderStartServiceParam;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.order.OrderDetailServicing;
import com.yinfeng.wypzh.ui.order.PzyDetailActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.CustomLoadMoreView;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Asen
 */
public class OrderServicingFragment extends BaseOrderChildFragment {

    private OrderServicingAdapter mAdapter;
    private boolean isConfirmCompleting = false;
    private String orderIdComplete;
    private int positionComplete = -1;

    public static OrderServicingFragment newInstance() {
        Bundle args = new Bundle();
        OrderServicingFragment fragment = new OrderServicingFragment();
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
        mAdapter = new OrderServicingAdapter(mList);
    }

    @Override
    protected String initOrderState() {
        return Constants.ORDER_STATE_SERVICE;
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
                    case R.id.btConfirmComplete:
                        showConfirmCompleteDialog(position, mAdapter.getData().get(position).getId());
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
                    case R.id.llItem:
                        goToDetail(position);
                        break;
                    case R.id.ivHeadIcon:
                        goToPzyInfo(position);
                        break;
                }
            }
        });
    }

    private void showConfirmCompleteDialog(final int position, final String id) {
        final MaterialDialog confirmDialog = DialogHelper.getMaterialDialog(getActivity(), "确认服务已完成?");
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
                        doConfirmComplete(position, id);
                    }
                }
        );
        confirmDialog.show();
    }

    private void doConfirmComplete(int position, String id) {
        if (isConfirmCompleting) {
            showLoadingDialog();
            return;
        }
        isConfirmCompleting = true;
        orderIdComplete = id;
        positionComplete = position;
        showLoadingDialog();
        OrderConfirmCompleteParam param = new OrderConfirmCompleteParam(id);
        OrderApi.getInstance().confirmComplete(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        hideLoadingDialog();
                        isConfirmCompleting = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderUtil.addOrderWaitComment(orderIdComplete);
                            RedPointUtil.showOrderDot(3);
//                            RedPointUtil.showBottomDot(1);
                            if (mAdapter.getData().size() > positionComplete && TextUtils.equals(mAdapter.getData().get(positionComplete).getId(), orderIdComplete)) {
                                if (mAdapter.getData().size() == 1) {
                                    setEmptyViewNoData();
                                }
                                mAdapter.remove(positionComplete);
                                positionComplete = -1;
                            }
                        } else {
                            ToastUtil.getInstance().showLong(getActivity(), result.getMessage());
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

    private void goToPzyInfo(int position) {
        OrderDetailBean bean = mAdapter.getData().get(position);
        WaiterInfo info = ContextUtils.getWaiterInfo(bean);
        if (info != null)
            PzyDetailActivity.activityStart(getActivity(), info);
    }

    private void goToDetail(int position) {
        OrderDetailServicing.activityStart(getActivity(), mAdapter.getData().get(position));
    }

    private void callPhone(String phoneNum) {
        ContextUtils.callPhone(getActivity(), phoneNum);
    }

    //刷新增加 双方到达开始服务 state变更为service
    //刷新删除 service->comment 服务完成
    @Subscriber(tag = Constants.EVENTBUS_TAG_ORDER_REFRESH_SERVICING, mode = ThreadMode.MAIN)
    private void refreshOrderListServicing(String orderId) {
        LogUtil.error("EventBus refreshOrderListServicing orderId :" + orderId);
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

