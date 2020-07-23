package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.OrderCommentParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

import static com.yinfeng.wypzh.base.Constants.KEY_ORDER_DETAIL;

public class OrderCommentActivity extends BaseActivity {
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private ImageView ivHeadIcon, ivPhone, ivMsg;
    private TextView tvName, tvPhone;
    private Button btCommitComment;
    private EditText etComment;
    private ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
    private TextView tvAttitude;
    private OrderDetailBean orderDetailBean;
    private String phone;
    private String account;
    private boolean isCommenting = false;
    private String commentContent;
    private int level;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("服务评价");
        mTopBar.setTopRightTxt("投诉");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mTopBar.setTopBarRightTxtListener(new TopBar.TopBarRightTextCickListener() {
            @Override
            public void topRightTxtClick() {
                ContextUtils.callComplainPhone(OrderCommentActivity.this);
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        btCommitComment = mRootView.findViewById(R.id.btCommitComment);
        etComment = mRootView.findViewById(R.id.etComment);
        etComment.clearFocus();

        ivStar1 = mRootView.findViewById(R.id.ivStar1);
        ivStar2 = mRootView.findViewById(R.id.ivStar2);
        ivStar3 = mRootView.findViewById(R.id.ivStar3);
        ivStar4 = mRootView.findViewById(R.id.ivStar4);
        ivStar5 = mRootView.findViewById(R.id.ivStar5);
        tvAttitude = mRootView.findViewById(R.id.tvAttitude);

        initLevel();

        ivHeadIcon = mRootView.findViewById(R.id.ivHeadIcon);
        ivPhone = mRootView.findViewById(R.id.ivPhone);
        ivMsg = mRootView.findViewById(R.id.ivMsg);
        tvName = mRootView.findViewById(R.id.tvName);
        tvPhone = mRootView.findViewById(R.id.tvPhone);
        if (orderDetailBean != null) {
//            String profile = orderDetailBean.getProfile();
            String name = orderDetailBean.getWaiterName();
            phone = orderDetailBean.getWaiterPhone();
            account = orderDetailBean.getWaiterId();
            String waiterInfoStr = orderDetailBean.getWaiter();
            if (!TextUtils.isEmpty(waiterInfoStr)) {
                try {
                    WaiterInfo waiterInfo = new Gson().fromJson(waiterInfoStr, WaiterInfo.class);
                    if (waiterInfo != null) {
                        String profile = waiterInfo.getProfile();
                        ImageUtil.getInstance().loadImgCircle(this, profile, ivHeadIcon);
                    }
                } catch (Exception e) {
                }
            }
//            ImageUtil.getInstance().loadImgCircle(this, profile, ivHeadIcon);
            tvName.setText(name);
            tvPhone.setText("联系电话:" + phone);
        }
    }

    private void initLevel() {
        ivStar1.setImageResource(R.drawable.star_selected);
        ivStar2.setImageResource(R.drawable.star_selected);
        ivStar3.setImageResource(R.drawable.star_selected);
        ivStar4.setImageResource(R.drawable.star_selected);
        ivStar5.setImageResource(R.drawable.star_selected);
        level = 5;
        tvAttitude.setText(getResources().getString(R.string.order_leval_5));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btCommitComment).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (checkCanComment()) {
                            doComment();
                        }
                    }
                });

        RxView.clicks(ivPhone).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String phoneNum = orderDetailBean.getWaiterPhone();
                        if (!TextUtils.isEmpty(phoneNum))
                            ContextUtils.callPhone(OrderCommentActivity.this, phoneNum);
                    }
                });
        RxView.clicks(ivMsg).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String account = orderDetailBean.getWaiterId();
                        if (!TextUtils.isEmpty(account))
                            openMsg(account);
//                            requestIMessagePermissions(account);
                    }
                });
        RxView.clicks(ivHeadIcon).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        goToPzyInfo();
                    }
                });
        RxView.clicks(ivStar1).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_unselect);
                        ivStar3.setImageResource(R.drawable.star_unselect);
                        ivStar4.setImageResource(R.drawable.star_unselect);
                        ivStar5.setImageResource(R.drawable.star_unselect);
                        level = 1;
                        tvAttitude.setText(getResources().getString(R.string.order_leval_1));
                    }
                });
        RxView.clicks(ivStar2).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);
                        ivStar3.setImageResource(R.drawable.star_unselect);
                        ivStar4.setImageResource(R.drawable.star_unselect);
                        ivStar5.setImageResource(R.drawable.star_unselect);
                        level = 2;
                        tvAttitude.setText(getResources().getString(R.string.order_leval_2));
                    }
                });
        RxView.clicks(ivStar3).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);
                        ivStar3.setImageResource(R.drawable.star_selected);
                        ivStar4.setImageResource(R.drawable.star_unselect);
                        ivStar5.setImageResource(R.drawable.star_unselect);
                        level = 3;
                        tvAttitude.setText(getResources().getString(R.string.order_leval_3));
                    }
                });
        RxView.clicks(ivStar4).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);
                        ivStar3.setImageResource(R.drawable.star_selected);
                        ivStar4.setImageResource(R.drawable.star_selected);
                        ivStar5.setImageResource(R.drawable.star_unselect);
                        level = 4;
                        tvAttitude.setText(getResources().getString(R.string.order_leval_4));

                    }
                });
        RxView.clicks(ivStar5).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);
                        ivStar3.setImageResource(R.drawable.star_selected);
                        ivStar4.setImageResource(R.drawable.star_selected);
                        ivStar5.setImageResource(R.drawable.star_selected);
                        level = 5;
                        tvAttitude.setText(getResources().getString(R.string.order_leval_5));

                    }
                });
    }

    private void doComment() {
        if (isCommenting) {
            showLoadingDialog();
            return;
        }
        isCommenting = true;
        showLoadingDialog();
        OrderCommentParam param = new OrderCommentParam();
        param.setContent(commentContent);
        param.setLevel(String.valueOf(level));
        param.setOrderId(orderDetailBean.getId());
        param.setWaiterId(orderDetailBean.getWaiterId());
        OrderApi.getInstance().commentWaiter(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        hideLoadingDialog();
                        isCommenting = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderUtil.addOrderComplete(orderDetailBean.getId());
                            OrderUtil.deleteOrderWaitComment(orderDetailBean.getId());
                            RedPointUtil.showOrderDot(4);
                            goToCommentSuccess();
                        } else {
                            ToastUtil.getInstance().showLong(OrderCommentActivity.this, result.getMessage());
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isCommenting = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private boolean checkCanComment() {
        commentContent = etComment.getText().toString().trim();
        if (!TextUtils.isEmpty(commentContent)) {
            return true;
        }
        ToastUtil.getInstance().showShort(OrderCommentActivity.this, "请输入评论内容");
        return false;
    }

    private void goToPzyInfo() {
        WaiterInfo info = ContextUtils.getWaiterInfo(orderDetailBean);
        if (info != null)
            PzyDetailActivity.activityStart(this, info);
    }

    private void goToCommentSuccess() {
        Intent intent = new Intent(this, OrderCommentSuccessActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void initData() {

    }

    public static void activityStart(Context context, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderCommentActivity.class);
        intent.putExtra(KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(KEY_ORDER_DETAIL);
        if (orderDetailBean == null) {
            finish();
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_comment;
    }
}
