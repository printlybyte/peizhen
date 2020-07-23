package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.CommentAdapter;
import com.yinfeng.wypzh.adapter.TipAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.TipBaseBean;
import com.yinfeng.wypzh.bean.waiter.CommentBean;
import com.yinfeng.wypzh.bean.waiter.CommentListResult;
import com.yinfeng.wypzh.bean.waiter.CommentParam;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.PatientApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.login.FillInfoActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.widget.RecycleViewDivider;
import com.yinfeng.wypzh.widget.SpaceItemDecoration;
import com.yinfeng.wypzh.widget.TopBar;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class PzyDetailActivity extends BaseActivity {

    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private ImageView ivheader;
    private TextView tvName, tvDesc, tvServiceNumb, tvAge;

    private ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;

    private LinearLayout llServiceName;
    private RecyclerView mRecyclerViewServiceName, mRecyclerViewCertificate, mRecyclerViewConsumerComments;
    private TextView tvExpand;
    private TextView tvConsult;
    private LinearLayout llComments;
    private TipAdapter mAdapterServiceOption, mAdapterCertificate;
    private CommentAdapter mAdapterComment;
    private List<TipBaseBean> mListServiceOptions, mListCertificates;
    private List<CommentBean> mListComments;
    private List<CommentBean> mListCommentsFirstPage;
    private WaiterInfo info;


    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("陪诊员详情");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableRefresh(false);

        ivheader = mRootView.findViewById(R.id.ivheader);
        tvName = mRootView.findViewById(R.id.tvName);
        tvDesc = mRootView.findViewById(R.id.tvDesc);
        tvServiceNumb = mRootView.findViewById(R.id.tvServiceNumb);
        tvAge = mRootView.findViewById(R.id.tvAge);

        ivStar1 = mRootView.findViewById(R.id.ivStar1);
        ivStar2 = mRootView.findViewById(R.id.ivStar2);
        ivStar3 = mRootView.findViewById(R.id.ivStar3);
        ivStar4 = mRootView.findViewById(R.id.ivStar4);
        ivStar5 = mRootView.findViewById(R.id.ivStar5);

        llServiceName = mRootView.findViewById(R.id.llServiceName);
        mRecyclerViewServiceName = mRootView.findViewById(R.id.mRecyclerViewServiceName);
        mRecyclerViewCertificate = mRootView.findViewById(R.id.mRecyclerViewCertificate);
        mRecyclerViewConsumerComments = mRootView.findViewById(R.id.mRecyclerViewConsumerComments);
        llComments = mRootView.findViewById(R.id.llComments);

        tvConsult = mRootView.findViewById(R.id.tvConsult);
        tvExpand = mRootView.findViewById(R.id.tvExpand);
        tvExpand.setVisibility(View.GONE);

    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
//        mAdapterServiceOption.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                boolean isSelected = mListServiceOptions.get(position).isSelected();
//                mListServiceOptions.get(position).setSelected(!isSelected);
//                mAdapterServiceOption.setData(position, mListServiceOptions.get(position));
//            }
//        });
//        mAdapterCertificate.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                boolean isSelected = mListCertificates.get(position).isSelected();
//                mListCertificates.get(position).setSelected(!isSelected);
//                mAdapterCertificate.setData(position, mListCertificates.get(position));
//            }
//        });
        RxView.clicks(tvConsult).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String account = info.getAccountId();
                        if (!TextUtils.isEmpty(account))
                            openMsg(account);
//                            requestIMessagePermissions(account);
                    }
                });
        RxView.clicks(tvExpand).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommentListActivity.activityStart(PzyDetailActivity.this, info, mListCommentsFirstPage);
                    }
                });
    }

    @Override
    protected void initData() {
        initTopWaiterInfo();
        llServiceName.setVisibility(View.GONE);
//        initServiceOptionsView();
        initCertificatesView();
//        resetServiceOptionsViewParams();
        resetCertificateViewParams();
        initCommentView();
//        resetCommentsBeforeExpandViewParams();
        resetCommentsBeforeGetNetData();
        doGetCommentList();
        doGetWaiterInfo();

    }

    private void doGetWaiterInfo() {
        if (info != null) {
            PatientApi.getInstance().getWaiterInfo(info.getAccountId())
                    .compose(RxSchedulers.<Response<BaseBean<WaiterInfo>>>applySchedulers())
                    .compose(this.<Response<BaseBean<WaiterInfo>>>bindToLife())
                    .subscribe(new BaseObserver<BaseBean<WaiterInfo>>() {
                        @Override
                        public void success(BaseBean<WaiterInfo> result) {
                            if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                                WaiterInfo waiterInfo = result.getResult();
                                if (waiterInfo != null) {
                                    info = waiterInfo;
                                    initTopWaiterInfo();
                                }
                            }
                        }

                        @Override
                        public void fail(int httpCode, int errCode, String errorMsg) {

                        }
                    });
        }
    }

    private void initTopWaiterInfo() {
        if (info != null) {
            String profile = info.getProfile();
            String name = info.getRealname();
            String description = info.getServiceDescription();
            if (TextUtils.isEmpty(description))
                description = "个人简介暂无";
            int serviceNumb = info.getServiceSum();
            String age = info.getAge();
            int level = 5;
            try {
                level = Integer.parseInt(info.getLevel());
            } catch (Exception e) {
            }
            initStarLevel(level);
            ImageUtil.getInstance().changCacheKey(this, System.currentTimeMillis());
            ImageUtil.getInstance().loadImgCircle(this, profile, ivheader);
            tvName.setText(name);
            tvDesc.setText(description);
            tvServiceNumb.setText(String.valueOf(serviceNumb));
            tvAge.setText(age + "岁");

        }
    }

    private void initStarLevel(int level) {
        switch (level) {
            case 1:
                ivStar1.setImageResource(R.drawable.star_selected);
                ivStar2.setImageResource(R.drawable.star_unselect);
                ivStar3.setImageResource(R.drawable.star_unselect);
                ivStar4.setImageResource(R.drawable.star_unselect);
                ivStar5.setImageResource(R.drawable.star_unselect);
                break;
            case 2:
                ivStar1.setImageResource(R.drawable.star_selected);
                ivStar2.setImageResource(R.drawable.star_selected);
                ivStar3.setImageResource(R.drawable.star_unselect);
                ivStar4.setImageResource(R.drawable.star_unselect);
                ivStar5.setImageResource(R.drawable.star_unselect);
                break;
            case 3:
                ivStar1.setImageResource(R.drawable.star_selected);
                ivStar2.setImageResource(R.drawable.star_selected);
                ivStar3.setImageResource(R.drawable.star_selected);
                ivStar4.setImageResource(R.drawable.star_unselect);
                ivStar5.setImageResource(R.drawable.star_unselect);
                break;
            case 4:
                ivStar1.setImageResource(R.drawable.star_selected);
                ivStar2.setImageResource(R.drawable.star_selected);
                ivStar3.setImageResource(R.drawable.star_selected);
                ivStar4.setImageResource(R.drawable.star_selected);
                ivStar5.setImageResource(R.drawable.star_unselect);
                break;
            case 5:
                ivStar1.setImageResource(R.drawable.star_selected);
                ivStar2.setImageResource(R.drawable.star_selected);
                ivStar3.setImageResource(R.drawable.star_selected);
                ivStar4.setImageResource(R.drawable.star_selected);
                ivStar5.setImageResource(R.drawable.star_selected);
                break;
        }
    }

    private void initCommentView() {
//        assembleComments(4);
        mAdapterComment = new CommentAdapter(mListComments);
        setEmptyViewLoadingCircle();
        RecycleViewDivider mDivider = new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, ContextUtils.dip2px(this, 1), this.getResources().getColor(R.color.ceeeeee));
        LinearLayoutManager mLinearyLayoutManager = new LinearLayoutManager(this);
        mLinearyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewConsumerComments.setLayoutManager(mLinearyLayoutManager);
        mRecyclerViewConsumerComments.addItemDecoration(mDivider);
        mRecyclerViewConsumerComments.setAdapter(mAdapterComment);
    }

    private void resetServiceOptionsViewParams() {
        if (mListServiceOptions.size() > 0) {
            int space = mAdapterServiceOption.getSpace();
            int hangNum = getRecyclerViewHangNumbServiceOptions();
            float itemHeightDimen = getResources().getDimension(R.dimen.item_sick_height);
            int itemHeight = Float.valueOf(itemHeightDimen).intValue();
            int recyclerviewHeight = (itemHeight + space) * hangNum;
            LinearLayout.LayoutParams recyclerviewParam = (LinearLayout.LayoutParams) mRecyclerViewServiceName.getLayoutParams();
            recyclerviewParam.height = recyclerviewHeight;
            mRecyclerViewServiceName.setLayoutParams(recyclerviewParam);
        }
    }

    private void resetCertificateViewParams() {
        if (mListCertificates.size() > 0) {
            int space = mAdapterCertificate.getSpace();
            int hangNum = getRecyclerViewHangNumbCertificates();
            float itemHeightDimen = getResources().getDimension(R.dimen.item_sick_height);
            int itemHeight = Float.valueOf(itemHeightDimen).intValue();
            int recyclerviewHeight = (itemHeight + space) * hangNum;
            LinearLayout.LayoutParams recyclerviewParam = (LinearLayout.LayoutParams) mRecyclerViewCertificate.getLayoutParams();
            recyclerviewParam.height = recyclerviewHeight;
            mRecyclerViewCertificate.setLayoutParams(recyclerviewParam);
        }
    }

    private void resetCommentsBeforeExpandViewParams() {
        if (mListComments.size() > 0) {
            int space = ContextUtils.dip2px(this, 1);
            int hangNum = mListComments.size();
            float itemHeightDimen = getResources().getDimension(R.dimen.item_pzy_comment_height);
            int itemHeight = Float.valueOf(itemHeightDimen).intValue();
            int recyclerviewHeight = (itemHeight + space) * hangNum - space;
            LinearLayout.LayoutParams recyclerviewParam = (LinearLayout.LayoutParams) mRecyclerViewConsumerComments.getLayoutParams();
            recyclerviewParam.height = recyclerviewHeight;
            recyclerviewParam.gravity = Gravity.LEFT;
            recyclerviewParam.topMargin = ContextUtils.dip2px(this, 6);
            recyclerviewParam.bottomMargin = ContextUtils.dip2px(this, 6);
            recyclerviewParam.leftMargin = 0;
            mRecyclerViewConsumerComments.setLayoutParams(recyclerviewParam);
            mRecyclerViewConsumerComments.setNestedScrollingEnabled(false);
        }
    }

    private void resetCommentsBeforeGetNetData() {
        float heightFloat = getResources().getDimension(R.dimen.dp236);
        int height = Float.valueOf(heightFloat).intValue();
        LinearLayout.LayoutParams recyclerviewParam = (LinearLayout.LayoutParams) mRecyclerViewConsumerComments.getLayoutParams();
        recyclerviewParam.height = height;
//        recyclerviewParam.gravity = Gravity.LEFT;
//        recyclerviewParam.topMargin = ContextUtils.dip2px(this, 6);
//        recyclerviewParam.bottomMargin = ContextUtils.dip2px(this, 6);
//        recyclerviewParam.leftMargin = 0;
        mRecyclerViewConsumerComments.setLayoutParams(recyclerviewParam);
        mRecyclerViewConsumerComments.setNestedScrollingEnabled(false);
    }

    private void resetCommentsExpandViewParams() {
        if (mListComments.size() > 2) {
            int space = ContextUtils.dip2px(this, 1);
            int hangNum = mListComments.size();
            float itemHeightDimen = getResources().getDimension(R.dimen.item_pzy_comment_height);
            int itemHeight = Float.valueOf(itemHeightDimen).intValue();
            int recyclerviewHeight = (itemHeight + space) * hangNum - space;
            LinearLayout.LayoutParams recyclerviewParam = (LinearLayout.LayoutParams) mRecyclerViewConsumerComments.getLayoutParams();
            recyclerviewParam.height = recyclerviewHeight;
            recyclerviewParam.gravity = Gravity.LEFT;
            recyclerviewParam.topMargin = ContextUtils.dip2px(this, 6);
            recyclerviewParam.bottomMargin = ContextUtils.dip2px(this, 6);
            recyclerviewParam.leftMargin = 0;
            mRecyclerViewConsumerComments.setLayoutParams(recyclerviewParam);
            mRecyclerViewConsumerComments.setNestedScrollingEnabled(false);
            LinearLayout.LayoutParams llCommentsParams = (LinearLayout.LayoutParams) llComments.getLayoutParams();
            llCommentsParams.bottomMargin = ContextUtils.dip2px(this, 12);
            llComments.setLayoutParams(llCommentsParams);
        }
    }

    private int getRecyclerViewHangNumbServiceOptions() {
        int spanNum = mAdapterServiceOption.getSpanNum();
        if (mListServiceOptions.size() % spanNum == 0) {
            return mListServiceOptions.size() / spanNum;
        } else {
            return mListServiceOptions.size() / spanNum + 1;
        }
    }

    private int getRecyclerViewHangNumbCertificates() {
        int spanNum = mAdapterCertificate.getSpanNum();
        if (mListCertificates.size() % spanNum == 0) {
            return mListCertificates.size() / spanNum;
        } else {
            return mListCertificates.size() / spanNum + 1;
        }
    }

    private void initServiceOptionsView() {
        assembleServiceOptions();
        int space = ContextUtils.dip2px(this, 8);
        int spanNum = 3;
        mAdapterServiceOption = new TipAdapter(this, mListServiceOptions);
        mAdapterServiceOption.setSpaceAndSpanNumb(space, spanNum, 124);
        GridLayoutManager manager = new GridLayoutManager(this, spanNum);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(space, spanNum);
        mRecyclerViewServiceName.setLayoutManager(manager);
        mRecyclerViewServiceName.addItemDecoration(itemDecoration);
        mRecyclerViewServiceName.setAdapter(mAdapterServiceOption);
    }

    private void initCertificatesView() {
        assembleCertificates();
        int space = ContextUtils.dip2px(this, 8);
        int spanNum = 3;
        mAdapterCertificate = new TipAdapter(this, mListCertificates);
        mAdapterCertificate.setSpaceAndSpanNumb(space, spanNum, 124);
        GridLayoutManager manager = new GridLayoutManager(this, spanNum);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(space, spanNum);
        mRecyclerViewCertificate.setLayoutManager(manager);
        mRecyclerViewCertificate.addItemDecoration(itemDecoration);
        mRecyclerViewCertificate.setAdapter(mAdapterCertificate);
    }

    //    private void assembleComments(int numb) {
//        mListComments = new ArrayList<>();
//        for (int i = 0; i < numb; i++) {
//            CommentBean bean = new CommentBean();
//            mListComments.add(bean);
//        }
//    }
    private List<CommentBean> takePartDataFormList(List<CommentBean> list) {
        List<CommentBean> targetList = new ArrayList<>();
        int maxNum = Math.min(list.size(), 4);
        for (int i = 0; i < maxNum; i++) {
            targetList.add(list.get(i));
        }
        return targetList;
    }

    private void assembleCertificates() {
        mListCertificates = new ArrayList<>();
        TipBaseBean b1 = new TipBaseBean();
        b1.setName("银丰员工");
        b1.setSelected(true);
//        TipBaseBean b2 = new TipBaseBean();
//        b2.setName("健康证");
        mListCertificates.add(b1);
//        mListCertificates.add(b2);
//        for (int i = 0; i < 5; i++) {
//            TipBaseBean bean = new TipBaseBean();
//            bean.setName("标签名" + i);
//            mListCertificates.add(bean);
//        }
    }

    private void assembleServiceOptions() {
        mListServiceOptions = new ArrayList<>();
        TipBaseBean b1 = new TipBaseBean();
        b1.setName("医院导诊");
        mListServiceOptions.add(b1);
        for (int i = 0; i < 4; i++) {
            TipBaseBean bean = new TipBaseBean();
            bean.setName("标签名" + i);
            mListServiceOptions.add(bean);
        }
    }

    private void doGetCommentList() {
        setEmptyViewLoadingCircle();
        CommentParam param = new CommentParam();
        param.setWaiterId(info.getAccountId());
        PatientApi.getInstance().getCommentList(1, 10, param)
                .compose(RxSchedulers.<Response<BaseBean<CommentListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<CommentListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<CommentListResult>>() {
                    @Override
                    public void success(BaseBean<CommentListResult> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            CommentListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<CommentBean> list = listResult.getList();
                                if (list != null && list.size() > 0) {
                                    mListCommentsFirstPage = list;
                                    List<CommentBean> partList = takePartDataFormList(list);
                                    mAdapterComment.setNewData(partList);
                                    mListComments = mAdapterComment.getData();
                                    resetCommentsBeforeExpandViewParams();
                                    if (list.size() > 4)
                                        tvExpand.setVisibility(View.VISIBLE);
                                    return;
                                } else {
                                    setEmptyViewNoData();
                                    return;
                                }
                            }
                        }
                        setEmptyViewRetry(result.getMessage());
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        setEmptyViewRetry("");
                    }
                });
    }

    private void setEmptyViewLoadingCircle() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_loading_circle, null);
        mAdapterComment.setEmptyView(emptyView);
    }

    private void setEmptyViewRetry(String message) {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_retry, null);
        TextView tvRetry = emptyView.findViewById(R.id.tvRetry);
        if (TextUtils.isEmpty(message))
            message = "获取失败,请重试";
        tvRetry.setText(message);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        mAdapterComment.setEmptyView(emptyView);
    }

    private void retry() {
        setEmptyViewLoadingCircle();
        doGetCommentList();
    }

    private void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("暂无评论！");
        mAdapterComment.setEmptyView(emptyView);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_pzy_detail;
    }

    public static void activityStart(Context context, WaiterInfo info) {
        Intent intent = new Intent(context, PzyDetailActivity.class);
        intent.putExtra(Constants.KEY_WAITERINFO_DETAIL, info);
        context.startActivity(intent);
    }

    private void getIntentData() {
        info = (WaiterInfo) getIntent().getSerializableExtra(Constants.KEY_WAITERINFO_DETAIL);
        if (info == null)
            finish();
    }
}
