package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.order.OrderCancelReason;

import java.util.List;

/**
 * @author Asen
 */
public class CancelReasonAdapter extends BaseQuickAdapter<OrderCancelReason, BaseViewHolder> {
    public CancelReasonAdapter(@Nullable List<OrderCancelReason> data) {
        super(R.layout.item_cancel_reason, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderCancelReason item) {
        if (item != null) {
            helper.setText(R.id.tvContent, item.getName());
            ImageView ivOption = helper.getView(R.id.ivOption);
            if (item.isSelected()) {
                ivOption.setImageResource(R.drawable.selected_oval);
            } else {
                ivOption.setImageResource(R.drawable.unselect_oval);
            }
            helper.addOnClickListener(R.id.layout_item);
        }
    }
}
