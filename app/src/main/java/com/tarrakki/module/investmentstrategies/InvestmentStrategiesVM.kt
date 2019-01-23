package com.tarrakki.module.investmentstrategies

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import org.supportcompact.FragmentViewModel

class InvestmentStrategiesVM : FragmentViewModel() {

    val investmentStrategies = arrayListOf<InvestmentStrategy>()

    val secondaryCategoriesList = MutableLiveData<List<HomeData.Data.Category.SecondLevelCategory>>()

    init {
        investmentStrategies.add(InvestmentStrategy(
                "Very Long Term Investments",
                "Diversified equity founds, 10+ years or longer",
                imgUrl = R.drawable.very_long_investments
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Long Term Investments",
                "Diversified equity founds, 5+ years or longer",
                imgUrl = R.drawable.very_long_investments
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Short Term Investments",
                "Debt fund for 1-5 years, FD Alternative",
                imgUrl = R.drawable.very_long_investments
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Emergency Fund",
                "Deft fund for 0-12 months, Debit card",
                imgUrl = R.drawable.emergency_fund
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Tax Savers",
                "Save tax up to 45,000 with ELSS funds",
                imgUrl = R.drawable.tax_savers
        ))
        investmentStrategies.add(InvestmentStrategy(
                "High Risk - High Return",
                "Lorem ipsum is simply dummy text of the printing",
                imgUrl = R.drawable.high_risk_high_return
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Medium Risk - Medium Return",
                "Lorem ipsum is simply dummy text of the printing",
                imgUrl = R.drawable.medium_risk_medium_return
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Invest in Large Cap",
                "Lorem ipsum is simply dummy text of the printing",
                imgUrl = R.drawable.invest_in_large_cap
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Invest in Mid Cap",
                "Lorem ipsum is simply dummy text of the printing",
                imgUrl = R.drawable.invest_in_mid_cap
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Invest in Small Cap",
                "Lorem ipsum is simply dummy text of the printing",
                imgUrl = R.drawable.invest_in_small_cap
        ))
        investmentStrategies.add(InvestmentStrategy(
                "Thematic Investment",
                "Lorem ipsum is simply dummy text of the printing",
                imgUrl = R.drawable.thematic_investment
        ))
    }
}

data class InvestmentStrategy(var title: String, var description: String = "", @DrawableRes var imgUrl: Int = R.drawable.very_long_investments)