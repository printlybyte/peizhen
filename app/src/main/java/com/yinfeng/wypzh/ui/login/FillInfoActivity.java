package com.yinfeng.wypzh.ui.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.view.RxView;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.SickAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.base.MyApplication;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.OssResult;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.login.FillInfoHistoryParam;
import com.yinfeng.wypzh.bean.login.FillInfoParam;
import com.yinfeng.wypzh.bean.login.UserInfoNetResult;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.bean.patient.SickListData;
import com.yinfeng.wypzh.bean.realmbean.SickBean;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.PatientApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.listener.OnGlideCacheGetListener;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.ui.dialog.CaptureAndGalleryDialog;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.utils.BarUtils;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.FileUtils;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.RealmManager;
import com.yinfeng.wypzh.utils.RegexUtils;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.SpaceItemDecoration;
import com.yinfeng.wypzh.widget.TopBar;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.realm.RealmResults;
import retrofit2.Response;

public class FillInfoActivity extends BaseActivity implements CaptureAndGalleryDialog.CaptureAndGallerySelectListener {
    private static final String TAG = FillInfoActivity.class.getSimpleName();
    TopBar mTopBar;
    SmartRefreshLayout mSmartRefreshLayout;
    EditText etName, etID;
    RelativeLayout rlChooseHeader;
    LinearLayout llMan, llWoman, llHas, llNo, llContainer;
    ImageView ivHeader, ivMan, ivWoman, ivHas, ivNo;
    Button btComplete;
    RecyclerView mRecyclerview;
    EditText etSickDesc;
    LinearLayout llHistoryAll;
    PermissionTipDialog permissionTipDialog;
    CaptureAndGalleryDialog captureAndGalleryDialog;
    SickAdapter mAdapter;
    boolean isMan = true;
    boolean isHas = true;
    List<SickBean> mList;
    String name, ID;
    String description;
    boolean isGalleryOk = false;
    boolean isCaptureOk = false;
    private int recyclerviewHeight;
    //    private String headIconImgName;
    private String headIconImgPath;
    private static final int REQUESTCODE_CAPTURE = 0x01;
    private static final int REQUESTCODE_GALLERY = 0x02;
    public static final String KEY_BOOLEAN_ISEDIT = "key_isedit";
    RequestOptions imageOptions;
    private List<String> choosedSickList = new ArrayList<>();
    private boolean isGetSickTips = false;//正在获取病例信息
    private boolean isCompleting = false;
    private boolean isOperating = false;//正在执行showAndCheckImgTypeAndSaveImg方法
    private String uploadFilePath;
    private String uploadFileNameWithSuffix;
    private OSS oss;
    private OSSAsyncTask task;
    private String ossObjKey;
    private String headImgUrl;
    private boolean isEdit;
    private OssResult ossResult;
    private UserInfo savedInfo;
    private boolean isGetUserInfoing = false;
    private boolean hasSelectedLocalPic = false;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        BarUtils.setStatusBarVisibility(FillInfoActivity.this,false);

        initRealm();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("完善个人信息");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                cancelToNextPage();
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        etName = mRootView.findViewById(R.id.etName);
        etID = mRootView.findViewById(R.id.etID);
        rlChooseHeader = mRootView.findViewById(R.id.rlChooseHeadIcon);
        ivHeader = mRootView.findViewById(R.id.ivheader);
        llMan = mRootView.findViewById(R.id.llGenderMan);
        llWoman = mRootView.findViewById(R.id.llGenderWoman);
        ivMan = mRootView.findViewById(R.id.ivGenderMan);
        ivWoman = mRootView.findViewById(R.id.ivGenderWoman);
        llHas = mRootView.findViewById(R.id.llHistoryHas);
        llNo = mRootView.findViewById(R.id.llHistoryNo);
        ivHas = mRootView.findViewById(R.id.ivHistoryHas);
        ivNo = mRootView.findViewById(R.id.ivHistoryNo);
        llContainer = mRootView.findViewById(R.id.llTipsContainer);
        btComplete = mRootView.findViewById(R.id.btComplete);
        mRecyclerview = mRootView.findViewById(R.id.mRecyclerView);
        etSickDesc = mRootView.findViewById(R.id.etSickDescription);
        llHistoryAll = mRootView.findViewById(R.id.llHistoryAll);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        etSickDesc.setVisibility(View.GONE);
        etName.clearFocus();
        initGender();
        initHistoryHasOrNot();
        initSickListView();
//        resetRelativeViewParams();
    }

    private void resetRelativeViewParams(boolean isEditVisible) {
        if (mList.size() > 0) {
            int space = mAdapter.getSpace();
            int hangNum = getRecyclerViewHangNumb();
            float itemHeightDimen = getResources().getDimension(R.dimen.item_sick_height);
            int itemHeight = Float.valueOf(itemHeightDimen).intValue();
            recyclerviewHeight = (itemHeight + space) * hangNum;
            LinearLayout.LayoutParams recyclerviewParam = (LinearLayout.LayoutParams) mRecyclerview.getLayoutParams();
            recyclerviewParam.height = recyclerviewHeight;
            mRecyclerview.setLayoutParams(recyclerviewParam);

            LinearLayout.LayoutParams containParams = (LinearLayout.LayoutParams) llContainer.getLayoutParams();
            float etDescripHeightDimen = getResources().getDimension(R.dimen.addpatient_etdescription_height);
            int etDescripHeight = Float.valueOf(etDescripHeightDimen).intValue();
            if (isEditVisible) {
                containParams.height = recyclerviewHeight + etDescripHeight + ContextUtils.dip2px(this, 8);
            } else {
                containParams.height = recyclerviewHeight + ContextUtils.dip2px(this, 2);
            }
            llContainer.setLayoutParams(containParams);
        }
    }

    private int getRecyclerViewHangNumb() {
        int spanNum = mAdapter.getSpanNum();

        if (mList.size() % spanNum == 0) {
            return mList.size() / spanNum;
        } else {
            return mList.size() / spanNum + 1;
        }
    }

    private void initSickListView() {
        int space = ContextUtils.dip2px(this, 8);
        int spanNum = 4;
        mAdapter = new SickAdapter(this, mList);
        mAdapter.setSpaceAndSpanNumb(space, spanNum, 48);
        setEmptyViewLoadingCircle();
        GridLayoutManager manager = new GridLayoutManager(this, spanNum);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(space);
        mRecyclerview.setLayoutManager(manager);
        mRecyclerview.addItemDecoration(itemDecoration);
        mRecyclerview.setAdapter(mAdapter);
    }

    private void initHistoryHasOrNot() {
        if (isHas) {
            ivHas.setImageResource(R.drawable.selected_oval);
            ivNo.setImageResource(R.drawable.unselect_oval);
            llContainer.setVisibility(View.VISIBLE);
        } else {
            ivHas.setImageResource(R.drawable.unselect_oval);
            ivNo.setImageResource(R.drawable.selected_oval);
            llContainer.setVisibility(View.GONE);
        }
    }

    private void initGender() {
        if (isMan) {
            ivMan.setImageResource(R.drawable.selected_oval);
            ivWoman.setImageResource(R.drawable.unselect_oval);
        } else {
            ivMan.setImageResource(R.drawable.unselect_oval);
            ivWoman.setImageResource(R.drawable.selected_oval);
        }
    }

    private void cancelToNextPage() {
        finish();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int count = mAdapter.getItemCount();
                boolean isSelected = mList.get(position).isSelected();
                if (isSelected) {
                    if (position == count - 1) {
                        etSickDesc.setVisibility(View.GONE);
                        etSickDesc.clearFocus();
                        ContextUtils.hideSoftInput(FillInfoActivity.this);
                        resetRelativeViewParams(false);
                    }
                    mList.get(position).setSelected(false);
                } else {
                    if (position == count - 1) {
                        etSickDesc.setVisibility(View.VISIBLE);
                        etSickDesc.findFocus();
                        etSickDesc.requestFocus();
                        etSickDesc.setFocusable(true);
                        etSickDesc.setFocusableInTouchMode(true);
                        ContextUtils.showSoftInput(FillInfoActivity.this, etSickDesc);
                        resetRelativeViewParams(true);
                    }
                    mList.get(position).setSelected(true);
                }
                String name = mList.get(position).getName();
                boolean isChoosed = mList.get(position).isSelected();
                if (choosedSickList.contains(name)) {
                    if (!isChoosed)
                        choosedSickList.remove(name);
                } else {
                    if (isChoosed) {
                        choosedSickList.add(name);
                    }
                }
                mAdapter.setData(position, mList.get(position));
            }
        });

        etSickDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtils.showSoftInput(FillInfoActivity.this, etSickDesc);
            }
        });
        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtils.showSoftInput(FillInfoActivity.this, etSickDesc);
            }
        });
        etID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtils.showSoftInput(FillInfoActivity.this, etSickDesc);
            }
        });

        llMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMan = true;
                ivMan.setImageResource(R.drawable.selected_oval);
                ivWoman.setImageResource(R.drawable.unselect_oval);
            }
        });

        llWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMan = false;
                ivMan.setImageResource(R.drawable.unselect_oval);
                ivWoman.setImageResource(R.drawable.selected_oval);
            }
        });


        llHas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHas = true;
                ivHas.setImageResource(R.drawable.selected_oval);
                ivNo.setImageResource(R.drawable.unselect_oval);
                llContainer.setVisibility(View.VISIBLE);
            }
        });

        llNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckBoxNo();
            }
        });


        RxView.clicks(btComplete).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (checkParamsValid()) {
                            checkCanDoNextOperate();
//                            doCompleteInfo();
                            if (checkCanCommitRequest()) {
//                                if (TextUtils.isEmpty(headImgUrl) || headImgUrl.contains(Constants.OSS_ROOT_PATH)) {
                                if (!hasSelectedLocalPic) {
                                    doCompleteInfo(false);
                                } else {
                                    doGetOssToken();
                                }
                            }
                        }
                    }
                });

        RxView.clicks(rlChooseHeader).

                throttleFirst(500, TimeUnit.MILLISECONDS)
                .

                        subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) {
                                checkCanDoNextOperate();
                                requestPermissions();
                            }
                        });
    }

    /**
     * 添加602 过滤选项  默认为空数据  后期优化 radiobutton代替image
     */
    private void setCheckBoxNo() {
        isHas = false;
        ivHas.setImageResource(R.drawable.unselect_oval);
        ivNo.setImageResource(R.drawable.selected_oval);
        llContainer.setVisibility(View.GONE);
    }


    private boolean checkCanCommitRequest() {
        if (isEdit) {
            if (savedInfo == null || TextUtils.isEmpty(savedInfo.getName())) {
                showRetryUserInfoDialog();
                return false;
            }
        }
        return true;
    }

    private void doGetOssToken() {
        if (isCompleting) {
            showLoadingDialog();
            return;
        }
        showLoadingDialog();
        isCompleting = true;
        LoginApi.getInstance().getOssTokens()
                .compose(RxSchedulers.<Response<BaseBean<OssResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<OssResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OssResult>>(FillInfoActivity.this) {
                    @Override
                    public void success(BaseBean<OssResult> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ossResult = result.getResult();
                            oss = initOssManager();
                            doUploadPhote(uploadFilePath, uploadFileNameWithSuffix);
                        } else {
                            isCompleting = false;
                            hideLoadingDialog();
                            ToastUtil.getInstance().showShort(FillInfoActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isCompleting = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void doUploadPhote(String filePath, String fileName) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        String timeStamp = formatter.format(Calendar.getInstance().getTime());
        ossObjKey = timeStamp + "/" + fileName;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Constants.OSS_BUCKET_NAME, ossObjKey, filePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtil.error("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                final String uploadingTip = "上传头像" + currentSize * 100 / totalSize + "%";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isCompleting) {
                            showLoadingDialog(uploadingTip);
                        } else {
                            hideLoadingDialog();
                        }
                    }
                });
            }
        });
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtil.error("PutObject", "UploadSuccess");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoadingDialog("上传资料中..");
                    }
                });
                headImgUrl = Constants.OSS_ROOT_PATH + ossObjKey;
                hasSelectedLocalPic = false;
//                ImageUtil.getInstance().changCacheKey(FillInfoActivity.this, System.currentTimeMillis());
                doCompleteInfo(true);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    ToastUtil.getInstance().showShort(FillInfoActivity.this, "请检查网络设置");
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtil.error("ErrorCode", serviceException.getErrorCode());
                    LogUtil.error("RequestId", serviceException.getRequestId());
                    LogUtil.error("HostId", serviceException.getHostId());
                    LogUtil.error("RawMessage", serviceException.getRawMessage());
                    if (!TextUtils.isEmpty(serviceException.getRawMessage()))
                        ToastUtil.getInstance().showShort(FillInfoActivity.this, serviceException.getRawMessage());

                }
                isCompleting = false;
                hideLoadingDialog();
                task.cancel();
            }
        });
// task.cancel(); // 可以取消任务
// task.waitUntilFinished(); // 可以等待直到任务完成
    }

    private OSS initOssManager() {
        OSSLog.enableLog();
        String endpoint = Constants.OSS_ENDPOINT;
//        String stsServer = Constants.OSS_STS_SERVER;
        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossResult.getAccessKeyId(), ossResult.getAccessKeySecret(), ossResult.getTokenSecret());
//        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(1); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
        return oss;
    }

    private void doCompleteInfo(boolean isNeedUpload) {
        if (!isNeedUpload) {
            if (isCompleting) {
                showLoadingDialog("上传资料中...");
                return;
            }
            isCompleting = true;
            showLoadingDialog("上传资料中...");
        }
        FillInfoParam param = assembleParam();
        LoginApi.getInstance().fillUserInfo(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>(FillInfoActivity.this) {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(FillInfoActivity.this);
                            userInfo.setComplete(true);
                            userInfo.setSex(isMan ? "0" : "1");
                            userInfo.setIsHistory(isHas ? "1" : "0");
                            userInfo.setProfile(headImgUrl);
                            userInfo.setIdcard(ID);
                            userInfo.setName(name);
                            userInfo.setOtherMedical(description);
                            userInfo.setMedicalHistory(getMedicalHistory());
                            SFUtil.getInstance().setUserInfo(FillInfoActivity.this, userInfo);
                            MyApplication.getInstance().setComplete(true);
//                            ImageUtil.getInstance().changCacheKey(FillInfoActivity.this, System.currentTimeMillis());
                            EventBus.getDefault().post(userInfo, Constants.EVENTBUS_TAG_UPDATE_USERINFO);
                            if (isEdit) {
                                hideLoadingDialog();
                                isCompleting = false;
                                finish();
                            } else {
//                                Intent intent = new Intent(FillInfoActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
                                doGetUserInfoForAccountId();
                            }
                        } else {
                            hideLoadingDialog();
                            isCompleting = false;
                            ToastUtil.getInstance().showShort(FillInfoActivity.this, result.getMessage());
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isCompleting = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });

    }

    private void doGetUserInfoForAccountId() {
        LoginApi.getInstance().getUserInfo()
                .compose(RxSchedulers.<Response<BaseBean<UserInfoNetResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<UserInfoNetResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UserInfoNetResult>>() {
                    @Override
                    public void success(BaseBean<UserInfoNetResult> result) {
                        hideLoadingDialog();
                        isCompleting = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UserInfoNetResult uInfo = result.getResult();
                            PatientInfo pInfo = uInfo.getMemberPatient();

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(FillInfoActivity.this);
                            userInfo.setAccountId(uInfo.getAccountId());
                            userInfo.setName(uInfo.getName());
                            userInfo.setSex(uInfo.getSex());
                            userInfo.setLevel(uInfo.getLevel());
                            userInfo.setProfile(uInfo.getProfile());
                            userInfo.setIdcard(pInfo.getIdcard());
                            userInfo.setIsHistory(pInfo.getIsHistory());
                            userInfo.setMedicalHistory(pInfo.getMedicalHistory());
                            userInfo.setOtherMedical(pInfo.getOtherMedical());
                            SFUtil.getInstance().setUserInfo(FillInfoActivity.this, userInfo);
                        }
                        Intent intent = new Intent(FillInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isCompleting = false;
                        Intent intent = new Intent(FillInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private FillInfoParam assembleParam() {
        FillInfoParam param = new FillInfoParam();
        param.setNickname(name);
        param.setSex(isMan ? "0" : "1");
        param.setIdCard(ID);
        param.setProfile(headImgUrl);
        FillInfoHistoryParam historyParam = new FillInfoHistoryParam();
        historyParam.setIsHistory(isHas ? "1" : "0");
        historyParam.setMedicalHistory(getMedicalHistory());
        historyParam.setOtherMedical(description);
        param.setMemberPatient(historyParam);
        return param;
    }

    private String getMedicalHistory() {
        String medicalHistory = "";
        description = "";
        if (choosedSickList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < choosedSickList.size(); i++) {
                sb.append(choosedSickList.get(i) + ",");
                if (TextUtils.equals(choosedSickList.get(i), "其他"))
                    description = etSickDesc.getText().toString().trim();
            }
            String temp = sb.toString();
            medicalHistory = temp.substring(0, temp.length() - 1);
        }
        return medicalHistory;
    }

    private boolean checkParamsValid() {
        name = etName.getText().toString().trim();
        ID = etID.getText().toString().trim();
        if (etSickDesc.getVisibility() == View.VISIBLE) {
            description = etSickDesc.getText().toString().trim();
        } else {
            description = "";
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtil.getInstance().showShort(this, "请输入您的真实姓名！");
            return false;
        }
        if (TextUtils.isEmpty(ID)) {
            ToastUtil.getInstance().showShort(this, "请输入您的身份证号！");
            return false;
        } else if (!RegexUtils.checkIdCard(ID)) {
            ToastUtil.getInstance().showShort(this, "请检查您的身份证号是否正确！");
            return false;
        }
        if (isHas) {
            return checkSickParamValidWhenHas();
        } else {
            description = "";
        }
        return true;
    }

    private boolean checkSickParamValidWhenHas() {
        if (choosedSickList.size() == 0) {
            ToastUtil.getInstance().showShort(this, "请选择病历！");
            return false;
        }
        if (choosedSickList.size() > 0 && choosedSickList.contains("其他")) {
            description = etSickDesc.getText().toString().trim();
            if (TextUtils.isEmpty(description)) {
                ToastUtil.getInstance().showShort(this, "请输入病情描述！");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void initData() {
        imageOptions = ImageUtil.getInstance().getDefineOptions(80, R.drawable.head_default);
        imageOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        imageOptions.circleCrop();
        getIntentData();
    }

    private void assembleSicks() {
        getSicksFromDb();
    }

    private void getSicksFromDb() {
        mList = new ArrayList<>();
        RealmResults<SickBean> dbList = mRealm.where(SickBean.class).findAll();
        for (int i = 0; i < dbList.size(); i++) {
            mList.add(dbList.get(i));
        }
        if (isEdit) {
            resetDataListEdit();
        }
        boolean isEtDescripShouldShow = isEdit && choosedSickList.contains("其他");
        resetRelativeViewParams(isEtDescripShouldShow);
        mAdapter.setNewData(mList);
        if (isEtDescripShouldShow) {
            etSickDesc.setVisibility(View.VISIBLE);
            etSickDesc.setText(savedInfo.getOtherMedical());
        }
        doGetSickTips();
    }

    private void resetDataListEdit() {
        if (choosedSickList.size() > 0) {
            if (mList.size() > 0) {
                for (int i = 0; i < choosedSickList.size(); i++) {
                    String selectedName = choosedSickList.get(i);
                    for (int j = 0; j < mList.size(); j++) {
                        String name = mList.get(j).getName();
                        if (TextUtils.equals(selectedName, name))
                            mList.get(j).setSelected(true);
                    }

                }
            }
        }
    }

    private void doGetSickTips() {
        if (isGetSickTips) {
            return;
        }
        isGetSickTips = true;
        PatientApi.getInstance().getSickTips()
                .compose(RxSchedulers.<Response<BaseBean<SickListData>>>applySchedulers())
                .compose(this.<Response<BaseBean<SickListData>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<SickListData>>() {
                    @Override
                    public void success(BaseBean<SickListData> result) {
                        isGetSickTips = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            SickListData data = result.getResult();
                            if (data != null) {
                                List<SickBean> list = data.getList();
                                RealmManager.getInstance().saveSickListOrUpdate(mRealm, list);
                                mList = list;
                                if (isEdit) {
                                    resetDataListEdit();
                                }
                                boolean isEtDescripShouldShow = isEdit && choosedSickList.contains("其他");
                                resetRelativeViewParams(isEtDescripShouldShow);
                                mAdapter.setNewData(mList);
                                if (isEtDescripShouldShow) {
                                    etSickDesc.setVisibility(View.VISIBLE);
                                    etSickDesc.setText(savedInfo.getOtherMedical());
                                }
                                return;
                            }
                            if (mList == null || mList.size() == 0)
                                setEmptyViewNoData();
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetSickTips = false;
                        if (mList == null || mList.size() == 0)
                            setEmptyViewRetry();
                    }
                });
    }

    private void setEmptyViewLoadingCircle() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_loading_circle, null);
        mAdapter.setEmptyView(emptyView);
    }

    private void setEmptyViewRetry() {
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
        setEmptyViewLoadingCircle();
        doGetSickTips();
    }

    private void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("暂无病例！");
        mAdapter.setEmptyView(emptyView);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_fill_info;
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        cancelToNextPage();
    }

    @SuppressLint("CheckResult")
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(FillInfoActivity.this);
        rxPermission
                .requestEach(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.READ_CALENDAR,
//                        Manifest.permission.READ_CALL_LOG,
//                        Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.READ_SMS,
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.CALL_PHONE,
//                        Manifest.permission.SEND_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                                isGalleryOk = true;
                            }
                            if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
                                isCaptureOk = true;
                            }
                            if (isCaptureOk && isGalleryOk) {
                                captureAndGalleryDialog = DialogHelper.getGalleryAndCaptureDialog(FillInfoActivity.this, FillInfoActivity.this);
                                captureAndGalleryDialog.show();
                                isGalleryOk = false;
                                isCaptureOk = false;
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            LogUtil.error(TAG, permission.name + " is denied.");
                            String permissionName = "";
                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                                permissionName = "相册";
                            }
                            if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
                                permissionName = "摄像头";
                            }
                            permissionTipDialog = DialogHelper.getPermissionTipDialog(FillInfoActivity.this, permissionName);
                            permissionTipDialog.show();
                        }
                    }
                });


    }

    private String getHeadIconPath() {
        headIconImgPath = FileUtils.getFilePath(this, System.currentTimeMillis() + ".jpeg");
        return headIconImgPath;
    }


    @Override
    public void selectCapture() {
        ContextUtils.takePhoto(this, getHeadIconPath(), REQUESTCODE_CAPTURE);
    }

    @Override
    public void selectGallery() {
        ContextUtils.choosePhoto(this, REQUESTCODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri imageUri = null;
            if (requestCode == REQUESTCODE_CAPTURE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = ContextUtils.getImgUri(this, headIconImgPath);
                } else {
                    imageUri = Uri.fromFile(new File(headIconImgPath));
                }
//                ivHeader.setImageURI(imageUri);
//                ImageUtil.getInstance().loadImg(FillInfoActivity.this, imageUri, imageOptions, ivHeader);
//                ImageUtil.getInstance().changCacheKey(FillInfoActivity.this, System.currentTimeMillis());
            }
            if (requestCode == REQUESTCODE_GALLERY) {
                imageUri = data.getData();
            }
            showAndCheckImgTypeAndSaveImg(imageUri);
        }
    }

    private void showAndCheckImgTypeAndSaveImg(final Uri imageUri) {
        isOperating = true;
        showLoadingDialog("处理图片中...");
        ImageUtil.getInstance().loadImgCircle(FillInfoActivity.this, imageUri, ivHeader);
//        ImageUtil.getInstance().clearMemoryAndDiskCache(FillInfoActivity.this);
//        ImageUtil.getInstance().loadImg(FillInfoActivity.this, imageUri, imageOptions, ivHeader);
//        ImageUtil.getInstance().loadImg(FillInfoActivity.this, imageUri, imageOptions, ivHeader);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Glide.with(FillInfoActivity.this)
//                        .load(imageUri)
//                        .apply(imageOptions)
//                        .into(ivHeader);
////                ImageUtil.getInstance().loadImg(FillInfoActivity.this, imageUri, imageOptions, ivHeader);
//            }
//        }, 300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageUtil.getInstance().getCacheFilePath(FillInfoActivity.this, imageUri, ivHeader.getWidth(), ivHeader.getHeight(), new OnGlideCacheGetListener() {
                    @Override
                    public void onSuccess(String filePath, String fileNameWithSuffix, String suffix) {
                        isOperating = false;
                        hideLoadingDialog();
                        uploadFilePath = filePath;
                        uploadFileNameWithSuffix = fileNameWithSuffix;
                        headImgUrl = filePath;
                        hasSelectedLocalPic = true;
//                        ImageUtil.getInstance().loadImg(FillInfoActivity.this, filePath, imageOptions, ivHeader);
                    }

                    @Override
                    public void onFail() {
                        isOperating = false;
                        hideLoadingDialog();
                    }
                });
            }
        }, 300);

    }

    private void checkCanDoNextOperate() {
        if (isOperating) {
            showLoadingDialog("处理图片中,请稍候...");
            return;
        }
    }

    public static void activityStart(Context context, boolean isEdit) {
        Intent intent = new Intent(context, FillInfoActivity.class);
        intent.putExtra(KEY_BOOLEAN_ISEDIT, isEdit);
        context.startActivity(intent);
    }

    private void getIntentData() {
        isEdit = getIntent().getBooleanExtra(KEY_BOOLEAN_ISEDIT, false);
        if (isEdit) {
            savedInfo = SFUtil.getInstance().getUserInfo(FillInfoActivity.this);
            if (TextUtils.isEmpty(savedInfo.getName())) {
                doGetUserInfo();
            } else {
                resetView(savedInfo);
                assembleSicks();
            }
        } else {
            doGetUserInfo();
//            assembleSicks();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        doGetUserInfo();
    }

    private void doGetUserInfo() {
        if (isGetUserInfoing) {
            showLoadingDialog("获取个人信息..");
            return;
        }
        isGetUserInfoing = true;
        showLoadingDialog("获取个人信息..");
//192.168.1.143
        LoginApi.getInstance().getUserInfo()
                .compose(RxSchedulers.<Response<BaseBean<UserInfoNetResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<UserInfoNetResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UserInfoNetResult>>() {
                    @Override
                    public void success(BaseBean<UserInfoNetResult> result) {
                        isGetUserInfoing = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UserInfoNetResult uInfo = result.getResult();
                            PatientInfo pInfo = uInfo.getMemberPatient();
                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(FillInfoActivity.this);
                            userInfo.setName(uInfo.getName());
                            userInfo.setSex(uInfo.getSex());
                            userInfo.setProfile(uInfo.getProfile());
                            userInfo.setIdcard(uInfo.getIdcard());
                            userInfo.setIsHistory(pInfo.getIsHistory());
                            userInfo.setMedicalHistory(pInfo.getMedicalHistory());
                            userInfo.setOtherMedical(pInfo.getOtherMedical());
                            SFUtil.getInstance().setUserInfo(FillInfoActivity.this, userInfo);
                            savedInfo = userInfo;
                            resetView(userInfo);
                            assembleSicks();
                        } else if (result.getCode() == ApiContents.CODE_REQUEST_NO_MEMBER_INFORMATION) { //会员信息不存在  完善信息
                            setCheckBoxNo();
                        } else if (result.getCode() == ApiContents.CODE_REQUEST_ACCOUNT_EXCEPTION) { //账号异常
                            finish();
                        } else {
                            showRetryUserInfoDialog();
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isGetUserInfoing = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        showRetryUserInfoDialog();
                    }
                });

    }

    private void showRetryUserInfoDialog() {
        final MaterialDialog retryDialog = DialogHelper.getMaterialDialogOneQuick(FillInfoActivity.this, "重新获取个人信息");
        retryDialog.setCanceledOnTouchOutside(false);
        retryDialog.setCancelable(true);
        retryDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                retryDialog.dismiss();
                doGetUserInfo();
            }
        });
        retryDialog.show();

    }

    private void resetView(UserInfo info) {
        if (!TextUtils.isEmpty(info.getName()))
            etName.setText(info.getName());

        String sex = info.getSex();
        if (TextUtils.equals(sex, "0")) {
            isMan = true;
        } else {
            isMan = false;
        }
        initGender();
        if (!TextUtils.isEmpty(info.getIdcard()))
            etID.setText(info.getIdcard());
        String historyFlag = info.getIsHistory();
        if (TextUtils.equals(historyFlag, "1")) {
            isHas = true;
        } else {
            isHas = false;
        }
        initHistoryHasOrNot();
        if (isHas) {
            choosedSickList = getChoosedSickListEdit(info.getMedicalHistory());
        }
        String path = info.getProfile();
        if (!TextUtils.isEmpty(path)) {
            ImageUtil.getInstance().loadImgCircle(this, path, ivHeader);
            headImgUrl = path;
//            ImageUtil.getInstance().loadImg(this, path, imageOptions, ivHeader);
        }
    }

    private List<String> getChoosedSickListEdit(String medicalHistory) {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(medicalHistory)) {
            String[] ss = medicalHistory.split(",");
            for (int i = 0; i < ss.length; i++) {
                list.add(ss[i]);
            }
        }
        return list;
    }

}
