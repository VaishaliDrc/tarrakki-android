package com.tarrakki.module.home

import android.databinding.ObservableField
import android.support.annotation.LayoutRes
import android.view.View
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
                                        "Diversified equity founds, 10+ years or longer"
                                ),
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 10+ years or longer"
                                ),
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 10+ years or longer"
                                )
                        ))
        )
        homeSections.add(
                HomeSection(
                        R.layout.row_section_investment_item,
                        "Set a Goal",
                        arrayListOf(
                                HomeItem(
                                        "Very Long Term Investments"
                                ),
                                HomeItem(
                                        "Very Long Term Investments"
                                ),
                                HomeItem(
                                        "Very Long Term Investments"
                                ),
                                HomeItem(
                                        "Very Long Term Investments"
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

data class HomeItem(var title: String, var description: String = "", var imgUrl: String = "")