package com.yinfeng.wypzh.ui.homepage;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.widget.MyWebView;

import java.util.ArrayList;

/**
 * @author Asen
 */
public class ServiceOptionQuestionFragment extends BaseFragment implements MyWebView.MyWebViewListener {
    private MyWebView mWebView;
    private String content;

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        mWebView = view.findViewById(R.id.mWebView);
        content = getArguments().getString(ServiceOptionDetailActivity.KEY_HTML_DATA);
        mWebView.setMyWebViewListener(this);
        mWebView.setHtmlContent(content,12,12);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_service_option_question;
    }

    @Override
    public void pageFinished(WebView view, String tag, String url, ArrayList<String> imgList) {

    }

    @Override
    public void jsImgClick(String img, int position, ArrayList<String> imgList) {

    }

    @Override
    public void overrideUrlLoading(String url) {

    }
    @Override
    public void onDestroy() {
        clearWebViewResource(mWebView);
        super.onDestroy();
    }

    public void clearWebViewResource(WebView webView) {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.freeMemory();
            webView.pauseTimers();
            webView.removeAllViews();
            // in android 5.1(sdk:21) we should invoke this to avoid memory leak
            // see (https://coolpers.github.io/webview/memory/leak/2015/07/16/
            // android-5.1-webview-memory-leak.html)
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.setTag(null);
            webView.clearHistory();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }
    }
}
