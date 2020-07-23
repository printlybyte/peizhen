package com.yinfeng.wypzh.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;

public class UserAgrentActivity extends BaseActivity {


    private TextView mActivityUserAgrentDis;
    private TextView mActivityTitle;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        initView();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_user_agrent;
    }


    public void initView() {
        mActivityUserAgrentDis = (TextView) findViewById(R.id.activity_user_agrent_dis);
        mActivityTitle = (TextView) findViewById(R.id.activity_user_agrent_title);
        Intent intent = getIntent();
        String resu = "";
        String type = intent.getStringExtra("us_ag");
        if (!TextUtils.isEmpty(type) && type.equals("yhxy")) {
            resu = getResources().getString(R.string.yh_xy);
            mActivityTitle.setText("用户协议");
        } else if (!TextUtils.isEmpty(type) && type.equals("yszc")) {
            resu = getResources().getString(R.string.ys_zc);
            mActivityTitle.setText("隐私政策");

        }
        mActivityUserAgrentDis.setText(resu);
    }
}
