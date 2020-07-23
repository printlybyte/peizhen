package com.yinfeng.wypzh.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.widget.MaterialDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.order.OrderCancelReason;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.RenewDetail;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailBean;
import com.yinfeng.wypzh.ui.dialog.CancelOrderDialog;
import com.yinfeng.wypzh.ui.dialog.CancelOrderDialogNew;
import com.yinfeng.wypzh.ui.dialog.CaptureAndGalleryDialog;
import com.yinfeng.wypzh.ui.dialog.HasArrivedDialog;
import com.yinfeng.wypzh.ui.dialog.HasTakeOrderDialog;
import com.yinfeng.wypzh.ui.dialog.OrderTimeOutDialog;
import com.yinfeng.wypzh.ui.dialog.OrderTipDialog;
import com.yinfeng.wypzh.ui.dialog.OrderTipDialogNew;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.ui.dialog.RenewOrderDialog;
import com.yinfeng.wypzh.ui.dialog.RenewReplyDialog;
import com.yinfeng.wypzh.ui.dialog.RenewReplyDialogNew;
import com.yinfeng.wypzh.ui.dialog.WheelDialog;
import com.yinfeng.wypzh.ui.dialog.WheelDialogNew;

import java.util.List;


public class DialogHelper {

    public static Dialog getLoadingDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setDimAmount(0.1f);
        return dialog;
    }

    public static PermissionTipDialog getPermissionTipDialog(Context context, String permissionName) {
        PermissionTipDialog dialog = new PermissionTipDialog(context, permissionName);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static CaptureAndGalleryDialog getGalleryAndCaptureDialog(Context context, CaptureAndGalleryDialog.CaptureAndGallerySelectListener listener) {
        CaptureAndGalleryDialog dialog = new CaptureAndGalleryDialog(context, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static WheelDialog getWheelDialog(Context context, String time, WheelDialog.WheelTimeSelectListener listener) {
        WheelDialog dialog = new WheelDialog(context, time, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.showAnim(new SlideBottomEnter());
        dialog.dismissAnim(new SlideBottomExit());
        return dialog;
    }

    public static WheelDialogNew getWheelDialogNew(Context context, int servicTimeDelt, int advanceTimeDelt, String start, String end, String time, WheelDialogNew.WheelTimeSelectListener listener) {
        WheelDialogNew dialog = new WheelDialogNew(context, servicTimeDelt, advanceTimeDelt, start, end, time, listener);
//        dialog.onCreateView();
//        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.showAnim(new SlideBottomEnter());
        dialog.dismissAnim(new SlideBottomExit());
        return dialog;
    }

    public static MaterialDialog getMaterialDialog(Context context, String content) {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.isTitleShow(false)
                .content(content).btnText("取消", "确定")
                .showAnim(new FadeEnter())
                .dismissAnim(new FadeExit());
//                .showAnim(new ZoomInTopEnter())
//                .dismissAnim(new ZoomOutBottomExit());
        return dialog;
    }

    public static MaterialDialog getDownLoadDialog(Context context, String title, String content) {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.title(title)
                .content(content).btnText("取消", "后台下载")
                .showAnim(new FadeEnter());
//                .dismissAnim(new FadeExit());
//                .showAnim(new ZoomInTopEnter())
//                .dismissAnim(new ZoomOutBottomExit());
        return dialog;
    }

    /**
     * 点击立即finish dialog来不及完成动画回收报错
     *
     * @param context
     * @param content
     * @return
     */
    public static MaterialDialog getMaterialDialogQuick(Context context, String content) {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.isTitleShow(false)
                .content(content).btnText("取消", "确定")
//                .showAnim(new ZoomInBottomEnter());
                .showAnim(new FadeEnter());
        return dialog;
    }

    /**
     * 一个button
     * 点击立即finish dialog来不及完成动画回收报错
     *
     * @param context
     * @param content
     * @return
     */
    public static MaterialDialog getMaterialDialogOneQuick(Context context, String content) {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.isTitleShow(false)
                .btnNum(1)
                .content(content).btnText("确定")
                .showAnim(new FadeEnter());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static CancelOrderDialog getCancelOrderDialog(Context context, CancelOrderDialog.OnCancelOrderListener listener) {
        CancelOrderDialog dialog = new CancelOrderDialog(context, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static CancelOrderDialogNew getCancelOrderDialogNew(Context context, List<OrderCancelReason> list, CancelOrderDialogNew.OnCancelOrderListener listener) {
        CancelOrderDialogNew dialog = new CancelOrderDialogNew(context, listener);
        dialog.setCancelReasonList(list);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 延时弹窗
     *
     * @param context
     * @param listener
     * @return
     */
    public static RenewOrderDialog getRenewOrderDialog(Context context, ServiceOptionDetailBean bean, RenewOrderDialog.OnConfirmRenewOrderListener listener) {
        RenewOrderDialog dialog = new RenewOrderDialog(context, bean, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 延时反馈天窗
     *
     * @param context
     * @param listener
     * @return
     */
    public static RenewReplyDialog getRenewReplyDialog(Context context, RenewDetail bean, RenewReplyDialog.onToOrderDetailListener listener) {
        RenewReplyDialog dialog = new RenewReplyDialog(context, bean, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static HasTakeOrderDialog getHasTakeOrderDialog(Context context) {
        HasTakeOrderDialog dialog = new HasTakeOrderDialog(context);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static HasArrivedDialog getHasArrivedDialog(Context context) {
        HasArrivedDialog dialog = new HasArrivedDialog(context);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCancelable(false);
        return dialog;
    }


    //=========订单状态弹框 通知/轮询=======

    public static OrderTipDialog getOrderTipDialog(Context context, OrderDetailBean bean, String content, OrderTipDialog.goToDetailListener listener) {
        OrderTipDialog dialog = new OrderTipDialog(context, bean, content, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static OrderTimeOutDialog getOrderTimeOutDialog(Context context, OrderDetailBean bean, String content, OrderTimeOutDialog.toSeeListener listener) {
        OrderTimeOutDialog dialog = new OrderTimeOutDialog(context, bean, content, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static OrderTipDialogNew getOrderTipDialogNew(Context context, OrderDetailBean bean, String content, OrderTipDialogNew.toSeeListener listener) {
        OrderTipDialogNew dialog = new OrderTipDialogNew(context, bean, content, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static RenewReplyDialogNew getOrderRenewReplyNew(Context context, String orderId, String orderCode, String content, RenewReplyDialogNew.toSeeListener listener) {
        RenewReplyDialogNew dialog = new RenewReplyDialogNew(context, orderId, orderCode, content, listener);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }


    //=========订单状态弹框 通知/轮询=======


}
