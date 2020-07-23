package com.yinfeng.wypzh.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.TipBaseBean;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.widget.MarqueTextView;

import java.util.List;

public  class TipAdapter extends BaseQuickAdapter<TipBaseBean, BaseViewHolder> {
    private int space;
    private int spanNum;
    private int itemWidth;

    public TipAdapter(Context context, @Nullable List<TipBaseBean> data) {
        super(R.layout.item_sick, data);
        mContext = context;
    }

    public void setSpaceAndSpanNumb(int space, int spanNum, int needMinusDp) {
        this.space = space;
        this.spanNum = spanNum;
        int screenWidth = ContextUtils.getSreenWidth(mContext);
        itemWidth = (screenWidth - ContextUtils.dip2px(mContext, needMinusDp) - space * (spanNum - 1)) / spanNum - ContextUtils.dip2px(mContext, 4);
    }

    public int getSpace() {
        return this.space;
    }

    public int getSpanNum() {
        return this.spanNum;
    }

    @Override
    protected void convert(BaseViewHolder helper, TipBaseBean item) {
        MarqueTextView tvSick = helper.getView(R.id.tvSick);
        if (itemWidth > 0) {
            ViewGroup.LayoutParams params = tvSick.getLayoutParams();
            params.width = itemWidth;
            tvSick.setLayoutParams(params);
            tvSick.setSingleLine();
            tvSick.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tvSick.setMarqueeRepeatLimit(3);
        }
//        int position = helper.getLayoutPosition();
//        if (position == getItemCount() - 1) {
//            item.setSelected(false);
//        }
        if (!TextUtils.isEmpty(item.getName())) {
            tvSick.setText(item.getName());
            boolean isSelected = item.isSelected();
            if (isSelected) {
                tvSick.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                tvSick.setBackgroundResource(R.drawable.shape_sick_selected);
            } else {
                tvSick.setTextColor(ContextCompat.getColor(mContext, R.color.cb5b5b5));
                tvSick.setBackgroundResource(R.drawable.shape_sick_unselect);
            }
        }
        helper.addOnClickListener(R.id.tvSick);
    }
}
