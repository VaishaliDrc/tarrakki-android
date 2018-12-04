package com.tarrakki.module.investmentstrategies

import android.support.annotation.DrawableRes
import com.tarrakki.R
import org.supportcompact.FragmentViewModel

class InvestmentStrategiesVM : FragmentViewModel() {

    val investmentStrategies = arrayListOf<InvestmentStrategy>()

    init {
        investmentStrategies.add(InvestmentStrategy(
                "Very Long Term Investments",
                "Diversified equity founds, 10+ years or longer"
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Long Term Investments",
                "Diversified equity founds, 5+ years or longer"
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Short Long Term Investments",
                "Diversified equity founds, 3+ years or longer"
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Very Long Term Investments",
                "Diversified equity founds, 2+ years or longer"
        ))
    }
}

data class InvestmentStrategy(var title: String, var description: String = "", @DrawableRes var imgUrl: Int = R.drawable.very_long_investments)