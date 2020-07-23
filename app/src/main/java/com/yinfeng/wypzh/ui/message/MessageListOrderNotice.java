package com.yinfeng.wypzh.ui.message;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.message.PushAgent;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.MsgOrderNoticeAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.message.MessageOrderNotice;
import com.yinfeng.wypzh.bean.message.MsgOrderNoticeParam;
import com.yinfeng.wypzh.bean.message.MsgOrderNoticeResult;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.CustomLoadMoreView;
import com.yinfeng.wypzh.widget.RecycleViewDivider;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MessageListOrderNotice extends BaseActivity {
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private MsgOrderNoticeAdapter mAdapter;
    private List<MessageOrderNotice> mList;
    private LinearLayoutManager mLinearLayoutManager;

    protected boolean isGetDataing = false;
    protected int pageNumb = 1;
    protected int pageSize = 10;
    private String accountId;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("订单提醒");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });


        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        mSmartRefreshLayout.setRefreshHeader(new MaterialHeader(this));
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableRefresh(false);

    }

    private boolean checkAccountIdAndUmengState() {
        UserInfo info = SFUtil.getInstance().getUserInfo(this);
        accountId = info.getAccountId();
        String deviceToken = PushAgent.getInstance(this).getRegistrationId();
        if (!TextUtils.isEmpty(deviceToken)) {
            String alias = SFUtil.getInstance().getAliasByDeviceToken(this, deviceToken);
            if (!TextUtils.isEmpty(alias) && TextUtils.equals(accountId, alias))
                return true;
        }
        ToastUtil.getInstance().showShort(this, "状态未知");
        accountId = null;
        setEmptyViewNoData();
        return false;
    }

    @Override
    protected void setListener() {
//        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshLayout) {
//                if (!TextUtils.isEmpty(accountId))
//                    doRefresh();
//            }
//        });
//        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                if (!TextUtils.isEmpty(accountId))
//                    doLoadMore();
//            }
//        }, mRecyclerView);
    }

    @Override
    protected void initData() {
        mAdapter = new MsgOrderNoticeAdapter(mList);
        initRecyclerView();
        setEmptyViewLoading();
//        if (checkAccountIdAndUmengState()) {
////            mSmartRefreshLayout.autoRefresh();
//            if (!TextUtils.isEmpty(accountId))
//                doRefresh();
//        }
        assembleData();

    }

    private void assembleData() {
        List<MessageOrderNotice> newList = SFUtil.getInstance().getMsgNoticeListNew(this);
        List<MessageOrderNotice> oldList = SFUtil.getInstance().getMsgNoticeListOld(this);
        List<MessageOrderNotice> list = new ArrayList<>();
        if (newList != null)
            list.addAll(newList);
        if (oldList != null)
            list.addAll(oldList);
        mAdapter.setNewData(list);
        mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        setEmptyViewNoData();
        if (newList != null){
            SFUtil.getInstance().convertMsgNoticeNewToOld(this);
        }
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecycleViewDivider mDivider = new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, ContextUtils.dip2px(this, 12), this.getResources().getColor(R.color.cf7f8f8));
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(mDivider);
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        //预加载 无感滑动 倒数第N个开始预加载
        mAdapter.setPreLoadNumber(6);
        addLoadAnimition();
//        设置不显示动画数量(设置的这count个item是不执行动画的)
        mAdapter.setNotDoAnimationCount(3);//MD不起效果啊
//        动画默认只执行一次
        mAdapter.isFirstOnly(true);
        mRecyclerView.setAdapter(mAdapter);
        setEmptyViewLoading();
    }

    protected synchronized void doRefresh() {
        if (isGetDataing)
            return;
        isGetDataing = true;
        setEmptyViewLoading();
        MsgOrderNoticeParam param = new MsgOrderNoticeParam();
        param.setAlias(accountId);
        OrderApi.getInstance().getMsgOrderNoticeList(1, pageSize, param)
                .compose(RxSchedulers.<Response<BaseBean<MsgOrderNoticeResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<MsgOrderNoticeResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<MsgOrderNoticeResult>>() {
                    @Override
                    public void success(BaseBean<MsgOrderNoticeResult> result) {
                        isGetDataing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            MsgOrderNoticeResult listResult = result.getResult();
                            if (listResult != null) {
                                List<MessageOrderNotice> list = listResult.getList();
                                if (list.size() == 0) {
                                    setEmptyViewNoData();
                                } else {
                                    pageNumb = 2;
                                }
                                mAdapter.setNewData(list);
                                mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
                                mList = mAdapter.getData();
                                return;
                            }
                        }
                        ToastUtil.getInstance().showShort(MessageListOrderNotice.this, result.getMessage());
                        setEmptyViewRetry();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        mSmartRefreshLayout.finishRefresh();
                        isGetDataing = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        if (mList == null || mList.size() == 0)
                            setEmptyViewRetry();
                    }
                });

    }

    protected void doLoadMore() {
        if (isGetDataing)
            return;
        isGetDataing = true;
        MsgOrderNoticeParam param = new MsgOrderNoticeParam();
        param.setAlias(accountId);
        OrderApi.getInstance().getMsgOrderNoticeList(pageNumb, pageSize, param)
                .compose(RxSchedulers.<Response<BaseBean<MsgOrderNoticeResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<MsgOrderNoticeResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<MsgOrderNoticeResult>>() {
                    @Override
                    public void success(BaseBean<MsgOrderNoticeResult> result) {
                        isGetDataing = false;
                        mAdapter.loadMoreComplete();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            MsgOrderNoticeResult listResult = result.getResult();
                            if (listResult != null) {
                                List<MessageOrderNotice> list = listResult.getList();
                                if (list.size() == 0) {
                                    mAdapter.loadMoreEnd();
                                } else {
                                    mAdapter.addData(list);
                                    pageNumb++;
                                }
                                mList = mAdapter.getData();
                            }
                        } else {
                            mAdapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetDataing = false;
                        mAdapter.loadMoreComplete();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        mAdapter.loadMoreFail();
                    }
                });
    }


    protected void addLoadAnimition() {
//        mAdapter.openLoadAnimation(new BaseAnimation() {
//
//            @Override
//            public Animator[] getAnimators(View view) {
//                return new Animator[]{
//                        ObjectAnimator.ofFloat(view, "alpha", 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f),
//                        ObjectAnimator.ofFloat(view, "scaleX", 0.94f, 0.96f, 0.97f, 0.98f, 0.99F, 1.0f)};
//
//            }
//
//        });
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
    }

    protected void setEmptyViewLoading() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_loading, null);
        mAdapter.setEmptyView(emptyView);
    }

    protected void setEmptyViewRetry() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_retry, null);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        TextView tvRetry = emptyView.findViewById(R.id.tvRetry);
        tvRetry.setText("获取失败，请重试！");
        mAdapter.setEmptyView(emptyView);
    }

    private void retry() {
        setEmptyViewLoading();
        mSmartRefreshLayout.autoRefresh();
    }

    protected void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("没有新的订单提醒！");
        mAdapter.setEmptyView(emptyView);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_message_list_order_notice;
    }
}
