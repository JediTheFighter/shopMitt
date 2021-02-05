package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ecommerce.shopmitt.views.fragments.TimeSlotFragment
import java.util.*
import kotlin.collections.ArrayList

class ViewPagerFragmentAdapter(fm: FragmentActivity, context: Context):
    FragmentStateAdapter(fm) {

    private val mFragmentList: ArrayList<Fragment> = ArrayList()


    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getItemCount() = mFragmentList.size

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun getCurrentFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}