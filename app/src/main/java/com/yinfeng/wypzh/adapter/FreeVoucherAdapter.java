package com.yinfeng.wypzh.adapter;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.order.FreeVoucherBean;
import com.yinfeng.wypzh.bean.patient.PatientInfo;

import java.util.List;

public class FreeVoucherAdapter extends BaseItemDraggableAdapter<FreeVoucherBean, BaseViewHolder> {
    private String choosedInfoId = null;
    private boolean isNeedChoose;

    public FreeVoucherAdapter(@Nullable List<FreeVoucherBean> data) {
        super(R.layout.item_freevoucher, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FreeVoucherBean item) {
        LinearLayout llItem = helper.getView(R.id.llItem);
        TextView tvValidTime = helper.getView(R.id.tvValidTime);
        TextView tvUse = helper.getView(R.id.tvUse);
        tvUse.setVisibility(View.VISIBLE);
        tvUse.setBackgroundResource(R.drawable.shape_green_solid_corner10);
        tvUse.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        tvUse.setText("立即使用");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llItem.setBackgroundResource(R.drawable.ripple_white_solid_corner2);
        } else {
            llItem.setBackgroundResource(R.drawable.shape_white_solid_corner_small);
        }
        String startTime = item.getStartTime();
        String endTime = item.getEndTime();
        String validContent = "有效期:" + startTime + "至" + endTime;
        tvValidTime.setText(validContent);
        if (TextUtils.equals(choosedInfoId, item.getId())) {
            llItem.setBackgroundResource(R.drawable.shape_qianbai_solid_corner_small);
            tvUse.setText("取消使用");
            tvUse.setBackgroundResource(R.drawable.shape_grey_solid_corner10);
            tvUse.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_999999));
        }

//        helper.addOnClickListener(R.id.llItem);
        if (isNeedChoose) {
            helper.addOnClickListener(R.id.tvUse);
        } else {
            tvUse.setVisibility(View.GONE);
        }
        helper.addOnClickListener(R.id.llItem);
    }

    public void setIsNeedChoose(boolean isNeedChoose) {
        this.isNeedChoose = isNeedChoose;
    }

    public String getChoosedInfoID() {
        return this.choosedInfoId;
    }

    public void setChoosedInfoId(String choosedInfoId) {
        this.choosedInfoId = choosedInfoId;
    }
}
