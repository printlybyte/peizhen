package com.yinfeng.wypzh.ui.homepage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.SickAdapter;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.patient.PatientAddParam;
import com.yinfeng.wypzh.bean.patient.PatientEditParam;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.bean.patient.SickListData;
import com.yinfeng.wypzh.bean.realmbean.SickBean;
import com.yinfeng.wypzh.http.PatientApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.RealmManager;
import com.yinfeng.wypzh.utils.RegexUtils;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.SpaceItemDecoration;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Response;

public class AddPatientActivity extends BaseActivity {

    TopBar mTopBar;
    SmartRefreshLayout mSmartRefreshLayout;
    EditText etName, etPhone, etID;
    LinearLayout llMan, llWoman, llHas, llNo, llContainer;
    ImageView ivMan, ivWoman, ivHas, ivNo;
    RecyclerView mRecyclerview;
    EditText etSickDesc;
    LinearLayout llHistoryAll;
    SickAdapter mAdapter;
    boolean isMan = true;
    boolean isHas = true;
    List<SickBean> mList;
    String name, phone, ID;
    String description;
    private List<String> choosedSickList = new ArrayList<>();
    private int recyclerviewHeight;
    private boolean isGetSickTips = false;//正在获取病例信息
    private boolean isAddPatient = false;
    private boolean isEditPatient = false;

    PatientInfo info;
    public static final String KEY_PATIENT_INFO = "KEY_INFO";
    public static final int REQUESTCODE_ADD = 0x01;
    public static final int REQUESTCODE_EDIT = 0x02;
    private boolean isAdd = true;
    private static final String KEY_REQUESTCODE = "KEY_FORM";

    /**
     * @param activity
     * @param info        新增 传null
     * @param requestCode
     */
    public static void activityStartForResult(Activity activity, PatientInfo info, int requestCode) {
        Intent intent = new Intent(activity, AddPatientActivity.class);
        intent.putExtra(KEY_PATIENT_INFO, info);
        intent.putExtra(KEY_REQUESTCODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        initRealm();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("添加就诊人");
        mTopBar.setTopRightTxt("保存");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                cancelToPrePage();
            }
        });
        mTopBar.setTopBarRightTxtListener(new TopBar.TopBarRightTextCickListener() {
            @Override
            public void topRightTxtClick() {
                saveOrEdit();
            }
        });

        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        etName = mRootView.findViewById(R.id.etName);
        etPhone = mRootView.findViewById(R.id.etPhone);
        etID = mRootView.findViewById(R.id.etID);
        llMan = mRootView.findViewById(R.id.llGenderMan);
        llWoman = mRootView.findViewById(R.id.llGenderWoman);
        ivMan = mRootView.findViewById(R.id.ivGenderMan);
        ivWoman = mRootView.findViewById(R.id.ivGenderWoman);
        llHas = mRootView.findViewById(R.id.llHistoryHas);
        llNo = mRootView.findViewById(R.id.llHistoryNo);
        ivHas = mRootView.findViewById(R.id.ivHistoryHas);
        ivNo = mRootView.findViewById(R.id.ivHistoryNo);
        llContainer = mRootView.findViewById(R.id.llTipsContainer);
        mRecyclerview = mRootView.findViewById(R.id.mRecyclerView);
        etSickDesc = mRootView.findViewById(R.id.etSickDescription);
        llHistoryAll = mRootView.findViewById(R.id.llHistoryAll);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);

        etName.clearFocus();

        initGender();
        initHistoryHasOrNot();
        initSickListView();

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
        if (!isAdd) {
            resetDataListEdit();
        }
        boolean isEtDescripShouldShow = !isAdd && choosedSickList.contains("其他");
        resetRelativeViewParams(isEtDescripShouldShow);
        mAdapter.setNewData(mList);
        if (isEtDescripShouldShow) {
            etSickDesc.setVisibility(View.VISIBLE);
            etSickDesc.setText(info.getOtherMedical());
        }
        doGetSickTips();

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
                                if (!isAdd) {
                                    resetDataListEdit();
                                }
                                boolean isEtDescripShouldShow = !isAdd && choosedSickList.contains("其他");
                                resetRelativeViewParams(isEtDescripShouldShow);
                                mAdapter.setNewData(mList);
                                if (isEtDescripShouldShow) {
                                    etSickDesc.setVisibility(View.VISIBLE);
                                    etSickDesc.setText(info.getOtherMedical());
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

    private void initGender() {
        if (isMan) {
            ivMan.setImageResource(R.drawable.selected_oval);
            ivWoman.setImageResource(R.drawable.unselect_oval);
        } else {
            ivMan.setImageResource(R.drawable.unselect_oval);
            ivWoman.setImageResource(R.drawable.selected_oval);
        }
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

    private void changeToPrePage() {
        setResult(RESULT_OK);
        finish();
    }

    private void saveOrEdit() {
        if (checkParamsValid()) {
            ContextUtils.hideSoftInput(this);
            assembleParams();
        }
    }

    private void assembleParams() {
        if (isAdd) {
            PatientAddParam addParam = new PatientAddParam();
            addParam.setName(name);
            addParam.setPhone(phone);
            addParam.setIsHistory(isHas ? 1 : 0);
            addParam.setSex(isMan ? 0 : 1);
            addParam.setIdcard(ID);
            addParam.setMedicalHistory(getMedicalHistory());
            addParam.setOtherMedical(description);
            doAddPatient(addParam);
        } else {
            PatientEditParam editParam = new PatientEditParam();
            editParam.setName(name);
            editParam.setPhone(phone);
            editParam.setIsHistory(isHas ? 1 : 0);
            editParam.setIdcard(ID);
            editParam.setMedicalHistory(getMedicalHistory());
            editParam.setOtherMedical(description);
            editParam.setId(info.getId());
            doEditPatient(editParam);
        }
    }

    private void doEditPatient(PatientEditParam editParam) {
        if (isEditPatient) {
            showLoadingDialog();
            return;
        }
        isEditPatient = true;
        PatientApi.getInstance().editPatient(editParam)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        isEditPatient = false;
                        hideLoadingDialog();
                        cancelToPrePage();
                    }

                    @Override
                    public void success(BaseBean<String> result) {
                        isEditPatient = false;
                        hideLoadingDialog();
                        if (result != null && result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(AddPatientActivity.this, "修改成功");
                            changeToPrePage();
                        } else {
                            ToastUtil.getInstance().showShort(AddPatientActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isEditPatient = false;
                        hideLoadingDialog();
                        ToastUtil.getInstance().showShort(AddPatientActivity.this, "添加失败，请重试！");
                    }
                });
    }

    private synchronized void doAddPatient(PatientAddParam addParam) {
        if (isAddPatient) {
            showLoadingDialog("正在添加就诊人");
            return;
        }
        isAddPatient = true;
        PatientApi.getInstance().addPatient(addParam)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        isAddPatient = false;
                        hideLoadingDialog();
                        cancelToPrePage();
                    }

                    @Override
                    public void success(BaseBean<String> result) {
                        isAddPatient = false;
                        hideLoadingDialog();
                        if (result != null && result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(AddPatientActivity.this, "添加成功");
                            changeToPrePage();
                        } else {
                            ToastUtil.getInstance().showShort(AddPatientActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isAddPatient = false;
                        hideLoadingDialog();
                        ToastUtil.getInstance().showShort(AddPatientActivity.this, "添加失败，请重试！");
                    }
                });
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

    private void cancelToPrePage() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        cancelToPrePage();
    }

    @SuppressLint("ClickableViewAccessibility")
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
                        ContextUtils.hideSoftInput(AddPatientActivity.this);
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
                        ContextUtils.showSoftInput(AddPatientActivity.this, etSickDesc);
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
                ContextUtils.showSoftInput(AddPatientActivity.this, etSickDesc);
            }
        });
        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtils.showSoftInput(AddPatientActivity.this, etSickDesc);
            }
        });
        etID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtils.showSoftInput(AddPatientActivity.this, etSickDesc);
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
                isHas = false;
                ivHas.setImageResource(R.drawable.unselect_oval);
                ivNo.setImageResource(R.drawable.selected_oval);
                llContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initData() {
        getIntentData();
        if (isAdd) {
            mTopBar.setTopCenterTxt("添加就诊人");
        } else {
            mTopBar.setTopCenterTxt("编辑就诊人");
            resetView(info);
        }
        assembleSicks();
    }

    private boolean checkParamsValid() {
        name = etName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        ID = etID.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.getInstance().showShort(this, "请输入您的真实姓名！");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.getInstance().showShort(this, "请输入您的手机号！");
            return false;
        }
        if (!ContextUtils.isValidPhoneNum(phone)) {
            ToastUtil.getInstance().showShort(this, "请检查手机号码是否正确！");
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

    private void resetView(PatientInfo info) {
        if (!TextUtils.isEmpty(info.getName()))
            etName.setText(info.getName());
        if (!TextUtils.isEmpty(info.getPhone()))
            etPhone.setText(info.getPhone());

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
        if (TextUtils.equals(historyFlag, "0")) {
            isHas = false;
        } else {
            isHas = true;
        }
        initHistoryHasOrNot();
        if (isHas) {
            choosedSickList = getChoosedSickListEdit(info.getMedicalHistory());
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

    private void getIntentData() {
        int requestcode = getIntent().getIntExtra(KEY_REQUESTCODE, REQUESTCODE_ADD);
        if (requestcode == REQUESTCODE_EDIT) {
            info = (PatientInfo) getIntent().getSerializableExtra(KEY_PATIENT_INFO);
            if (info == null) {
                finish();
            }
            isAdd = false;
            return;
        }
        isAdd = true;
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


    private void setEmptyViewNoData() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_page_nodata, null);
        TextView tvNoDataTip = emptyView.findViewById(R.id.tvNoDataTip);
        tvNoDataTip.setText("暂无病例！");
        mAdapter.setEmptyView(emptyView);
    }

    private void retry() {
        setEmptyViewLoadingCircle();
        doGetSickTips();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_add_patient;
    }

    @Override
    protected void onPause() {
        ContextUtils.hideSoftInput(this);
        super.onPause();
    }
}
