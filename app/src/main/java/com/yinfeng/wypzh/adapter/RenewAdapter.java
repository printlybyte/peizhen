package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.waiter.CommentBean;

import java.util.List;

public class RenewAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RenewAdapter(@Nullable List<String> data) {
        super(R.layout.item_renew, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tvRenew = helper.getView(R.id.tvRenew);
        tvRenew.setText(item);
        helper.addOnClickListener(R.id.tvRenew);
    }
}
