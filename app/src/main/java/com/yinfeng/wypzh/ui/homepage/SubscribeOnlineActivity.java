package com.yinfeng.wypzh.ui.homepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.ConstantApi;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.order.ConmonExtoryBean;
import com.yinfeng.wypzh.bean.order.ConmonSsBean;
import com.yinfeng.wypzh.bean.order.FreeVoucherBean;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailBean;
import com.yinfeng.wypzh.bean.order.ServiceTimeBean;
import com.yinfeng.wypzh.bean.order.SubmitOrderParam;
import com.yinfeng.wypzh.bean.patient.HospitalBean;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.ui.dialog.WheelDialogNew;
import com.yinfeng.wypzh.ui.mine.FreeVoucherActivity;
import com.yinfeng.wypzh.ui.order.OrderPrePayActivity;
import com.yinfeng.wypzh.ui.order.OrderWaitReceiveActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

import static com.yinfeng.wypzh.http.common.ApiContents.BASE_URL;

public class SubscribeOnlineActivity extends BaseActivity implements WheelDialogNew.WheelTimeSelectListener {
    public static final int REQUESTCODE_CHOOSE_HOSPITAL = 0x01;
    public static final int REQUESTCODE_CHOOSE_PATIENT = 0x02;
    public static final int REQUESTCODE_CHOOSE_FREEVOUCHER = 0x03;
    public static final String KEY_SERVICEOPTION_DETAIL = "key_serviceoption_detail";
    public static final String KEY_SERVICETIME_DETAIL = "key_servicetime_detail";

    TopBar mTopBar;
    SmartRefreshLayout mRefreshLayout;
    RelativeLayout rlChoosePatient;
    RelativeLayout rlchoosehospital;
    RelativeLayout rlcoupon;
    TextView tvName;
    //    TextView tvTimeTipOne, tvTimeTipTwo;
    ImageView ivArrowName, ivArrowHospital, ivArrowFreeVoucher;
    EditText etPhoneSecond;
    LinearLayout llChooseTime;
    LinearLayout llTime;
    TextView tvTimeStart, tvTimeEnd;
    TextView tvHospital;
    EditText etDepartment, etRemark;
    TextView tvFreeVoucher;
    TextView tvServicePrice, tvTotalCount;

    Button btConfirmOrder;
    ServiceOptionDetailBean serviceOptionBean;
    ServiceTimeBean serviceTimeBean;
    PatientInfo patientInfo;
    HospitalBean hospitalBean;
    FreeVoucherBean freeVoucherBean;
    private String startTime, endTime;
    private SubmitOrderParam submitOrderParam;
    private boolean isSubmiting = false;
    PermissionTipDialog permissionTipDialog;
    AMapLocationClient mLocationClient;
    AMapLocationListener mLocationListener;
    AMapLocationClientOption mLocationOption;
    double latitude = -1;//获取纬度
    double longitude = -1;//获取经度
    private String address = "";

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("在线预约");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);

        rlChoosePatient = mRootView.findViewById(R.id.rlchooseperson);
        rlchoosehospital = mRootView.findViewById(R.id.rlchoosehospital);
        rlcoupon = mRootView.findViewById(R.id.rlcoupon);
        btConfirmOrder = mRootView.findViewById(R.id.btconfirmorder);

        tvName = mRootView.findViewById(R.id.tvName);
//        tvTimeTipOne = mRootView.findViewById(R.id.tvTimeTipOne);
//        tvTimeTipTwo = mRootView.findViewById(R.id.tvTimeTipTwo);
        llChooseTime = mRootView.findViewById(R.id.llChooseTime);
        llTime = mRootView.findViewById(R.id.llTime);
        tvTimeStart = mRootView.findViewById(R.id.tvTimeStart);
        tvTimeEnd = mRootView.findViewById(R.id.tvTimeEnd);
        tvHospital = mRootView.findViewById(R.id.tvHospital);
        etDepartment = mRootView.findViewById(R.id.etDepartment);
        etRemark = mRootView.findViewById(R.id.etRemark);
        etPhoneSecond = mRootView.findViewById(R.id.etPhone);
        tvServicePrice = mRootView.findViewById(R.id.tvServicePrice);
        tvTotalCount = mRootView.findViewById(R.id.tvTotalCount);
        ivArrowName = mRootView.findViewById(R.id.ivArrowName);
        tvFreeVoucher = mRootView.findViewById(R.id.tvFreeVoucher);
        ivArrowFreeVoucher = mRootView.findViewById(R.id.ivArrowFreeVoucher);
        ivArrowHospital = mRootView.findViewById(R.id.ivArrowHospital);

        etPhoneSecond.requestFocus();
        rxPermissions = new RxPermissions(this);


    }

    RxPermissions rxPermissions;

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btConfirmOrder).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(rxPermissions.ensure(Manifest.permission.ACCESS_FINE_LOCATION))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {

                        if (checkParamsValid()) {
                            doX();

                        }
                    }
                });
        RxView.clicks(rlChoosePatient).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ChoosePatientActivity.activityForResult(SubscribeOnlineActivity.this, patientInfo, REQUESTCODE_CHOOSE_PATIENT);
                    }
                });
        RxView.clicks(rlchoosehospital).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        ChooseHospitalActivity.activityStartForResult(SubscribeOnlineActivity.this, REQUESTCODE_CHOOSE_HOSPITAL);
                    }
                });
        RxView.clicks(llChooseTime).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
//                        WheelDialog wheelDialog = DialogHelper.getWheelDialog(SubscribeOnlineActivity.this, startTime, SubscribeOnlineActivity.this);
//                        wheelDialog.setServiceTime(serviceOptionBean.getServiceTime());
//                        wheelDialog.show();
                        WheelDialogNew wheelDialog = DialogHelper.getWheelDialogNew(SubscribeOnlineActivity.this, serviceOptionBean.getServiceTime(), serviceOptionBean.getPreStartTime(), serviceTimeBean.getStart(), serviceTimeBean.getEnd(), startTime, SubscribeOnlineActivity.this);
//                        wheelDialog.setStartAndEndTime(serviceOptionBean.getServiceTime(), serviceTimeBean.getStart(), serviceTimeBean.getEnd());
                        wheelDialog.show();
                    }
                });
        RxView.clicks(rlcoupon).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        FreeVoucherActivity.activityStartForResult(SubscribeOnlineActivity.this, freeVoucherBean, REQUESTCODE_CHOOSE_FREEVOUCHER);
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void checkLocationPermission() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            if (ContextUtils.isGpsOpen(SubscribeOnlineActivity.this)) {
                                getLocation();
                            } else {
                                showOpenGpsDialog();
                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』

                            String permissionName = "";
                            permissionName = "地理位置";
                            permissionTipDialog = DialogHelper.getPermissionTipDialog(SubscribeOnlineActivity.this, permissionName);
                            permissionTipDialog.show();
                        }
                    }
                });
    }

    private void getLocation() {
        showLoadingDialog();
        mLocationClient.startLocation();
    }

    private void showOpenGpsDialog() {
        MaterialDialog dialog = DialogHelper.getMaterialDialogOneQuick(this, "下单需上传地理位置,请打开Gps开关");
        dialog.show();
    }


    private void doX() {

//        Map<String, String> mParamMap = new LinkedHashMap();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hospitalId", submitOrderParam.getHospitalId() + "");
            jsonObject.put("hospitalName", submitOrderParam.getHospitalName() + "");
            jsonObject.put("departmentId", submitOrderParam.getDepartmentId() + "");
            jsonObject.put("departmentName", submitOrderParam.getDepartmentName() + "");
            jsonObject.put("phone", submitOrderParam.getPhone() + "");
            jsonObject.put("patientName", submitOrderParam.getPatientName() + "");
            jsonObject.put("makeStartTime", submitOrderParam.getMakeStartTime() + "");
            jsonObject.put("makeEndTime", submitOrderParam.getMakeEndTime() + "");
            jsonObject.put("patientId", submitOrderParam.getPatientId() + "");
            jsonObject.put("patientPhone", submitOrderParam.getPatientPhone() + "");
            jsonObject.put("remark", submitOrderParam.getRemark() + "");
            jsonObject.put("productId", submitOrderParam.getProductId() + "");
            jsonObject.put("couponId", submitOrderParam.getCouponId() + "");
            if (serviceOptionBean.isPositionable()) {
                jsonObject.put("lat", submitOrderParam.getLat() + "");
                jsonObject.put("lon", submitOrderParam.getLon() + "");
                jsonObject.put("position", submitOrderParam.getPosition() + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(ConstantApi.LOG_I, "下单 json :" + new Gson().toJson(jsonObject));
        String token = Hawk.get(ConstantApi.DO_TOKEN);
        OkGo.<String>post(BASE_URL + "order/submit")
//        OkGo.<String>post(BASE_URL_AUTH + "order/submit")
                .tag(this)
                .headers("MD-TOKEN", token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        String data = response.body();//这个就是返回来的结果
                        Log.i(ConstantApi.LOG_I, "下单 结果  onSuccess:" + data);
                        isSubmiting = false;
                        hideLoadingDialog();
                        ConmonExtoryBean bean = new Gson().fromJson(response.body(), ConmonExtoryBean.class);
                        if (bean != null) {
                            if (bean.getCode() == 500) {
                                Toast.makeText(SubscribeOnlineActivity.this, "" + bean.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    ConmonSsBean beanx = new Gson().fromJson(response.body(), ConmonSsBean.class);
                                    if (beanx != null && beanx.getCode() == 200) {
                                        goToNext(beanx);
                                        ToastUtil.getInstance().showShort(SubscribeOnlineActivity.this, "下单成功");
                                    } else {
                                        Toast.makeText(SubscribeOnlineActivity.this, "" + bean.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    ToastUtil.getInstance().showShort(SubscribeOnlineActivity.this, "解析数据异常");
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        Log.i(ConstantApi.LOG_I, "下单 结果 onError:" + response.body());
                        try {
                            ConmonExtoryBean bean = new Gson().fromJson(response.body(), ConmonExtoryBean.class);
                            if (bean.getCode() != 200) {
                                Toast.makeText(SubscribeOnlineActivity.this, "" + bean.getMessage(), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SubscribeOnlineActivity.this, "" + bean.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SubscribeOnlineActivity.this, "解析数据异常"  , Toast.LENGTH_SHORT).show();
                        }
                        isSubmiting = false;
                        hideLoadingDialog();
                    }
                });
    }


    @Override
    protected void initData() {
        getIntentData();
        tvServicePrice.setText(ContextUtils.getPriceStrConvertFenToYuan(serviceOptionBean.getPrice()));
        tvTotalCount.setText(ContextUtils.getPriceStrConvertFenToYuan(serviceOptionBean.getPrice()));
//        double price = ((double) serviceOptionBean.getRenewalPrice() / 100d) * (60d / (double) serviceOptionBean.getRenewalTime());
//        double price = (double) serviceOptionBean.getRenewalPrice() / 100d;
//        String priceStr = new DecimalFormat("0.00").format(price);
//        String tip1 = "1. 基础服务时长为" + serviceOptionBean.getServiceTime() + "分钟，超时" + priceStr + "元/" + serviceOptionBean.getRenewalTime() + "分钟";
//        String tip2 = "2. 最早预约时间为当前时间后的" + serviceOptionBean.getPreStartTime() + "分钟";
//        tvTimeTipOne.setText(tip1);
//        tvTimeTipTwo.setText(tip2);
        String[] remarkTips = serviceOptionBean.getRemarks();
        if (remarkTips.length > 0) {
            for (int i = 0; i < remarkTips.length; i++) {
                String tip = remarkTips[i];
                addChildTipView(i, tip);
            }
        }
//        addChildTipView(remarkTips.length,"我是一个粉刷匠，粉刷本领强，我想怎样就怎样，生活真是香");
//        addChildTipView(remarkTips.length+1,"");
//        addChildTipView(remarkTips.length+2,"上面一行是空的");
        if (serviceOptionBean.isPositionable())
            initLocationClient();
    }

    private void addChildTipView(int index, String tip) {
        LinearLayout llChild = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llChild.setLayoutParams(params);
        llChild.setOrientation(LinearLayout.HORIZONTAL);
        TextView placeHoldTv = new TextView(this);
        placeHoldTv.setText("注: ");
        placeHoldTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        placeHoldTv.setTextColor(ContextCompat.getColor(this, R.color.c06b49b));
        if (index != 0) {
            placeHoldTv.setVisibility(View.INVISIBLE);
        }
        llChild.addView(placeHoldTv);

        TextView tv = new TextView(this);
        tv.setText((index + 1) + "、" + tip);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setTextColor(ContextCompat.getColor(this, R.color.c06b49b));
        llChild.addView(tv);
        llChooseTime.addView(llChild);
    }

    private void initLocationClient() {
        if (mLocationClient == null)
            mLocationClient = new AMapLocationClient(getApplicationContext());
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocationLatest(true);
            mLocationOption.setNeedAddress(true);
            mLocationClient.setLocationOption(mLocationOption);
        }
        if (mLocationListener == null) {
            mLocationListener = new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    hideLoadingDialog();
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            //可在其中解析amapLocation获取相应内容。
                            latitude = aMapLocation.getLatitude();//获取纬度
                            longitude = aMapLocation.getLongitude();//获取经度
                            address = aMapLocation.getAddress();
                            LogUtil.error(" latitude " + latitude + "  longitude:" + longitude);

                            if (checkParamsValid()) {
//                                doSubmitOrder();
                                doX();
                            }

                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            LogUtil.error("AmapError", "location Error, ErrCode:"
                                    + aMapLocation.getErrorCode() + ", errInfo:"
                                    + aMapLocation.getErrorInfo());
                            if (aMapLocation != null)
                                ToastUtil.getInstance().showShort(SubscribeOnlineActivity.this, "错误码 Errcode :" + aMapLocation.getErrorCode());
                        }
                    }
                    mLocationClient.stopLocation();
                }
            };
            mLocationClient.setLocationListener(mLocationListener);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_subscribe_online;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_CHOOSE_PATIENT) {
                patientInfo = (PatientInfo) data.getSerializableExtra(ChoosePatientActivity.KEY_CHOOSED_INFO);
                if (patientInfo != null && !TextUtils.isEmpty(patientInfo.getName())) {
                    tvName.setText(patientInfo.getName());
                    tvName.setTextColor(ContextCompat.getColor(SubscribeOnlineActivity.this, R.color.c434343));
                    ivArrowName.setImageResource(R.drawable.arrow_right_dark);
                } else {
                    tvName.setText("选择就诊人");
                    tvName.setTextColor(ContextCompat.getColor(SubscribeOnlineActivity.this, R.color.cb5b5b5));
                    ivArrowName.setImageResource(R.drawable.arrow_right_grey);
                }
            }
            if (requestCode == REQUESTCODE_CHOOSE_HOSPITAL) {
                hospitalBean = (HospitalBean) data.getSerializableExtra(ChooseHospitalActivity.KEY_HOSPITAL_BEAN);
                if (hospitalBean != null && !TextUtils.isEmpty(hospitalBean.getName())) {
                    tvHospital.setText(hospitalBean.getName());
                    tvHospital.setTextColor(ContextCompat.getColor(SubscribeOnlineActivity.this, R.color.c434343));
                    ivArrowHospital.setImageResource(R.drawable.arrow_right_dark);
                } else {
                    tvHospital.setText("选择医院");
                    tvHospital.setTextColor(ContextCompat.getColor(SubscribeOnlineActivity.this, R.color.cb5b5b5));
                    ivArrowHospital.setImageResource(R.drawable.arrow_right_grey);
                }

            }
            if (requestCode == REQUESTCODE_CHOOSE_FREEVOUCHER) {
                freeVoucherBean = (FreeVoucherBean) data.getSerializableExtra(FreeVoucherActivity.KEY_FREEVOUCHER_BEAN);
                if (freeVoucherBean != null && !TextUtils.isEmpty(freeVoucherBean.getId())) {
                    tvFreeVoucher.setText("已选择免单券");
                    tvFreeVoucher.setTextColor(ContextCompat.getColor(SubscribeOnlineActivity.this, R.color.c434343));
                    ivArrowFreeVoucher.setImageResource(R.drawable.arrow_right_dark);
                    tvTotalCount.setText(ContextUtils.getPriceStrConvertFenToYuan(0));
                } else {
                    tvFreeVoucher.setText(getString(R.string.subscribe_coupon_choose));
                    tvFreeVoucher.setTextColor(ContextCompat.getColor(SubscribeOnlineActivity.this, R.color.cb5b5b5));
                    ivArrowFreeVoucher.setImageResource(R.drawable.arrow_right_grey);
                    tvServicePrice.setText(ContextUtils.getPriceStrConvertFenToYuan(serviceOptionBean.getPrice()));
                    tvTotalCount.setText(ContextUtils.getPriceStrConvertFenToYuan(serviceOptionBean.getPrice()));
                }
            }
        }

    }

    @Override
    public void selectedTime(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        setTvTimeStart();
        setTvTimeEnd();
    }

    private void setTvTimeStart() {
        if (!TextUtils.isEmpty(startTime)) {
            tvTimeStart.setTextColor(ContextCompat.getColor(this, R.color.c06b49b));
            tvTimeStart.setText(startTime);
        }
    }

    private void setTvTimeEnd() {
        if (!TextUtils.isEmpty(endTime)) {
            tvTimeEnd.setTextColor(ContextCompat.getColor(this, R.color.c06b49b));
            String startYear = startTime.substring(0, 5);
            String endYear = endTime.substring(0, 5);
            if (TextUtils.equals(startYear, endYear)) {
                String temp = endTime.substring(5, endTime.length());
                tvTimeEnd.setText(temp);
            } else {
                tvTimeEnd.setText(endTime);
            }
        }
    }

    private void getIntentData() {
        serviceOptionBean = (ServiceOptionDetailBean) getIntent().getSerializableExtra(KEY_SERVICEOPTION_DETAIL);
        serviceTimeBean = (ServiceTimeBean) getIntent().getSerializableExtra(KEY_SERVICETIME_DETAIL);

        if (serviceOptionBean == null || serviceTimeBean == null)
            finish();
    }

    public static void activityStart(Context context, ServiceOptionDetailBean bean, ServiceTimeBean serviceTimeBean) {
        Intent intent = new Intent(context, SubscribeOnlineActivity.class);
        intent.putExtra(KEY_SERVICEOPTION_DETAIL, bean);
        intent.putExtra(KEY_SERVICETIME_DETAIL, serviceTimeBean);
        context.startActivity(intent);
    }

    private boolean checkParamsValid() {
        String patientName = "";
        String secondPhone = "";
        if (patientInfo != null) {
            patientName = patientInfo.getName();
            secondPhone = etPhoneSecond.getText().toString().trim();
        }
        String hospitalName = "";
        String departmentName = "";
        String remarkContent = "";
        if (hospitalBean != null) {
            hospitalName = hospitalBean.getName();
            departmentName = etDepartment.getText().toString().trim();
            remarkContent = etRemark.getText().toString().trim();
        }

        if (TextUtils.isEmpty(patientName)) {
            ToastUtil.getInstance().showShort(this, "请选择就诊人");
            return false;
        }
//        if (TextUtils.isEmpty(secondPhone)) {
//            ToastUtil.getInstance().showShort(this, "请填写手机号");
//            return false;
//        }
        if (!ContextUtils.isValidPhoneNum(secondPhone)) {
            ToastUtil.getInstance().showShort(this, "请检查手机号是否正确");
            return false;
        }
        if (TextUtils.isEmpty(startTime)) {
            ToastUtil.getInstance().showShort(this, "请选择时间");
            return false;
        }
        if (TextUtils.isEmpty(hospitalName)) {
            ToastUtil.getInstance().showShort(this, "请选择医院");
            return false;
        }
        if (TextUtils.isEmpty(departmentName)) {
            ToastUtil.getInstance().showShort(this, "请填写科室名称");
            return false;
        }
//        if (TextUtils.isEmpty(remarkContent)) {
//            ToastUtil.getInstance().showShort(this, "请填写备注");
//            return false;
//        }
        submitOrderParam = new SubmitOrderParam();
        submitOrderParam.setHospitalId(hospitalBean.getId());
        submitOrderParam.setHospitalName(hospitalName);
        submitOrderParam.setDepartmentName(departmentName);
        submitOrderParam.setPhone(secondPhone);
        submitOrderParam.setPatientName(patientName);
        submitOrderParam.setMakeStartTime(startTime);
        submitOrderParam.setMakeEndTime(endTime);
        submitOrderParam.setPatientId(patientInfo.getId());
        submitOrderParam.setPatientPhone(patientInfo.getPhone());
        submitOrderParam.setRemark(remarkContent);
        submitOrderParam.setProductId(serviceOptionBean.getId());
        submitOrderParam.setCouponId(getFreeVoucherId());

        if (serviceOptionBean.isPositionable()) {
            submitOrderParam.setLat(String.valueOf(latitude));
            submitOrderParam.setLon(String.valueOf(longitude));
            submitOrderParam.setPosition(address);
        }
        return true;
    }

    private String getFreeVoucherId() {
        if (freeVoucherBean != null && !TextUtils.isEmpty(freeVoucherBean.getId())) {
            return freeVoucherBean.getId();
        }
        return "";
    }

    private void goToNext(ConmonSsBean data) {
        OrderDetailBean detailBean = new OrderDetailBean();
        detailBean.setId(data.getData().getId());
        detailBean.setCode(data.getData().getCode());
        detailBean.setCreateTime(data.getData().getCreateTime());
        detailBean.setPayPrice(data.getData().getPrice());
        detailBean.setCouponId(getFreeVoucherId());
        detailBean.setServicePrice(serviceOptionBean.getPrice());
        detailBean.setMakeStartTime(startTime);
        detailBean.setMakeEndTime(endTime);
        detailBean.setRemark(submitOrderParam.getRemark());
        detailBean.setProductId(submitOrderParam.getProductId());
        detailBean.setHospitalName(submitOrderParam.getHospitalName());
        detailBean.setDepartmentName(submitOrderParam.getDepartmentName());
        detailBean.setHospitalId(submitOrderParam.getHospitalId());

        if (data.getData().getPrice() == 0) {
            OrderWaitReceiveActivity.activityStart(this, detailBean);
            OrderUtil.addOrderWaitReceive(detailBean.getId());
            SFUtil.getInstance().addOrderLoopWaitReceive(this, detailBean.getId());
            OrderUtil.startLoopWaitReceive();
            RedPointUtil.showOrderDot(0);
            RedPointUtil.showBottomDot(1);
            finish();
        } else {
            OrderPrePayActivity.activityStart(this, detailBean);
        }
//        finish();
    }
}
