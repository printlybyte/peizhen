package com.yinfeng.wypzh.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.NetUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.MultiStateView;
import com.yinfeng.wypzh.widget.SimpleMultiStateView;

import org.simple.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportHelper;


public abstract class BaseFragment extends SupportFragment {
    protected Context mContext;
    protected View mRootView;
    protected Dialog mLoadingDialog = null;
//    protected SimpleMultiStateView mSimpleMultiStateView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = inflater.inflate(getContentLayout(), container, false);
        }
//        mSimpleMultiStateView = mRootView.findViewById(R.id.SimpleMultiStateView);
        mContext = mRootView.getContext();
        mLoadingDialog = DialogHelper.getLoadingDialog(getActivity());
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view, savedInstanceState);
//        initStateView();
        initEventBus();
    }

    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
        setListener();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onDestroy() {
        unregistEvenBus();
        super.onDestroy();
    }

    protected abstract void bindView(View view, Bundle savedInstanceState);

    protected abstract void setListener();

    protected abstract void initData();

    protected abstract int getContentLayout();

//    private void initStateView() {
//        if (mSimpleMultiStateView == null) return;
//        mSimpleMultiStateView.setEmptyResource(R.layout.view_empty)
//                .setRetryResource(R.layout.view_retry)
//                .setLoadingResource(R.layout.view_loading)
//                .setNoNetResource(R.layout.view_retry)
//                .build()
//                .setonReLoadlistener(new MultiStateView.onReLoadlistener() {
//                    @Override
//                    public void onReload() {
//                        onRetry();
//                    }
//                });
//    }

    //    protected void showLoading() {
//        if (mSimpleMultiStateView != null) {
//            mSimpleMultiStateView.showLoadingView();
//        }
//    }
//
//    protected void showSuccess() {
//        hideLoadingDialog();
//        if (mSimpleMultiStateView != null) {
//            mSimpleMultiStateView.showContent();
//        }
//    }
//
//    protected void showFail() {
//        hideLoadingDialog();
//        if (mSimpleMultiStateView != null) {
//            mSimpleMultiStateView.showErrorView();
//        }
//    }
    protected void initEventBus() {
        EventBus.getDefault().register(this);
    }

    protected void unregistEvenBus() {
        EventBus.getDefault().unregister(this);
    }

    protected void checkNetValidAndToast() {
        if (!NetUtil.isNetworkAvailable(getActivity())) {
            ToastUtil.getInstance().showShort(getActivity(), "请检查网络设置");
        } else {
            ToastUtil.getInstance().showShort(getActivity(), "操作失败,请重试");
        }
    }

    protected void checkNetValidAndToast(int httpCode, int errCode, String errMsg) {
        if (!NetUtil.isNetworkAvailable(getActivity())) {
            ToastUtil.getInstance().showShort(getActivity(), "请检查网络设置");
        } else {
            if (TextUtils.isEmpty(errMsg)) {
                ToastUtil.getInstance().showShort(getActivity(), "操作失败,请重试");
            } else {
                ToastUtil.getInstance().showShort(getActivity(), errMsg);
            }
        }
    }

    protected void showLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.show();
    }


    protected void showLoadingDialog(String str) {
        if (mLoadingDialog != null) {
            TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv_load_dialog);
            tv.setText(str);
            mLoadingDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }
//    /**
//     * 获取栈内的fragment对象
//     */
//    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
//        return SupportHelper.findFragment(getActivity().getSupportFragmentManager(), fragmentClass);
//
//    }

}
