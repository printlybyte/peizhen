package com.yinfeng.wypzh.ui.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.FreeVoucherAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.FreeVoucherBean;
import com.yinfeng.wypzh.bean.order.FreeVoucherListData;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.widget.RecycleViewDivider;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.List;

import retrofit2.Response;

public class FreeVoucherActivity extends BaseActivity {
    public static final String KEY_FREEVOUCHER_BEAN = "key_freevoucher_bean";
    public static final String KEY_FLAG_NEEDCHOOSE = "key_flag_needchoose";
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private FreeVoucherAdapter mAdapter;
    private List<FreeVoucherBean> mList;
    private LinearLayoutManager mManager;
    private boolean isRefreshing = false;
    private FreeVoucherBean choosedInfo;
    private boolean isNeedChoose = false;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("我的优惠券");
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
        initRecyclerview();
        mSmartRefreshLayout.autoRefresh();
    }

    private void initRecyclerview() {
        mAdapter = new FreeVoucherAdapter(mList);
        mManager = new LinearLayoutManager(this);
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecycleViewDivider mDivider = new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, ContextUtils.dip2px(this, 6), this.getResources().getColor(R.color.cf7f8f8));
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.addItemDecoration(mDivider);
        mRecyclerView.setAdapter(mAdapter);

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                doRefresh();
            }
        });
    }

    @Override
    protected void setListener() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.llItem:
                        break;
                    case R.id.tvUse:
                        if (isNeedChoose) {
                            String preChoosedId = mAdapter.getChoosedInfoID();
                            String currentChoosedId = mAdapter.getData().get(position).getId();
                            if (!TextUtils.isEmpty(preChoosedId) && TextUtils.equals(preChoosedId, currentChoosedId)) {
                                confirmToPrePage(null);
                            } else {
                                confirmToPrePage(mList.get(position));
                            }
                        }
                        break;
                }
            }
        });
    }

    private synchronized void doRefresh() {
        if (isRefreshing)
            return;
        isRefreshing = true;
        setEmptyViewLoading();
        OrderApi.getInstance().getFreeVoucherList()
                .compose(RxSchedulers.<Response<BaseBean<FreeVoucherListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<FreeVoucherListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<FreeVoucherListData>>(this) {
                    @Override
                    public void success(BaseBean<FreeVoucherListData> result) {
                        isRefreshing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            FreeVoucherListData listData = result.getResult();
                            if (listData != null) {
                                mList = listData.getList();
                                if (mList == null || mList.size() == 0) {
                                    setEmptyViewNoData();
                                }
                                mAdapter.setNewData(mList);
                            }
                        } else {
                            if (mList == null || mList.size() == 0) {
                                setEmptyViewRetry();
                                mAdapter.setNewData(mList);
                            }
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isRefreshing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (mList == null || mList.size() == 0) {
                            setEmptyViewRetry();
                            mAdapter.setNewData(mList);
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        getIntentData();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_free_voucher;
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
        mSmartRefreshLayout.autoRefresh();
    }


    protected void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("您还没有可用的免单券哦！");
        mAdapter.setEmptyView(emptyView);
    }

    public static void activityStartForResult(Activity activity, FreeVoucherBean bean, int requestCode) {
        Intent intent = new Intent(activity, FreeVoucherActivity.class);
        intent.putExtra(KEY_FREEVOUCHER_BEAN, bean);
        intent.putExtra(KEY_FLAG_NEEDCHOOSE, true);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, FreeVoucherActivity.class);
        context.startActivity(intent);
    }

    private void getIntentData() {
        choosedInfo = (FreeVoucherBean) getIntent().getSerializableExtra(KEY_FREEVOUCHER_BEAN);
        isNeedChoose = getIntent().getBooleanExtra(KEY_FLAG_NEEDCHOOSE, false);
        if (choosedInfo != null && !TextUtils.isEmpty(choosedInfo.getId())) {
            mAdapter.setChoosedInfoId(choosedInfo.getId());
        }
        mAdapter.setIsNeedChoose(isNeedChoose);
    }

    private void confirmToPrePage(FreeVoucherBean bean) {
        Intent intent = new Intent();
        intent.putExtra(KEY_FREEVOUCHER_BEAN, bean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
