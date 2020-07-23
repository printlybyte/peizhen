package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderInfo;
import com.yinfeng.wypzh.utils.ContextUtils;

import java.util.List;

/**
 * @author Asen
 */
public class OrderWaitReceiveAdapter extends BaseQuickAdapter<OrderDetailBean, BaseViewHolder> {

    public OrderWaitReceiveAdapter(@Nullable List<OrderDetailBean> data) {
        super(R.layout.item_order_wait_receive, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetailBean item) {
        TextView tvOrderId = helper.getView(R.id.tvOrderId);
        TextView tvOrderTime = helper.getView(R.id.tvOrderTime);
        TextView tvServiceAddress = helper.getView(R.id.tvServiceAddress);
        TextView tvServiceTime = helper.getView(R.id.tvServiceTime);
        TextView tvServiceAsk = helper.getView(R.id.tvServiceAsk);
        TextView tvFinalPrice = helper.getView(R.id.tvFinalPrice);

        String orderId = "订单号: " + item.getCode();
        String orderTime = "下单时间：" + item.getCreateTime();
        String serviceTime = ContextUtils.getServiceTime(item.getMakeStartTime(), item.getMakeEndTime());
        String serviceAddress = item.getHospitalName() + " " + item.getDepartmentName();
        String serviceAsk = item.getRemark();
        if (TextUtils.isEmpty(serviceAsk))
            serviceAsk = "";
        String finalPrice = ContextUtils.getPriceStrConvertFenToYuan(item.getPayPrice());

        tvOrderId.setText(orderId);
        tvOrderTime.setText(orderTime);
        tvServiceTime.setText(serviceTime);
        tvServiceAddress.setText(serviceAddress);
        tvServiceAsk.setText(serviceAsk);
        tvFinalPrice.setText(finalPrice);
        helper.addOnClickListener(R.id.btCancelOrder);
        helper.addOnClickListener(R.id.llItem);
    }
}
