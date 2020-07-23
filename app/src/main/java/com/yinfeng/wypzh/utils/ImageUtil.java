package com.yinfeng.wypzh.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.listener.OnGlideCacheGetListener;
import com.yinfeng.wypzh.ui.login.FillInfoActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class ImageUtil {

    private RequestOptions defaultRequestOptions;
    private RequestOptions defaultRequestOptionsCircle;

    private DrawableTransitionOptions defaultBitmapTransitionOptions;
    private static volatile ImageUtil instance;
    private ObjectKey cacheKey;

    public static ImageUtil getInstance() {
        if (instance == null) {
            synchronized (ImageUtil.class) {
                if (instance == null) {
                    instance = new ImageUtil();
                }
            }
        }
        return instance;
    }

    @SuppressLint("CheckResult")
    private ImageUtil() {
        defaultBitmapTransitionOptions = new DrawableTransitionOptions();
        defaultBitmapTransitionOptions.transition(R.anim.img_load_transition_default);

        defaultRequestOptions = new RequestOptions();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#eeeeee"));
        defaultRequestOptions
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(colorDrawable)
                .error(colorDrawable)
                .fallback(colorDrawable)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
        //.encodeQuality(90)//1-100
        //.encodeFormat(Bitmap.CompressFormat.WEBP)//Bitmap.CompressFormat.JPEG
        //.override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
        //.dontTransform()//禁止转换
        //.dontAnimate()//禁止动画
//        .centerCrop()
        //.centerInside()
        //.fitCenter()
        //.circleCrop()
        //.transform(new BlurTransformation())//GrayscaleTransformation()
        ;
        defaultRequestOptionsCircle = new RequestOptions();
        defaultRequestOptionsCircle
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.head_default)
                .error(R.drawable.head_default)
                .fallback(R.drawable.head_default)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                .circleCrop();

    }

    @SuppressLint("CheckResult")
    public RequestOptions getDefineOptions(int quality, int resourceId) {
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(resourceId)
                .error(resourceId)
                .fallback(resourceId)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                .encodeQuality(quality)//1-100
                //.encodeFormat(Bitmap.CompressFormat.WEBP)//Bitmap.CompressFormat.JPEG
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                //.dontTransform()//禁止转换
                //.dontAnimate()//禁止动画
                .centerCrop()
        //.centerInside()
//        .fitCenter()
//                .circleCrop()
//        .transform(new BlurTransformation())//GrayscaleTransformation()
        ;
        return options;
    }

    public void changCacheKey(Context context, long currentTime) {
        SFUtil.getInstance().setGlideSignatureCacheVersion(context, currentTime);
        cacheKey = new ObjectKey(currentTime);
    }

    public void setCacheKey(Context context, RequestOptions requestOptions) {
//        if (cacheKey == null) {
        cacheKey = new ObjectKey(SFUtil.getInstance().getGlideSignatureCacheVersion(context));
//        }
        requestOptions.signature(cacheKey);
    }

    public void loadImgIntoTarget(Context context, Object url, SimpleTarget<Bitmap> simpleTarget) {
        Glide.with(context).asBitmap().load(url).into(simpleTarget);
    }

    public void loadImg(Context context, Object url, ImageView iv) {
        setCacheKey(context, defaultRequestOptions);
        Glide.with(context)
                .load(url)
                .apply(defaultRequestOptions)
                .into(iv);
    }

    public void loadImgCircle(Context context, Object url, ImageView iv) {
        setCacheKey(context, defaultRequestOptionsCircle);
        Glide.with(context)
                .load(url)
                .apply(defaultRequestOptionsCircle)
                .into(iv);
    }

    public void loadImg(Context context, Object url, RequestOptions options, ImageView iv) {
        setCacheKey(context, options);
        Glide.with(context)
                .load(url)
                .apply(options)
                .transition(defaultBitmapTransitionOptions)
                .into(iv);
    }

    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    public void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

    public void clearMemoryAndDiskCache(Context context) {
        clearMemoryCache(context);
        clearDiskCache(context);
    }

    public File saveBitmapToFile(File file, Bitmap bitmap) {
        return saveBitmapToFile(file, bitmap, true);
    }

    public File saveBitmapToFile(File file, Bitmap bitmap, boolean isNeedRecycle) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (isNeedRecycle)
                bitmap.recycle();
        }
        return file;
    }

    /**
     * 保存图片到本地
     *
     * @param context
     * @param toSaveFile
     * @param fileName
     * @param description
     */
    public void saveImageFileToLocal(Context context, File toSaveFile, String fileName, String description) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    toSaveFile.getAbsolutePath(), fileName, description);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(toSaveFile)));
            ToastUtil.getInstance().showShort(context, "保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveImgBitmapToLocal(Context context, Bitmap bitmap, File file, String fileName, String description) {
        File saveFile = saveBitmapToFile(file, bitmap);
        if (saveFile != null) {
            saveImageFileToLocal(context, saveFile, fileName, description);
        } else {
            ToastUtil.getInstance().showShort(context, "保存失败");
        }

    }

    public String getImgType(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        String type = options.outMimeType;
        LogUtil.error("cacheFile  type :" + type);
        String imgType = "jpeg";
        if (!TextUtils.isEmpty(type)) {
            int index = type.lastIndexOf("/");
            imgType = type.substring(index + 1, type.length());
        }
        LogUtil.error("cacheFile  imgType :" + imgType);
        return imgType;
    }

    /**
     * 获取Glide缓存图片绝对路径
     *
     * @param context
     * @param uri
     * @param width
     * @param height
     */
    public void getCacheFilePath(final Context context, Object uri, int width, int height, final OnGlideCacheGetListener listener) {
        final FutureTarget<File> future = Glide.with(context)
                .load(uri)
                .downloadOnly(width, height);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File cacheFile = future.get();
                    String path = cacheFile.getAbsolutePath();
                    LogUtil.error("cacheFile path " + path);
                    String suffix = ImageUtil.getInstance().getImgType(path);
//                    String userID = SFUtil.getInstance().getUserID(context);
                    String userID = String.valueOf(System.currentTimeMillis());
//                    String imgPath = ContextUtils.getImgPath(userID + "." + suffix);
                    String imgPath = FileUtils.getFilePath(context, userID + "." + suffix);
                    boolean copyResult = ContextUtils.copyFileToFile(new File(path), new File(imgPath));
//                    boolean copyResult = false;
//                    if (TextUtils.equals(suffix, "gif")) {
//                        copyResult = ContextUtils.copyFileToFile(new File(path), new File(imgPath));
//                    } else {
//                        copyResult = ContextUtils.copyFileToFileCompress(context, new File(path), new File(imgPath));
//                    }
                    if (copyResult) {
//                        SFUtil.getInstance().putUserHeadLastImgPath(context, imgPath);
                        if (listener != null)
                            listener.onSuccess(imgPath, userID + "." + suffix, suffix);
                    } else {
//                        SFUtil.getInstance().putUserHeadLastImgPath(context, path);
                        if (listener != null)
                            listener.onSuccess(path, userID + "." + suffix, suffix);
                    }
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                }
                if (listener != null)
                    listener.onFail();
            }
        }).start();

    }


    /**
     * 压缩图片至1M以内，按目标宽高压缩
     *
     * @param image
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public Bitmap compressAndScaleBitmap(Bitmap image, int targetWidth, int targetHeight) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int rawBitmapSize = baos.toByteArray().length / 1024;
        if (rawBitmapSize > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            int sizeScale = Math.min(100, rawBitmapSize / 1024);
            int quality = Math.min(100, 100 / sizeScale);
            LogUtil.error("目标文件大小：" + rawBitmapSize + "压缩系数：" + quality);
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = targetWidth;
        float ww = targetHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }

    public String getHeadImgTempNameWithSufixJPG() {
        return "wypzheader.jpeg";
    }
}
