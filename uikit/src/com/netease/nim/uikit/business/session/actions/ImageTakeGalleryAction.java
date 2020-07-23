package com.netease.nim.uikit.business.session.actions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.constant.RequestCode;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * Created by N
 */
public class ImageTakeGalleryAction extends PickImageAction {

    public ImageTakeGalleryAction() {
//        super(R.drawable.nim_message_plus_photo_selector, R.string.input_panel_photo, true);
        super(R.drawable.nim_message_plus_photo_selector, R.string.input_panel_take_gallery, true);
    }

    @Override
    protected void onPicked(File file) {
        IMMessage message;
        if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom) {
            message = ChatRoomMessageBuilder.createChatRoomImageMessage(getAccount(), file, file.getName());
        } else {
            message = MessageBuilder.createImageMessage(getAccount(), getSessionType(), file, file.getName());
        }
        sendMessage(message);
    }

    @Override
    protected void showSelector(int titleId, int requestCode, boolean multiSelect, String outPath) {
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = titleId;
        option.multiSelect = multiSelect;
        option.multiSelectMaxCount = PICK_IMAGE_COUNT;
        option.crop = crop;
        option.cropOutputImageWidth = PORTRAIT_IMAGE_WIDTH;
        option.cropOutputImageHeight = PORTRAIT_IMAGE_WIDTH;
        option.outputPath = outPath;
        PickImageHelper.pickTakeGallery(getActivity(), requestCode, option);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    // 用户已经同意该权限
                    if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
                        isGalleryOk = true;
                    }
                    if (isGalleryOk) {
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
                    showPermissDialog(getActivity(), permissionName);
                }
            }
        });
    }


}

