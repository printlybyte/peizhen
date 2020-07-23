package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.LogUtil;

public class CaptureAndGalleryDialog extends BaseDialog<CaptureAndGalleryDialog> {
    private Context mContext;
    private TextView tvCapture, tvGallery;

    public CaptureAndGalleryDialog(Context context, CaptureAndGallerySelectListener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_captureandgallery, null);
        tvCapture = view.findViewById(R.id.tvCapture);
        tvGallery = view.findViewById(R.id.tvGallery);
        tvCapture.setText("拍照");
        tvGallery.setText("相册");
        return view;
    }

    @Override
    public void setUiBeforShow() {
        tvCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.selectCapture();
                dismiss();
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.selectGallery();
                dismiss();
            }
        });
    }

    public interface CaptureAndGallerySelectListener {
        void selectCapture();

        void selectGallery();
    }

    public void setCaptureAndGallerySelectListener(CaptureAndGallerySelectListener listener) {
        this.mListener = listener;
    }

    private CaptureAndGallerySelectListener mListener;
}
