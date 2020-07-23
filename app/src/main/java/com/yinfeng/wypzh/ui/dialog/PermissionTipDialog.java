package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.ContextUtils;

public class PermissionTipDialog extends BaseDialog<PermissionTipDialog> {
    private Context mContext;
    private String title, content;
    private TextView tvTitle, tvContent, tvConfirm;

    public PermissionTipDialog(Context context, String permissionName) {
        super(context);
        mContext = context;
        this.title = initTitle(permissionName);
        this.content = initContent(permissionName);
    }

    private String initContent(String permissionName) {
        return "打开" + permissionName + "失败，请尝试以下路径开启" + permissionName + "权限:"
                + "\n"
                + "方法一,安全中心\u2192授权管理\u2192应用权限管理\u2192应用管理\u2192无忧陪诊\u2192打开" + permissionName + "\u2192允许";

    }

    private String initTitle(String permissionName) {
        return "开启" + permissionName + "权限";
    }

    @Override
    public View onCreateView() {
//        widthScale(0.85f);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_permissiontip, null);
        tvTitle = view.findViewById(R.id.permissionTipTitile);
        tvContent = view.findViewById(R.id.permissionTipContent);
        tvConfirm = view.findViewById(R.id.permissionTipConfirm);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvConfirm.setText("知道了");
        return view;
    }

    @Override
    public void setUiBeforShow() {
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ContextUtils.skipToSettingDetail(mContext);
            }
        });
    }
}
