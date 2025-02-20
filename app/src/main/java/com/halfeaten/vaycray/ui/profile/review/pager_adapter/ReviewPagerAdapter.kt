package com.halfeaten.vaycray.ui.profile.review.pager_adapter

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.ui.profile.review.FragmentReviewAboutYou
import com.halfeaten.vaycray.ui.profile.review.FragmentReviewByYou

class ReviewPagerAdapter (fm: FragmentManager, private var context: Activity) : FragmentStatePagerAdapter(fm){
    override fun getCount(): Int {
       return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentReviewAboutYou()
            1 -> FragmentReviewByYou()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return context.getString(R.string.about_you)
            1 -> return context.getString(R.string.by_you)
        }
        return null
    }
}