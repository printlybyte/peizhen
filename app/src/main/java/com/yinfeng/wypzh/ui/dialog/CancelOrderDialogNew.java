package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.CancelReasonAdapter;
import com.yinfeng.wypzh.bean.order.OrderCancelReason;

import java.util.List;

public class CancelOrderDialogNew extends BaseDialog<CancelOrderDialogNew> {
    private Context mContext;
    private TextView tvCancel, tvConfirm;
    private RecyclerView mRecyclerView;
    private OnCancelOrderListener mListener;
    private CancelReasonAdapter mAdapter;
    private List<OrderCancelReason> list;
    private int currentSelectPosition = 0;
    private String currentSelectReason = "";


    public CancelOrderDialogNew(Context context, OnCancelOrderListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
    }
    public void setCancelReasonList(List<OrderCancelReason> list) {
        this.list = list;
        list.get(0).setSelected(true);
        currentSelectPosition = 0;
        currentSelectReason = list.get(0).getName();
    }

    @Override
    public View onCreateView() {
//        widthScale(0.85f);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_cancel_ordernew, null);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        mAdapter = new CancelReasonAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void setUiBeforShow() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.confirmCancelOrder(currentSelectReason);
                dismiss();
            }
        });

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                list.get(currentSelectPosition).setSelected(false);
                list.get(position).setSelected(true);
                mAdapter.notifyDataSetChanged();
                currentSelectPosition = position;
                currentSelectReason = list.get(position).getName();
            }
        });
    }


    public interface OnCancelOrderListener {
        void confirmCancelOrder(String cancelReason);
    }


}
