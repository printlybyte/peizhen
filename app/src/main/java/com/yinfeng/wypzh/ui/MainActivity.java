package com.yinfeng.wypzh.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.base.SupportFragment;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UpdateVersionBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.login.IMLogInfo;
import com.yinfeng.wypzh.bean.login.UserInfoNetResult;
import com.yinfeng.wypzh.bean.message.MessageOrderNotice;
import com.yinfeng.wypzh.bean.message.MsgOrderNoticeParam;
import com.yinfeng.wypzh.bean.message.MsgOrderNoticeResult;
import com.yinfeng.wypzh.bean.message.PushMessage;
import com.yinfeng.wypzh.bean.order.CancelOrderParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderListParam;
import com.yinfeng.wypzh.bean.order.OrderListResult;
import com.yinfeng.wypzh.bean.order.RenewDetail;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.download.DownLoadManager;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.OrderTimeOutDialog;
import com.yinfeng.wypzh.ui.dialog.OrderTipDialogNew;
import com.yinfeng.wypzh.ui.dialog.RenewReplyDialog;
import com.yinfeng.wypzh.ui.dialog.RenewReplyDialogNew;
import com.yinfeng.wypzh.ui.homepage.HomePageFragment;
import com.yinfeng.wypzh.ui.login.LoginActivity;
import com.yinfeng.wypzh.ui.login.SycCodeLoginActivity;
import com.yinfeng.wypzh.ui.message.MessageFragment;
import com.yinfeng.wypzh.ui.mine.MineFragment;
import com.yinfeng.wypzh.ui.order.OrderDetailServicing;
import com.yinfeng.wypzh.ui.order.OrderDetailWaitService;
import com.yinfeng.wypzh.ui.order.OrderWaitReceiveActivity;
import com.yinfeng.wypzh.ui.order.fragment.OrderFragment;
import com.yinfeng.wypzh.utils.BarUtils;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.IMUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.StatusBarUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.utils.UmUtil;
import com.yinfeng.wypzh.widget.BottomBar;
import com.yinfeng.wypzh.widget.BottomBarTab;

import org.json.JSONObject;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private String TAG = MainActivity.class.getSimpleName();
    BottomBar mBottomBar;
    private SupportFragment[] mFragments = new SupportFragment[4];
    private int currentPosition = 0;
    public static final String KEY_NEED_SWITCH_POSITION = "key_current_position";
    public static final String KEY_SWITCH_ORDERFRAGMENT_POSITION = "key_orderfragment_position";
    private Disposable mDisposeTimeTask;
    private Disposable mLoopDisposeWaitReceive;
    private Disposable mLoopDisposeWaitService;
    private boolean isLoopingWaitReceive = false;
    private boolean isLoopingWaitService = false;
    private boolean isTimeTaskRuning = false;
    private boolean isLoopingRenewReply = false;
    private Disposable mLoopDisposeRenew;
    private boolean isGetMsgNoticing = false;
    private boolean isCheckVersioning = false;
    private RenewReplyDialogNew mRenewReplyDialog;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
//        StatusBarUtil.setColor(MainActivity.this, Color.parseColor("#d7f1ed"), 0);
        StatusBarUtil.setColor(MainActivity.this, Color.parseColor("#ffffff"), 0);
        mBottomBar = mRootView.findViewById(R.id.bottomBar);
        if (savedInstanceState == null) {
            SFUtil.getInstance().clearStateConfigSF(this);
            mFragments[0] = HomePageFragment.newInstance();
            mFragments[1] = OrderFragment.newInstance();
            mFragments[2] = MessageFragment.newInstance();
            mFragments[3] = MineFragment.newInstance();
            getSupportDelegate().loadMultipleRootFragment(R.id.contentContainer, 0,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2],
                    mFragments[3]);
        } else {
            currentPosition = SFUtil.getInstance().getCurrentPostionMainActivity(this);
            resetStatusBarColor(currentPosition);
            mFragments[0] = findFragment(HomePageFragment.class);
            mFragments[1] = findFragment(OrderFragment.class);
            mFragments[2] = findFragment(MessageFragment.class);
            mFragments[3] = findFragment(MineFragment.class);
        }

        mBottomBar.addItem(new BottomBarTab(this, R.drawable.bb_homepage, "首页"))
                .addItem(new BottomBarTab(this, R.drawable.bb_order, "订单"))
                .addItem(new BottomBarTab(this, R.drawable.bb_message, "消息"))
                .addItem(new BottomBarTab(this, R.drawable.bb_mine, "我的"));

        //设置初屏状态黑色字体
        BarUtils.setStatusBarLightMode(MainActivity.this,true);


    }

    @Override
    protected void setListener() {
        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {

                if (position==0){
                    BarUtils.setStatusBarLightMode(MainActivity.this,true);
                }else {
                    BarUtils.setStatusBarLightMode(MainActivity.this,false);
                }

                getSupportDelegate().showHideFragment(mFragments[position], mFragments[prePosition]);
                currentPosition = position;
                resetStatusBarColor(position);

                BottomBarTab bottomBarTab = mBottomBar.getBottomBarTabByPosition(position);
                ImageView redPoint = bottomBarTab.getRedPoint();
                redPoint.setVisibility(View.INVISIBLE);

//                BottomBarTab bottomBarTab1 = mBottomBar.getBottomBarTabByPosition(1);
//                BottomBarTab bottomBarTab3 = mBottomBar.getBottomBarTabByPosition(3);
//                BadgeView tipView1 = bottomBarTab1.getBadgeView();
//                BadgeView tipView3 = bottomBarTab3.getBadgeView();
//                BottomBarTab bottomBarTab2 = mBottomBar.getBottomBarTabByPosition(2);
//                ImageView redPoint = bottomBarTab2.getRedPoint();
//                boolean isShow = tipView1.isHideOnNull();
//                if (isShow) {
//                    tipView1.setBadgeCount(6);
//                    tipView3.setBadgeCount(66);
//                }
//                boolean isVisible = redPoint.getVisibility() == View.VISIBLE;
//                Log.i(TAG, "isVisible :" + isVisible);
//                if (isVisible) {
//                    redPoint.setVisibility(View.INVISIBLE);
//                } else {
//                    redPoint.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_BOTTOM_REDPOINT_SHOW, mode = ThreadMode.MAIN)
    private void showBottomRedPoint(int position) {
        LogUtil.error("EventBus showBottomRedPoint orderId :" + position);
        if (position == currentPosition)
            return;
        BottomBarTab bottomBarTab = mBottomBar.getBottomBarTabByPosition(position);
        ImageView redPoint = bottomBarTab.getRedPoint();
        redPoint.setVisibility(View.VISIBLE);
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_BOTTOM_REDPOINT_HIDE, mode = ThreadMode.MAIN)
    private void hideBottomRedPoint(int position) {
        LogUtil.error("EventBus showBottomRedPoint orderId :" + position);
        BottomBarTab bottomBarTab = mBottomBar.getBottomBarTabByPosition(position);
        ImageView redPoint = bottomBarTab.getRedPoint();
        redPoint.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initData() {
        checkIMState();
        checkUserInfoAccountId();
        doGetWaitReceiveListFirstPage();
        doGetWaitServiceListFirstPage();
//        doGetRenewList();
        checkComplainPhone();
        getIntentDataFromNotif(getIntent());
        refreshNotifNoticeInMsgFragment();
        doCheckVersion();
        doActiveCoupon();

    }

    private void doActiveCoupon() {
        LoginApi.getInstance().activeCoupon()
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {

                    }
                });
    }


    private void refreshNotifNoticeInMsgFragment() {
        //==
        UserInfo info = SFUtil.getInstance().getUserInfo(this);
        if (info != null) {
            String accountId = info.getAccountId();
            if (!TextUtils.isEmpty(accountId))
                doGetMessageNoticeNew(accountId);
        }
    }

    private void checkUserInfoAccountId() {
        UserInfo info = SFUtil.getInstance().getUserInfo(this);
        String accountId = info.getAccountId();
        if (TextUtils.isEmpty(accountId)) {
            doGetUserInfo();
        } else {
            checkUmengPushState(accountId);
        }
    }

    private void checkUmengPushState(final String accountId) {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable(new IUmengCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });//调用过disable后必须调用此方法启用
        String deviceToken = mPushAgent.getRegistrationId();
        if (TextUtils.isEmpty(deviceToken)) {
//            mPushAgent.setNotificaitonOnForeground(false);
            mPushAgent.register(new IUmengRegisterCallback() {

                @Override
                public void onSuccess(String deviceToken) {
                    LogUtil.error("MainActivity umeng 注册成功 devicetoken :" + deviceToken);
                    doSetUmengAlias(deviceToken, accountId);
                }

                @Override
                public void onFailure(String s, String s1) {
                }
            });
        } else {
            LogUtil.error("MainActivity umeng 已经注册成功 devicetoken :" + deviceToken);
            doSetUmengAlias(deviceToken, accountId);
        }
    }

    private void doSetUmengAlias(final String deviceToken, final String accountId) {
        String alias = SFUtil.getInstance().getAliasByDeviceToken(this, deviceToken);
        if (TextUtils.equals(alias, accountId)) {
            LogUtil.error("别名已经注册成功：" + accountId);
            doGetMessageNoticeNew(accountId);
            return;
        }
        SFUtil.getInstance().clearUmengAliasSF(this);
        ContextUtils.setUmengAlias(MainActivity.this, accountId, Constants.UMEGN_ALIAS_TYPE_MEMBER, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {
                if (b) {
                    LogUtil.error("别名注册成功：" + accountId);
                    SFUtil.getInstance().putDeviceTokenAndRelativeAlias(MainActivity.this, deviceToken, accountId);
                    doGetMessageNoticeNew(accountId);
                }
            }
        });
    }

    private synchronized void doGetMessageNoticeNew(String accountId) {
        if (isGetMsgNoticing) {
            return;
        }
        isGetMsgNoticing = true;
        MsgOrderNoticeParam param = new MsgOrderNoticeParam();
        param.setAlias(accountId);
        OrderApi.getInstance().getMsgOrderNoticeList(1, 30, param)
                .compose(RxSchedulers.<Response<BaseBean<MsgOrderNoticeResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<MsgOrderNoticeResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<MsgOrderNoticeResult>>() {
                    @Override
                    public void success(BaseBean<MsgOrderNoticeResult> result) {
                        isGetMsgNoticing = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            MsgOrderNoticeResult listResult = result.getResult();
                            if (listResult != null) {
                                List<MessageOrderNotice> list = listResult.getList();
                                if (list != null && list.size() > 0) {
                                    SFUtil.getInstance().addMsgNoticeListNew(MainActivity.this, list);
                                    RedPointUtil.showBottomDot(2);
                                } else {
                                    List<MessageOrderNotice> localList = SFUtil.getInstance().getMsgNoticeListNew(MainActivity.this);
                                    if (localList != null && localList.size() > 0)
                                        RedPointUtil.showBottomDot(2);
                                }
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetMsgNoticing = false;
                        List<MessageOrderNotice> list = SFUtil.getInstance().getMsgNoticeListNew(MainActivity.this);
                        if (list != null && list.size() > 0)
                            RedPointUtil.showBottomDot(2);

                    }
                });

    }

    private void checkComplainPhone() {
        String phone = SFUtil.getInstance().getComplainPhone(this);
        if (TextUtils.isEmpty(phone))
            doGetComplainPhone();
    }

    private void doCheckVersion() {
        if (isCheckVersioning) {
            return;
        }
        isCheckVersioning = true;
        LoginApi.getInstance().checkVersion()
                .compose(RxSchedulers.<Response<BaseBean<UpdateVersionBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<UpdateVersionBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UpdateVersionBean>>(this) {
                    @Override
                    public void success(BaseBean<UpdateVersionBean> result) {
                        isCheckVersioning = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UpdateVersionBean bean = result.getResult();
                            if (bean != null) {
                                String versionCodeStr = bean.getVersionCode();
                                int versionCodeNet = Integer.parseInt(versionCodeStr);
                                int versionCodeLocal = ContextUtils.getLocalVersion(MainActivity.this);

                                if (versionCodeNet > versionCodeLocal) {
                                    if (bean.isForce()) {
                                        showForceUpdateVersionDialog(bean);
                                        return;
                                    }
                                    RedPointUtil.showBottomDot(3);
                                    RedPointUtil.showUpdateVersionDot();
                                } else {
                                    RedPointUtil.hideUpdateVersionDot();
                                }
                            }

                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isCheckVersioning = false;
                    }
                });
    }

    private void showForceUpdateVersionDialog(final UpdateVersionBean bean) {
        String title = "更新版本 " + bean.getVersionName();
        String content = "部分功能优化，解决了若干bug";
        if (!TextUtils.isEmpty(bean.getUpdateLog()))
            content = bean.getUpdateLog();
        final MaterialDialog downLoadDialog = DialogHelper.getDownLoadDialog(MainActivity.this, title, content);
        downLoadDialog.setCanceledOnTouchOutside(false);
        downLoadDialog.setCancelable(false);
        downLoadDialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        downLoadDialog.dismiss();
                        ContextUtils.exitApp(MainActivity.this);
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        downLoadDialog.dismiss();
                        if (!DownLoadManager.getInstance().isDownLoading() && !TextUtils.isEmpty(bean.getDownLoadUrl()))
                            DownLoadManager.getInstance().downLoadApk(MainActivity.this, bean.getDownLoadUrl());
                    }
                });
        downLoadDialog.show();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    private void resetStatusBarColor(int position) {
        if (position == 0) {
//            StatusBarUtil.setColor(MainActivity.this, Color.parseColor("#d7f1ed"), 0);
            StatusBarUtil.setColor(MainActivity.this, Color.parseColor("#ffffff"), 0);
        } else {
            StatusBarUtil.setColor(MainActivity.this, Color.parseColor("#06b49b"), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SFUtil.getInstance().putCurrentPostionMainActivity(this, currentPosition);
    }

    private void doGetUserInfo() {
        LoginApi.getInstance().getUserInfo()
                .compose(RxSchedulers.<Response<BaseBean<UserInfoNetResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<UserInfoNetResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UserInfoNetResult>>(this) {
                    @Override
                    public void success(BaseBean<UserInfoNetResult> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UserInfoNetResult uInfo = result.getResult();
                            PatientInfo pInfo = uInfo.getMemberPatient();

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(MainActivity.this);
                            userInfo.setAccountId(uInfo.getAccountId());
                            userInfo.setName(uInfo.getName());
                            userInfo.setSex(uInfo.getSex());
                            userInfo.setLevel(uInfo.getLevel());
                            userInfo.setProfile(uInfo.getProfile());
                            userInfo.setIdcard(pInfo.getIdcard());
                            userInfo.setIsHistory(pInfo.getIsHistory());
                            userInfo.setMedicalHistory(pInfo.getMedicalHistory());
                            userInfo.setOtherMedical(pInfo.getOtherMedical());
                            SFUtil.getInstance().setUserInfo(MainActivity.this, userInfo);
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });

    }

    private void doGetComplainPhone() {
        LoginApi.getInstance().getComplainPhone()
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>(this) {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            String phone = result.getResult();
                            if (!TextUtils.isEmpty(phone)) {
                                SFUtil.getInstance().putComplainPhone(MainActivity.this, phone);
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {

                    }
                });
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.getInstance().show(this, "再按一次退出程序", 15000, Gravity.BOTTOM);
                mExitTime = System.currentTimeMillis();
            } else {
//                System.exit(0);
//                ContextUtils.exitApp(MainActivity.this);
                //不真正退出程序，模拟home键操作
                ContextUtils.clickHome(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static final String KEY_EXIT_APP = "key_exitapp";
    public static final String KEY_LOGOUT = "key_logout";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExitApp = intent.getBooleanExtra(KEY_EXIT_APP, false);
            if (isExitApp) {
                finish();
            }

            boolean isLogout = intent.getBooleanExtra(KEY_LOGOUT, false);
//            if (isLogout || !ContextUtils.isLogin(this)) {
            if (isLogout) {
//                IMUtil.logoutIM();
//                ContextUtils.closeUmengPush(this);
                UmUtil.removeAllNotification(this);
//                Intent in = new Intent(this, LoginActivity.class);
                Intent in = new Intent(this, SycCodeLoginActivity.class);
                startActivity(in);
                finish();
            }
            int position = intent.getIntExtra(KEY_NEED_SWITCH_POSITION, 0);
            if (position == 1) {
                int orderFragmentPosition = intent.getIntExtra(KEY_SWITCH_ORDERFRAGMENT_POSITION, 0);
                if (mFragments[1] != null) {
                    OrderFragment orderFragment = (OrderFragment) mFragments[1];
                    orderFragment.setCurrentItemPosition(orderFragmentPosition);
                }
            }
            if (position < mFragments.length) {
                mBottomBar.setCurrentItem(position);
            }
            getIntentFromIm(intent);
            getIntentDataFromNotif(intent);
        }
    }

    private void getIntentFromIm(Intent intent) {
        // 可以获取消息的发送者，跳转到指定的单聊、群聊界面。
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages != null && messages.size() > 0) {
            IMMessage message = messages.get(0);
            String account = message.getFromAccount();
            mBottomBar.setCurrentItem(2);
            if (!TextUtils.isEmpty(account)) {
                openMsg(account);
            }
        }
    }

    private void checkIMState() {
        UserInfo userInfo = SFUtil.getInstance().getUserInfo(this);
        String imAccount = userInfo.getimAccount();
        String imToken = userInfo.getImToken();
        if (TextUtils.isEmpty(imAccount) || TextUtils.isEmpty(imToken)) {
            doGetIcloudToken();
        } else {
            StatusCode status = NIMClient.getStatus();
            switch (status) {
                case LOGINING:
                case SYNCING:
                case LOGINED:
                    break;
                default:
                    LoginInfo mLogInfo = IMUtil.loginInfo(MainActivity.this, imAccount, imToken);
                    doLogIMCloud(mLogInfo);
                    break;
            }

        }
    }

    private void doGetIcloudToken() {
        LoginApi.getInstance().getIMcloudLoginInfo()
                .compose(RxSchedulers.<Response<BaseBean<IMLogInfo>>>applySchedulers())
                .compose(this.<Response<BaseBean<IMLogInfo>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<IMLogInfo>>(this) {
                    @Override
                    public void success(BaseBean<IMLogInfo> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            IMLogInfo logInfo = result.getResult();
                            LoginInfo mLogInfo = IMUtil.loginInfo(MainActivity.this, logInfo.getAccid(), logInfo.getToken());
                            doLogIMCloud(mLogInfo);
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });
    }

    private void doLogIMCloud(LoginInfo mLogInfo) {
        AbortableFuture abortableFuture = IMUtil.loginIM(mLogInfo, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(final LoginInfo loginInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo userInfo = SFUtil.getInstance().getUserInfo(MainActivity.this);
                        userInfo.setimAccount(loginInfo.getAccount());
                        userInfo.setImToken(loginInfo.getToken());
                        SFUtil.getInstance().setUserInfo(MainActivity.this, userInfo);
//                        IMUtil.initIMSDK(MainActivity.this, loginInfo);
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

    private void stopTimeTaskForRemainTimeRefresh() {
        if (mDisposeTimeTask != null)
            mDisposeTimeTask.dispose();
        isTimeTaskRuning = false;
    }

    private void startTimeTaskForRemainTimeRefresh() {
        if (isTimeTaskRuning) {
            return;
        }
        isTimeTaskRuning = true;
        if (mDisposeTimeTask != null)
            mDisposeTimeTask.dispose();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.MINUTE, 1);
        long targetTime = currentCalendar.getTimeInMillis();
        long initDelay = (targetTime - System.currentTimeMillis());
        Observable.interval(initDelay, 22 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposeTimeTask = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        OrderUtil.refreshOrderWaitServiceAndServicing();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void stopLoopWaitService() {
        if (mLoopDisposeWaitService != null)
            mLoopDisposeWaitService.dispose();
        isLoopingWaitService = false;
    }

    private void startLoopWaitService() {
        if (isLoopingWaitService) {
            return;
        }
        if (mLoopDisposeWaitService != null)
            mLoopDisposeWaitService.dispose();
        isLoopingWaitService = true;
        Observable.interval(8, 10, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoopDisposeWaitService = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.error("Loop Wait service looping.....");
                        List<String> orderList = SFUtil.getInstance().getOrderLoopListWaitService(MainActivity.this);
                        if (orderList != null && orderList.size() > 0) {
                            LogUtil.error("Loop send service size :" + orderList.size());
                            for (int i = 0; i < orderList.size(); i++) {
                                String orderId = orderList.get(i);
                                doGetOrderArriveStateForLoopCheck(orderId);
                            }
                        } else {
                            if (mLoopDisposeWaitService != null)
                                mLoopDisposeWaitService.dispose();
                            isLoopingWaitService = false;
                            LogUtil.error("Loop Wait service stop !!!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void stopLoopWaitReceive() {
        if (mLoopDisposeWaitReceive != null)
            mLoopDisposeWaitReceive.dispose();
        isLoopingWaitReceive = false;
    }

    private void startLoopWaitReceive() {
        if (isLoopingWaitReceive) {
            return;
        }
        if (mLoopDisposeWaitReceive != null)
            mLoopDisposeWaitReceive.dispose();
        isLoopingWaitReceive = true;
        Observable.interval(5, 10, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoopDisposeWaitReceive = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.error("Loop Wait Receive looping.....");
                        List<String> orderList = SFUtil.getInstance().getOrderLoopListWaitReceive(MainActivity.this);
                        if (orderList != null && orderList.size() > 0) {
                            LogUtil.error("Loop send orderDetaiPost size :" + orderList.size());
                            for (int i = 0; i < orderList.size(); i++) {
                                String orderId = orderList.get(i);
                                doGetOrderStateForLoopCheck(orderId);
                            }
                        } else {
                            if (mLoopDisposeWaitReceive != null)
                                mLoopDisposeWaitReceive.dispose();
                            isLoopingWaitReceive = false;
                            LogUtil.error("Loop Wait Receive stop !!!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void doGetOrderStateForLoopCheck(String orderId) {
        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            if (bean != null && !TextUtils.isEmpty(bean.getState())) {
                                operateDifStateWaitReceive(bean.getState(), bean.getId());
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });
    }

    private void doGetOrderArriveStateForLoopCheck(String orderId) {
        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            if (bean != null) {
                                String arriveState = bean.getArrivedState();
                                if (TextUtils.equals(arriveState, Constants.ORDER_WAIT_SERVICE_ARRIVE)) {
                                    OrderUtil.addOrderWaitService(bean.getId());
                                    RedPointUtil.showOrderDot(1);
                                    RedPointUtil.showBottomDot(1);
//                                    ToastUtil.getInstance().showLong(MainActivity.this, "陪诊员已到达！");
                                    OrderUtil.waiterHasArrived(bean.getId());
                                    boolean isNeedStop = SFUtil.getInstance().removeLoopWaitService(MainActivity.this, bean.getId());
                                    if (isNeedStop) {
                                        stopLoopWaitService();
                                    }
                                    refreshNotifNoticeInMsgFragment();
                                }
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });
    }


    private void operateDifStateWaitReceive(String state, String orderId) {
        if (TextUtils.equals(state, Constants.ORDER_STATE_TAKE)) {
            SFUtil.getInstance().addOrderLoopWaitService(MainActivity.this, orderId);
            startLoopWaitService();
            boolean isNeedStop = SFUtil.getInstance().removeLoopWaitReceive(this, orderId);
            if (isNeedStop) {
                if (mLoopDisposeWaitReceive != null)
                    mLoopDisposeWaitReceive.dispose();
                isLoopingWaitReceive = false;
                LogUtil.error("Loop Wait Receive stop !!!");
            }
            OrderUtil.addOrderWaitService(orderId);
            OrderUtil.deleteOrderWaitReceive(orderId);
            RedPointUtil.showOrderDot(1);
            RedPointUtil.showBottomDot(1);
            refreshNotifNoticeInMsgFragment();
        }
    }

    private void doGetRenewList() {
        OrderApi.getInstance().getRenewList()
                .compose(RxSchedulers.<Response<BaseBean<List<RenewDetail>>>>applySchedulers())
                .compose(this.<Response<BaseBean<List<RenewDetail>>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<List<RenewDetail>>>(this) {
                    @Override
                    public void success(BaseBean<List<RenewDetail>> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            stopLoopRenewReply();
                            SFUtil.getInstance().clearAllLoopRenewReply(MainActivity.this);
                            List<RenewDetail> list = result.getResult();
                            if (list != null && list.size() > 0) {
                                List<String> loopList = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    RenewDetail bean = list.get(i);
                                    loopList.add(bean.getId());
                                }
                                SFUtil.getInstance().addListOrderLoopRenew(MainActivity.this, loopList);
                                startLoopRenewReply();
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        startLoopRenewReply();
                    }
                });
    }

    /**
     * 获取等待接单的首页列表数据 加入轮询队列
     */
    private void doGetWaitReceiveListFirstPage() {
        OrderListParam param = new OrderListParam();
        param.setState(Constants.ORDER_STATE_PAID);
        param.setEveState("");
        OrderApi.getInstance().getOrderList(1, 10, param)
                .compose(RxSchedulers.<Response<BaseBean<OrderListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderListResult>>(this) {
                    @Override
                    public void success(BaseBean<OrderListResult> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            stopLoopWaitReceive();
                            SFUtil.getInstance().clearAllLoopWaitReceive(MainActivity.this);
                            OrderListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<OrderDetailBean> list = listResult.getList();
                                if (list != null && list.size() > 0) {
                                    List<String> loopList = new ArrayList<>();
                                    for (int i = 0; i < list.size(); i++) {
                                        OrderDetailBean bean = list.get(i);
                                        loopList.add(bean.getId());
                                    }
                                    SFUtil.getInstance().addListOrderLoopWaitReceive(MainActivity.this, loopList);
                                    startLoopWaitReceive();
                                }
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        startLoopWaitReceive();
                    }
                });
    }

    /**
     * 获取等待服务的首页列表数据 加入轮询队列
     */
    private void doGetWaitServiceListFirstPage() {
        OrderListParam param = new OrderListParam();
        param.setState(Constants.ORDER_STATE_TAKE);
        param.setEveState("");
        OrderApi.getInstance().getOrderList(1, 10, param)
                .compose(RxSchedulers.<Response<BaseBean<OrderListResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderListResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderListResult>>(this) {
                    @Override
                    public void success(BaseBean<OrderListResult> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            stopLoopWaitService();
                            SFUtil.getInstance().clearAllLoopWaitService(MainActivity.this);
                            OrderListResult listResult = result.getResult();
                            if (listResult != null) {
                                List<OrderDetailBean> list = listResult.getList();
                                if (list != null && list.size() > 0) {
                                    List<String> loopList = new ArrayList<>();
                                    for (int i = 0; i < list.size(); i++) {
                                        OrderDetailBean bean = list.get(i);
                                        if (!TextUtils.equals(Constants.ORDER_WAIT_SERVICE_ARRIVE, bean.getArrivedState()))
                                            loopList.add(bean.getId());
                                    }
                                    SFUtil.getInstance().addListOrderLoopWaitService(MainActivity.this, loopList);
                                    if (loopList.size() > 0)
                                        startLoopWaitService();
                                }
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        startLoopWaitReceive();
                    }
                });
    }

    private void stopLoopRenewReply() {
        if (mLoopDisposeRenew != null)
            mLoopDisposeRenew.dispose();
        isLoopingRenewReply = false;
    }

    private void startLoopRenewReply() {
        if (isLoopingRenewReply) {
            return;
        }
        if (mLoopDisposeRenew != null)
            mLoopDisposeRenew.dispose();
        isLoopingRenewReply = true;
        Observable.interval(10, 15, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoopDisposeRenew = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.error("Loop renew reply looping.....");
                        List<String> renewList = SFUtil.getInstance().getRenewLoopList(MainActivity.this);
                        if (renewList != null && renewList.size() > 0) {
                            LogUtil.error("Loop renew send size :" + renewList.size());
                            for (int i = 0; i < renewList.size(); i++) {
                                String renewId = renewList.get(i);
                                doGetRenewDetail(renewId);
                            }
                        } else {
                            if (mLoopDisposeRenew != null)
                                mLoopDisposeRenew.dispose();
                            isLoopingRenewReply = false;
                            LogUtil.error("Loop renew reply stop !!!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void doGetRenewDetail(String renewId) {
        OrderApi.getInstance().getRenewDetail(renewId)
                .compose(RxSchedulers.<Response<BaseBean<RenewDetail>>>applySchedulers())
                .compose(this.<Response<BaseBean<RenewDetail>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<RenewDetail>>(this) {
                    @Override
                    public void success(BaseBean<RenewDetail> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            RenewDetail renewDetail = result.getResult();
                            operateDifStateRenew(renewDetail);
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {

                    }
                });
    }

    private void operateDifStateRenew(RenewDetail renewDetail) {
        String state = renewDetail.getState();
        String orderId = renewDetail.getOrderId();
        String renewId = renewDetail.getId();

        if (TextUtils.equals(state, Constants.ORDER_RENEW_AGREE)) {
            OrderUtil.addOrderServicing(orderId);
            RedPointUtil.showOrderDot(2);
//            showRenewReplyDialog(renewDetail);
            String tipAgree = "您好，陪诊员已同意您的延时服务申请。";
            showApplyAgreeDialog(renewDetail.getOrderId(), renewDetail.getOrderCode(), tipAgree);
            UmUtil.removeNotification(this, UmUtil.getPushNotifId(renewDetail.getOrderId()));
            refreshNotifNoticeInMsgFragment();
        } else if (TextUtils.equals(state, Constants.ORDER_RENEW_REJECT)) {
//            showRenewReplyDialog(renewDetail);
            String tipDeny = "抱歉，陪诊员拒绝了你的延时申请。";
            showApplyAgreeDialog(renewDetail.getOrderId(), renewDetail.getOrderCode(), tipDeny);
            UmUtil.removeNotification(this, UmUtil.getPushNotifId(renewDetail.getOrderId()));
            refreshNotifNoticeInMsgFragment();
        }
        if (!TextUtils.equals(state, Constants.ORDER_RENEW_PAID)) {
            boolean isNeedStop = SFUtil.getInstance().removeLoopRenewReply(this, renewId);
            if (isNeedStop) {
                stopLoopRenewReply();
            }
            refreshNotifNoticeInMsgFragment();
        }
    }

    private void showRenewReplyDialog(RenewDetail renewDetail) {
        RenewReplyDialog dialog = DialogHelper.getRenewReplyDialog(MainActivity.this, renewDetail, new RenewReplyDialog.onToOrderDetailListener() {
            @Override
            public void toDetail(String orderId) {
                OrderDetailServicing.activityStart(MainActivity.this, orderId);
            }
        });
        dialog.show();
    }

    private void getIntentDataFromNotif(Intent intent) {
        boolean isFromNotif = intent.getBooleanExtra(Constants.KEY_INTENT_FROM_NOTIF, false);
        if (isFromNotif) {
            boolean isLocal = intent.getBooleanExtra(Constants.KEY_UMENG_IS_LOCAL, true);
            PushMessage pushMessage = (PushMessage) intent.getSerializableExtra(Constants.KEY_UMENG_PUSHMSG);

            if (isLocal) {

            } else {
                if (pushMessage != null) {
                    String orderId = pushMessage.getOrderID();
                    if (!TextUtils.isEmpty(orderId)) {
                        String renewId = pushMessage.getId();
                        String pushType = pushMessage.getPushType();
                        String content = pushMessage.getText();
                        doGetOrderDetailForNotif(orderId, pushType, content, renewId);
                    }
                }

            }
        }

    }

    private void doGetOrderDetailForNotif(String orderId, final String pushType, final String content, final String renewId) {

        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>(this) {

                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            if (bean != null) {
                                String state = bean.getState();
                                if (TextUtils.equals(state, Constants.ORDER_STATE_PAID) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_TIMEOUT)) {
                                    // 已超过20分钟未接单
                                    showTimeOutDialog(bean, content);
                                    return;
                                }
                                if (TextUtils.equals(state, Constants.ORDER_STATE_TAKE) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_RECEIPT)) {
                                    // 已接单，请保持通讯畅通
                                    showReceiptDialog(bean, content);
                                    doGetWaitReceiveListFirstPage();
                                    OrderUtil.addOrderWaitService(bean.getId());
                                    OrderUtil.deleteOrderWaitReceive(bean.getId());
                                    RedPointUtil.showOrderDot(1);
                                    return;
                                }
                                if (TextUtils.equals(state, Constants.ORDER_STATE_TAKE) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_ARRIVED)) {
                                    // 陪诊员已确认到达，请尽快确认并开始服务
                                    showArrivedDialog(bean, content);
//                                    doGetRenewList();
                                    OrderUtil.addOrderWaitService(bean.getId());
                                    RedPointUtil.showOrderDot(1);
                                    return;
                                }

                                if (TextUtils.equals(state, Constants.ORDER_STATE_TAKE) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_HEAD_OF_TIME)) {
                                    // 请尽快到达指定地点并开始服务
                                    showHeadTimeDialog(bean, content);
                                    return;
                                }
                                if (TextUtils.equals(state, Constants.ORDER_STATE_SERVICE) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_OVERTIME)) {
                                    // 距离服务结束还有20分钟
                                    showOverTimeDialog(bean, content);
                                    return;
                                }
                                if (TextUtils.equals(state, Constants.ORDER_STATE_SERVICE) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_APPLY_AGREE)) {
                                    // 陪诊员已同意您的延时服务申请
                                    if (!TextUtils.isEmpty(renewId)) {
                                        stopLoopRenewReply();
                                        SFUtil.getInstance().removeLoopRenewReply(MainActivity.this, renewId);
                                        startLoopRenewReply();
                                    }
                                    showApplyDenyDialog(bean.getId(), bean.getCode(), content);
                                    OrderUtil.addOrderServicing(bean.getId());
                                    return;

                                }
                                if (TextUtils.equals(state, Constants.ORDER_STATE_SERVICE) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_APPLY_REFUSE)) {
                                    // 陪诊员拒绝了你的延时申请
                                    if (!TextUtils.isEmpty(renewId)) {
                                        stopLoopRenewReply();
                                        SFUtil.getInstance().removeLoopRenewReply(MainActivity.this, renewId);
                                        startLoopRenewReply();
                                    }
                                    showApplyDenyDialog(bean.getId(), bean.getCode(), content);
                                    return;
                                }
                                if (TextUtils.equals(state, Constants.ORDER_STATE_CANCEL) && TextUtils.equals(pushType, Constants.NOTIF_ORDER_STATE_WAITER_CANCEL)) {
                                    // 陪诊员繁忙，暂无人接单 ,已被取消
                                    showAutoCancelDialog(bean, content);
                                    return;
                                }
                                showOtherDialog(bean, content);
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {

                    }
                });
    }

    private void showOtherDialog(OrderDetailBean bean, String content) {
        OrderTipDialogNew dialog = DialogHelper.getOrderTipDialogNew(MainActivity.this, bean, content, new OrderTipDialogNew.toSeeListener() {

            @Override
            public void toNext(OrderDetailBean bean) {
            }
        });
        dialog.show();
    }

    private void showHeadTimeDialog(OrderDetailBean bean, String content) {
        OrderTipDialogNew dialog = DialogHelper.getOrderTipDialogNew(MainActivity.this, bean, content, new OrderTipDialogNew.toSeeListener() {

            @Override
            public void toNext(OrderDetailBean bean) {
                OrderDetailWaitService.activityStart(MainActivity.this, bean);
            }
        });
        dialog.show();
    }

    private void showAutoCancelDialog(OrderDetailBean bean, String content) {
        OrderTipDialogNew dialog = DialogHelper.getOrderTipDialogNew(MainActivity.this, bean, content, new OrderTipDialogNew.toSeeListener() {

            @Override
            public void toNext(OrderDetailBean bean) {
                UmUtil.removeNotification(MainActivity.this, UmUtil.getPushNotifId(bean.getId()));
            }
        });
        dialog.show();
    }

    private void showApplyDenyDialog(String orderId, String orderCode, String content) {
//        OrderTipDialog applyDenyDialog = DialogHelper.getOrderTipDialog(MainActivity.this, bean, content, new OrderTipDialog.goToDetailListener() {
//            @Override
//            public void toDetail(OrderDetailBean bean) {
//                OrderDetailServicing.activityStart(MainActivity.this, bean);
//            }
//        });
//        applyDenyDialog.show();
        if (mRenewReplyDialog != null && mRenewReplyDialog.isShowing()) {
            mRenewReplyDialog.dismiss();
        }
        RenewReplyDialogNew applyDenyDialog = DialogHelper.getOrderRenewReplyNew(MainActivity.this, orderId, orderCode, content, new RenewReplyDialogNew.toSeeListener() {
            @Override
            public void toNext(String id) {
                OrderDetailServicing.activityStart(MainActivity.this, id);
                mRenewReplyDialog = null;
            }
        });
        applyDenyDialog.show();
        mRenewReplyDialog = applyDenyDialog;
    }

    private void showApplyAgreeDialog(String orderId, String orderCode, String content) {
//        OrderTipDialog applyAgreeDialog = DialogHelper.getOrderTipDialog(MainActivity.this, bean, content, new OrderTipDialog.goToDetailListener() {
//            @Override
//            public void toDetail(OrderDetailBean bean) {
//                OrderDetailServicing.activityStart(MainActivity.this, bean);
//            }
//        });
//        applyAgreeDialog.show();
        if (mRenewReplyDialog != null && mRenewReplyDialog.isShowing()) {
            mRenewReplyDialog.dismiss();
        }
        RenewReplyDialogNew applyAgreeDialog = DialogHelper.getOrderRenewReplyNew(MainActivity.this, orderId, orderCode, content, new RenewReplyDialogNew.toSeeListener() {
            @Override
            public void toNext(String id) {
                OrderDetailServicing.activityStart(MainActivity.this, id);
                mRenewReplyDialog = null;
            }
        });
        applyAgreeDialog.show();
        mRenewReplyDialog = applyAgreeDialog;

    }

    private void showOverTimeDialog(OrderDetailBean bean, String content) {
        OrderTipDialogNew overTimeDialog = DialogHelper.getOrderTipDialogNew(MainActivity.this, bean, content, new OrderTipDialogNew.toSeeListener() {
            @Override
            public void toNext(OrderDetailBean bean) {
                OrderDetailServicing.activityStart(MainActivity.this, bean);
            }
        });
        overTimeDialog.show();
    }

    private void showArrivedDialog(OrderDetailBean bean, String content) {
        OrderTipDialogNew arriveDialog = DialogHelper.getOrderTipDialogNew(MainActivity.this, bean, content, new OrderTipDialogNew.toSeeListener() {
            @Override
            public void toNext(OrderDetailBean bean) {
                OrderDetailWaitService.activityStart(MainActivity.this, bean);
            }
        });
        arriveDialog.show();
    }

    private void showReceiptDialog(OrderDetailBean bean, String content) {
        OrderTipDialogNew receiptDialog = DialogHelper.getOrderTipDialogNew(MainActivity.this, bean, content, new OrderTipDialogNew.toSeeListener() {
            @Override
            public void toNext(OrderDetailBean bean) {
                OrderDetailWaitService.activityStart(MainActivity.this, bean);
            }
        });
        receiptDialog.show();
    }

    private void showTimeOutDialog(OrderDetailBean bean, String content) {
        OrderTimeOutDialog timeOutDialog = DialogHelper.getOrderTimeOutDialog(MainActivity.this, bean, content, new OrderTimeOutDialog.toSeeListener() {
            @Override
            public void toNext(OrderDetailBean bean) {
                OrderWaitReceiveActivity.activityStart(MainActivity.this, bean);
            }

            @Override
            public void toCancel(OrderDetailBean bean) {
//                showCancelOrderDialog(bean);
                doCancelOrder(bean);
            }
        });
        timeOutDialog.show();
    }

    private void showCancelOrderDialog(final OrderDetailBean bean) {
        final MaterialDialog cancelOrderDialog = DialogHelper.getMaterialDialog(this, "确定取消订单?");
        cancelOrderDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                cancelOrderDialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                cancelOrderDialog.dismiss();
                doCancelOrder(bean);
            }
        });
        cancelOrderDialog.show();
    }

    private void doCancelOrder(OrderDetailBean bean) {
        CancelOrderParam cancelOrderParam = new CancelOrderParam();
        cancelOrderParam.setId(bean.getId());
        cancelOrderParam.setCancelType(ApiContents.USER_TYPE);
        cancelOrderParam.setCancelReason("");
        OrderApi.getInstance().cancelOrder(cancelOrderParam)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
//                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(MainActivity.this, "订单取消成功");
                        } else {
                            ToastUtil.getInstance().showShort(MainActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_TIME_TASK_START, mode = ThreadMode.MAIN)
    private void startTimeTask(String defaultContent) {
        LogUtil.error("EventBus startTimeTask Main");
        startTimeTaskForRemainTimeRefresh();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_TIME_TASK_STOP, mode = ThreadMode.MAIN)
    private void stopTimeTask(String defaultContent) {
        LogUtil.error("EventBus stopTimeTask Main");
        stopTimeTaskForRemainTimeRefresh();
    }


    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_RECEIVE_START, mode = ThreadMode.MAIN)
    private void startLoopWaitReceive(String defaultContent) {
        LogUtil.error("EventBus startLoopWaitReceive Main");
        startLoopWaitReceive();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_RECEIVE_STOP, mode = ThreadMode.MAIN)
    private void stopLoopWaitReceive(String defaultContent) {
        LogUtil.error("EventBus stopLoopWaitReceive Main");
        stopLoopWaitReceive();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_ARRIVE_START, mode = ThreadMode.MAIN)
    private void startLoopWaitService(String defaultContent) {
        LogUtil.error("EventBus startLoopWaitReceive Main");
        startLoopWaitService();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_ARRIVE_STOP, mode = ThreadMode.MAIN)
    private void stopLoopWaitService(String defaultContent) {
        LogUtil.error("EventBus stopLoopWaitReceive Main");
        stopLoopWaitService();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_LOOP_RENEW_REPLY_START, mode = ThreadMode.MAIN)
    private void startLoopRenewReply(String defaultContent) {
        LogUtil.error("EventBus startLoopRenewReply Main");
        startLoopRenewReply();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_MAIN_LOOP_RENEW_REPLY_STOP, mode = ThreadMode.MAIN)
    private void stopLoopRenewReply(String defaultContent) {
        LogUtil.error("EventBus stopLoopRenewReply Main");
        stopLoopRenewReply();
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_GET_MSG_NOTICE, mode = ThreadMode.MAIN)
    private void getNoticeInMsg(String defaultContent) {
        LogUtil.error("EventBus getNoticeInMsg Main");
        refreshNotifNoticeInMsgFragment();
    }

    @Override
    protected void onDestroy() {
        if (mDisposeTimeTask != null)
            mDisposeTimeTask.dispose();
        if (mLoopDisposeWaitReceive != null)
            mLoopDisposeWaitReceive.dispose();
        if (mLoopDisposeWaitService != null)
            mLoopDisposeWaitService.dispose();
        if (mLoopDisposeRenew != null)
            mLoopDisposeRenew.dispose();
        super.onDestroy();
        LogUtil.error("MainActivity onDestroy ");
    }
}
