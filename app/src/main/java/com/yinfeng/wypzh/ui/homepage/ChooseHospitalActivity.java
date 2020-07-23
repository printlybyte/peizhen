package com.yinfeng.wypzh.ui.homepage;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.HospitalAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.patient.HospitalBean;
import com.yinfeng.wypzh.bean.patient.HospitalListData;
import com.yinfeng.wypzh.http.PatientApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.CustomLoadMoreView;
import com.yinfeng.wypzh.widget.RecycleViewDivider;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class ChooseHospitalActivity extends BaseActivity {
    public static final String KEY_HOSPITAL_BEAN = "key_hospitalbean";

    private TopBar mTopBar;
    private ImageView ivSearch;
    private EditText etSearch;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<HospitalBean> mList;
    private List<HospitalBean> mSearchList;
    private HospitalAdapter mAdapter;
    private Disposable mDispose;
    private int start = 0;
    private String searchProgressTxt = "搜索中,请稍后";
    private int pageNum = 1;
    private int pageSize = 10;
    private int pageNumSearch = 1;
    private int pageSizeSearch = 10;
    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private boolean isSearching = false;
    private boolean isSearchState = false;
    private String searchTxt = "";
    private int preNotedPosition = 0;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("选择医院");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                topBackClick();
            }
        });

        ivSearch = mRootView.findViewById(R.id.ivSearch);
        etSearch = mRootView.findViewById(R.id.etSearch);

        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        mSmartRefreshLayout.setRefreshHeader(new MaterialHeader(this));
        mSmartRefreshLayout.setEnableLoadMore(false);

    }


    @Override
    protected void setListener() {

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isSearchState) {
                    doSearch();
                } else {
                    doRefresh();
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (isSearchState) {
                    doSearchLoadMore();
                } else {
                    doLoadMore();
                }
            }
        }, mRecyclerView);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isSearchState) {
                    if (mDispose != null)
                        mDispose.dispose();
                }
                confirmComplete(mAdapter.getData().get(position));
            }
        });
//        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshLayout) {
//                doRefresh();
//            }
//        });
//        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshLayout) {
//                doLoadMore();
//            }
//
//
//        });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteNormalListState();
                doSearch();
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ContextUtils.hideSoftInput(ChooseHospitalActivity.this);
                    noteNormalListState();
                    doSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void noteNormalListState() {
        if (mList != null && mList.size() > 0) {
            preNotedPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        }
    }

    private void doSearch() {
        if (isRefreshing)
            return;
        if (isSearching) {
            showLoadingDialog(searchProgressTxt);
            return;
        }
        String searchContent = etSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(searchContent)) {
            searchTxt = searchContent;
            isSearching = true;
            isSearchState = true;
            mTopBar.setTopLeftTxtWithArrow("返回列表");
            showLoadingDialog(searchProgressTxt);
            PatientApi.getInstance().searchHospitalByName(1, pageSizeSearch, searchContent)
                    .compose(RxSchedulers.<Response<BaseBean<HospitalListData>>>applySchedulers())
                    .compose(this.<Response<BaseBean<HospitalListData>>>bindToLife())
                    .subscribe(new BaseObserver<BaseBean<HospitalListData>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            super.onSubscribe(d);
                            mDispose = d;
                        }

                        @Override
                        public void success(BaseBean<HospitalListData> result) {
//                            isSearching = false;
////                            hideLoadingDialog();
////                            if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
////                                HospitalListData listData = result.getResult();
////                                if (listData != null) {
////                                    mAdapter.setNewData(listData.getList());
////                                    mSearchList = mAdapter.getData();
////                                    if (mSearchList == null || mSearchList.size() == 0) {
////                                        setEmptyViewNoDataSearch();
////                                    }
////                                }
////                                return;
////                            }
////                            if (mSearchList == null || mSearchList.size() == 0) {
////                                ToastUtil.getInstance().showShort(ChooseHospitalActivity.this, result.getMessage());
////                                setEmptyViewRetrySearch();
////                            }
                            isSearching = false;
                            hideLoadingDialog();
                            mSmartRefreshLayout.finishRefresh();
                            if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                                HospitalListData listData = result.getResult();
                                if (listData != null) {
                                    List<HospitalBean> list = listData.getList();
                                    if (list.size() == 0) {
                                        setEmptyViewNoDataSearch();
                                    } else {
                                        pageNumSearch = 2;
                                    }
                                    mAdapter.setNewData(list);
                                    mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
                                    mSearchList = mAdapter.getData();
                                    return;
                                }
                            }
                            ToastUtil.getInstance().showShort(ChooseHospitalActivity.this, result.getMessage());
                            setEmptyViewRetrySearch();
                        }

                        @Override
                        public void fail(int httpCode, int errCode, String errorMsg) {
                            isSearching = false;
                            hideLoadingDialog();
                            mSmartRefreshLayout.finishRefresh();
                            if (mSearchList == null || mSearchList.size() == 0) {
                                ToastUtil.getInstance().showShort(ChooseHospitalActivity.this, "搜索失败，请重试！");
                                setEmptyViewRetrySearch();
                            }
                        }

                    });

        } else {
            ToastUtil.getInstance().showShort(this, "请输入要检索的医院名称");
        }
    }


    private synchronized void doRefresh() {
        if (isRefreshing)
            return;
        isRefreshing = true;
        PatientApi.getInstance().getHospitalList(1, pageSize)
                .compose(RxSchedulers.<Response<BaseBean<HospitalListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<HospitalListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<HospitalListData>>() {
                    @Override
                    public void success(BaseBean<HospitalListData> result) {
                        isRefreshing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            HospitalListData listData = result.getResult();
                            if (listData != null) {
                                List<HospitalBean> list = listData.getList();
                                if (list.size() == 0) {
                                    setEmptyViewNoData();
                                } else {
                                    pageNum = 2;
                                }
                                mAdapter.setNewData(list);
                                mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
                                mList = mAdapter.getData();
                                return;
                            }
                        }
                        ToastUtil.getInstance().showShort(ChooseHospitalActivity.this, result.getMessage());
                        setEmptyViewRetry();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isRefreshing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (mList == null || mList.size() == 0) {
//                            ToastUtil.getInstance().showShort(ChooseHospitalActivity.this, "获取失败");
                            setEmptyViewRetry();
                        }
                    }
                });

    }

    private void doSearchLoadMore() {
        if (isRefreshing)
            return;
        if (isLoading)
            return;
        isLoading = true;
        PatientApi.getInstance().searchHospitalByName(pageNumSearch, pageSizeSearch, searchTxt)
                .compose(RxSchedulers.<Response<BaseBean<HospitalListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<HospitalListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<HospitalListData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDispose = d;
                    }

                    @Override
                    public void success(BaseBean<HospitalListData> result) {
                        isLoading = false;
                        mAdapter.loadMoreComplete();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {

                            HospitalListData listData = result.getResult();
                            if (listData != null) {
                                List<HospitalBean> list = listData.getList();
                                if (list.size() == 0) {
                                    mAdapter.loadMoreEnd();
                                } else {
                                    mAdapter.addData(list);
                                    pageNumSearch++;
                                }
                                mSearchList = mAdapter.getData();
                            }
                        } else {
                            mAdapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isLoading = false;
                        mAdapter.loadMoreComplete();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        mAdapter.loadMoreFail();
                    }

                });
    }

    private void doLoadMore() {
        if (isRefreshing)
            return;
        if (isLoading)
            return;
        isLoading = true;
        PatientApi.getInstance().getHospitalList(pageNum, pageSize)
                .compose(RxSchedulers.<Response<BaseBean<HospitalListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<HospitalListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<HospitalListData>>() {
                    @Override
                    public void success(BaseBean<HospitalListData> result) {
                        isLoading = false;
                        mAdapter.loadMoreComplete();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {

                            HospitalListData listData = result.getResult();
                            if (listData != null) {
                                List<HospitalBean> list = listData.getList();
                                if (list.size() == 0) {
                                    mAdapter.loadMoreEnd();
                                } else {
                                    mAdapter.addData(list);
                                    pageNum++;
                                }
                                mList = mAdapter.getData();
                            }
                        } else {
                            mAdapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isLoading = false;
                        mAdapter.loadMoreComplete();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        mAdapter.loadMoreFail();
                    }
                });

    }

    private void confirmComplete(HospitalBean hospitalBean) {
        Intent intent = new Intent();
        intent.putExtra(KEY_HOSPITAL_BEAN, hospitalBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void initData() {
        mList = new ArrayList<>();
        mAdapter = new HospitalAdapter(mList);
        initRecyclerView();
        mSmartRefreshLayout.autoRefresh();
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

    protected void addLoadAnimition() {
        mAdapter.openLoadAnimation(new BaseAnimation() {

            @Override
            public Animator[] getAnimators(View view) {
                return new Animator[]{
                        ObjectAnimator.ofFloat(view, "alpha", 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f),
                        ObjectAnimator.ofFloat(view, "scaleX", 0.94f, 0.96f, 0.97f, 0.98f, 0.99F, 1.0f)};

            }

        });
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_choose_hospital;
    }

    public static void activityStartForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ChooseHospitalActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private void topBackClick() {
        if (isSearchState) {
            if (mDispose != null)
                mDispose.dispose();
            hideLoadingDialog();
            mTopBar.hideLeftTxtWithArrow();
            isSearchState = false;
            mAdapter.setNewData(mList);
            mRecyclerView.scrollToPosition(preNotedPosition);
        } else {
            cancelComplete();
        }
    }

    private void cancelComplete() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressedSupport() {
        topBackClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDispose != null)
            mDispose.dispose();
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
        if (isSearchState) {
            doSearch();
        } else {
            setEmptyViewLoading();
            mSmartRefreshLayout.autoRefresh();
        }
    }

    protected void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("暂无医院数据！");
        mAdapter.setEmptyView(emptyView);
    }

    protected void setEmptyViewNoDataSearch() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        String tip = "没有查询到" + searchTxt + "相关结果！";
        String target = searchTxt;
        int index = tip.indexOf(target);
        SpannableString ss = new SpannableString(tip);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.c06b49b));
        ss.setSpan(colorSpan, index, index + target.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvNoDataTip.setText(ss);
        mAdapter.setEmptyView(emptyView);
    }

    protected void setEmptyViewRetrySearch() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_retry, null);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        TextView tvRetry = emptyView.findViewById(R.id.tvRetry);
        tvRetry.setText("搜索失败，请重试！");
        mAdapter.setEmptyView(emptyView);
    }
}
