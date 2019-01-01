package com.tarrakki.module.recommended

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.support.annotation.ColorRes
import android.text.SpannableStringBuilder
import com.tarrakki.R
import com.tarrakki.api.model.RecommendedFunds
import com.tarrakki.module.investmentstrategies.InvestmentOption
import org.supportcompact.FragmentViewModel

class RecommendedVM : FragmentViewModel() {

    val AMCList = arrayListOf<AMC>()
    val investment = MutableLiveData<InvestmentOption>()
    /****Api***/
    val funds = MutableLiveData<RecommendedFunds>()
    val goalVM: MutableLiveData<com.tarrakki.api.model.Goal.Data.GoalData> = MutableLiveData()
    val lumpsumpFor = ObservableField<SpannableStringBuilder>()


    init {
        AMCList.add(AMC(
                "ICICI Prudential NIFTY Next 50 Index Growth Direct Plan",
                "Equity - India Multi Cap Equity",
                0.0,
                2300.toDouble(),
                16.1f,
                "Last 5Y",
                48f,
                R.color.balanced_fund_color))
        AMCList.add(AMC(
                "DSP 10Y G SEC Growth Direct Plan",
                "Debt - Bonds - Government Bond Fund",
                3900.toDouble(),
                1800.toDouble(),
                7f,
                "Last 5Y",
                38.5f,
                R.color.equity_fund_color))

        AMCList.add(AMC(
                "IDFC NIFTY Growth Direct Plan",
                "Equity - India Large Cap Equity",
                6200.toDouble(),
                600.toDouble(),
                14.7f,
                "Last 5Y",
                13.5f,
                R.color.debt_fund_color
        ))
    }


}

data class AMC(
        var name: String,
        var description: String,
        var oneTime: Double,
        var monthly: Double,
        var returns: Float,
        var returnsSince: String,
        var weightage: Float,
        @ColorRes var fundColor: Int = R.color.equity_fund_color
)