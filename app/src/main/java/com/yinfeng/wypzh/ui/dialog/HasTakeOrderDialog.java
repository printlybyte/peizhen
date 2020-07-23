package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.order.RenewDetail;
import com.yinfeng.wypzh.utils.ContextUtils;

public class HasTakeOrderDialog extends BaseDialog<HasTakeOrderDialog> {

    private TextView tvHasKnow;

    public HasTakeOrderDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_has_take, null);
        tvHasKnow = view.findViewById(R.id.tvHasKnow);
        return view;
    }

    @Override
    public void setUiBeforShow() {

            tvHasKnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
}
