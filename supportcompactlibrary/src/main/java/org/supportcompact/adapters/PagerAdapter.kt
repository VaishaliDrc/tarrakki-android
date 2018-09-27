package org.supportcompact.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import org.supportcompact.ktx.inflate

/**
 * This is extension function setup the of ViewPager's PagerAdapter.
 * @param layout layout to be bound to adapter.
 * @param items data to be bound with layout.
 * @param onBind Is Unit function to override the  instantiateItem of PagerAdapter.
 * */
fun <T, U : ViewDataBinding> ViewPager.setPageAdapter(@LayoutRes layout: Int, items: ArrayList<T>, onBind: (binder: U, item: T) -> Unit): PagerAdapter? {
    adapter = object : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item: T = items[position]
            val binder: U = DataBindingUtil.bind(container.inflate(layout))!!
            container.addView(binder.root)
            onBind.invoke(binder, item)
            return binder.root
        }

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = items.size

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return items[position].toString()
        }
    }
    return adapter
}

/**
 * This is extension function setup the of ViewPager's FragmentPagerAdapter.
 * @param fm Is fragment manager.
 * @param fragments is list of fragment to be show.
 * */
fun <T : Fragment> ViewPager.setFragmentPagerAdapter(fm: FragmentManager, fragments: ArrayList<T>): FragmentPagerAdapter? {
    adapter = object : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = fragments[position]
        override fun getCount() = fragments.size
        override fun getPageTitle(position: Int) = fragments[position].toString()
    }
    return adapter as FragmentPagerAdapter
}