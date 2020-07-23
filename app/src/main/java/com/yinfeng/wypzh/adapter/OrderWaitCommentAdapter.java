package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderInfo;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ImageUtil;

import java.util.List;

/**
 * @author Asen
 */
public class OrderWaitCommentAdapter extends BaseQuickAdapter<OrderDetailBean, BaseViewHolder> {

    public OrderWaitCommentAdapter(@Nullable List<OrderDetailBean> data) {
        super(R.layout.item_order_comment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetailBean item) {
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
//        ImageUtil.getInstance().loadImgCircle(mContext, profile, ivHeadIcon);
        tvName.setText(name);
        tvPhone.setText(waiterPhone);
        tvOrderId.setText(orderId);
        tvOrderTime.setText(orderTime);
        tvServiceTime.setText(serviceTime);
        tvServiceAddress.setText(serviceAddress);
        tvServiceAsk.setText(serviceAsk);
        tvFinalPrice.setText(finalPrice);


        helper.addOnClickListener(R.id.btComment);
        helper.addOnClickListener(R.id.ivPhone);
        helper.addOnClickListener(R.id.ivMsg);
        helper.addOnClickListener(R.id.llItem);
        helper.addOnClickListener(R.id.ivHeadIcon);
    }
}
