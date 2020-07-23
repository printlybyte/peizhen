package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;

public class HasArrivedDialog extends BaseDialog<HasArrivedDialog> {

    private TextView tvHasKnow;

    public HasArrivedDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_has_arrived, null);
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
