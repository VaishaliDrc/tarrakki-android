package com.tarrakki.module.intro

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import androidx.appcompat.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.tarrakki.R

internal class IntroPagerAdapter(var mContext: Context, var mResources: IntArray) : androidx.viewpager.widget.PagerAdapter() {
    var mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mResources.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.layout_introducation_item, container, false)

        val imageView = itemView.findViewById<View>(R.id.imageView) as AppCompatImageView
        imageView.setImageResource(mResources[position])

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}