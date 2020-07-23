package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ToastUtil;

public class OrderTimeOutDialog extends BaseDialog<OrderTimeOutDialog> {
    OrderDetailBean detail;
    LinearLayout llAll;
    TextView tvTip;
    TextView tvOrderId;
    TextView tvToSee, tvToCancel;
    String content;
    toSeeListener listener;

    public OrderTimeOutDialog(Context context, OrderDetailBean detail, String content, toSeeListener listener) {
        super(context);
        mContext = context;
        this.detail = detail;
        this.content = content;
        this.listener = listener;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_order_time_out, null);
        llAll = view.findViewById(R.id.llAll);
        tvTip = view.findViewById(R.id.tvTip);
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvToSee = view.findViewById(R.id.tvToSee);
        tvToCancel = view.findViewById(R.id.tvToCancel);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        if (detail != null) {
            final String orderCode = detail.getCode();
            tvOrderId.setText(orderCode);
            if (!TextUtils.isEmpty(content))
                tvTip.setText(content);
            tvToSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null)
                        listener.toNext(detail);
                }
            });
            tvToCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null)
                        listener.toCancel(detail);
                }
            });
            llAll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!TextUtils.isEmpty(orderCode)) {
                        ContextUtils.copy(mContext, orderCode);
                        ToastUtil.getInstance().showShort(mContext, "已复制订单号");
                    }
                    return true;
                }
            });
        }
    }


    public interface toSeeListener {
        void toNext(OrderDetailBean bean);

        void toCancel(OrderDetailBean bean);
    }

}
