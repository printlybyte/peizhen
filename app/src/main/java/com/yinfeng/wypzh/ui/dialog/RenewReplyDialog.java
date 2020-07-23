package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.order.RenewDetail;
import com.yinfeng.wypzh.utils.ContextUtils;

public class RenewReplyDialog extends BaseDialog<RenewReplyDialog> {
    onToOrderDetailListener listener;
    RenewDetail detail;
    TextView tvTitle, tvOrderCode, tvRenewInfo;
    TextView tvHasKnow, tvToDetail;
    String orderId;
    String orderCode;
    String title;
    String renewInfo;

    public RenewReplyDialog(Context context, RenewDetail detail, onToOrderDetailListener listener) {
        super(context);
        mContext = context;
        this.listener = listener;
        this.detail = detail;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_renew_reply, null);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvOrderCode = view.findViewById(R.id.tvOrderCode);
        tvRenewInfo = view.findViewById(R.id.tvRenewInfo);
        tvHasKnow = view.findViewById(R.id.tvHasKnow);
        tvToDetail = view.findViewById(R.id.tvToDetail);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        if (detail != null) {
            orderCode = detail.getOrderCode();
            orderId = detail.getOrderId();
            String state = detail.getState();
            String updateTime = detail.getUpdateTime();
            String cancelReason = detail.getCancelReason();
            if (TextUtils.isEmpty(cancelReason))
                cancelReason = "未填写";
            int expire = detail.getExpire();
            int payPrice = detail.getPayPrice();//fen
            title = "延时请求反馈";
            renewInfo = "处理结果:未知状态\n" +
                    "延时订单:" + detail.getId();
            if (TextUtils.equals(state, Constants.ORDER_RENEW_REJECT)) {
                renewInfo = "处理时间:" + updateTime + "\n"
                        + "处理结果:已拒绝\n"
                        + "拒绝原因:" + cancelReason;
            }
            if (TextUtils.equals(state, Constants.ORDER_RENEW_AGREE)) {

                renewInfo = "处理时间:" + updateTime + "\n"
                        + "处理结果:已接单\n"
                        + "加时时长:" + expire + "分钟\n"
                        + "加时费用:" + ContextUtils.getPriceStrConvertFenToYuan(payPrice);
            }
            if (TextUtils.equals(state, Constants.ORDER_RENEW_OVERTIME)) {
                renewInfo = "处理结果:已超时";
            }
            tvTitle.setText(title);
            tvOrderCode.setText("订单号 :" + orderCode);
            tvRenewInfo.setText(renewInfo);
            tvHasKnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            tvToDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null)
                        listener.toDetail(orderId);
                }
            });
        }
    }


    public interface onToOrderDetailListener {
        void toDetail(String orderId);
    }

}
