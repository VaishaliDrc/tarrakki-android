package com.tarrakki.module.portfolio

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import org.supportcompact.BR
import org.supportcompact.FragmentViewModel

class PortfolioDetailsVM : FragmentViewModel() {

    val portfolioFunds = arrayListOf<PortfolioFund>()
    val investment = MutableLiveData<Investment>()

    init {

        portfolioFunds.add(PortfolioFund(
                "HDFC Small Cap",
                6000.00,
                8000.00)
        )

        portfolioFunds.add(PortfolioFund(
                "ICIC Mid Cap",
                8000.00,
                10000.00)
        )

        portfolioFunds.add(PortfolioFund(
                "DSP BR Liquid",
                6000.00,
                9500.00)
        )

    }

}

data class PortfolioFund(
        var name: String,
        var totalInvestment: Double,
        var currentValue: Double
) : BaseObservable() {

    @get:Bindable
    var isStart: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.start)
        }

}