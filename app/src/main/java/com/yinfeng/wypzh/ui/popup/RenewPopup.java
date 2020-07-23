package com.yinfeng.wypzh.ui.popup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.RenewAdapter;
import com.yinfeng.wypzh.ui.dialog.ArrayWheelAdapter;
import com.yinfeng.wypzh.utils.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RenewPopup extends BasePopup<RenewPopup> {
    private RecyclerView mRecyclerView;
    private RenewAdapter mAdapter;
    private List<String> mList;

    public RenewPopup(Context context) {
        super(context);
    }

    public void setData(List<String> list) {
        this.mList = list;
    }

    @Override
    public View onCreatePopupView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_renew, null);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        mAdapter = new RenewAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new com.chad.library.adapter.base.BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(com.chad.library.adapter.base.BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }
}
