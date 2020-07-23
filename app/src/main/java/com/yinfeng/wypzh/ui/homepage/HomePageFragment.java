package com.yinfeng.wypzh.ui.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.StatusBarUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class HomePageFragment extends BaseFragment {

    TextView tvServiceAndQuestion;
    Button btSubscribe;

    public static HomePageFragment newInstance() {
        Bundle args = new Bundle();
        HomePageFragment fragment = new HomePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        tvServiceAndQuestion = view.findViewById(R.id.tvServiceAndQuestion);
        tvServiceAndQuestion.setVisibility(View.GONE);
        btSubscribe = view.findViewById(R.id.btSubscribeOnline);
//        initServiceAndQuestion();

    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {

        RxView.clicks(btSubscribe).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ServiceOptionDetailActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_homepage;
    }

    private void initServiceAndQuestion() {
        String agreenMentScript = getResources().getString(R.string.hp_serviceandquestion);
        String service = "服务详情";
        String question = "常见问题";
        int indexService = agreenMentScript.indexOf(service);
        int indexQuestion = agreenMentScript.indexOf(question);

        SpannableString ss = new SpannableString(agreenMentScript);
        MyClickableSpan serviceClickSpan = new MyClickableSpan(service);
        MyClickableSpan questionClickSpan = new MyClickableSpan(question);
        ss.setSpan(serviceClickSpan, indexService, service.length() + indexService, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(questionClickSpan, indexQuestion, question.length() + indexQuestion, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvServiceAndQuestion.setText(ss);
        ContextUtils.setTextViewLinkClickable(tvServiceAndQuestion);
    }

    class MyClickableSpan extends ClickableSpan {

        private String content;

        public MyClickableSpan(String content) {
            this.content = content;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(true);
            ds.setColor(Color.parseColor("#06b49b"));
        }

        @Override
        public void onClick(View widget) {

        }
    }
}
