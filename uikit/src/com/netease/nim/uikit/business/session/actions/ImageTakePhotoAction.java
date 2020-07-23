package com.netease.nim.uikit.business.session.actions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.netease.nim.uikit.R;
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
public class ImageTakePhotoAction extends PickImageAction {
    public ImageTakePhotoAction() {
//        super(R.drawable.nim_message_plus_photo_selector, R.string.input_panel_photo, true);
        super(R.drawable.nim_message_plus_video_selector, R.string.input_panel_take_photo, true);
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
        PickImageHelper.pickTakePhote(getActivity(), requestCode, option);
    }

}

