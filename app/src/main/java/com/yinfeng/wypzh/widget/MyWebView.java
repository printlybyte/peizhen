package com.yinfeng.wypzh.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyWebView extends WebView {
    private WebSettings mSettings;
    private ArrayList<String> imgList;
    private HashMap<String, Integer> imgMap;
    private String tag;

    public MyWebView(Context context) {
        super(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initSettings() {

        mSettings.setAppCacheEnabled(true);
        mSettings.setDatabaseEnabled(true);
        mSettings.setDomStorageEnabled(true);//开启DOM缓存，关闭的话H5自身的一些操作是无效的
        mSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // 避免中文乱码
        mSettings.setDefaultTextEncodingName("utf-8");
        // 支持JavaScript
        mSettings.setJavaScriptEnabled(true);
        // 不支持缩放
        mSettings.setSupportZoom(false);
//		mSettings.setUseWideViewPort(false);
        mSettings.setUseWideViewPort(true);
        // 自适应屏幕
//		mSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mSettings.setLoadWithOverviewMode(true);
//        mSettings.setDomStorageEnabled(true);
        mSettings.setBlockNetworkImage(false);
        // 背景色透明
//        setBackgroundColor(0);
//        getBackground().setAlpha(0);
        // js交互（图片点击）
//        addJavascriptInterface(this, "imagelistner");
        setWebViewClient(new MyWebViewClient());
    }

    /**
     * @param content html string
     * @param right   mywebview 内容 实际 距离屏幕边界右侧的距离
     * @param left
     */
    public void setHtmlContent(String content, int right, int left) {
        LogUtil.error("haha", "setHtmlContent  the  method has run -----!!!");
        content = ContextUtils.replaceHtmlCotnent(content);
//        content = getImgAlist(content);
//		getImgAlist(Html.fromHtml(content).toString());
        mSettings = getSettings();
        initSettings();
        // 图片适配啊
        int maxWidth = getWebPicMaxWidth(right, left);
        String viewPort_prefix = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\" />";
        String prefix = "<head><style>img{max-width:" + maxWidth
                + "px !important;}</style>"
                + "<style>body{word-wrap:break-word;color:#5c5c5c}</style>" + viewPort_prefix
                + "</head>";
//		content = XwgUtils.replaceHtmlCotnent(content);
        String strContent = prefix + content;
        loadData(strContent, "text/html;charset=UTF-8", null);
        LogUtil.error("haha", strContent);
    }

    /**
     * 遍历出 html 所有 img标签中的 src
     *
     * @param content
     * @return
     */
    private String getImgAlist(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        LogUtil.error("haha", "getImglist  content :" + content);
        if (imgList == null) {
            imgList = new ArrayList<String>();
        } else {
            imgList.clear();
        }
        if (imgMap == null) {
            imgMap = new HashMap<String, Integer>();
        } else {
            imgMap.clear();
        }
        String regxpForImgTag = "<img\\s[^>]+/>";
        Pattern pattern = Pattern.compile(regxpForImgTag);
        Matcher matcher = pattern.matcher(content);
        int index = 0;
        while (matcher.find()) {
            String temp = matcher.group();
            String tempUrl = temp.substring(temp.indexOf("src=") + 5);
            tempUrl = tempUrl.substring(0, tempUrl.indexOf("\""));
            if (!TextUtils.isEmpty(tempUrl)) {
                imgList.add(tempUrl);
                imgMap.put(tempUrl, index);
                index++;
            }
//			// 空格的处理
//			String tempp = temp.replace(" ","&imgkg&");
//			content = content.replace(temp,tempp);
        }
//		LogUtil.error("haha","getImglist  imglist size : :"+imgList.size());
//		content = content.replace(" ", "&nbsp");
//		if (content.contains("&imgkg&")){
//			content =content.replace("&imgkg&"," ");
//		}
        //换行处理
        content = content.replace("\n", "<br>");
        return content;
    }

    private int getWebPicMaxWidth(int right, int left) {
        int screenwidth = getResources().getDisplayMetrics().widthPixels;
        final float scale = getResources().getDisplayMetrics().density;
        // 24 是父view的padding 再减去40px确保照片不出界
        int mWidth = screenwidth - (int) ((right + left) * scale + 0.5f) - 40;
        int maxWidth = (int) (mWidth / scale + 0.5f);
        return maxWidth;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"img\"); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "    objs[i].onclick=function()  " + "    {  "
                + "        window.imagelistner.openImage(this.src);  "
                + "    }  " + "}" + "})()");
    }

    @JavascriptInterface
    public void openImage(String img) {
        // 图片的点击处理
        LogUtil.error("haha", "imglist.size : " + imgList.size());
        if (mListener != null) {
            mListener.jsImgClick(img, imgMap.get(img), imgList);
        }
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url) && mListener != null) {
                mListener.overrideUrlLoading(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mSettings.setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
//			// html加载完成之后，添加监听图片的点击js函数
//            addImageClickListner();
//			// 获取评论列表
//			commentAdapter = new CommentAdapter(BlogDetail.this,
//					Constants.NOTICE_BLOG, bean.getOid(), BlogDetail.this);
//			lvComment.setAdapter(commentAdapter);
//			bcommentGetList();
            if (mListener != null) {
                mListener.pageFinished(view, tag, url, imgList);
            }
//			requestFocus();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    public interface MyWebViewListener {
        /**
         * webview 加载完毕
         *
         * @param view
         * @param url
         */
        void pageFinished(WebView view, String tag, String url, ArrayList<String> imgList);

        /**
         * 图片点击
         *
         * @param img
         * @param position
         * @param imgList
         */
        void jsImgClick(String img, int position, ArrayList<String> imgList);

        /**
         * webview里的链接点击
         *
         * @param url
         */
        void overrideUrlLoading(String url);
    }

    private MyWebViewListener mListener;

    public void setMyWebViewListener(MyWebViewListener listener) {
        mListener = listener;
    }

}
