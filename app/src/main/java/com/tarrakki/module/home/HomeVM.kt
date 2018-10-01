package com.tarrakki.module.home

import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class HomeVM : FragmentViewModel() {

    val whayTarrakki = ObservableField(true)
    var homeSections = ArrayList<WidgetsViewModel>()

    init {
        homeSections.add(
                HomeSection(
                        R.layout.row_section_investment_item,
                        "Investment Strategies",
                        arrayListOf(
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 10+ years or longer"
                                ),
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 5+ years or longer"
                                ),
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 3+ years or longer"
                                ),
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 2+ years or longer"
                                )
                        ))
        )
        homeSections.add(
                HomeSection(
                        R.layout.row_section_investment_item,
                        "Set a Goal",
                        arrayListOf(
                                HomeItem(
                                        title = "Wealth creation",
                                        imgUrl = R.drawable.wealth_creation
                                ),
                                HomeItem(
                                        title = "Holiday",
                                        imgUrl = R.drawable.holiday
                                ),
                                HomeItem(
                                        title = "Electronic Gadget",
                                        imgUrl = R.drawable.electronic_gadget
                                ),
                                HomeItem(
                                        title = "Automobile",
                                        imgUrl = R.drawable.automobile
                                )
                        ))
        )
    }
}

data class HomeSection(@LayoutRes val layout: Int, var title: String, var homeItems: ArrayList<HomeItem>?) : WidgetsViewModel {
    override fun layoutId(): Int {
        return layout
    }
}

data class HomeItem(var title: String, var description: String = "", @DrawableRes var imgUrl: Int = R.drawable.very_long_investments)