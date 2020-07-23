package com.yinfeng.wypzh.adapter;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.patient.PatientInfo;

import java.util.List;

//public class ChoosePatientAdapter extends BaseItemDraggableAdapter<PatientInfo, BaseViewHolder> {
public class ChoosePatientAdapter extends BaseMultiItemQuickAdapter<PatientInfo, BaseViewHolder> {
    private String choosedInfoId = null;

    public ChoosePatientAdapter(@Nullable List<PatientInfo> data) {
        super(data);
//        super(R.layout.item_choosepatientlist, data);
        addItemType(0, R.layout.item_choosepatientlist_mine);
        addItemType(1, R.layout.item_choosepatientlist);
    }

    @Override
    protected void convert(BaseViewHolder helper, PatientInfo item) {
        RelativeLayout rlItem = helper.getView(R.id.layout_item);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvPhone = helper.getView(R.id.tvPhone);

        if (item != null && !TextUtils.isEmpty(item.getName()))
            tvName.setText(item.getName());
        if (item != null && !TextUtils.isEmpty(item.getPhone()))
            tvPhone.setText(item.getPhone());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rlItem.setBackgroundResource(R.drawable.ripple_white_solid_corner2);
        } else {
            rlItem.setBackgroundResource(R.drawable.shape_white_solid_corner_small);
        }
        if (TextUtils.equals(choosedInfoId, item.getId()))
            rlItem.setBackgroundResource(R.drawable.shape_qianbai_solid_corner_small);
        helper.addOnClickListener(R.id.layout_item);
        ImageView ivEdit = helper.getView(R.id.ivEdit);
        switch (item.getItemType()) {

            case 0:
//                tvName.setText("我");
                ivEdit.setVisibility(View.GONE);
                break;
            case 1:
                ivEdit.setVisibility(View.VISIBLE);
                helper.addOnClickListener(R.id.ivEdit);
                helper.addOnClickListener(R.id.btDelete);
                break;
        }

    }

    public void setChoosedInfoId(String choosedInfoId) {
        this.choosedInfoId = choosedInfoId;
    }
}
