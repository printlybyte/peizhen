package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.adapter.RenewAdapter;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailBean;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class RenewOrderDialog extends BaseDialog<RenewOrderDialog> {
    private Context mContext;
    private LinearLayout llTime;
    private RelativeLayout rlItem;
    private TextView tvTime, tvPrice;
    private TextView tvCancel, tvConfirm;
    private TextView tvMarketPrice;
    private RecyclerView mRecyclerView;
    private OnConfirmRenewOrderListener mListener;
    private RenewAdapter mAdapter;
    private List<String> mList;
    private ServiceOptionDetailBean bean;
    private int time = 0;
    private int price = 0;
    private int priceCell = 0;
    private int timeCell = 0;
    private int marketPrice = -1;

    public RenewOrderDialog(Context context, ServiceOptionDetailBean optionBean, OnConfirmRenewOrderListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        bean = optionBean;
        if (bean != null) {
            priceCell = bean.getRenewalPrice();//   pricecell fen /timecell minute
            timeCell = bean.getRenewalTime();//minute
            marketPrice = bean.getMarketPrice();
        }
        getShowTimeList();
        time = timeCell;
        price = priceCell;
    }

    private void getShowTimeList() {
        mList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mList.add(timeCell * (i + 1) + "分钟");
        }
    }

    @Override
    public View onCreateView() {
//        widthScale(0.85f);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_renew_order, null);
        llTime = view.findViewById(R.id.llTime);
        rlItem = view.findViewById(R.id.rlItem);
        tvTime = view.findViewById(R.id.tvTime);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        tvMarketPrice = view.findViewById(R.id.tvMarketPrice);
        tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mAdapter = new RenewAdapter(mList);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void setUiBeforShow() {
        tvTime.setText(time + "分钟");
        tvPrice.setText(ContextUtils.getPriceStrConvertFenToYuan(price));
        tvMarketPrice.setText(ContextUtils.getPriceStrConvertFenToYuan(marketPrice));

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
                    mListener.confirmRenew(time, price);
                dismiss();
            }
        });

        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                time = (position + 1) * timeCell;
                price = (position + 1) * priceCell;
                tvTime.setText(time + "分钟");
                tvPrice.setText(ContextUtils.getPriceStrConvertFenToYuan(price));
                mRecyclerView.setVisibility(View.GONE);
            }
        });
    }


    public interface OnConfirmRenewOrderListener {
        void confirmRenew(int time, int price);
    }

}
