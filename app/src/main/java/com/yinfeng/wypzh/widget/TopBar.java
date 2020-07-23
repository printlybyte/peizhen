package com.yinfeng.wypzh.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yinfeng.wypzh.R;


public class TopBar extends LinearLayout {
    LinearLayout llLeft;
    TextView tvCenter, tvLeft, tvLeftRightOfBt, tvRight;
    ImageView ivBack;
    TopBarBackListener mBackListener;
    TopBarLeftTextCickListener mLeftListener;
    TopBarRightTextCickListener mRightListener;

    public TopBar(Context context) {
        this(context, null);
    }


    public TopBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        init(context);
    }

    private void init(Context context) {
        View parent = LayoutInflater.from(context).inflate(R.layout.topbar, null);
        llLeft = parent.findViewById(R.id.llLeft);
        tvCenter = parent.findViewById(R.id.topbar_center_tx);
        tvLeft = parent.findViewById(R.id.topbar_left_tx);
        tvLeftRightOfBt = parent.findViewById(R.id.topbar_left_tx_rightofbt);
        tvRight = parent.findViewById(R.id.topbar_right_tx);
        ivBack = parent.findViewById(R.id.topbar_left_bt);
        ivBack.setVisibility(View.VISIBLE);
        llLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBackListener != null)
                    mBackListener.topBack();
            }
        });

        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftListener != null)
                    mLeftListener.topLeftTxtClick();
            }
        });
        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightListener != null)
                    mRightListener.topRightTxtClick();
            }
        });

        addView(parent);
    }

    public void setTopCenterTxt(String content) {
        tvCenter.setText(content);
    }

    public void setTopLeftTxt(String content) {
        showLeftTxt();
        tvLeft.setText(content);
    }

    public void setTopLeftTxtWithArrow(String content) {
        showLeftTxtWithArrow();
        tvLeftRightOfBt.setText(content);
    }

    public void setTopRightTxt(String content) {
        showRightTxt();
        tvRight.setText(content);
    }

    public void showTopBack() {
        ivBack.setVisibility(View.VISIBLE);
    }

    public void hideTopBack() {
        ivBack.setVisibility(View.GONE);
    }

    public void showLeftTxt() {
        tvLeft.setVisibility(View.VISIBLE);
    }

    public void showLeftTxtWithArrow() {
        tvLeftRightOfBt.setVisibility(View.VISIBLE);
    }

    public void hideLeftTxt() {
        tvLeft.setVisibility(View.GONE);
    }

    public void hideLeftTxtWithArrow() {
        tvLeftRightOfBt.setVisibility(View.GONE);
    }

    public void showRightTxt() {
        tvRight.setVisibility(View.VISIBLE);
    }

    public void hideRightTxt() {
        tvRight.setVisibility(View.GONE);
    }


    public interface TopBarBackListener {
        void topBack();
    }

    public void setTopBarBackListener(TopBarBackListener listener) {
        mBackListener = listener;
    }

    public interface TopBarLeftTextCickListener {
        void topLeftTxtClick();
    }

    public void setTopBarLeftTxtClick(TopBarLeftTextCickListener listener) {
        mLeftListener = listener;
    }

    public interface TopBarRightTextCickListener {
        void topRightTxtClick();
    }

    public void setTopBarRightTxtListener(TopBarRightTextCickListener listener) {
        mRightListener = listener;
    }

}
