package com.yinfeng.wypzh.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.umeng.commonsdk.utils.UMUtils;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.UmUtil;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ExistDialogActivity extends Activity {

    public static final String KEY_EXIST_APP = "exist";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_exist_activity);
        ContextUtils.kickOut(this);
        UmUtil.removeAllNotification(this);
        ((TextView) findViewById(R.id.title)).setText(R.string.str_exit_title);
        ((TextView) findViewById(R.id.content)).setText(R.string.str_exit_content);
        ((TextView) findViewById(R.id.confirm)).setText("确 定");

        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exist();
            }
        });
    }

    private void exist() {
        ContextUtils.logOut(this);
    }

    @Override
    public void onBackPressed() {
    }
}
