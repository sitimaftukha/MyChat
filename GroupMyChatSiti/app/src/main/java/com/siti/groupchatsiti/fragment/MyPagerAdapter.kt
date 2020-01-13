package com.siti.groupchatsiti.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPagerAdapter(fm: androidx.fragment.app.FragmentManager?, titles: Array<CharSequence>, numboftabs: Int) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    private val title = titles
    private val numboftab = numboftabs

    override fun getItem(p: Int): androidx.fragment.app.Fragment {
        if (p == 0) {
            return UserListFragment()
        }
        if (p == 1) {
            return GroupListFragment()
        }
        return UserListFragment()
    }

    override fun getCount(): Int {
        return numboftab
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}