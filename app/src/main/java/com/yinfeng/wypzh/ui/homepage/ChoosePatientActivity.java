package com.yinfeng.wypzh.ui.homepage;

import android.app.Activity;
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
import com.yinfeng.wypzh.adapter.ChoosePatientAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.bean.patient.PatientListData;
import com.yinfeng.wypzh.http.PatientApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.CustomLoadMoreView;
import com.yinfeng.wypzh.widget.RecycleViewDivider;
import com.yinfeng.wypzh.widget.SwipeItemLayout;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.List;

import retrofit2.Response;

public class ChoosePatientActivity extends BaseActivity {
    public static final String KEY_CHOOSED_INFO = "key_choosedinfo";
    private BaseBean<PatientListData> mListResult;
    TopBar mTopBar;
    SmartRefreshLayout mSmartRefreshLayout;
    RecyclerView mRecyclerView;
    List<PatientInfo> mList;
    ChoosePatientAdapter mAdapter;
    LinearLayoutManager mManager;
    private int postionEdit = -1;
    private PatientInfo choosedInfo;
    private int pageNum = 1;
    private int pageSize = 10;
    private boolean isRefreshing = false;
    private boolean isLoading = false;


    public static void activityForResult(Activity activity, PatientInfo info, int requestCode) {
        Intent intent = new Intent(activity, ChoosePatientActivity.class);
        intent.putExtra(KEY_CHOOSED_INFO, info);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("选择就诊人");
        mTopBar.setTopRightTxt("增加");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
//                cancelToPrePage();
                confirmToPrePage();
            }
        });
        mTopBar.setTopBarRightTxtListener(new TopBar.TopBarRightTextCickListener() {
            @Override
            public void topRightTxtClick() {
                AddPatientActivity.activityStartForResult(ChoosePatientActivity.this, null, AddPatientActivity.REQUESTCODE_ADD);
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        mSmartRefreshLayout.setRefreshHeader(new MaterialHeader(this));
        mSmartRefreshLayout.setEnableLoadMore(false);
        initRecyclerview();
    }

    @Override
    protected void setListener() {

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.layout_item:
                        choosedInfo = mList.get(position);
                        confirmToPrePage();
                        break;
                    case R.id.ivEdit:
                        postionEdit = position;
                        AddPatientActivity.activityStartForResult(ChoosePatientActivity.this, mList.get(position), AddPatientActivity.REQUESTCODE_EDIT);
                        break;
                    case R.id.btDelete:
                        doDelete(position);
                        break;
                }
            }
        });
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                setEmptyViewLoading();
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

    private void doDelete(final int position) {
        showLoadingDialog("正在删除中...");
        final String deleteId = mList.get(position).getId();
        PatientApi.getInstance().deletePatient(deleteId)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            setEmptyViewNoData();
                            mAdapter.remove(position);
                            if (choosedInfo != null && TextUtils.equals(deleteId, choosedInfo.getId())) {
                                choosedInfo = null;
                            }
                            ToastUtil.getInstance().showShort(ChoosePatientActivity.this, result.getMessage());
                        } else {
                            ToastUtil.getInstance().showShort(ChoosePatientActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        ToastUtil.getInstance().showShort(ChoosePatientActivity.this, "删除失败");

                    }
                });
    }

    private void doLoadMore() {
        if (isRefreshing)
            return;
        if (isLoading)
            return;
        isLoading = true;
        PatientApi.getInstance().getPatientList(pageNum, pageSize)
                .compose(RxSchedulers.<Response<BaseBean<PatientListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<PatientListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<PatientListData>>() {

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        isLoading = false;
                        if (mListResult != null) {
                            if (mListResult.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                                pageNum++;
                                PatientListData listData = mListResult.getResult();
                                if (listData != null && listData.getList() != null && listData.getList().size() > 0) {
                                    if (listData.getList().size() == pageSize) {
                                        mAdapter.loadMoreComplete();
                                    } else {
                                        mAdapter.loadMoreEnd();
                                    }
                                    mAdapter.addData(listData.getList());
                                    mList = mAdapter.getData();
                                    return;
                                }
                                mAdapter.loadMoreEnd();
                            } else {
                                mAdapter.loadMoreFail();
                            }
                        } else {
                            mAdapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void success(BaseBean<PatientListData> result) {
                        mListResult = result;
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isLoading = false;
                        mAdapter.loadMoreFail();
                    }

                });

    }

    private synchronized void doRefresh() {
        if (isRefreshing)
            return;
        isRefreshing = true;
        PatientApi.getInstance().getPatientList(1, pageSize)
                .compose(RxSchedulers.<Response<BaseBean<PatientListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<PatientListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<PatientListData>>() {

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        isRefreshing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (mListResult != null) {
                            if (mListResult.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                                pageNum = 2;
                                PatientListData listData = mListResult.getResult();
                                if (listData != null && listData.getList() != null && listData.getList().size() > 0) {
                                    mList = listData.getList();
                                    mAdapter.setNewData(mList);
                                    mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
                                    return;
                                }
                                mList = null;
                                setEmptyViewNoData();
                                mAdapter.setNewData(mList);
                            } else {
                                afterRefreshFail();
                            }
                        } else {
                            setEmptyViewRetry();
                        }
                    }

                    @Override
                    public void success(BaseBean<PatientListData> result) {
                        mListResult = result;
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isRefreshing = false;
                        mSmartRefreshLayout.finishRefresh();
                        afterRefreshFail();
                    }

                });
    }

    private void afterRefreshFail() {
        if (mList != null && mList.size() > 0)
            return;
        setEmptyViewRetry();
        mAdapter.setNewData(mList);
    }

    private void confirmToPrePage() {
//        PatientInfo info = mList.get(postion);
        Intent intent = new Intent();
        intent.putExtra(KEY_CHOOSED_INFO, choosedInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

//    private void cancelToPrePage() {
//        setResult(RESULT_CANCELED);
//        finish();
//    }

    @Override
    public void onBackPressedSupport() {
        confirmToPrePage();
//        cancelToPrePage();
    }

    @Override
    protected void initData() {
        getIntentData();
        assembleData();
//        mAdapter.setNewData(mList);
    }

    private void getIntentData() {
        choosedInfo = (PatientInfo) getIntent().getSerializableExtra(KEY_CHOOSED_INFO);
        if (choosedInfo != null && !TextUtils.isEmpty(choosedInfo.getId())) {
            mAdapter.setChoosedInfoId(choosedInfo.getId());
        }
    }

    private void retry() {
        setEmptyViewLoading();
        mSmartRefreshLayout.autoRefresh();
    }

    private void initRecyclerview() {
        mAdapter = new ChoosePatientAdapter(mList);
        setEmptyViewLoading();
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        mManager = new LinearLayoutManager(this);
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecycleViewDivider mDivider = new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, ContextUtils.dip2px(this, 6), this.getResources().getColor(R.color.cf7f8f8));
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.addItemDecoration(mDivider);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));


//
//        //下面是拖拽的代码
//        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
//        itemTouchHelper.attachToRecyclerView(mRecyclerView);
//
//        // 开启拖拽,上面R.id.textView_my_name 是表示你按住这个控件才可以实现拖拽
//        mAdapter.enableDragItem(itemTouchHelper, R.id.layout_item, true);
//        mAdapter.setOnItemDragListener(new OnItemDragListener() {
//            @Override
//            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
//                LogUtil.error("onItemDragStart");
//            }
//
//            @Override
//            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
//                LogUtil.error("onItemDragMoving");
//            }
//
//            @Override
//            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
//                LogUtil.error("onItemDragEnd");
//            }
//        });
//
//        // 开启滑动删除
//        mAdapter.enableSwipeItem();
//        mAdapter.setOnItemSwipeListener(new OnItemSwipeListener() {
//            @Override
//            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
//                LogUtil.error("onItemSwipeStart");
//            }
//
//            @Override
//            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
//                LogUtil.error("clearView");
//            }
//
//            @Override
//            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
//                LogUtil.error("onItemSwiped");
//            }
//
//            @Override
//            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
//                LogUtil.error("onItemSwipeMoving");
//            }
//
//        });

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
        mAdapter.setEmptyView(emptyView);
    }


    protected void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("暂无就诊人数据，请添加！");
        mAdapter.setEmptyView(emptyView);
    }

    private void assembleData() {
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_choose_patient;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mSmartRefreshLayout.autoRefresh();
        }
    }
}
