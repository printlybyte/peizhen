package com.yinfeng.wypzh.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.message.MessageOrderNotice;
import com.yinfeng.wypzh.bean.message.PushMessage;
import com.yinfeng.wypzh.bean.patient.HospitalBean;
import com.yinfeng.wypzh.utils.ImageUtil;

import java.util.List;

public class MsgOrderNoticeAdapter extends BaseQuickAdapter<MessageOrderNotice, BaseViewHolder> {


    public MsgOrderNoticeAdapter(@Nullable List<MessageOrderNotice> data) {
        super(R.layout.item_msgordernotice, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageOrderNotice item) {
        TextView tvTime = helper.getView(R.id.tvTime);
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvContent = helper.getView(R.id.tvContent);
        LinearLayout llTip = helper.getView(R.id.llTip);

        String time = item.getCreateTime();
        String title = item.getTitle();
        String content = item.getContent();
        if (TextUtils.isEmpty(time))
            time = "";
        if (TextUtils.isEmpty(title))
            title = "";
        if (TextUtils.isEmpty(content))
            content = "";
        tvTime.setText(time);
        tvTitle.setText(title);
        tvContent.setText(content);

        boolean isRead = item.isRead();
        int currentPos = helper.getLayoutPosition();
        if (currentPos > 0) {
            MessageOrderNotice preItem = getData().get(currentPos - 1);
            boolean isReadPre = preItem.isRead();
            if (isRead && !isReadPre) {
                llTip.setVisibility(View.VISIBLE);
            } else {
                llTip.setVisibility(View.GONE);
            }
        }

    }
}
