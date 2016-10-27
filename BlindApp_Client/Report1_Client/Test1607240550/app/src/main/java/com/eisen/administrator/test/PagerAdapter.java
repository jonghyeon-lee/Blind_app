package com.eisen.administrator.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.eisen.administrator.test.blind.BlindFragment;
import com.eisen.administrator.test.mem.MemberFragment;

/**
 * Created by Administrator on 2016-07-17.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1: // 멤버
                MemberFragment mem = new MemberFragment();
                return mem;
            case 2:
                SetFragment set = new SetFragment();
                return set;
            case 0: // 라이브 - 실시간 유저 채팅
            default:
                BlindFragment live = new BlindFragment();
                return live;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
