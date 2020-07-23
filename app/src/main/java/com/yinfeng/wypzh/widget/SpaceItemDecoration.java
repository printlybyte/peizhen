package com.yinfeng.wypzh.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount = 4;
    private int space;

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    public SpaceItemDecoration(int space, int spanCout) {
        this.space = space;
        this.spanCount = spanCout;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.bottom = space;
        //由于每行都只有spanCount个，所以第一个都是spanCount的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % spanCount == 0) {
            outRect.left = 0;
        }
    }
}
