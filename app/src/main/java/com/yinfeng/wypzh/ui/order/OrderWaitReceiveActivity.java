package com.yinfeng.wypzh.ui.order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.order.CancelOrderParam;
import com.yinfeng.wypzh.bean.order.OnlineWaiterParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.patient.HospitalBean;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.ui.dialog.CancelOrderDialog;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.NetUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class OrderWaitReceiveActivity extends BaseActivity implements AMapLocationListener {
    private static final String TAG = OrderWaitReceiveActivity.class.getSimpleName();

    private TopBar mTopBar;
    private LinearLayout llMapFloatTop;
    private TextView tvMapFloatCenter;
    private RelativeLayout rlMapFloatBottom;
    private TextView tvServiceTime, tvHospital, tvDepartment;

    private ImageView ivMsg, ivPhone;
    private ImageView ivGetOwnerLoc;
    private TextView tvName;
    private ImageView ivheader;
    private TextView tvServiceSum, tvServiceDescription;

    private MapView mMapView;
    //地图控制器
    private AMap aMap;
    //定位需要的数据
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    //蓝点
    MyLocationStyle myLocationStyle;
    private double mLatitude;
    private double mLongitude;
    private double hopitalLatitude = -1;
    private double hopitalLongitude = -1;
    private String cancelTip = "您确定需要取消订单吗？";
    private MaterialDialog cancelDialog;
    private boolean isLocation = false;
    private int errCode = -1; //-1 初始化值（尚未响应）0定位成功
    private boolean isOwnerLocationGeting = false;
    private boolean isGetReceiver = false;//是否已经接到单
    private boolean isFloatShowing = true;
    private PermissionTipDialog permissionTipDialog;
    private Disposable mDispose;//地理位置的dispose
    private Disposable mLoopDispose;//轮询定时器的dispose
    private Disposable mCheckDispose;//轮询 发起的请求的dispose
    private Disposable mOnlineDispose;//轮询 在线人数
    private int countTime = 0;
    private Marker mLocationMarker;
    private List<Marker> mMarkerList;
    private AMap.InfoWindowAdapter mInfoWindowAdapter;
    private AlphaAnimation mHideAnimationTop;
    private AlphaAnimation mHideAnimationBottom;
    private AlphaAnimation mShowAnimationTop;
    private AlphaAnimation mShowAnimationBottom;
    private AlphaAnimation mShowAnimationCenter;
    private AlphaAnimation mHideAnimationCenter;
    private String getLocProgressTxt = "正在获取医院位置";
    private HashMap<String, Boolean> infoShowMap;
    private OrderDetailBean orderDetailBean;
    private HospitalBean hospitalBean;
    private boolean isGetDetailing = false;
    private String orderId;
    private String waiterPhone;
    private String hospitalName;
    private RequestOptions requestOptions;
    private boolean isCancelOrdering = false;
    private int onlineNum = 0;
    private static final String TAG_HIDE = "hide";
    private static final String TAG_SHOW = "show";
    private CharSequence onlineTipCs;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        initTopBar(mRootView);
        llMapFloatTop = mRootView.findViewById(R.id.llMapFloatTop);
        tvMapFloatCenter = mRootView.findViewById(R.id.tvMapFloatCenter);
        rlMapFloatBottom = mRootView.findViewById(R.id.rlMapFloatBottom);
        tvServiceTime = mRootView.findViewById(R.id.tvServiceTime);
        tvHospital = mRootView.findViewById(R.id.tvHospital);
        tvDepartment = mRootView.findViewById(R.id.tvDepartment);
        ivMsg = mRootView.findViewById(R.id.ivMsg);
        ivPhone = mRootView.findViewById(R.id.ivPhone);
        ivGetOwnerLoc = mRootView.findViewById(R.id.ivGetOwnerLoc);
        tvName = mRootView.findViewById(R.id.tvName);
        ivheader = mRootView.findViewById(R.id.ivheader);
        tvServiceSum = mRootView.findViewById(R.id.tvServiceSum);
        tvServiceDescription = mRootView.findViewById(R.id.tvServiceDescription);

        onlineTipCs = getString(R.string.online_txt_no);
        tvMapFloatCenter.setText(getString(R.string.online_txt_no));

        mMapView = mRootView.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        initMapView();
        requestOptions = ImageUtil.getInstance().getDefineOptions(100, R.drawable.head_default);
        requestOptions.circleCrop();


        llMapFloatTop.setVisibility(View.VISIBLE);
        tvMapFloatCenter.setVisibility(View.VISIBLE);
        tvMapFloatCenter.setTag(TAG_SHOW);
        rlMapFloatBottom.setVisibility(View.GONE);
    }

    private void initMapView() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        //aMap.setMapType(AMap.MAP_TYPE_NIGHT);// 设置夜晚地图模
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setCompassEnabled(true);//指南针
        aMap.getUiSettings().setScaleControlsEnabled(true);//比例尺
        aMap.getUiSettings().setLogoLeftMargin(ContextUtils.dip2px(this, 12));
        initInfoWindowAdapter();
        aMap.setInfoWindowAdapter(mInfoWindowAdapter);
        //显示定位图标
//        initBlueMark();

//        llMapFloatTop.setVisibility(View.GONE);
//        tvMapFloatCenter.setVisibility(View.GONE);
        rlMapFloatBottom.setVisibility(View.GONE);
    }

    private void initInfoWindowAdapter() {
        infoShowMap = new HashMap<>();
        mInfoWindowAdapter = new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = LayoutInflater.from(OrderWaitReceiveActivity.this).inflate(R.layout.map_infowindow, null);
                if (!TextUtils.isEmpty(marker.getTitle())) {
                    TextView tvContent = view.findViewById(R.id.tvInfoWindow);
                    tvContent.setText(marker.getTitle());
                }
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };

    }

    private void initBlueMark() {
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.clear(false);//不清除小蓝点
    }

    private synchronized void getOwnerLoc() {
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(this);
        }
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//        mLocationOption.setInterval(2000);//2秒一次定位,连续模式才起作用
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mLocationClient.startLocation();//启动定位
        startTimeTask();
    }

    private void afterGetLoc() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private void startTimeTask() {
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mDispose != null && !mDispose.isDisposed()) {
                            mDispose.dispose();
                        }
                        mDispose = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (!NetUtil.isNetworkAvailable(OrderWaitReceiveActivity.this) || countTime == 3 || errCode == 0) {
                            if (mDispose != null)
                                mDispose.dispose();
                            afterGetLoc();
                            String brand = ContextUtils.getUserAgent().getMobile_brand();
                            if (errCode == 12) {
                                requestPermissions();
                                if (isLocation) {
                                    if (brand.contains("vivo")) {
                                        if (permissionTipDialog == null) {
                                            permissionTipDialog = DialogHelper.getPermissionTipDialog(OrderWaitReceiveActivity.this, "地理位置");
                                        }
                                        permissionTipDialog.show();
                                    } else {
                                        ToastUtil.getInstance().showLong(OrderWaitReceiveActivity.this, "请打开位置信息开关！");
                                    }
                                }
                            }
                            if (!NetUtil.isNetworkAvailable(OrderWaitReceiveActivity.this) || errCode == 4) {
                                requestPermissions();
                                if (isLocation) {
                                    ToastUtil.getInstance().showLong(OrderWaitReceiveActivity.this, "请连接网络！");
                                }
                            }
                            errCode = -1;
                            countTime = 0;
                            isOwnerLocationGeting = false;
                            hideLoadingDialog();
                        } else {
                            countTime++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        errCode = -1;
                        countTime = 0;
                        isOwnerLocationGeting = false;
                        hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            errCode = 0;
//            mLatitude = aMapLocation.getLatitude();//纬度
//            mLongitude = aMapLocation.getLongitude();
//            LogUtil.error("mLatitude: " + mLatitude + " , mLongitude :" + mLongitude);
//            LatLng latLng;
            //此处替换为目标医院的坐标
//            if (hopitalLatitude != -1) {
//                latLng = new LatLng(hopitalLatitude, hopitalLongitude);
//                showMyLocMarker(latLng, hospitalName);
//            } else {
//                latLng = new LatLng(mLatitude, mLongitude);
//                showMyLocMarker(latLng, "您所在位置");
//            }
////            if (myLocationStyle == null || !myLocationStyle.isMyLocationShowing()) {
////                initBlueMark();
////            }
////            myLocationStyle.anchor((float) mLatitude, (float) mLongitude);
//            CameraUpdate mCameraUpdate;
//            if (aMapLocation != null) {
////                addMarker(new LatLng(mLatitude, mLongitude), "您所在的位置");
//                mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 14, 30, 0));
//            } else {
//                mCameraUpdate = CameraUpdateFactory.zoomTo(14);
//            }
//            aMap.animateCamera(mCameraUpdate, 500, null);
        } else {
//            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
//            LogUtil.error("定位AmapErr ：" + errText);
            errCode = aMapLocation.getErrorCode();
        }
        isOwnerLocationGeting = false;
        showHospitalAddress();
//        showMultiMarkers();
        showMultiMarkersNew();
    }

    private void showMyLocMarker(LatLng latLng, String title) {
        if (mLocationMarker != null) {
            mLocationMarker.setPosition(latLng);
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
//        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.title(title).snippet("济南市");
        markerOption.visible(true)
                .autoOverturnInfoWindow(true);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.map_mylocationmark)));
        //        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        markerOption.setFlat(false);//设置marker平贴地图效果
        mLocationMarker = aMap.addMarker(markerOption);
//        mLocationMarker.setAlpha(0.8f);
        mLocationMarker.setClickable(true);
        com.amap.api.maps.model.animation.Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
        markerAnimation.setDuration(1000);  //设置动画时间 单位毫秒
        mLocationMarker.setAnimation(markerAnimation);
    }

    private Marker addMarker(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.title("陪诊员").snippet("济南市");
        markerOption.visible(true)
                .autoOverturnInfoWindow(true);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.message_head)));
        markerOption.setFlat(false);//设置marker平贴地图效果
        Marker marker = aMap.addMarker(markerOption);
        marker.setClickable(false);
        com.amap.api.maps.model.animation.Animation markerAnimation = new TranslateAnimation(latLng); //初始化生长效果动画
        markerAnimation.setDuration(1000);  //设置动画时间 单位毫秒
        marker.setAnimation(markerAnimation);
        marker.startAnimation();
        return marker;
    }

    private void showMultiMarkers() {
        if (mMarkerList == null || mMarkerList.size() == 0) {
            int markersNumb = onlineNum;
            if (isGetReceiver)
                markersNumb = 1;
            if (markersNumb > 0) {
                List<LatLng> addresss = assembleMultiAdress(new LatLng(hopitalLatitude, hopitalLongitude), markersNumb);
                mMarkerList = new ArrayList<>();
                for (int i = 0; i < addresss.size(); i++) {
                    LatLng latLng = addresss.get(i);
                    Marker marker = addMarker(latLng);
                    mMarkerList.add(marker);
                }
            }
        }
        if (isGetReceiver && mMarkerList != null && mMarkerList.size() > 1) {
            for (int i = 0; i < mMarkerList.size() - 1; i++) {
                Marker marker = mMarkerList.get(i);
                if (marker != null)
                    marker.remove();
            }
        }
//        if (addresss.size() == mMarkerList.size()) {
//            for (int i = 0; i < mMarkerList.size(); i++) {
//                Marker marker = mMarkerList.get(i);
//                marker.setPosition(addresss.get(i));
//            }
//        }
    }

    private void showMultiMarkersNew() {
        int markersNumb;
        if (isGetReceiver) {
            markersNumb = 1;
            if (mMarkerList == null || mMarkerList.size() == 0) {
                List<LatLng> addresss = assembleMultiAdress(new LatLng(hopitalLatitude, hopitalLongitude), markersNumb);
                mMarkerList = new ArrayList<>();
                for (int i = 0; i < addresss.size(); i++) {
                    LatLng latLng = addresss.get(i);
                    Marker marker = addMarker(latLng);
                    mMarkerList.add(marker);
                }
            } else if (mMarkerList.size() > 1) {
                List<Marker> saveList = new ArrayList<>();
                saveList.add(mMarkerList.get(0));
                for (int i = 1; i < mMarkerList.size(); i++) {
                    Marker marker = mMarkerList.get(i);
                    if (marker != null)
                        marker.remove();
                }
                mMarkerList = saveList;
            }
        } else {
            markersNumb = onlineNum;
            if (markersNumb == 0) {
                if (mMarkerList != null && mMarkerList.size() > 0) {
                    for (int i = 0; i < mMarkerList.size(); i++) {
                        Marker marker = mMarkerList.get(i);
                        if (marker != null)
                            marker.remove();
                    }
                    mMarkerList = null;
                }
            } else {
                if (mMarkerList == null || mMarkerList.size() == 0) {
                    List<LatLng> addresss = assembleMultiAdress(new LatLng(hopitalLatitude, hopitalLongitude), markersNumb);
                    mMarkerList = new ArrayList<>();
                    for (int i = 0; i < addresss.size(); i++) {
                        LatLng latLng = addresss.get(i);
                        Marker marker = addMarker(latLng);
                        mMarkerList.add(marker);
                    }
                } else if (mMarkerList.size() > markersNumb) {
                    List<Marker> saveList = new ArrayList<>();
                    for (int i = 0; i < markersNumb; i++) {
                        saveList.add(mMarkerList.get(i));
                    }
                    for (int j = markersNumb; j < mMarkerList.size(); j++) {
                        Marker marker = mMarkerList.get(j);
                        if (marker != null)
                            marker.remove();
                    }
                    mMarkerList = saveList;
                } else if (mMarkerList.size() < markersNumb) {
                    List<LatLng> addresss = assembleMultiAdress(new LatLng(hopitalLatitude, hopitalLongitude), markersNumb - mMarkerList.size());
                    for (int i = 0; i < addresss.size(); i++) {
                        LatLng latLng = addresss.get(i);
                        Marker marker = addMarker(latLng);
                        mMarkerList.add(marker);
                    }
                }

            }
        }
    }

    private List<LatLng> assembleMultiAdress(LatLng anchor, int numb) {
        double latitudeAnchor = anchor.latitude;
        double longitudeAnchor = anchor.longitude;
        List<LatLng> list = new ArrayList<>();
        for (int i = 0; i < numb; i++) {
            double delt1 = Math.random() * 0.003;
            if (i > numb / 2) {
                delt1 = -delt1;
            }
            double delt2 = Math.random() * 0.003;
            if (i < numb / 2) {
                delt2 = -delt2;
            }
            BigDecimal latitudeMonitor = new BigDecimal(delt1 + latitudeAnchor);
            BigDecimal longitudeMonitor = new BigDecimal(delt2 + longitudeAnchor);
            LatLng latLngMonitor = new LatLng(latitudeMonitor.doubleValue(), longitudeMonitor.doubleValue());
            list.add(latLngMonitor);
        }
        return list;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(ivheader).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        try {
                            String waiterStr = orderDetailBean.getWaiter();
                            WaiterInfo waiterInfo = new Gson().fromJson(waiterStr, WaiterInfo.class);
                            PzyDetailActivity.activityStart(OrderWaitReceiveActivity.this, waiterInfo);
                        } catch (Exception e) {
                        }
                    }
                });
        RxView.clicks(ivMsg).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String account = orderDetailBean.getWaiterId();
                        openMsg(account);
//                        requestIMessagePermissions(account);
                    }
                });
        RxView.clicks(ivPhone).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (!TextUtils.isEmpty(waiterPhone))
                            ContextUtils.callPhone(OrderWaitReceiveActivity.this, waiterPhone);
                    }
                });


        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });
        RxView.clicks(ivGetOwnerLoc).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (isOwnerLocationGeting) {
                            showLoadingDialog(getLocProgressTxt);
                        } else {
                            isOwnerLocationGeting = true;
                            showLoadingDialog(getLocProgressTxt);
                            requestPermissions();
                            getOwnerLoc();
                        }
//                        showHospitalAddress();
                    }
                });
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if (isGetReceiver) {
                    tvMapFloatCenter.setVisibility(View.INVISIBLE);
                    if (isFloatShowing) {
                        setHideAnimationTop(llMapFloatTop);
                        setHideAnimationBottom(rlMapFloatBottom);
                        isFloatShowing = false;
                    } else {
                        setShowAnimationTop(llMapFloatTop);
                        setShowAnimationBottom(rlMapFloatBottom);
                        isFloatShowing = true;
                    }
                } else {
                    if (isFloatShowing) {
                        setHideAnimationTop(llMapFloatTop);
                        setHideAnimationCenter(tvMapFloatCenter);
                        isFloatShowing = false;
                    } else {
                        setShowAnimationTop(llMapFloatTop);
                        setShowAnimationCenter(tvMapFloatCenter);
                        isFloatShowing = true;
                    }
                }
            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LogUtil.error("marker.getId():" + marker.getId());
//                marker.hideInfoWindow();
                String title = marker.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    if (infoShowMap.containsKey(title) && infoShowMap.get(title)) {
                        marker.hideInfoWindow();
                        infoShowMap.put(title, false);
                    } else {
                        marker.showInfoWindow();
                        infoShowMap.put(title, true);
                    }
                }

                return true;
            }
        });
    }

    private void showHospitalAddress() {
        LatLng latLng = new LatLng(hopitalLatitude, hopitalLongitude);
        showMyLocMarker(latLng, hospitalName);
        CameraUpdate mCameraUpdate;
        mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 16, 30, 0));
        aMap.animateCamera(mCameraUpdate, 500, null);
        mLocationMarker.startAnimation();
    }

    public void setHideAnimationTop(final View view) {
        if (null == view) {
            return;
        }
        if (null != mShowAnimationTop) {
            mShowAnimationTop.cancel();
            mShowAnimationTop = null;
        }
        if (null == mHideAnimationTop) {
            mHideAnimationTop = new AlphaAnimation(1.0f, 0.0f);
            mHideAnimationTop.setDuration(600);
            mHideAnimationTop.setFillAfter(true);

            view.startAnimation(mHideAnimationTop);
        }

    }

    public void setHideAnimationBottom(final View view) {
        if (null == view) {
            return;
        }
        if (null != mShowAnimationBottom) {
            mShowAnimationBottom.cancel();
            mShowAnimationBottom = null;
        }
        if (null == mHideAnimationBottom) {
            mHideAnimationBottom = new AlphaAnimation(1.0f, 0.0f);
            mHideAnimationBottom.setDuration(600);
            mHideAnimationBottom.setFillAfter(true);

            view.startAnimation(mHideAnimationBottom);
        }

    }

    public void setHideAnimationCenter(final View view) {
        if (null == view) {
            return;
        }
        if (null != mShowAnimationCenter) {
            mShowAnimationCenter.cancel();
            mShowAnimationCenter = null;
            view.setTag(TAG_HIDE);
        }
        if (null == mHideAnimationCenter) {
            mHideAnimationCenter = new AlphaAnimation(1.0f, 0.0f);
            mHideAnimationCenter.setDuration(600);
            mHideAnimationCenter.setFillAfter(true);
            view.setTag(TAG_HIDE);
            view.startAnimation(mHideAnimationCenter);
        }

    }

    public void setShowAnimationTop(View view) {
        if (null == view) {
            return;
        }
        if (null != mHideAnimationTop) {
            mHideAnimationTop.cancel();
            mHideAnimationTop = null;
        }
        if (null == mShowAnimationTop) {
            mShowAnimationTop = new AlphaAnimation(0.0f, 1.0f);
            mShowAnimationTop.setDuration(600);
            mShowAnimationTop.setFillAfter(true);
            mShowAnimationTop.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShowAnimationTop = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(mShowAnimationTop);
        }

    }

    public void setShowAnimationBottom(View view) {
        if (null == view) {
            return;
        }
        if (null != mHideAnimationBottom) {
            mHideAnimationBottom.cancel();
            mHideAnimationBottom = null;
        }
        if (null == mShowAnimationBottom) {
            mShowAnimationBottom = new AlphaAnimation(0.0f, 1.0f);
            mShowAnimationBottom.setDuration(600);
            mShowAnimationBottom.setFillAfter(true);
            mShowAnimationBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShowAnimationBottom = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(mShowAnimationBottom);
        }

    }

    public void setShowAnimationCenter(final View view) {
        if (isGetReceiver)
            return;
        if (null == view) {
            return;
        }
        if (null != mHideAnimationCenter) {
            mHideAnimationCenter.cancel();
            mHideAnimationCenter = null;
            view.setTag(TAG_SHOW);
        }
        if (null == mShowAnimationCenter) {
            mShowAnimationCenter = new AlphaAnimation(0.0f, 1.0f);
            mShowAnimationCenter.setDuration(600);
            mShowAnimationCenter.setFillAfter(true);
            mShowAnimationCenter.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShowAnimationCenter = null;
                    view.setTag(TAG_SHOW);
                    if (view instanceof TextView) {
                        ((TextView) view).setText(onlineTipCs);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(mShowAnimationCenter);
        }

    }

    @Override
    protected void initData() {
        if (orderDetailBean != null)
            doGetOnlineWaiterNum(orderDetailBean.getHospitalId());
//        syncOrderState();
    }

    private void doGetOnlineWaiterNum(String hid) {
        OnlineWaiterParam param = new OnlineWaiterParam(hid);
        OrderApi.getInstance().getOnlineWaiterNum(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            try {
                                String numbStr = result.getResult();
                                onlineNum = Integer.parseInt(numbStr);
//                                if (onlineNum > 0) {
////                                    String onlineStr = "  " + onlineNum + "  ";
//                                    String text = String.format(getResources().getString(R.string.online_txt_has), onlineNum);
//                                    int remainWaitTime = getRemainTime();
//                                    String waitTimeStr = ",请耐心等待！";
//                                    if (remainWaitTime > 0) {
//                                        waitTimeStr = ",请等待" + remainWaitTime + "分钟";
//                                    }
//
//                                    SpannableString ss = new SpannableString(text + waitTimeStr);
//                                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#06b49b"));
//                                    ss.setSpan(colorSpan, 3, 3 + String.valueOf(onlineNum).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                                    if (remainWaitTime > 0)
//                                        ss.setSpan(colorSpan, text.length() + 4, text.length() + 4 + String.valueOf(remainWaitTime).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                                    tvMapFloatCenter.setText(ss);
//                                } else {
//                                    tvMapFloatCenter.setText(getString(R.string.online_txt_no));
//                                }
                                resetCenterTxt();
                            } catch (Exception e) {
                            }
                        }
                        syncOrderState();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        syncOrderState();
                    }
                });
    }

    private void syncOrderState() {
        if (isGetDetailing) {
            showLoadingDialog("同步订单信息...");
            return;
        }
        isGetDetailing = true;
        showLoadingDialog("同步订单信息...");
        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>() {
                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        isGetDetailing = false;
                        hideLoadingDialog();
                        String errorMsg;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            if (bean != null && !TextUtils.isEmpty(bean.getState())) {
                                orderDetailBean = bean;
                                operateDifState(bean.getState());
                                getHospital(bean.getHospital());
                            } else {
                                errorMsg = "同步订单状态失败！\n 数据异常，请重试！";
                                showFailDialog(errorMsg);
                            }
                        } else {
                            errorMsg = "同步订单状态失败！\n" + result.getMessage();
                            showFailDialog(errorMsg);
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetDetailing = false;
                        hideLoadingDialog();
                        String failTip;
                        if (NetUtil.isNetworkAvailable(OrderWaitReceiveActivity.this)) {
                            failTip = "同步订单状态失败！\n 操作失败，请重试！";
                        } else {
                            failTip = "同步订单状态失败！\n请检查网络设置,重新同步订单信息！";
                        }
                        showFailDialog(failTip);
                    }
                });

    }

    private void getHospital(String hospital) {
        if (TextUtils.isEmpty(hospital))
            return;
        Gson gson = new Gson();
        hospitalBean = gson.fromJson(hospital, HospitalBean.class);
        if (hospital == null || TextUtils.isEmpty(hospitalBean.getLat()) || TextUtils.isEmpty(hospitalBean.getLon())) {
            ToastUtil.getInstance().showShort(this, "医院信息解析错误");
            return;
        }
        try {
            hopitalLatitude = Double.parseDouble(hospitalBean.getLat());
            hopitalLongitude = Double.parseDouble(hospitalBean.getLon());
        } catch (Exception e) {
            hopitalLatitude = -1;
            hopitalLongitude = -1;
        }
    }

    private void operateDifState(String state) {
        operateDifState(state, false);
    }

    private void operateDifState(String state, boolean isCheckForLoop) {

        if (TextUtils.equals(state, Constants.ORDER_STATE_PAID)) {
            isGetReceiver = false;
//            llMapFloatTop.setVisibility(View.VISIBLE);
//            tvMapFloatCenter.setVisibility(View.VISIBLE);
//            rlMapFloatBottom.setVisibility(View.GONE);
            initTopView();
            if (!isCheckForLoop) {
                isOwnerLocationGeting = true;
                showLoadingDialog(getLocProgressTxt);
                requestPermissions();
                getOwnerLoc();
                OrderUtil.stopLoopWaitReceive();
                startLoopCheckOrderState();
            }
        } else if (TextUtils.equals(state, Constants.ORDER_STATE_TAKE)) {
            SFUtil.getInstance().addOrderLoopWaitService(OrderWaitReceiveActivity.this, orderId);
            OrderUtil.startLoopWaitService();
            isGetReceiver = true;
            mTopBar.setTopCenterTxt("等待服务");
            setHideAnimationCenter(tvMapFloatCenter);
            setShowAnimationTop(llMapFloatTop);
            llMapFloatTop.setVisibility(View.VISIBLE);
            tvMapFloatCenter.setVisibility(View.GONE);
            tvMapFloatCenter.setTag(TAG_HIDE);
            rlMapFloatBottom.setVisibility(View.VISIBLE);
            initBottomView();
            isOwnerLocationGeting = true;
            showLoadingDialog(getLocProgressTxt);
            requestPermissions();
            getOwnerLoc();

            OrderUtil.addOrderWaitService(orderId);
            OrderUtil.deleteOrderWaitReceive(orderId);
            RedPointUtil.showOrderDot(1);
            RedPointUtil.showBottomDot(1);
            if (isCheckForLoop) {
                if (mLoopDispose != null)
                    mLoopDispose.dispose();
                SFUtil.getInstance().removeLoopWaitReceive(OrderWaitReceiveActivity.this, orderId);
                OrderUtil.startLoopWaitReceive();
            }
            showHasTakeDialog();
            OrderUtil.getNoticeInMsg();
        } else {
            if (mDispose != null)
                mDispose.dispose();
            if (mLoopDispose != null)
                mLoopDispose.dispose();
            if (mCheckDispose != null)
                mCheckDispose.dispose();
            if (mOnlineDispose != null)
                mOnlineDispose.dispose();
            String msg = "订单状态异常，请反馈客服";
            if (TextUtils.equals(state, Constants.ORDER_STATE_CANCEL)) {
                msg = "订单已取消";
            }
            showErrorStateDialog(msg);
        }
    }

    private void showHasTakeDialog() {
        DialogHelper.getHasTakeOrderDialog(this).show();
    }

    private void showErrorStateDialog(String msg) {
        final MaterialDialog errorDialog = DialogHelper.getMaterialDialogOneQuick(this, msg);
        errorDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                errorDialog.dismiss();
                OrderUtil.deleteOrderWaitService(orderId);
                OrderUtil.deleteOrderWaitReceive(orderId);
                toMainActvity();
            }
        });
        errorDialog.show();
    }

    private void initTopView() {
        String serviceTime = getServiceTime(orderDetailBean.getMakeStartTime(), orderDetailBean.getMakeEndTime());
        hospitalName = orderDetailBean.getHospitalName();
        String departMent = orderDetailBean.getDepartmentName();

        tvServiceTime.setText(serviceTime);
        tvHospital.setText(hospitalName);
        tvDepartment.setText(departMent);
    }

    private void initBottomView() {
        String faceImg = orderDetailBean.getProfile();
        String waiterName = orderDetailBean.getWaiterName();
        waiterPhone = orderDetailBean.getWaiterPhone();
        String waiterStr = orderDetailBean.getWaiter();
        if (!TextUtils.isEmpty(waiterStr)) {
            try {
                WaiterInfo waiterInfo = new Gson().fromJson(waiterStr, WaiterInfo.class);
                String description = waiterInfo.getServiceDescription();
                int serviceSum = waiterInfo.getServiceSum();
                String serviceSumStr = "共接单" + serviceSum + "次";
                tvServiceSum.setVisibility(View.VISIBLE);
                tvServiceSum.setText(serviceSumStr);
                if (!TextUtils.isEmpty(description))
                    tvServiceDescription.setText(description);
            } catch (Exception e) {
            }

        }
        ImageUtil.getInstance().loadImg(this, faceImg, requestOptions, ivheader);
        tvName.setText(waiterName);
    }

    private String getServiceTime(String startTime, String endTime) {
        String startYear = startTime.substring(0, 5);
        String endYear = endTime.substring(0, 5);
        if (TextUtils.equals(startYear, endYear)) {
            String temp = endTime.substring(5, endTime.length());
            return startTime + "\u2014" + temp;
        } else {
            return startTime + "\u2014" + endTime;
        }
    }

    private void toMainActvity() {
        int position = 0;
        if (isGetReceiver)
            position = 1;
        OrderUtil.toOrderListFragment(this, position);
    }

    private void showFailDialog(String content) {
        final MaterialDialog failDialog = DialogHelper.getMaterialDialogOneQuick(this, content);
        failDialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                failDialog.dismiss();
//                syncOrderState();
                finish();
            }
        });
        failDialog.show();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_wait_receive;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        if (cancelDialog != null) {
            cancelDialog.cancel();
            cancelDialog = null;
        }
        if (mDispose != null)
            mDispose.dispose();
        if (mLoopDispose != null)
            mLoopDispose.dispose();
        if (mCheckDispose != null)
            mCheckDispose.dispose();
        if (mOnlineDispose != null)
            mOnlineDispose.dispose();
        afterGetLoc();
        mMapView.onDestroy();
        super.onDestroy();
    }


    private void initTopBar(View mRootView) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("等待接单");
        mTopBar.setTopRightTxt("取消");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
//                toMainActvity();
//                justFinish();
                backToOrderList();
            }
        });
        mTopBar.setTopBarRightTxtListener(new TopBar.TopBarRightTextCickListener() {
            @Override
            public void topRightTxtClick() {
                showCancelDialog();
            }
        });
    }

    private void showCancelDialog() {
        if (isGetReceiver) {
            CancelOrderDialog cancelOrderDialog = DialogHelper.getCancelOrderDialog(this, new CancelOrderDialog.OnCancelOrderListener() {
                @Override
                public void confirmCancelOrder(String cancelReason) {
                    doCancelOrder(cancelReason);
                }
            });
            cancelOrderDialog.setCancelable(false);
            cancelOrderDialog.setCanceledOnTouchOutside(false);
            cancelOrderDialog.show();
        } else {
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
                    doCancelOrder("");
                }
            });
            cancelOrderDialog.show();
        }
    }

    private void doCancelOrder(String cancelReason) {
        if (isCancelOrdering) {
            showLoadingDialog("取消订单中...");
            return;
        }
        isCancelOrdering = true;
        showLoadingDialog("取消订单中...");
        CancelOrderParam cancelOrderParam = new CancelOrderParam();
        cancelOrderParam.setId(orderId);
        cancelOrderParam.setCancelType(ApiContents.USER_TYPE);
        cancelOrderParam.setCancelReason(cancelReason);
        OrderApi.getInstance().cancelOrder(cancelOrderParam)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        isCancelOrdering = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(OrderWaitReceiveActivity.this, "订单取消成功");
                            OrderUtil.deleteOrderWaitService(orderId);
                            OrderUtil.deleteOrderWaitReceive(orderId);
                            justFinish();
                        } else {
                            ToastUtil.getInstance().showShort(OrderWaitReceiveActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isCancelOrdering = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void startLoopCheckOrderState() {
        Observable.interval(5, 5, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoopDispose = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        doGetOrderStateForLoopCheck();
                        doGetOnlineRefresh(orderDetailBean.getHospitalId());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void doGetOrderStateForLoopCheck() {
        if (mCheckDispose != null)
            mCheckDispose.dispose();
        OrderApi.getInstance().getOrderDetail(orderId)
                .compose(RxSchedulers.<Response<BaseBean<OrderDetailBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<OrderDetailBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<OrderDetailBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCheckDispose = d;
                    }

                    @Override
                    public void success(BaseBean<OrderDetailBean> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            OrderDetailBean bean = result.getResult();
                            if (bean != null && !TextUtils.isEmpty(bean.getState())) {
                                orderDetailBean = bean;
                                operateDifState(bean.getState(), true);
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });
    }

    private void doGetOnlineRefresh(String hid) {
        if (mOnlineDispose != null)
            mOnlineDispose.dispose();
        OnlineWaiterParam param = new OnlineWaiterParam(hid);
        OrderApi.getInstance().getOnlineWaiterNum(param)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mOnlineDispose = d;
                    }

                    @Override
                    public void success(BaseBean<String> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            try {
                                String numbStr = result.getResult();
                                onlineNum = Integer.parseInt(numbStr);
                                resetCenterTxt();
                                showMultiMarkersNew();
                            } catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                    }
                });
    }

    private void resetCenterTxt() {
        if (onlineNum > 0) {
//                                    String onlineStr = "  " + onlineNum + "  ";
            String text = String.format(getResources().getString(R.string.online_txt_has), onlineNum);
            int remainWaitTime = getRemainTime();
            String waitTimeStr = ",请耐心等待！";
            if (remainWaitTime > 0) {
                waitTimeStr = ",请等待" + remainWaitTime + "分钟";
            }

            SpannableString ss = new SpannableString(text + waitTimeStr);
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#06b49b"));
            ss.setSpan(colorSpan1, 3, 3 + String.valueOf(onlineNum).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (remainWaitTime > 0) {
                ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#06b49b"));
                ss.setSpan(colorSpan2, text.length() + 4, text.length() + 4 + String.valueOf(remainWaitTime).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            onlineTipCs = ss;
            if (TextUtils.equals(TAG_SHOW, (String) tvMapFloatCenter.getTag())) {
                tvMapFloatCenter.setText(ss);
            }
        } else {
            onlineTipCs = getString(R.string.online_txt_no);
            if (TextUtils.equals(TAG_SHOW, (String) tvMapFloatCenter.getTag())) {
                tvMapFloatCenter.setText(getString(R.string.online_txt_no));
            }
        }
    }

    private int getRemainTime() {
        String time = orderDetailBean.getCreateTime();
        if (!TextUtils.isEmpty(time)) {
            try {
                String SimpleFromatDATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat formater = new SimpleDateFormat(SimpleFromatDATE_PATTERN, Locale.CHINA);
                Date date = formater.parse(time);
                Calendar target = Calendar.getInstance();
                target.setTime(date);

                long targetTime = target.getTimeInMillis();
                long currentTime = System.currentTimeMillis();

                long delt = currentTime - targetTime;
                if (delt < 30 * 60 * 1000) {
                    return 30 - (int) (delt / 60000);
                }


            } catch (Exception e) {
            }
        }

        return -1;
    }

    @SuppressLint("CheckResult")
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(OrderWaitReceiveActivity.this);
        rxPermission
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            isLocation = true;
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            isLocation = false;
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            isLocation = false;
                            String permissionName = "";
                            if (TextUtils.equals(Manifest.permission.ACCESS_FINE_LOCATION, permission.name)) {
                                permissionName = "地理位置";
                            }
                            permissionTipDialog = DialogHelper.getPermissionTipDialog(OrderWaitReceiveActivity.this, permissionName);
                            permissionTipDialog.show();
                        }
                    }
                });


    }

    private void getIntentData() {
        orderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(Constants.KEY_ORDER_DETAIL);
        if (orderDetailBean == null) {
            finish();
            return;
        }
        if (orderDetailBean != null) {
            orderId = orderDetailBean.getId();
        }
    }

    public static void activityStart(Context context, OrderDetailBean orderDetailBean) {
        Intent intent = new Intent(context, OrderWaitReceiveActivity.class);
        intent.putExtra(Constants.KEY_ORDER_DETAIL, orderDetailBean);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressedSupport() {
//        justFinish();
        backToOrderList();
    }

    private void justFinish() {
        OrderUtil.startLoopWaitReceive();
        finish();
    }

    private void backToOrderList() {
        OrderUtil.startLoopWaitReceive();
        if (isGetReceiver) {
            OrderUtil.toOrderListFragment(this, 1);
        } else {
            OrderUtil.toOrderListFragment(this, 0);
        }
        finish();
    }
}
