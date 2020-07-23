package com.yinfeng.wypzh.ui.order.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.login.IMLogInfo;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderListParam;
import com.yinfeng.wypzh.bean.order.OrderListResult;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.IMUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.NetUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.CustomLoadMoreView;

import java.util.List;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

public abstract class BaseOrderChildFragment extends BaseFragment {
    protected SmartRefreshLayout mSmartRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter mAdapter;
    protected boolean isGetDataing = false;
    protected String orderState = Constants.ORDER_STATE_PAID;
    protected String orderCommentState = "";
    protected int pageNumb = 1;
    protected int pageSize = 10;
    protected List<OrderDetailBean> mList;
    private boolean isGetIMTokening = false;
    private boolean isGalleryOk = false;
    private boolean isCaptureOk = false;
    private boolean isAudio = false;
    //    private boolean isCanIntoMsgPage = false;
    private PermissionTipDialog permissionTipDialog;
    protected boolean hasInit = false;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_orderbase;
    }

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        mSmartRefreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        mSmartRefreshLayout.setEnableLoadMore(false);

        orderState = initOrderState();
        orderCommentState = initCommentState();
    }


    @Override
    protected void initData() {
        assembleData();
        initRecyclerView();
        mSmartRefreshLayout.autoRefresh();
        hasInit = true;
    }


    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = getAdapter();

        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        //预加载 无感滑动 倒数第N个开始预加载
        mAdapter.setPreLoadNumber(4);
//        addLoadAnimition();
//        设置不显示动画数量(设置的这count个item是不执行动画的)
        mAdapter.setNotDoAnimationCount(3);//MD不起效果啊
//        动画默认只执行一次
        mAdapter.isFirstOnly(true);
        mRecyclerView.setAdapter(mAdapter);
        setEmptyViewLoading();
    }

    protected void setEmptyViewLoading() {
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_page_loading, null);
        mAdapter.setEmptyView(emptyView);
    }

    protected void setEmptyViewRetry() {
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_page_retry, null);
        TextView tvRetry = emptyView.findViewById(R.id.tvRetry);
        if (!NetUtil.isNetworkAvailable(getActivity())) {
            tvRetry.setText("请检查网络设置");
        } else {
            tvRetry.setText("操作失败,请重试");
        }
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        mAdapter.setEmptyView(emptyView);
    }

    protected void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText(initEmptyViewTip());
        mAdapter.setEmptyView(emptyView);
    }

    protected abstract BaseQuickAdapter getAdapter();

    protected abstract void assembleData();

    protected void retry() {
        setEmptyViewLoading();
        doRefresh();
    }

    protected abstract String initOrderState();

    protected abstract String initCommentState();

    protected abstract CharSequence initEmptyViewTip();


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

    protected void addLoadAnimition() {
        mAdapter.openLoadAnimation(new BaseAnimation() {

            @Override
            public Animator[] getAnimators(View view) {
                return new Animator[]{
//                        ObjectAnimator.ofFloat(view, "alpha", 0.6f, 0.8f, 1.0f),
                        ObjectAnimator.ofFloat(view, "scaleX", 0.96f, 0.97f, 0.98f, 0.99F, 1.0f)};

            }

        });
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
    }

    protected synchronized void doRefresh() {
        LogUtil.error("dorefresh " + orderState);
        if (isGetDataing)
            return;
        isGetDataing = true;
//        setEmptyViewLoading();
        OrderListParam param = new OrderListParam();
        param.setState(orderState);
        param.setEveState(orderCommentState);
        OrderApi.getInstance().getOrderList(1, pageSize, param)
                .compose(RxSchedulers.<Response<BaseBean<OrderListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderListResult>>(getActivity()) {
                    @Override
                    public void success(BaseBean<OrderListResult> result) {
                        isGetDataing = false;
                        mSmartRefreshLayout.finishRefresh();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<OrderDetailBean> list = listResult.getList();
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
//                        ToastUtil.getInstance().showShort(getActivity(), result.getMessage());
                        setEmptyViewRetry();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        mSmartRefreshLayout.finishRefresh();
                        isGetDataing = false;
//                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        if (mList == null || mList.size() == 0)
                            setEmptyViewRetry();
                    }
                });

    }

    protected void doLoadMore() {
        if (isGetDataing)
            return;
        isGetDataing = true;
        OrderListParam param = new OrderListParam();
        param.setState(orderState);
        param.setEveState(orderCommentState);
        OrderApi.getInstance().getOrderList(pageNumb, pageSize, param)
                .compose(RxSchedulers.<Response<BaseBean<OrderListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderListResult>>() {
                    @Override
                    public void success(BaseBean<OrderListResult> result) {
                        isGetDataing = false;
                        mAdapter.loadMoreComplete();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<OrderDetailBean> list = listResult.getList();
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
//                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        mAdapter.loadMoreFail();
                    }
                });
    }

    protected boolean checkIMCloudState() {
        UserInfo userInfo = SFUtil.getInstance().getUserInfo(getActivity());
        String imAccount = userInfo.getimAccount();
        String imToken = userInfo.getImToken();
        if (TextUtils.isEmpty(imAccount) || TextUtils.isEmpty(imToken)) {
            doGetIcloudToken();
            return false;
        } else {
            StatusCode status = NIMClient.getStatus();
            if (status == StatusCode.LOGINED) {
                NimUIKit.setAccount(imAccount);
                return true;
            } else {
                if (status == StatusCode.LOGINING || status == StatusCode.SYNCING)
                    return false;
                LoginInfo mLogInfo = IMUtil.loginInfo(getActivity(), imAccount, imToken);
                doLogIMCloud(mLogInfo);
                return false;
            }

        }
    }

    private void doGetIcloudToken() {
        if (isGetIMTokening)
            return;
        isGetIMTokening = true;
        LoginApi.getInstance().getIMcloudLoginInfo()
                .compose(RxSchedulers.<Response<BaseBean<IMLogInfo>>>applySchedulers())
                .compose(this.<Response<BaseBean<IMLogInfo>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<IMLogInfo>>() {
                    @Override
                    public void success(BaseBean<IMLogInfo> result) {
                        isGetIMTokening = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            IMLogInfo logInfo = result.getResult();
                            LoginInfo mLogInfo = IMUtil.loginInfo(getActivity(), logInfo.getAccid(), logInfo.getToken());
                            doLogIMCloud(mLogInfo);
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetIMTokening = false;
                    }
                });
    }

    private void doLogIMCloud(LoginInfo mLogInfo) {
        AbortableFuture abortableFuture = IMUtil.loginIM(mLogInfo, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(final LoginInfo loginInfo) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo userInfo = SFUtil.getInstance().getUserInfo(getActivity());
                        userInfo.setimAccount(loginInfo.getAccount());
                        userInfo.setImToken(loginInfo.getToken());
                        SFUtil.getInstance().setUserInfo(getActivity(), userInfo);
                    }
                });

            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }


    protected void openMsg(String account) {
        if (checkIMCloudState()) {
//            if (NIMUtil.isMainProcess(getActivity())) {
//                // 在主进程中初始化UI组件，判断所属进程方法请参见demo源码。
//                NimUIKit.init(getActivity());
//            }
            if (!TextUtils.isEmpty(account))
                NimUIKit.startP2PSession(getActivity(), account);
        } else {
            ToastUtil.getInstance().showShort(getActivity(), "消息服务器未登录");
        }

    }

//    @SuppressLint("CheckResult")
//    protected void requestPermissions(final String account) {
//        RxPermissions rxPermission = new RxPermissions(getActivity());
//        rxPermission.requestEach(
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO
//        ).subscribe(new Consumer<Permission>() {
//            @Override
//            public void accept(Permission permission) throws Exception {
//                if (permission.granted) {
//                    // 用户已经同意该权限
//                    if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
//                        isGalleryOk = true;
//                    }
//                    if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
//                        isCaptureOk = true;
//                    }
//                    if (TextUtils.equals(Manifest.permission.RECORD_AUDIO, permission.name)) {
//                        isAudio = true;
//                    }
//                    if (isCaptureOk && isGalleryOk && isAudio) {
////                                isCanIntoMsgPage = true;
//                        openMsg(account);
//                    }
//                } else if (permission.shouldShowRequestPermissionRationale) {
//                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                } else {
//                    // 用户拒绝了该权限，并且选中『不再询问』
//                    LogUtil.error("订单子页面", permission.name + " is denied.");
//                    String permissionName = "";
//                    if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
//                        permissionName = "相册";
//                    }
//                    if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
//                        permissionName = "摄像头";
//                    }
//                    if (TextUtils.equals(Manifest.permission.RECORD_AUDIO, permission.name)) {
//                        permissionName = "麦克风";
//                    }
//                    permissionTipDialog = DialogHelper.getPermissionTipDialog(getActivity(), permissionName);
//                    permissionTipDialog.show();
//                }
//            }
//        });
//
//
//    }


    @Override
    public void onSupportVisible() {
        LogUtil.error("onSupportVisible");
        if (hasInit) {
            doRefresh();
        }
        super.onSupportVisible();
    }
}
