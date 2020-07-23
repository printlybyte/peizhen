package com.yinfeng.wypzh.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.base.MyApplication;
import com.yinfeng.wypzh.bean.UserAgentBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.ui.ExistDialogActivity;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.ui.SplashActivity;
import com.yinfeng.wypzh.ui.dialog.WheelDialog;
import com.yinfeng.wypzh.ui.login.FillInfoActivity;
import com.yinfeng.wypzh.ui.order.OrderPaySuccessActivity;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cache;

import static com.yinfeng.wypzh.ui.MainActivity.KEY_EXIT_APP;
import static com.yinfeng.wypzh.ui.MainActivity.KEY_LOGOUT;
import static com.yinfeng.wypzh.ui.MainActivity.KEY_NEED_SWITCH_POSITION;


public class ContextUtils {
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * dip转换px
     */
    public static int dip2px(Context context, float dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2Dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

//    private static LayoutInflater inflater;
//
//    public static View inflate(Context context, int res) {
//        if (inflater == null) {
//            inflater = LayoutInflater.from(context);
//        }
//        return inflater.inflate(res, null);
//    }

    /**
     * 获取屏幕宽
     *
     * @param context
     * @return
     */
    public static int getSreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高
     *
     * @param context
     * @return
     */
    public static int getSreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundCompat(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * 获取圆形shapedrawable
     *
     * @param size  0 代表 wrapcontent >0 圆的宽高
     * @param color 圆的背景色
     * @return
     */
    public static ShapeDrawable getOvalShapeDrawable(int size, int color) {
        ShapeDrawable indicator = new ShapeDrawable(new OvalShape());
        indicator.setIntrinsicWidth(size);
        indicator.setIntrinsicHeight(size);
        indicator.getPaint().setColor(color);
        return indicator;
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getToken(Context context) {
        String token = MyApplication.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            token = SFUtil.getInstance().getToken(context);
            MyApplication.getInstance().setToken(token);
        }
        return token;
    }

    public static boolean isLogin(Context context) {
        String token = getToken(context);
        if (!TextUtils.isEmpty(token) && getCompleteSate(context)) {
            return true;
        }
        return false;
    }

    public static boolean getCompleteSate(Context context) {
        boolean isComplete = MyApplication.getInstance().isComplete();
        if (!isComplete) {
            isComplete = SFUtil.getInstance().getCompleteState(context);
            MyApplication.getInstance().setComplete(isComplete);
        }
        return isComplete;
    }

    /**
     * 获取UserAgent 机型 android app_version 蜂窝/wifi
     *
     * @return
     */
    public static UserAgentBean getUserAgent() {
        UserAgentBean userAgentBean = new UserAgentBean();
        //动态改变
//        setNetState(MDApplication.getInstance(), userAgentBean);
        String versionCode = getAppVersionCode(MyApplication.getInstance());
        userAgentBean.setVersionCode(versionCode);
        setDeviceInfo(userAgentBean);
        return userAgentBean;
    }

    public static String getUserAgentJsonStr() {
        String userAgentStr = MyApplication.getInstance().getUserAgentStr();
        if (TextUtils.isEmpty(userAgentStr)) {
            UserAgentBean userAgentBean = getUserAgent();
            userAgentStr = new Gson().toJson(userAgentBean);
            MyApplication.getInstance().setUserAgentStr(userAgentStr);
        }
        return userAgentStr;
    }

    public static String getNetState(Context context) {
        String netTypeName = NetUtil.getNetworkTypeName(context);
        return netTypeName;
    }

    private static UserAgentBean setDeviceInfo(UserAgentBean bean) {
//        Field[] fields = Build.class.getDeclaredFields();
        try {
            Field brandField = Build.class.getDeclaredField("BRAND");
            Field modelField = Build.class.getDeclaredField("MODEL");
            Field displayField = Build.class.getDeclaredField("DISPLAY");

            String brandValue = brandField.get(null).toString();
            String modelValue = modelField.get(null).toString();
            String displayValue = displayField.get(null).toString();

            bean.setMobile_brand(brandValue);
            bean.setMobile_model(modelValue);
            bean.setMobile_display(displayValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return bean;
    }

    public static String getAppVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
//                String versionName = pi.versionName == null ? "null"
//                        : pi.versionName;
                versionCode = pi.versionCode + "";
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionCode;
    }

    /**
     * 获取网络请求缓存
     *
     * @return
     */
    public static Cache getOkHttpCache() {
        Cache cache = new Cache(new File(MyApplication.getInstance().getCacheDir(), "HttpCache"),
                1024 * 1024 * 100);
        return cache;
    }

    public static boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    public static boolean isValidPhoneNum(String phoneNum) {
        LogUtil.info("正在校验的手机号是 : " + phoneNum);
        String phoneNumbPatternStr = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        Pattern phoneNumbPattern = Pattern.compile(phoneNumbPatternStr);
        Matcher matcher = phoneNumbPattern.matcher(phoneNum);
        return matcher.find();
    }

    public static boolean isPassWordValid(String content) {
        //必须有数字有字母，组成只能是字母或数字 6-20位
//        String patternStr = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";
        //字母或数字组成 6-20位
        String patternStr = "^[0-9a-zA-Z]{6,20}$";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }


    public static ObjectAnimator getShakeObjAnim(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.shake_delt);
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(800);
    }


    /**
     * 强制隐藏软键盘
     */
    public static void hideSoftInput(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 强制显示软键盘
     */
    public static void showSoftInput(final Activity activity, final View input) {
        Timer timer = new Timer();//设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }

        }, 300);//设置300毫秒的时长

//        View view = activity.getWindow().peekDecorView();
//        if (view != null) {
//            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputmanger.showSoftInputFromInputMethod(view.getWindowToken(), 0);
//        }
    }

    /**
     * 富文本点击
     *
     * @param textView
     */
    public static void setTextViewLinkClickable(TextView textView) {
        textView.setMovementMethod(ClickableMovementMethod.getInstance());
        // Reset for TextView.fixFocusableAndClickableSettings(). We don't want View.onTouchEvent()
        // to consume touch events.
        textView.setClickable(false);
        textView.setLongClickable(false);
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(WheelDialog.SimpleFromatDATE_PATTERN);
        return formatter.format(date);
    }

    public static int getMaxDaysNum(String time) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat(WheelDialog.SimpleFromatDATE_PATTERN);
            gc.setTime(sdf.parse(time));//设置指定时间日历
//            if (gc.isLeapYear(gc.get(Calendar.YEAR))) {//判断指定日历所在年份是否为闰年
//                LogUtil.info(gc.get(Calendar.YEAR) + "年是闰年");
//            } else {
//                LogUtil.info(gc.get(Calendar.YEAR) + "年不是闰年");
//            }
            return gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 拍照
     *
     * @param activity
     * @param requestCode
     */
    public static void takePhoto(Activity activity, String tempFilePath, int requestCode) {
        // 跳转到系统的拍照界面
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定照片存储位置为sd卡本目录下
        // 这里设置为固定名字 这样就只会只有一张temp图 如果要所有中间图片都保存可以通过时间或者加其他东西设置图片的名称
        // File.separator为系统自带的分隔符 是一个固定的常量
        // 获取图片所在位置的Uri路径
        Uri imageUri = getImgUri(activity, tempFilePath);
//        File imageFile = new File(tempFilePath);
//        Uri imageUri = Uri.fromFile(imageFile);
        //下面这句指定调用相机拍照后的照片存储的路径
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intentToTakePhoto, requestCode);
    }

//    /**
//     * 拍照
//     *
//     * @param fragment
//     * @param mTempPicName 照片名称 wypzheader.jpeg .jpeg/png结尾
//     * @param requestCode
//     */
//    public static void takePhoto(Fragment fragment, String mTempPicName, int requestCode) {
//        // 跳转到系统的拍照界面
//        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 指定照片存储位置为sd卡本目录下
//        // 这里设置为固定名字 这样就只会只有一张temp图 如果要所有中间图片都保存可以通过时间或者加其他东西设置图片的名称
//        // File.separator为系统自带的分隔符 是一个固定的常量
//        String mTempPhotoPath = getImgPath(mTempPicName);
//        // 获取图片所在位置的Uri路径
//        Uri imageUri = getImgUri(fragment.getActivity(), mTempPhotoPath);
//        //下面这句指定调用相机拍照后的照片存储的路径
//        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        fragment.startActivityForResult(intentToTakePhoto, requestCode);
//    }

    /**
     * 获取sd下图片路径
     *
     * @param imgNameWithSuffix 带有后缀的图片名称 imgname.jpeg
     * @return
     */
    public static String getImgPath(String imgNameWithSuffix) {
        return Environment.getExternalStorageDirectory() + File.separator + imgNameWithSuffix;
    }

    /**
     * 获取文件路径对应的Uri
     *
     * @param context
     * @param imgPathAbsolute 文件的绝对路径
     * @return
     */
    public static Uri getImgUri(Context context, String imgPathAbsolute) {
        return FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".my.provider",
                new File(imgPathAbsolute));
    }

    /**
     * 系统相册选取一张
     *
     * @param activity
     * @param requestCode
     */
    public static void choosePhoto(Activity activity, int requestCode) {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intentToPickPic, requestCode);
    }

    /**
     * 系统相册选取一张
     *
     * @param fragment
     * @param requestCode
     */
    public static void choosePhoto(Fragment fragment, int requestCode) {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        fragment.startActivityForResult(intentToPickPic, requestCode);
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     * 直接打电话（ACTION_CALL）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * html特殊转义
     */
    public static String replaceHtmlCotnent(String content) {
        if (!TextUtils.isEmpty(content)) {
            content = content.replace("&#039;", "'");
            content = content.replace("&quot;", "\"");
            content = content.replace("&lt;", "<");
            content = content.replace("&gt;", ">");
            content = content.replace("&amp;", "&");
        }
        return content;
    }

    public static String getOrderStateTip(String orderState) {
        String tip = "";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_SUBMIT))
            tip = "订单已提交";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_PAID))
            tip = "订单已支付";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_TAKE))
            tip = "已接单";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_SERVICE))
            tip = "服务进行中";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_COMPLETE))
            tip = "订单已完成";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_CANCEL))
            tip = "订单已取消";
        if (TextUtils.equals(orderState, Constants.ORDER_STATE_ERROR))
            tip = "订单异常";
        return tip;
    }

    public static String getPriceStrConvertFenToYuan(int priceFen) {
        double priceYuan = (double) priceFen / 100d;
//        BigDecimal bg = new BigDecimal(priceYuan);
//        double priceD = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return "￥" + df.format(priceYuan);
    }

    public static boolean copyFileToFile(File source, File dest) {
        boolean flag = false;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            flag = true;
        } catch (Exception e) {
            flag = false;
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (Exception exception) {
            }
        }
        return flag;
    }

    public static boolean copyFileToFileCompress(Context context, File source, File dest) {
        boolean flag = false;
        try {
            Bitmap rawBitmap = BitmapFactory.decodeFile(source.getAbsolutePath());
            int screenWidth = ContextUtils.getSreenWidth(context);
            Bitmap compressBitmap = ImageUtil.getInstance().compressAndScaleBitmap(rawBitmap, screenWidth, screenWidth);
            ImageUtil.getInstance().saveBitmapToFile(dest, compressBitmap, true);
            rawBitmap.recycle();
            flag = true;
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static void exitApp(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//service 中开启 栈中无此类
        intent.putExtra(KEY_EXIT_APP, true);
        context.startActivity(intent);
    }

    public static void kickOut(Context context) {
        ContextUtils.removeUmengAlias(context);
        SFUtil.getInstance().clearAllSf(context);
        MyApplication.getInstance().cleanAllConfig();
        IMUtil.logoutIM();
        ContextUtils.closeUmengPush(context);
        UmUtil.removeAllNotification(context);
    }

    public static void logOut(Context context) {
//        SFUtil.getInstance().clearAllSf(context);
//        MyApplication.getInstance().cleanAllConfig();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//service 中开启 栈中无此类
        intent.putExtra(KEY_LOGOUT, true);
        context.startActivity(intent);
    }

    public static void clickHome(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(intent);
    }

    public static String getServiceTime(String startTime, String endTime) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime))
            return "";
        try {
            String startYear = startTime.substring(0, 5);
            String endYear = endTime.substring(0, 5);
            if (TextUtils.equals(startYear, endYear)) {
                String temp = endTime.substring(5, endTime.length());
                return startTime + "\u2014" + temp;
            } else {
                return startTime + "\u2014" + endTime;
            }
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean isZfbAvilable(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * sina
     * 判断是否安装新浪微博
     */
    public static boolean isSinaInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getRemainTimeWaitService(String time) {
        try {
            String SimpleFromatDATE_PATTERN = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formater = new SimpleDateFormat(SimpleFromatDATE_PATTERN, Locale.CHINA);
            Date date = formater.parse(time);
            Calendar target = Calendar.getInstance();
            target.setTime(date);

            int yearTarget = target.get(Calendar.YEAR);
            int monthTarget = target.get(Calendar.MONTH) + 1;
            int dayTarget = target.get(Calendar.DAY_OF_MONTH);
//            int hourTarget = target.get(Calendar.HOUR_OF_DAY);
//            int minuteTarget = target.get(Calendar.MINUTE);

            Calendar current = Calendar.getInstance();
            int yearCurrent = current.get(Calendar.YEAR);
            int monthCurrent = current.get(Calendar.MONTH) + 1;
            int dayCurrent = current.get(Calendar.DAY_OF_MONTH);
//            int hourCurrent = current.get(Calendar.HOUR_OF_DAY);
//            int minuteCurrent = current.get(Calendar.MINUTE);
            int year, month, day, hour, minute;
            long delt = target.getTimeInMillis() - System.currentTimeMillis();
            if (delt >= 0) {
                if (delt / 1000L > 60 * 60 * 24 * 30 * 12) {
                    if (monthTarget > monthCurrent)
                        return (yearTarget - yearCurrent) + "年零" + (monthTarget - monthCurrent) + "个月";
                    return (yearTarget - yearCurrent) + "年";
                }
                if (delt / 1000L > 60 * 60 * 24 * 30) {
                    if (dayTarget > dayCurrent)
                        return (monthTarget - monthCurrent) + "个月零" + (dayTarget - dayCurrent) + "天";
                    return (monthTarget - monthCurrent) + "个月";
                }
                if (delt > 60 * 60 * 24 * 1000) {
                    day = (int) (delt / (60 * 60 * 24 * 1000));
                    hour = (int) ((delt % (60 * 60 * 24 * 1000)) / (60 * 60 * 1000));
                    if (hour > 0)
                        return day + "天" + hour + "个小时";
                    return day + "天";
                }
                if (delt > 60 * 60 * 1000) {
                    hour = (int) (delt / (60 * 60 * 1000));
                    minute = (int) ((delt % (60 * 60 * 1000)) / (60 * 1000));
                    if (minute > 0)
                        return hour + "小时零" + minute + "分钟";
                    return hour + "小时";
                }
                if (delt > 60 * 1000) {
                    minute = (int) (delt / 60000);
                    return minute + "分钟";
                }
            } else {
                return "已超时";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRemainTimeServicing(String time) {
        try {
            String SimpleFromatDATE_PATTERN = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formater = new SimpleDateFormat(SimpleFromatDATE_PATTERN, Locale.CHINA);
            Date date = formater.parse(time);
            Calendar target = Calendar.getInstance();
            target.setTime(date);

            int yearTarget = target.get(Calendar.YEAR);
            int monthTarget = target.get(Calendar.MONTH) + 1;
            int dayTarget = target.get(Calendar.DAY_OF_MONTH);
            int hourTarget = target.get(Calendar.HOUR_OF_DAY);
            int minuteTarget = target.get(Calendar.MINUTE);

            Calendar current = Calendar.getInstance();
            int yearCurrent = current.get(Calendar.YEAR);
            int monthCurrent = current.get(Calendar.MONTH) + 1;
            int dayCurrent = current.get(Calendar.DAY_OF_MONTH);
            int hourCurrent = current.get(Calendar.HOUR_OF_DAY);
            int minuteCurrent = current.get(Calendar.MINUTE);

            long targetTime = target.getTimeInMillis();
            long currentTime = System.currentTimeMillis();
            long max = Math.max(targetTime, currentTime);
            boolean isOut = (max == currentTime);

            int year, month, day, hour, minute;
            if (!isOut) {
                long delt = targetTime - currentTime;
                if (delt / 1000L > 60 * 60 * 24 * 30 * 12) {
                    if (monthTarget > monthCurrent)
                        return (yearTarget - yearCurrent) + "年零" + (monthTarget - monthCurrent) + "个月";
                    return (yearTarget - yearCurrent) + "年";
                }
                if (delt / 1000L > 60 * 60 * 24 * 30) {
                    if (dayTarget > dayCurrent)
                        return (monthTarget - monthCurrent) + "个月零" + (dayTarget - dayCurrent) + "天";
                    return (monthTarget - monthCurrent) + "个月";
                }
                if (delt > 60 * 60 * 24 * 1000) {
                    day = (int) (delt / (60 * 60 * 24 * 1000));
                    hour = (int) ((delt % (60 * 60 * 24 * 1000)) / (60 * 60 * 1000));
                    if (hour > 0)
                        return day + "天" + hour + "个小时";
                    return day + "天";
                }
                if (delt > 60 * 60 * 1000) {
                    hour = (int) (delt / (60 * 60 * 1000));
                    minute = (int) ((delt % (60 * 60 * 1000)) / (60 * 1000));
                    if (minute > 0)
                        return hour + "小时零" + minute + "分钟";
                    return hour + "小时";
                }
                if (delt > 60 * 1000) {
                    minute = (int) (delt / 60000);
                    return minute + "分钟";
                }
            } else {
                return "已超时";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void callComplainPhone(Context context) {
        String phone = SFUtil.getInstance().getComplainPhone(context);
        if (!TextUtils.isEmpty(phone))
            callPhone(context, phone);
    }

    public static WaiterInfo getWaiterInfo(OrderDetailBean bean) {
        if (bean != null && !TextUtils.isEmpty(bean.getWaiter())) {
            Gson gson = new Gson();
            WaiterInfo info = gson.fromJson(bean.getWaiter(), WaiterInfo.class);
            return info;
        }

        return null;
    }

    public static void setUmengAlias(Context context, String newAlias, String newType, UTrack.ICallBack callback) {
        PushAgent.getInstance(context).setAlias(newAlias, newType, callback);
    }

    public static void removeUmengAlias(Context context) {
        String deviceToken = PushAgent.getInstance(context).getRegistrationId();
        if (!TextUtils.isEmpty(deviceToken)) {
            String alias = SFUtil.getInstance().getAliasByDeviceToken(context, deviceToken);
            if (!TextUtils.isEmpty(alias)) {
                PushAgent.getInstance(context).deleteAlias(alias, Constants.UMEGN_ALIAS_TYPE_MEMBER, new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean b, String s) {

                    }
                });
            }
        }
    }

    public static void closeUmengPush(Context context) {
//        PushAgent.getInstance(context).disable(new IUmengCallback() {
//
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//
//            }
//
//        });
    }

    public static void reOpenUmengPush(Context context) {
        PushAgent.getInstance(context).enable(new IUmengCallback() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String s, String s1) {

            }

        });
    }

    public static void showKickDialog(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ExistDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取本地软件版本号
     */
    public static int getLocalVersion(Context context) {
        int localVersion = 1;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            LogUtil.error("本软件的版本号：" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            LogUtil.error("本软件的版本名：" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static void addTagsUm(Context context, String... tags) {

        PushAgent.getInstance(context).getTagManager().addTags(new TagManager.TCallBack() {

            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {

            }

        }, tags);
    }

    /**
     * 复制粘贴 文本
     */

    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        try {
            ClipboardManager cmb = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean isGpsOpen(Context context) {
        // 通过GPS卫星定位,定位级别可以精确到街(通过24颗卫星定位,在室外和空旷的地方定位准确、速度快)
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        LogUtil.error("GPS Open : " + gps);
        return gps;
    }

    public static boolean isAGpsOpen(Context context) {
        // 通过WLAN或移动网络(3G/2G)确定的位置(也称作AGPS,辅助GPS定位。主要用于在室内或遮盖物(建筑群或茂密的深林等)密集的地方定位)
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        LogUtil.error("AGps Open : " + network);
        return network;
    }
    public static void skipToSettingDetail(Context context) {
        Uri packageURI = Uri.parse("package:" + "com.yinfeng.wypzh");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        context.startActivity(intent);
    }
}


//        for (Field field : fields) {
//            try {
//                field.setAccessible(true);
////                infos.put(field.getName(), field.get(null).toString());
//                LogUtil.error("MobileInfos", field.getName() + " : " + field.get(null));
//            } catch (Exception e) {
//            }
//        }