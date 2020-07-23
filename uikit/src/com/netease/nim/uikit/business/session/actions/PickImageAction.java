package com.netease.nim.uikit.business.session.actions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.session.constant.RequestCode;
import com.netease.nim.uikit.business.session.helper.SendImageHelper;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.media.picker.activity.PreviewImageFromCameraActivity;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * Created by zhoujianghua on 2015/7/31.
 */
public abstract class PickImageAction extends BaseAction {

    protected boolean isGalleryOk = false;
    private boolean isCaptureOk = false;


    protected static final int PICK_IMAGE_COUNT = 9;
    protected static final int PORTRAIT_IMAGE_WIDTH = 720;

    public static final String MIME_JPEG = "image/jpeg";
    public static final String JPG = ".jpg";

    protected boolean multiSelect;
    protected boolean crop = false;

    protected abstract void onPicked(File file);

    protected PickImageAction(int iconResId, int titleId, boolean multiSelect) {
        super(iconResId, titleId);
        this.multiSelect = multiSelect;
    }

    @Override
    public void onClick() {
        requestPermissions();

//        int requestCode = makeRequestCode(RequestCode.PICK_IMAGE);
//        showSelector(getTitleId(), requestCode, multiSelect, tempFile());
    }

    protected String tempFile() {
        String filename = StringUtil.get32UUID() + JPG;
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
    }

    /**
     * 打开图片选择器
     */
    protected void showSelector(int titleId, final int requestCode, final boolean multiSelect, final String outPath) {
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = titleId;
        option.multiSelect = multiSelect;
        option.multiSelectMaxCount = PICK_IMAGE_COUNT;
        option.crop = crop;
        option.cropOutputImageWidth = PORTRAIT_IMAGE_WIDTH;
        option.cropOutputImageHeight = PORTRAIT_IMAGE_WIDTH;
        option.outputPath = outPath;

        PickImageHelper.pickImage(getActivity(), requestCode, option);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.PICK_IMAGE:
                onPickImageActivityResult(requestCode, data);
                break;
            case RequestCode.PREVIEW_IMAGE_FROM_CAMERA:
                onPreviewImageActivityResult(requestCode, data);
                break;
        }
    }

    /**
     * 图片选取回调
     */
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            Toast.makeText(getActivity(), R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return;
        }
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            sendImageAfterSelfImagePicker(data);
        } else {
            // 拍照
            Intent intent = new Intent();
            if (!handleImagePath(intent, data)) {
                return;
            }

            intent.setClass(getActivity(), PreviewImageFromCameraActivity.class);
            getActivity().startActivityForResult(intent, makeRequestCode(RequestCode.PREVIEW_IMAGE_FROM_CAMERA));
        }
    }

    /**
     * 是否可以获取图片
     */
    private boolean handleImagePath(Intent intent, Intent data) {
        String photoPath = data.getStringExtra(Extras.EXTRA_FILE_PATH);
        if (TextUtils.isEmpty(photoPath)) {
            Toast.makeText(getActivity(), R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        }

        File imageFile = new File(photoPath);
        intent.putExtra("OrigImageFilePath", photoPath);
        File scaledImageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, MIME_JPEG);

        boolean local = data.getExtras().getBoolean(Extras.EXTRA_FROM_LOCAL, true);
        if (!local) {
            // 删除拍照生成的临时文件
            AttachmentStore.delete(photoPath);
        }

        if (scaledImageFile == null) {
            Toast.makeText(getActivity(), R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ImageUtil.makeThumbnail(getActivity(), scaledImageFile);
        }
        intent.putExtra("ImageFilePath", scaledImageFile.getAbsolutePath());
        return true;
    }

    /**
     * 从预览界面点击发送图片
     */
    private void sendImageAfterPreviewPhotoActivityResult(Intent data) {
        SendImageHelper.sendImageAfterPreviewPhotoActivityResult(data, new SendImageHelper.Callback() {

            @Override
            public void sendImage(File file, boolean isOrig) {
                onPicked(file);
            }
        });
    }

    /**
     * 发送图片
     */
    private void sendImageAfterSelfImagePicker(final Intent data) {
        SendImageHelper.sendImageAfterSelfImagePicker(getActivity(), data, new SendImageHelper.Callback() {

            @Override
            public void sendImage(File file, boolean isOrig) {
                onPicked(file);

            }
        });
    }

    /**
     * 拍摄回调
     */
    private void onPreviewImageActivityResult(int requestCode, Intent data) {
        if (data.getBooleanExtra(PreviewImageFromCameraActivity.RESULT_SEND, false)) {
            sendImageAfterPreviewPhotoActivityResult(data);
        } else if (data.getBooleanExtra(PreviewImageFromCameraActivity.RESULT_RETAKE, false)) {
            String filename = StringUtil.get32UUID() + JPG;
            String path = StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);

            if (requestCode == RequestCode.PREVIEW_IMAGE_FROM_CAMERA) {
                PickImageActivity.start(getActivity(), makeRequestCode(RequestCode.PICK_IMAGE), PickImageActivity.FROM_CAMERA, path);
            }
        }
    }


    @SuppressLint("CheckResult")
    protected void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission.requestEach(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    // 用户已经同意该权限
                    if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                        isGalleryOk = true;
                    }
                    if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
                        isCaptureOk = true;
                    }
                    if (isCaptureOk && isGalleryOk) {
                        int requestCode = makeRequestCode(RequestCode.PICK_IMAGE);
                        showSelector(getTitleId(), requestCode, multiSelect, tempFile());
                    }
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』
                    String permissionName = "";
                    if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                        permissionName = "相册";
                    }
                    if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
                        permissionName = "摄像头";
                    }
                    showPermissDialog(getActivity(), permissionName);
                }
            }
        });


    }

    protected void showPermissDialog(Context context, String permissionName) {
        PermissionTipDialogNew dialog = new PermissionTipDialogNew(context, permissionName);
        dialog.onCreateView();
        dialog.setUiBeforShow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
}
