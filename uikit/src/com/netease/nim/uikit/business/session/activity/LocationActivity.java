package com.netease.nim.uikit.business.session.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.actions.PermissionTipDialogNew;
import com.netease.nim.uikit.business.session.fragment.MessageFragment;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;

import io.reactivex.functions.Consumer;

public class LocationActivity extends AppCompatActivity implements AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener {
    public static final String KEY_LATITUDE = "key_latitude";
    public static final String KEY_LONGITUDE = "key_longitude";
    public static final String KEY_ADDRESS = "key_address";

    private TopBarNew mTopBar;
    private ImageView ivGetOwnerLoc;
    private MapView mMapView;
    //地图控制器
    private AMap aMap;
    //定位需要的数据
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    AMapLocationListener mLocationListener;
    private HashMap<String, Boolean> infoShowMap;
    private AMap.InfoWindowAdapter mInfoWindowAdapter;
    private Marker mLocationMarker;
    private Marker mGPSMarker;
    private MarkerOptions markOptions;
    private LatLng latLng;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private AMapLocation location;
    private String actualAddr;
    private double actualLon;
    private double actualLat;
    protected Dialog mLoadingDialog = null;

    private boolean isJustShow = false;
    private double preLatitude;
    private double preLongitude;
    private String preAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mLoadingDialog = getLoadingDialog(this);
        bindView(savedInstanceState);

    }

    protected void showLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.show();
    }

    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public Dialog getLoadingDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialognew);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialognew, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setDimAmount(0.1f);
        return dialog;
    }

    private void bindView(Bundle savedInstanceState) {
        getIntentData();
        mTopBar = findViewById(R.id.topbarnew);
        mTopBar.setTopBarBackListener(new TopBarNew.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });
        mTopBar.setTopCenterTxt("地理位置");
        if (!isJustShow) {
            mTopBar.setTopRightTxt("确定");
            mTopBar.setTopBarRightTxtListener(new TopBarNew.TopBarRightTextCickListener() {
                @Override
                public void topRightTxtClick() {
                    if (!isJustShow) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_LATITUDE, latLng.latitude);
                        intent.putExtra(KEY_LONGITUDE, latLng.longitude);
                        intent.putExtra(KEY_ADDRESS, addressName);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        ivGetOwnerLoc = findViewById(R.id.ivGetOwnerLocNew);
        initMapView();
        initLocationClient();

        ivGetOwnerLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });

        aMap.setOnCameraChangeListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        checkLocationPermission();
    }

    @SuppressLint("CheckResult")
    private void checkLocationPermission() {
        RxPermissions rxPermission = new RxPermissions(LocationActivity.this);
        rxPermission
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            mLocationClient.startLocation();
                            showLoadingDialog();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            String permissionName = "";
                            if (TextUtils.equals(Manifest.permission.ACCESS_FINE_LOCATION, permission.name)) {
                                permissionName = "地理位置";
                            }
                            showPermissDialog(LocationActivity.this, permissionName);
                        }
                    }
                });

    }

    protected void showPermissDialog(Context context, String permissionName) {
        PermissionTipDialogNew dialog = new PermissionTipDialogNew(context, permissionName);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
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
                    Log.e("ss", "onCameraChangeFinish  onLocationChanged");
                    hideLoadingDialog();
                    if (isJustShow) {
                        LatLng preLatLng = new LatLng(preLatitude, preLongitude);
                        setMarket(preLatLng, preAddress, preAddress);
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(preLatLng, 17, 30, 0));
                        aMap.animateCamera(mCameraUpdate, 500, null);
                        mLocationClient.stopLocation();
                        return;
                    }
//                    if (aMapLocation != null) {
//                        if (aMapLocation.getErrorCode() == 0) {
//                            //可在其中解析amapLocation获取相应内容。
//                            double latitude = aMapLocation.getLatitude();//获取纬度
//                            double longitude = aMapLocation.getLongitude();//获取经度
//                            String address = aMapLocation.getAddress();
//                            Log.e("Location", " latitude " + latitude + "  longitude:" + longitude + "  address:" + address);
//                            showHospitalAddress(latitude, longitude, address);
//                        } else {
//                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                            Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:"
//                                    + aMapLocation.getErrorInfo());
//                            if (!TextUtils.isEmpty(aMapLocation.getErrorInfo())) {
//                                Toast.makeText(LocationActivity.this, aMapLocation.getErrorCode() + "#" + aMapLocation.getErrorInfo(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }
//                    mLocationClient.stopLocation();

                    location = aMapLocation;
                    if (location != null && location.getErrorCode() == 0) {

                        LatLng la = new LatLng(location.getLatitude(), location.getLongitude());
//                        setMarket(la, location.getCity(), location.getAddress());
                        setMarket(la, location.getAddress(), location.getAddress());
                        actualAddr = location.getAddress();
                        actualLon = location.getLongitude();
                        actualLat = location.getLatitude();

                        LatLng latLng = new LatLng(actualLat, actualLon);
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 17, 30, 0));
                        aMap.animateCamera(mCameraUpdate, 500, null);
                        mLocationClient.stopLocation();
                    } else {
                        if (isAGpsOpen(LocationActivity.this)) {
                            if (!isGpsOpen(LocationActivity.this)) {
                                Toast.makeText(LocationActivity.this, "请打开GPS开关", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            };
            mLocationClient.setLocationListener(mLocationListener);
        }
    }

    public boolean isGpsOpen(Context context) {
        // 通过GPS卫星定位,定位级别可以精确到街(通过24颗卫星定位,在室外和空旷的地方定位准确、速度快)
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps;
    }

    public boolean isAGpsOpen(Context context) {
        // 通过WLAN或移动网络(3G/2G)确定的位置(也称作AGPS,辅助GPS定位。主要用于在室内或遮盖物(建筑群或茂密的深林等)密集的地方定位)
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return network;
    }

    private void showHospitalAddress(double latitude, double longitude, String address) {
        LatLng latLng = new LatLng(latitude, longitude);
        showMyLocMarker(latLng, address);
        CameraUpdate mCameraUpdate;
        mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 16, 30, 0));
        aMap.animateCamera(mCameraUpdate, 500, null);
        mLocationMarker.startAnimation();
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
                .decodeResource(getResources(), R.drawable.location_mark)));
        //        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        markerOption.setFlat(false);//设置marker平贴地图效果
        mLocationMarker = aMap.addMarker(markerOption);
//        mLocationMarker.setAlpha(0.8f);
        mLocationMarker.setClickable(true);
        mLocationMarker.showInfoWindow();
        com.amap.api.maps.model.animation.Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
        markerAnimation.setDuration(1000);  //设置动画时间 单位毫秒
        mLocationMarker.setAnimation(markerAnimation);
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
//        aMap.getUiSettings().setLogoLeftMargin(ContextUtils.dip2px(this, 12));
        initInfoWindowAdapter();
        aMap.setInfoWindowAdapter(mInfoWindowAdapter);
    }

    private void initInfoWindowAdapter() {
        infoShowMap = new HashMap<>();
        mInfoWindowAdapter = new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = LayoutInflater.from(LocationActivity.this).inflate(R.layout.map_infowindow_new, null);
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

    private void getIntentData() {
        preLatitude = getIntent().getDoubleExtra(KEY_LATITUDE, -1d);
        preLongitude = getIntent().getDoubleExtra(KEY_LONGITUDE, -1d);
        preAddress = getIntent().getStringExtra(KEY_ADDRESS);
        if (preLatitude != -1)
            isJustShow = true;
    }

    public static void activityStart(Context context, double latitude, double longitude, String addressName) {
        if (context != null) {
            Intent intent = new Intent(context, LocationActivity.class);
            intent.putExtra(KEY_LATITUDE, latitude);
            intent.putExtra(KEY_LONGITUDE, longitude);
            intent.putExtra(KEY_ADDRESS, addressName);
            context.startActivity(intent);
        }
    }

    public static void activityStartForResult(MessageFragment fragment) {
        if (fragment != null) {
            Intent intent = new Intent(fragment.getActivity(), LocationActivity.class);
            fragment.startActivityForResult(intent, 0x110);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.e("hs", "onCameraChangeFinish");
        if (isJustShow)
            return;
        latLng = cameraPosition.target;
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        Log.e("latitude", latitude + "");
        Log.e("longitude", longitude + "");
        getAddress(latLng);
    }

    /**
     * 根据经纬度得到地址
     */
    public void getAddress(final LatLng latLonPoint) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        LatLonPoint point = new LatLonPoint(latLonPoint.latitude, latLonPoint.longitude);
        RegeocodeQuery query = new RegeocodeQuery(point, 500, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    private void setMarket(LatLng latLng, String title, String content) {
        if (mGPSMarker != null) {
            mGPSMarker.remove();
        }
        int statusBarHeight = getStatusBarHeight(this);
        int topbarHeight = (int) getResources().getDimension(R.dimen.topbarnew_height);
        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = (wm.getDefaultDisplay().getWidth()) / 2 + 22;
//        int height = ((wm.getDefaultDisplay().getHeight() - statusBarHeight - topbarHeight) / 2);
        int height = mMapView.getHeight() / 2 - 66;
        markOptions = new MarkerOptions();
//        markOptions.zIndex(2);
        markOptions.draggable(false);//设置Marker可拖动
        markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_mark))).anchor(0.5f, 0.7f);
//        markOptions.visible(true)
//                .autoOverturnInfoWindow(true);
        //设置一个角标
        mGPSMarker = aMap.addMarker(markOptions);
        //设置marker在屏幕的像素坐标
        mGPSMarker.setPosition(latLng);
        mGPSMarker.setTitle(title);
        mGPSMarker.setSnippet(content);

//        LatLng latLngNew = aMap.getCameraPosition().target;
        if (!isJustShow) {
            Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
            mGPSMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
            //设置像素坐标
            mGPSMarker.setPositionByPixels(width, height);
        }
        if (!TextUtils.isEmpty(content)) {
            mGPSMarker.showInfoWindow();
        }
        mMapView.invalidate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        // aMapEx.onRegister();
    }

    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        // 销毁定位
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                addressName = result.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi
                Log.e("逆地理编码回调", "得到的地址：" + addressName);
//              mAddressEntityFirst = new AddressSearchTextEntity(addressName, addressName, true, convertToLatLonPoint(mFinalChoosePosition));
                setMarket(latLng, addressName, addressName);
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    public int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
