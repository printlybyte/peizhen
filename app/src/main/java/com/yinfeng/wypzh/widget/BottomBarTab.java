package com.yinfeng.wypzh.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.ContextUtils;


public class BottomBarTab extends RelativeLayout {
    private ImageView mIcon;
    private Context mContext;
    private TextView mTextView;
    private BadgeView mBadgeView;
    private ImageView mRedPoint;
    private int mTabPosition = -1;
    private int icon;
    private static boolean ifshow = false;

    public BottomBarTab(Context context, @DrawableRes int icon, String title) {
        this(context, null, icon, title);
    }


    public BottomBarTab(Context context, AttributeSet attrs, int icon, String title) {
        this(context, attrs, 0, icon, title);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, int icon, String title) {
        super(context, attrs, defStyleAttr);
        init(context, icon, title);
    }

    private void init(Context context, int icon, String title) {
        mContext = context;
        this.icon = icon;
       /* TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackgroundDrawable(drawable);
        typedArray.recycle();*/
        setGravity(Gravity.CENTER);
        RelativeLayout container = new RelativeLayout(mContext);
        LayoutParams containerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        container.setGravity(Gravity.CENTER);

        mIcon = new ImageView(context);
        int mIconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        LayoutParams mIconParams = new LayoutParams(mIconSize, mIconSize);
        mIconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mIcon.setId(R.id.bottombaricon);
        mIcon.setImageResource(icon);
        mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mIcon.setLayoutParams(mIconParams);
//        mIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.tab_unselect));

        mRedPoint = new ImageView(context);
        int mRedPointSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        LayoutParams mRedPointParams = new LayoutParams(mRedPointSize, mRedPointSize);
        mRedPointParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.bottombaricon);
        mRedPointParams.addRule(RelativeLayout.ALIGN_TOP, R.id.bottombaricon);
        mRedPointParams.topMargin = ContextUtils.dip2px(context, -2.0f);
        mRedPointParams.rightMargin = ContextUtils.dip2px(context, -2.0f);

        mRedPoint.setLayoutParams(mRedPointParams);
        mRedPoint.setId(R.id.bottombarredpoint);
        ShapeDrawable bgDrawable = ContextUtils.getOvalShapeDrawable(8, Color.RED);
        ContextUtils.setBackgroundCompat(mRedPoint, bgDrawable);

        mBadgeView = new BadgeView(context);
        mBadgeView.setId(R.id.bottombartext);
        int redNumSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
        LayoutParams redNumParams = new LayoutParams(redNumSize, redNumSize);
        redNumParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.bottombaricon);
        redNumParams.addRule(RelativeLayout.ALIGN_TOP, R.id.bottombaricon);

        redNumParams.topMargin = ContextUtils.dip2px(context, -2.0f);
        redNumParams.rightMargin = ContextUtils.dip2px(context, -8.0f);
        mBadgeView.setLayoutParams(redNumParams);

        mRedPoint.setVisibility(View.INVISIBLE);
//        mRedPoint.setVisibility(View.VISIBLE);
        mBadgeView.setHideOnNull(true);
//        mBadgeView.setBadgeCount(999);

        LayoutParams textViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.BELOW, R.id.bottombaricon);
        textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textViewParams.topMargin = ContextUtils.dip2px(context, 2.0f);
        mTextView = new TextView(context);
        mTextView.setId(R.id.bottombartext);
        mTextView.setText(title);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mTextView.setLayoutParams(textViewParams);
        mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.hp_bottombar_txtcolor_default));

        container.addView(mIcon);
        container.addView(mRedPoint);
        container.addView(mBadgeView);
        container.addView(mTextView);

        addView(container);
        setClipChildren(false);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.hp_bottombar_txtcolor_selecter));

        } else {
            mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.hp_bottombar_txtcolor_default));
        }

        switch (mTabPosition) {
            case 0:
                if (selected) {
                    mIcon.setImageResource(R.drawable.bb_homepage);
                } else {
                    mIcon.setImageResource(R.drawable.bb_homepage);
                }
                break;
            case 1:
                if (selected) {
                    mIcon.setImageResource(R.drawable.bb_order);
                } else {
                    mIcon.setImageResource(R.drawable.bb_order);
                }
                break;
            case 2:
                if (selected) {
                    mIcon.setImageResource(R.drawable.bb_message);
                } else {
                    mIcon.setImageResource(R.drawable.bb_message);
                }
                break;
            case 3:
                if (selected) {
                    mIcon.setImageResource(R.drawable.bb_mine);
                } else {
                    mIcon.setImageResource(R.drawable.bb_mine);
                }
                break;
        }
    }

    public BadgeView getBadgeView() {
        return this.mBadgeView;
    }

    public ImageView getRedPoint() {
        return this.mRedPoint;
    }

    public void setTabPosition(int position) {
        mTabPosition = position;
        if (position == 0) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }
}
