package com.ccj.gilla;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

// 탭이 변할때마다 position을 받아 Fragment를 전환해  주는 역할을 한다.

//FragmentStatePagerAdapter란 ViewPager를 활용하여 Framgent를 관리해주는 Adapter이다.
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    // tab의 개수
    private int tabCount;

    // 액티비티와 프래그먼트의 중간에서 서로 이어주는 역할을 하는것이 바로 FragmentManager이다.
    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TabFragment1 tabFragment1 = new TabFragment1();
                return tabFragment1;
            case 1:
                TabFragment2 tabFragment2 = new TabFragment2();
                return tabFragment2;
            case 2:
                TabFragment3 tabFragment3 = new TabFragment3();
                return tabFragment3;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
