package com.yinfeng.wypzh.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.yinfeng.wypzh.R;


public class ToastUtil {


    private static ToastUtil instance;

    private ToastUtil() {
    }

    public static ToastUtil getInstance() {
        if (instance == null) {
            synchronized (ToastUtil.class) {
                if (instance == null) {
                    instance = new ToastUtil();
                }
            }
        }
        return instance;
    }

    private Toast currentToast;
    private String currentContent;

    public void showShort(Context context, String content) {
        show(context, content, 1500, Gravity.CENTER);
    }

    public void showLong(Context context, String content) {
        show(context, content, 2500, Gravity.CENTER);
    }

    public void show(Context context, String content, int duration, int gravity) {
        if (context == null)
            return;
        if (TextUtils.isEmpty(content))
            return;
        if (currentToast != null) {
            if (TextUtils.equals(content, currentContent)) {
                currentToast.show();
                return;
            }
            currentToast.cancel();
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Looper.prepare();
            currentToast = createCustomToast(context, content, duration, gravity);
            currentToast.show();
            currentContent = content;
            Looper.loop();
            return;
        }
        currentToast = createCustomToast(context, content, duration, gravity);
        currentToast.show();
        currentContent = content;
    }

    public void cancle() {
        if (currentToast != null)
            currentToast.cancel();
    }

    private Toast createCustomToast(Context context, String content, int duration, int gravity) {
        Toast customToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        customToast.setDuration(duration);
        //view 可以通过 LayoutInflater.from(context).inflat(R.layout.yourview);
        TextView view = new TextView(context);
        view.setBackgroundResource(R.drawable.shape_toast_bg);
        view.setText(content);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        int padding = ContextUtils.dip2px(context, 12);
        view.setPadding(padding, padding, padding, padding);
        view.setTextColor(Color.WHITE);
        customToast.setView(view);
        int yOffset = 0;
        if (gravity == Gravity.BOTTOM)
            yOffset = 200;
        customToast.setGravity(gravity, 0, yOffset);
        return customToast;
    }

}
