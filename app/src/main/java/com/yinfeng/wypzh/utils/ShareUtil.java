package com.yinfeng.wypzh.utils;

import android.app.Activity;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yinfeng.wypzh.R;

/**
 * @author Asen
 */
public class ShareUtil {
    public static void shareUrlToWx(Activity activity, String url, String title, String desc, UMShareListener listener) {
        if (!ContextUtils.isWeixinAvilible(activity)) {
            ToastUtil.getInstance().showShort(activity, "微信未安装或微信版本过低");
            return;
        }
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        UMImage thumb = new UMImage(activity, R.mipmap.ic_launcher);
        web.setThumb(thumb);  //缩略图
        web.setDescription(desc);//描述
        new ShareAction(activity).withMedia(web).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(listener).open();
    }

    public static void shareUrlToWxHy(Activity activity, String url, String title, String desc, UMShareListener listener) {
        if (!ContextUtils.isWeixinAvilible(activity)) {
            ToastUtil.getInstance().showShort(activity, "微信未安装或微信版本过低");
            return;
        }
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        UMImage thumb = new UMImage(activity, R.mipmap.ic_launcher);
        web.setThumb(thumb);  //缩略图
        web.setDescription(desc);//描述
        new ShareAction(activity)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.WEIXIN)//传入平台
                .setCallback(listener)//回调监听器
                .share();
    }

    public static void shareUrlToWxCircle(Activity activity, String url, String title, String desc, UMShareListener listener) {
        if (!ContextUtils.isWeixinAvilible(activity)) {
            ToastUtil.getInstance().showShort(activity, "微信未安装或微信版本过低");
            return;
        }
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        UMImage thumb = new UMImage(activity, R.mipmap.ic_launcher);
        web.setThumb(thumb);  //缩略图
        web.setDescription(desc);//描述
        new ShareAction(activity)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)//传入平台
                .setCallback(listener)//回调监听器
                .share();
    }

}
