package com.yinfeng.wypzh.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderInfo;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ImageUtil;

import java.util.List;

/**
 * @author Asen
 */
public class OrderWaitServiceAdapter extends BaseQuickAdapter<OrderDetailBean, BaseViewHolder> {
    public OrderWaitServiceAdapter(@Nullable List<OrderDetailBean> data) {
        super(R.layout.item_order_wait_service, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetailBean item) {
        TextView tvTimeRemain = helper.getView(R.id.tvTimeRemain);
        ImageView ivHeadIcon = helper.getView(R.id.ivHeadIcon);
//        ImageView ivPhone = helper.getView(R.id.ivPhone);
//        ImageView ivMsg = helper.getView(R.id.ivMsg);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvPhone = helper.getView(R.id.tvPhone);

        TextView tvOrderId = helper.getView(R.id.tvOrderId);
        TextView tvOrderTime = helper.getView(R.id.tvOrderTime);
        TextView tvServiceAddress = helper.getView(R.id.tvServiceAddress);
        TextView tvServiceTime = helper.getView(R.id.tvServiceTime);
        TextView tvServiceAsk = helper.getView(R.id.tvServiceAsk);
        TextView tvFinalPrice = helper.getView(R.id.tvFinalPrice);

        Button btConfirmStartService = helper.getView(R.id.btConfirmStartService);

//        String profile = item.getProfile();
        String name = item.getWaiterName();
        if (TextUtils.isEmpty(name))
            name = "";
        String waiterPhone = "联系电话:" + item.getWaiterPhone();

        String orderId = "订单号: " + item.getCode();
        String orderTime = "下单时间：" + item.getCreateTime();
        String serviceTime = ContextUtils.getServiceTime(item.getMakeStartTime(), item.getMakeEndTime());
        String serviceAddress = item.getHospitalName() + " " + item.getDepartmentName();
        String serviceAsk = item.getRemark();
        if (TextUtils.isEmpty(serviceAsk))
            serviceAsk = "";
        String finalPrice = ContextUtils.getPriceStrConvertFenToYuan(item.getPayPrice());

        String waiterInfoStr = item.getWaiter();
        if (!TextUtils.isEmpty(waiterInfoStr)) {
            try {
                WaiterInfo waiterInfo = new Gson().fromJson(waiterInfoStr, WaiterInfo.class);
                if (waiterInfo != null) {
                    String profile = waiterInfo.getProfile();
                    ImageUtil.getInstance().loadImgCircle(mContext, profile, ivHeadIcon);
                    ivHeadIcon.setTag(profile);
                }
            } catch (Exception e) {
            }
        }
        tvName.setText(name);
        tvPhone.setText(waiterPhone);
        tvOrderId.setText(orderId);
        tvOrderTime.setText(orderTime);
        tvServiceTime.setText(serviceTime);
        tvServiceAddress.setText(serviceAddress);
        tvServiceAsk.setText(serviceAsk);
        tvFinalPrice.setText(finalPrice);

        helper.addOnClickListener(R.id.ivPhone);
        helper.addOnClickListener(R.id.ivMsg);
        helper.addOnClickListener(R.id.llItem);
        helper.addOnClickListener(R.id.ivHeadIcon);
//        helper.addOnClickListener(R.id.rlPzyInfo);
        resetTimeRemain(tvTimeRemain, item.getMakeStartTime());
        String arriveState = item.getArrivedState();
        if (TextUtils.equals(arriveState, Constants.ORDER_WAIT_SERVICE_ARRIVE)) {
            btConfirmStartService.setClickable(true);
            helper.addOnClickListener(R.id.btConfirmStartService);
            btConfirmStartService.setBackgroundResource(R.drawable.shape_green_solid_corner);
        } else {
            btConfirmStartService.setClickable(false);
            btConfirmStartService.setBackgroundResource(R.drawable.shape_grey_solid_corner2);
        }
    }

    private void resetTimeRemain(TextView tv, String startTime) {
        String remainTime = ContextUtils.getRemainTimeWaitService(startTime);
        if (TextUtils.isEmpty(remainTime)) {
            tv.setText("");
        } else {
            if (TextUtils.equals(remainTime, "已超时")) {
                tv.setText(remainTime);
                tv.setTextColor(Color.RED);
            } else {
                tv.setText(remainTime);
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.c06b49b));
            }
        }
    }
}
