package com.tarrakki.module.portfolio

import org.supportcompact.FragmentViewModel

class PortfolioVM : FragmentViewModel() {

    val directInvestment = arrayListOf<Investment>()

    init {

        directInvestment.add(Investment(
                "",
                "",
                10.7,
                30000.00,
                45000.00,
                41.00))

    }

}

data class Investment(
        var name: String,
        var date: String,
        var returns: Double,
        var totalInvestment: Double,
        var currentValue: Double,
        var XIRR: Double
)