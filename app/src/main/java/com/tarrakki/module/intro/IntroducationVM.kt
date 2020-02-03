package com.tarrakki.module.intro

import androidx.databinding.BaseObservable
import androidx.annotation.DrawableRes
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.isTarrakki
import org.supportcompact.ActivityViewModel

class IntroducationVM : ActivityViewModel() {

    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()

    fun getIntroductionList() : ArrayList<Introduction>{
        val introductionList = arrayListOf<Introduction>()
        introductionList.add(Introduction("Make Your Money Grow!",
                "Watch your hard earned money grow beyond the rate of inflation.", R.drawable.intr_money_grow))
        introductionList.add(Introduction("Trusted",
                "Tarrakki is a SEBI registered investment adviser and a bank grade secure transaction portal.", R.drawable.intr_trusted))
        introductionList.add(Introduction("Assess Your Risk Profile",
                "Assess how much risk you should be taking with your money, based on your age,income and stage in life.", R.drawable.intr_risk_profile))
        introductionList.add(Introduction("Invest Strategically in Mutual Funds",
                "Ascertain the ideal investment strategy for you and invest in Tarrakki Recommended funds.", R.drawable.intr_invest_strateggy))
        introductionList.add(Introduction("Goal Based Investing",
                "Plan and save small amount of money regularly towards specific life goals and achieve your goals much faster.", R.drawable.intr_goal_basedd))
        introductionList.add(Introduction("Equity Advisory",
                "Seek expert guidance to invest directly in equities based on model portfolios tailored for your individual needs.", R.drawable.intr_equaity_advisor))
        return introductionList
    }

    class Introduction(val title: String,val message: String,@DrawableRes val image: Int) : BaseObservable()
}