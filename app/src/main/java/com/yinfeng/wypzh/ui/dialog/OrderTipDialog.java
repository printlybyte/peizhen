package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.RenewDetail;
import com.yinfeng.wypzh.utils.ContextUtils;

public class OrderTipDialog extends BaseDialog<OrderTipDialog> {
    OrderDetailBean detail;
    TextView tvOrderCode;
    TextView tvContent;
    TextView tvLeft, tvRight;
    String content;
    goToDetailListener listener;

    public OrderTipDialog(Context context, OrderDetailBean detail, String content, goToDetailListener listener) {
        super(context);
        mContext = context;
        this.detail = detail;
        this.content = content;
        this.listener = listener;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_order_tip, null);
        tvOrderCode = view.findViewById(R.id.tvOrderCode);
        tvContent = view.findViewById(R.id.tvContent);
        tvLeft = view.findViewById(R.id.tvLeft);
        tvRight = view.findViewById(R.id.tvRight);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        if (detail != null) {
            String orderCode = detail.getCode();
            tvOrderCode.setText(orderCode);
            if (!TextUtils.isEmpty(content))
                tvContent.setText(content);
            tvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null)
                        listener.toDetail(detail);
                }
            });
        }
    }


    public interface goToDetailListener {
        void toDetail(OrderDetailBean bean);
    }

}
