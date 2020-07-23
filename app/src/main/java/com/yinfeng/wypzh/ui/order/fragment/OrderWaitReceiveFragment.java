package com.yinfeng.wypzh.ui.order.fragment;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.OrderWaitReceiveAdapter;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.order.CancelOrderParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.order.OrderWaitReceiveActivity;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

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
public class OrderWaitReceiveFragment extends BaseOrderChildFragment {

    private OrderWaitReceiveAdapter mAdapter;
    private int cancelingOrderPostion = -1;//正在取消订单的集合位置
    private boolean isCancelOrdering = false;

    public static OrderWaitReceiveFragment newInstance() {
        Bundle args = new Bundle();
        OrderWaitReceiveFragment fragment = new OrderWaitReceiveFragment();
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
        mAdapter = new OrderWaitReceiveAdapter(mList);
    }

    @Override
    protected String initOrderState() {
        return Constants.ORDER_STATE_PAID;
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
                    case R.id.btCancelOrder:
                        showCancelOrderDialog(position);
                        break;
                    case R.id.llItem:
                        goToDetail(position);
                        break;
                }
            }
        });
    }

    private void goToDetail(int position) {
        OrderWaitReceiveActivity.activityStart(getActivity(), mList.get(position));
    }

    private void showCancelOrderDialog(final int position) {
        cancelingOrderPostion = position;
        final MaterialDialog cancelOrderDialog = DialogHelper.getMaterialDialog(getActivity(), "确定取消订单?");
        cancelOrderDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                cancelOrderDialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                cancelOrderDialog.dismiss();
                doCancelOrder(mList.get(cancelingOrderPostion));
            }
        });
        cancelOrderDialog.show();

    }

    private void doCancelOrder(OrderDetailBean bean) {
        if (isCancelOrdering) {
            showLoadingDialog("取消订单中...");
            return;
        }
        isCancelOrdering = true;
        showLoadingDialog("取消订单中...");
        CancelOrderParam cancelOrderParam = new CancelOrderParam();
        cancelOrderParam.setId(bean.getId());
        cancelOrderParam.setCancelType(ApiContents.USER_TYPE);
        cancelOrderParam.setCancelReason("");
        OrderApi.getInstance().cancelOrder(cancelOrderParam)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        isCancelOrdering = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(getActivity(), "订单取消成功");
                            if (cancelingOrderPostion <= mAdapter.getData().size() - 1&&cancelingOrderPostion!=-1) {
                                setEmptyViewNoData();
                                mAdapter.remove(cancelingOrderPostion);
                                mList = mAdapter.getData();
                                cancelingOrderPostion = -1;
                            }
                        } else {
                            ToastUtil.getInstance().showShort(getActivity(), result.getMessage());
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

    //刷新增加 在线预约支付成功or使用优惠券确认下单成功
    //刷新删除 payed下取消订单 or 有人接单
    @Subscriber(tag = Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_RECEIVER, mode = ThreadMode.MAIN)
    private void refreshOrderListWaitReceive(String orderId) {
        LogUtil.error("EventBus refreshOrderListWaitReceive orderId :" + orderId);
        if (hasInit)
            doRefresh();
    }
}
