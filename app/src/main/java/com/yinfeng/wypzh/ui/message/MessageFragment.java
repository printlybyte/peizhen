package com.yinfeng.wypzh.ui.message;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.bean.message.MessageOrderNotice;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.List;


public class MessageFragment extends BaseFragment {

    private TopBar mTopBar;
    //    private SmartRefreshLayout mSmartRefreshLayout;
    private LinearLayout llOrderNotice;
    private View redPoint;
    private RecentContactsFragment recentContactsFragment;
    private RecentContactsFragment.UnreadMsgImListener unreadMsgImListener;
    private boolean isHasInit = false;
    public static MessageFragment newInstance() {
        Bundle args = new Bundle();
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.hideTopBack();
        mTopBar.setTopCenterTxt("消息");
//        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
//        mSmartRefreshLayout.setEnableLoadMore(false);
//        mSmartRefreshLayout.setEnableRefresh(false);

        llOrderNotice = mRootView.findViewById(R.id.llOrderNotice);
        redPoint = mRootView.findViewById(R.id.redPoint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llOrderNotice.setBackgroundResource(R.drawable.ripple_white_solid_corner2);
        } else {
            llOrderNotice.setBackgroundResource(R.drawable.shape_white_solid_corner_small);
        }
        unreadMsgImListener = new RecentContactsFragment.UnreadMsgImListener() {
            @Override
            public void unreadNumb(int numb) {
                if (numb > 0)
                    RedPointUtil.showBottomDot(2);
            }
        };
        recentContactsFragment = (RecentContactsFragment) getChildFragmentManager().findFragmentById(R.id.mIMListFragment);
        recentContactsFragment.setUnReadMsgListener(unreadMsgImListener);
        recentContactsFragment.requestMessages(true);
        isHasInit = true;
    }

    @Override
    protected void setListener() {
        llOrderNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MessageListOrderNotice.class));
                redPoint.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void initData() {
    }


    @Override
    protected int getContentLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void onSupportVisible() {
        List<MessageOrderNotice> list = SFUtil.getInstance().getMsgNoticeListNew(getActivity());
        if (list != null && list.size() > 0) {
            redPoint.setVisibility(View.VISIBLE);
        } else {
            redPoint.setVisibility(View.GONE);
        }
        if (isHasInit) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
            recentContactsFragment.requestMessages(true);
        }
        super.onSupportVisible();
    }

    @Override
    public void onSupportInvisible() {
        if (isHasInit) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        }
        super.onSupportInvisible();
    }
}
