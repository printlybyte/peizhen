package com.yinfeng.wypzh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.ui.order.fragment.OrderFinishFragment;
import com.yinfeng.wypzh.ui.order.fragment.OrderServicingFragment;
import com.yinfeng.wypzh.ui.order.fragment.OrderWaitCommentFragment;
import com.yinfeng.wypzh.ui.order.fragment.OrderWaitReceiveFragment;
import com.yinfeng.wypzh.ui.order.fragment.OrderWaitServiceFragment;

import java.util.List;

/**
 * Description :
 *
 * @author Asen
 */
public class OrderChildViewPageAdapter extends FragmentStatePagerAdapter {
    List<BaseFragment> fragments;

    /**
     * @param fm        fragment 下的fragment  childfm
     * @param fragments
     */
    public OrderChildViewPageAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = OrderWaitReceiveFragment.newInstance();
                    break;
                case 1:
                    fragment = OrderWaitServiceFragment.newInstance();
                    break;
                case 2:
                    fragment = OrderServicingFragment.newInstance();
                    break;
                case 3:
                    fragment = OrderWaitCommentFragment.newInstance();
                    break;
                case 4:
                    fragment = OrderFinishFragment.newInstance();
                    break;
            }
            fragments.set(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }
}
