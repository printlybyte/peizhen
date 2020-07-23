package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.patient.HospitalBean;
import com.yinfeng.wypzh.utils.ImageUtil;

import java.util.List;

public class HospitalAdapter extends BaseQuickAdapter<HospitalBean, BaseViewHolder> {

    private RequestOptions imageOptions;

    public HospitalAdapter(@Nullable List<HospitalBean> data) {
        super(R.layout.item_hospital, data);
        imageOptions = ImageUtil.getInstance().getDefineOptions(100, R.drawable.hospital_default);
    }

    @Override
    protected void convert(BaseViewHolder helper, HospitalBean item) {
        ImageView ivIcon = helper.getView(R.id.ivIcon);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvAddress = helper.getView(R.id.tvAddress);
        if (!TextUtils.isEmpty(item.getName()))
            tvName.setText(item.getName());
        if (!TextUtils.isEmpty(item.getAddress()))
            tvAddress.setText(item.getAddress());
        ImageUtil.getInstance().loadImg(mContext, item.getImgUrl(), imageOptions, ivIcon);
    }
}
