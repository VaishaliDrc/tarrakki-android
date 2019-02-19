package com.tarrakki.module.portfolio

import org.supportcompact.FragmentViewModel

class PortfolioVM : FragmentViewModel() {

    val directInvestment = arrayListOf<Investment>()
    val goalBasedInvestment = arrayListOf<Investment>()

    init {

        directInvestment.add(Investment(
                "ICICI Prudential Balanced Advantage Fund-Direct-Growth",
                "NAV on\n29 Aug 2018",
                10.7,
                30000.00,
                45000.00,
                41.00))

        directInvestment.add(Investment(
                "ICICI Prudential Balanced Advantage Fund-Direct-Growth",
                "NAV on\n29 Aug 2018",
                10.7,
                30000.00,
                45000.00,
                41.00))

        goalBasedInvestment.add(Investment(
                "HOME GOAL",
                "",
                0.0,
                20000.00,
                27500.00,
                21.00)
        )

        goalBasedInvestment.add(Investment(
                "AUTO GOAL",
                "",
                0.0,
                12500.00,
                14800.00,
                8.50)
        )
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