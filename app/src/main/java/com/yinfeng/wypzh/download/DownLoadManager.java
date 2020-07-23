package com.yinfeng.wypzh.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.EventUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Asen
 */
public class DownLoadManager {
    private volatile static DownLoadManager instance;

    public static DownLoadManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadManager.class) {
                if (instance == null)
                    instance = new DownLoadManager();
            }
        }
        return instance;
    }

    private boolean isDownLoading = false;
    private boolean isStorageOk = false;
    private Notification notification;
    private Notification.Builder mNotifBuild;
    private NotificationManager notificationManager;
    private int notifId = 12;
    PermissionTipDialog permissionTipDialog;
    Disposable mDispose;
    int currentProgress = 0;
    boolean isLooping = false;
    Context mContext;
    boolean isAllApkDownloaded = false;

    public boolean isDownLoading() {
        return this.isDownLoading;
    }


    private void stopLoopGetProgress() {
        if (mDispose != null)
            mDispose.dispose();
        isLooping = false;

    }

    private synchronized void startLoopGetProgress() {
        if (isLooping) {
            return;
        }

        isLooping = true;
        if (mDispose != null)
            mDispose.dispose();
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>applySchedulers())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDispose = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (currentProgress >= 0 && currentProgress <= 100) {

                            if (mContext != null)
                                EventUtil.downloadProgress(currentProgress);
//                                refreshNotif(mContext, currentProgress);
                                if (currentProgress == 100) {
                                    mDispose.dispose();
                                    isLooping = false;
//                                notificationManager.cancel(notifId);
                                }
                            return;
                        }
                        mDispose.dispose();
                        isLooping = false;

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDispose.dispose();
                        isLooping = false;
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void downLoadFile(Context context, String downLoadUrl) {
        isDownLoading = true;
        mContext = context;
//        initNotif(context);
        File file = new File(getApkPath(context), Constants.DOWNLOAD_APK_NAME); //获取文件路径
        DownloadUtils downloadUtils = new DownloadUtils(new JsDownloadListener() {
            @Override
            public void onStartDownload(long length) {
            }

            @Override
            public void onProgress(int progress) {
                if (progress == 100)
                    isAllApkDownloaded = true;
                currentProgress = progress;
                startLoopGetProgress();
                LogUtil.error("progress :" + progress);
//                if (progress % 4 == 0)
//                    refreshNotif(context, progress);
//                if (progress >= 99) {
//                    notificationManager.cancel(notifId);
//                }
            }

            @Override
            public void onFail(String errorInfo) {
                isDownLoading = false;
            }
        });
        downloadUtils.download(downLoadUrl, file, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                LogUtil.error("download onNext");
                if (isAllApkDownloaded) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //android N的权限问题
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授权读权限
                        Uri contentUri = FileProvider.getUriForFile(mContext, "com.yinfeng.wypzh.my.provider", new File(getApkPath(mContext), Constants.DOWNLOAD_APK_NAME));//注意修改
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(new File(getApkPath(mContext), Constants.DOWNLOAD_APK_NAME)), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    mContext.startActivity(intent);
                    isDownLoading = false;
                }
            }

            @Override
            public void onError(Throwable e) {
                isDownLoading = false;
                LogUtil.error("download onError" + e.getMessage());
//                ToastUtil.getInstance().showShort(mContext, "下载出错,请重试");
//                if (notificationManager != null)
//                    notificationManager.cancel(notifId);
                stopLoopGetProgress();
                EventUtil.downloadProgress(-1);
            }

            @Override
            public void onComplete() {
                isDownLoading = false;
                LogUtil.error("download onComplete");

                if (!isAllApkDownloaded){

                    stopLoopGetProgress();
                    EventUtil.downloadProgress(-1);
                    ToastUtil.getInstance().showShort(mContext, "下载出错,请重试");
                    currentProgress =0;
                }

            }
        });
    }

    private void refreshNotif(Context context, int prorgress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_remoteview_download);
            mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.mipmap.ic_launcher);
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, prorgress, false);
            mNotifBuild.setCustomContentView(mRemoteViews);
        } else {
            mNotifBuild.setProgress(100, prorgress, false);
        }
//        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.layout_remoteview_download);
//        mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.mipmap.ic_launcher);
//        mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, prorgress, false);
//        notification.contentView = mRemoteViews;
        notification = mNotifBuild.build();
        notificationManager.notify(notifId, notification);
    }

    public String getApkPath(Context context) {
        String directoryPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//判断外部存储是否可用
            directoryPath = context.getExternalFilesDir("apk").getAbsolutePath();
        } else {//没外部存储就使用内部存储
            directoryPath = context.getFilesDir() + File.separator + "apk";
        }
        File file = new File(directoryPath);
        LogUtil.error("测试路径", directoryPath);
        if (!file.exists()) {//判断文件目录是否存在
            file.mkdirs();
        }
        return directoryPath;
    }


    private void initNotif(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifBuild = new Notification.Builder(context);
        mNotifBuild.setContentText("")
                .setContentTitle("版本更新")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_remoteview_download);
            mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.mipmap.ic_launcher);
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, 1, false);
            mNotifBuild.setCustomContentView(mRemoteViews);
        } else {
            mNotifBuild.setProgress(100, 1, false);
        }
        notification = mNotifBuild.build();
        notificationManager.notify(notifId, notification);
    }

    @SuppressLint("CheckResult")
    public void downLoadApk(final Activity activity, final String downLoadUrl) {
        RxPermissions rxPermission = new RxPermissions(activity);
        rxPermission
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                                isStorageOk = true;
                            }
                            if (isStorageOk) {
                                downLoadFile(activity, downLoadUrl);
                                isStorageOk = false;
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            String permissionName = "";
                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                                permissionName = "存储空间";
                            }
                            permissionTipDialog = DialogHelper.getPermissionTipDialog(activity, permissionName);
                            permissionTipDialog.show();
                        }
                    }
                });


    }
}
