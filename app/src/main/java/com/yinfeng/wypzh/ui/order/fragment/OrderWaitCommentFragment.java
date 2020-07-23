package com.yinfeng.wypzh.ui.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.OrderWaitCommentAdapter;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderInfo;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.ui.order.OrderCommentActivity;
import com.yinfeng.wypzh.ui.order.OrderDetailServicing;
import com.yinfeng.wypzh.ui.order.PzyDetailActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
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

/**
 * @author Asen
 */
public class OrderWaitCommentFragment extends BaseOrderChildFragment {

    private OrderWaitCommentAdapter mAdapter;

    public static OrderWaitCommentFragment newInstance() {
        Bundle args = new Bundle();
        OrderWaitCommentFragment fragment = new OrderWaitCommentFragment();
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
        mAdapter = new OrderWaitCommentAdapter(mList);
    }

    @Override
    protected String initOrderState() {
        return Constants.ORDER_STATE_COMPLETE;
    }

    @Override
    protected String initCommentState() {
        return ApiContents.ORDER_EVESTATE_WAIT;
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
                    case R.id.btComment:
                        goToDetail(position);
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

    private void goToPzyInfo(int position) {
        OrderDetailBean bean = mAdapter.getData().get(position);
        WaiterInfo info = ContextUtils.getWaiterInfo(bean);
        if (info != null)
            PzyDetailActivity.activityStart(getActivity(), info);
    }

    private void goToDetail(int position) {
        OrderCommentActivity.activityStart(getActivity(), mAdapter.getData().get(position));
    }

    private void callPhone(String phoneNum) {
        ContextUtils.callPhone(getActivity(), phoneNum);
    }

    //刷新增加 陪诊员点击确认，服务截至时间到达主动获取订单详情，状态的确变更comment
    //刷新删除 评论完成后
    @Subscriber(tag = Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_COMMENT, mode = ThreadMode.MAIN)
    private void refreshOrderListWaitComment(String orderId) {
        LogUtil.error("EventBus refreshOrderListWaitComment orderId :" + orderId);
        if (hasInit)
            doRefresh();
    }
}
