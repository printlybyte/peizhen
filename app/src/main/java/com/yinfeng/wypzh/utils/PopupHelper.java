package com.yinfeng.wypzh.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.yinfeng.wypzh.ui.popup.WheelPopup;

public class PopupHelper {
    public static WheelPopup getWheelPopup(Context context, View anchorView) {
        WheelPopup wheelPopup = new WheelPopup(context);
        wheelPopup.onCreatePopupView();
        wheelPopup.setUiBeforShow();
        wheelPopup.alignCenter(true)
                .anchorView(anchorView)//弹框从哪个控件里弹出，(这里的sort_tv是效果图中显示排序的TextView)
                .gravity(Gravity.BOTTOM)//弹框从控件的哪里弹出。这里设置的是底部
                .showAnim(new SlideBottomEnter())//选择弹出动画
                .dismissAnim(new SlideBottomExit())//选择消失动画
                .offset(0, 0)//设置弹窗的偏移量，这个你们不用管。
                .dimEnabled(false);//弹窗是否具有强制性（点其他位置会不会消失，false是无强制性，会消失）
        return wheelPopup;
    }
}
