package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.waiter.CommentBean;
import com.yinfeng.wypzh.utils.ImageUtil;

import java.util.List;

public class CommentAdapter extends BaseQuickAdapter<CommentBean, BaseViewHolder> {

    public CommentAdapter(@Nullable List<CommentBean> data) {
        super(R.layout.item_pzy_comment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBean item) {
        ImageView ivHeadIcon = helper.getView(R.id.ivHeadIcon);
        TextView tvTime = helper.getView(R.id.tvTime);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvContent = helper.getView(R.id.tvContent);

        String profile = item.getProfile();
        String time = item.getCreateTime();
        String name = item.getMemberName();
        String content = item.getContent();

        ImageUtil.getInstance().loadImgCircle(mContext, profile, ivHeadIcon);
        tvName.setText(name);
        tvTime.setText(time);
        tvContent.setText(content);
    }
}
