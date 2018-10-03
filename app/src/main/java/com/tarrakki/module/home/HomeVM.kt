package com.tarrakki.module.home

import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import com.tarrakki.R
import com.tarrakki.module.goal.Goal
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class HomeVM : FragmentViewModel() {

    val whayTarrakki = ObservableField(true)
    var homeSections = ArrayList<WidgetsViewModel>()

    init {
        homeSections.add(
                HomeSection(
                        "Investment Strategies",
                        arrayListOf(
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 10+ years or longer"
                                ),
                                HomeItem(
                                        "Long Term Investments",
                                        "Diversified equity founds, 5+ years or longer"
                                ),
                                HomeItem(
                                        "Short Long Term Investments",
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
                        "Set a Goal",
                        arrayListOf(
                                Goal("Wealth creation", R.drawable.wealth_creation),
                                Goal("Holiday", R.drawable.holiday),
                                Goal("Electronic Gadget", R.drawable.electronic_gadget),
                                Goal("Automobile", R.drawable.automobile),
                                Goal("Own a Home", R.drawable.own_a_home)
                        )
                ))
    }
}

data class HomeSection(var title: String, var homeItems: ArrayList<WidgetsViewModel>?) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_section_investment_item
    }
}

data class HomeItem(var title: String, var description: String = "", @DrawableRes var imgUrl: Int = R.drawable.very_long_investments) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_investment_list_item
    }
}