package com.netease.nim.uikit.common.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.netease.nim.uikit.R;

public class DeleteAndTopDialog extends BaseDialog<DeleteAndTopDialog> {
    private Context mContext;
    DeleteOrTopSelectListener mListener;
    private TextView tvTop, tvDelete;
    private String topShowContent;

    public DeleteAndTopDialog(Context context, String topShowContent, DeleteOrTopSelectListener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        this.topShowContent = topShowContent;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_deleteortop, null);
        tvTop = view.findViewById(R.id.tvTop);
        tvDelete = view.findViewById(R.id.tvDelete);
        tvTop.setText(topShowContent);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        tvTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.selectTop();
                dismiss();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.selectDelete();
                dismiss();
            }
        });
    }

    public interface DeleteOrTopSelectListener {
        void selectDelete();

        void selectTop();
    }

}
