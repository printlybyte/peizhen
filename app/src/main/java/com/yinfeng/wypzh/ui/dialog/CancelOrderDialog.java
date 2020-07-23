package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;

public class CancelOrderDialog extends BaseDialog<CancelOrderDialog> {
    private Context mContext;
    private LinearLayout llOption1, llOption2;
    private ImageView ivOption1, ivOption2;
    private TextView tvCancel, tvConfirm;
    private OnCancelOrderListener mListener;
    private boolean isOption1 = true;
    private String option1Str, option2Str;

    public CancelOrderDialog(Context context, OnCancelOrderListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        option1Str = context.getResources().getString(R.string.order_cancel_reason_waiter_later);
        option2Str = context.getResources().getString(R.string.order_cancel_reason_plan_change);
    }

    @Override
    public View onCreateView() {
//        widthScale(0.85f);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_cancel_order, null);
        llOption1 = view.findViewById(R.id.llOption1);
        llOption2 = view.findViewById(R.id.llOption2);
        ivOption1 = view.findViewById(R.id.ivOption1);
        ivOption2 = view.findViewById(R.id.ivOption2);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);

        initOptions(isOption1);
        return view;
    }

    private void initOptions(boolean isOption1) {
        if (isOption1) {
            ivOption1.setImageResource(R.drawable.selected_oval);
            ivOption2.setImageResource(R.drawable.unselect_oval);
        } else {
            ivOption1.setImageResource(R.drawable.unselect_oval);
            ivOption2.setImageResource(R.drawable.selected_oval);
        }
    }

    @Override
    public void setUiBeforShow() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.confirmCancelOrder(isOption1 ? option1Str : option2Str);
                dismiss();
            }
        });

        llOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOption1 = true;
                initOptions(isOption1);
            }
        });
        llOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOption1 = false;
                initOptions(isOption1);
            }
        });
    }


    public interface OnCancelOrderListener {
        void confirmCancelOrder(String cancelReason);
    }

}
