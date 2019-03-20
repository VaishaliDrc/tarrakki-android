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
import org.supportcompact.widgets.AutoScrollWrapContentViewPager
import org.supportcompact.widgets.CustomViewPager
import org.supportcompact.widgets.WrapContentViewPager


/**
 * This is extension function setup the of ViewPager's PagerAdapter.
 * @param layout layout to be bound to adapter.
 * @param items data to be bound with layout.
 * @param onBind Is Unit function to override the  instantiateItem of PagerAdapter.
 * */
fun <T : WidgetsViewModel> ViewPager.setMultiViewPageAdapter(items: ArrayList<T>, onBind: (binder: ViewDataBinding, item: T) -> Unit): PagerAdapter? {
    adapter = object : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item: T = items[position]
            val binder: ViewDataBinding = DataBindingUtil.bind(container.inflate(item.layoutId()))!!
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

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
    return adapter
}

/**
 * This is extension function setup the of ViewPager's PagerAdapter.
 * @param layout layout to be bound to adapter.
 * @param items data to be bound with layout.
 * @param onBind Is Unit function to override the  instantiateItem of PagerAdapter.
 * */
fun <T, U : ViewDataBinding> ViewPager.setPageAdapter(@LayoutRes layout: Int, items: ArrayList<T>, onBind: (binder: U, item: T) -> Unit): PagerAdapter? {
    adapter = object : PagerAdapter() {
        private var mCurrentPosition = -1

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

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
    return adapter
}

fun <T, U : ViewDataBinding> ViewPager.setWrapContentPageAdapter(@LayoutRes layout: Int, items: ArrayList<T>, onBind: (binder: U, item: T) -> Unit): PagerAdapter? {
    adapter = object : PagerAdapter() {
        private var mCurrentPosition = -1

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)

            if (container !is WrapContentViewPager) {
                throw UnsupportedOperationException("ViewPager is not a WrappingViewPager")
            }

            if (position != mCurrentPosition) {
                mCurrentPosition = position
                val item: T = items[mCurrentPosition]
                val binder: U = DataBindingUtil.bind(container.inflate(layout))!!
                container.onPageChanged(binder.root)
                onBind.invoke(binder, item)
            }
        }

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

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
    return adapter
}

fun <T, U : ViewDataBinding> ViewPager.setAutoWrapContentPageAdapter(@LayoutRes layout: Int, items: ArrayList<T>, onBind: (binder: U, item: T) -> Unit): PagerAdapter? {
    adapter = object : PagerAdapter() {
        private var mCurrentPosition = -1

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)

            if (container !is AutoScrollWrapContentViewPager) {
                throw UnsupportedOperationException("ViewPager is not a WrappingViewPager")
            }

            if (position != mCurrentPosition) {
                mCurrentPosition = position
                val item: T = items[mCurrentPosition]
                val binder: U = DataBindingUtil.bind(container.inflate(layout))!!
                container.onPageChanged(binder.root)
                onBind.invoke(binder, item)
            }
        }

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

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
    return adapter
}

/**
 * This is extension function setup the of ViewPager's PagerAdapter.
 * @param layout layout to be bound to adapter.
 * @param items data to be bound with layout.
 * @param onBind Is Unit function to override the  instantiateItem of PagerAdapter.
 * */
fun <T, U : ViewDataBinding> CustomViewPager.setPageAdapter(@LayoutRes layout: Int, items: ArrayList<T>, onBind: (binder: U, item: T) -> Unit): PagerAdapter? {
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

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
    return adapter
}


/**
 * This is extension function setup the of ViewPager's FragmentPagerAdapter.
 * @param fm Is fragment manager.
 * @param fragments is list of fragment to be show.
 * */
fun ViewPager.setFragmentPagerAdapter(fm: FragmentManager, pages: ArrayList<Page>): FragmentPagerAdapter? {
    adapter = object : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = pages[position].page
        override fun getCount() = pages.size
        override fun getPageTitle(position: Int) = pages[position].title
    }
    return adapter as FragmentPagerAdapter
}

data class Page(var title: String, var page: Fragment)