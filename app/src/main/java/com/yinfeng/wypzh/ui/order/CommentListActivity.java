package com.yinfeng.wypzh.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.CommentAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderListParam;
import com.yinfeng.wypzh.bean.order.OrderListResult;
import com.yinfeng.wypzh.bean.waiter.CommentBean;
import com.yinfeng.wypzh.bean.waiter.CommentListResult;
import com.yinfeng.wypzh.bean.waiter.CommentParam;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.PatientApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.CustomLoadMoreView;
import com.yinfeng.wypzh.widget.RecycleViewDivider;
import com.yinfeng.wypzh.widget.TopBar;

import java.io.Serializable;
import java.util.List;

import retrofit2.Response;

public class CommentListActivity extends BaseActivity {
    public static final String KEY_LIST_COMMENT = "key_comment_list";

    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    List<CommentBean> mList;
    WaiterInfo info;
    private CommentAdapter mAdapter;
    private boolean isGetDataing = false;
    private int pageNumb = 1;


    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("评论列表");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        mSmartRefreshLayout.setEnableLoadMore(false);

        mAdapter = new CommentAdapter(mList);
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        //预加载 无感滑动 倒数第N个开始预加载
        mAdapter.setPreLoadNumber(4);

        RecycleViewDivider mDivider = new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, ContextUtils.dip2px(this, 1), this.getResources().getColor(R.color.ceeeeee));
        LinearLayoutManager mLinearyLayoutManager = new LinearLayoutManager(this);
        mLinearyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearyLayoutManager);
        mRecyclerView.addItemDecoration(mDivider);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        pageNumb = 2;
    }

    @Override
    protected void setListener() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                doRefresh();
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                doLoadMore();
            }
        }, mRecyclerView);
    }

    private void doLoadMore() {
        if (isGetDataing)
            return;
        isGetDataing = true;
        CommentParam param = new CommentParam();
        param.setWaiterId(info.getAccountId());
        PatientApi.getInstance().getCommentList(pageNumb, 10, param)
                .compose(RxSchedulers.<Response<BaseBean<CommentListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<CommentListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<CommentListResult>>() {
                    @Override
                    public void success(BaseBean<CommentListResult> result) {
                        isGetDataing = false;
                        mAdapter.loadMoreComplete();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            CommentListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<CommentBean> list = listResult.getList();
                                if (list.size() == 0) {
                                    mAdapter.loadMoreEnd();
                                } else {
                                    mAdapter.addData(list);
                                    pageNumb++;
                                }
                                mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
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

    private void doRefresh() {
        if (isGetDataing)
            return;
        isGetDataing = true;
        CommentParam param = new CommentParam();
        param.setWaiterId(info.getAccountId());
        PatientApi.getInstance().getCommentList(1, 10, param)
                .compose(RxSchedulers.<Response<BaseBean<CommentListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<CommentListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<CommentListResult>>() {
                    @Override
                    public void success(BaseBean<CommentListResult> result) {
                        isGetDataing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            CommentListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<CommentBean> list = listResult.getList();
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

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_comment_list;
    }

    //    protected void setEmptyViewLoading() {
//        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_loading, null);
//        mAdapter.setEmptyView(emptyView);
//    }
//
    protected void setEmptyViewRetry() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_retry, null);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        mAdapter.setEmptyView(emptyView);
    }

    private void retry() {
        doRefresh();
    }

    protected void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("暂无评论");
        mAdapter.setEmptyView(emptyView);
    }

    private void getIntentData() {
        mList = (List<CommentBean>) getIntent().getSerializableExtra(KEY_LIST_COMMENT);
        info = (WaiterInfo) getIntent().getSerializableExtra(Constants.KEY_WAITERINFO_DETAIL);
        if (info == null || mList == null || mList.size() == 0)
            finish();
    }

    public static void activityStart(Context context, WaiterInfo info, List<CommentBean> list) {
        Intent intent = new Intent(context, CommentListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_WAITERINFO_DETAIL, info);
        bundle.putSerializable(KEY_LIST_COMMENT, (Serializable) list);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
