package com.tarrakki

import android.content.Context
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tarrakki.api.model.HomeData
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.home.ISSINGLEINVESTMENT
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.investmentstrategies.SelectInvestmentStrategyFragment
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import org.greenrobot.eventbus.EventBus
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

fun getRiskLevelVisibility(riskType: String?) : Int{
    return when {
        riskType.equals("HIGH RISK", false) -> View.VISIBLE
        riskType.equals("MEDIUM RISK", false) -> View.VISIBLE
        riskType.equals("LOW RISK", false) -> View.VISIBLE
        else -> View.GONE
    }
}

fun getReturnLevelVisibility(returnType: String?) : Int{
    return when {
        returnType.equals("HIGH RETURN", false) -> View.VISIBLE
        returnType.equals("MEDIUM RETURN", false) -> View.VISIBLE
        returnType.equals("LOW RETURN", false) -> View.VISIBLE
        else -> View.GONE
    }
}

fun getRiskLevelDrawable(riskType: String?) : Int{
    return when {
        riskType.equals("HIGH RISK", false) -> R.drawable.ic_red_up
        riskType.equals("MEDIUM RISK", false) -> R.drawable.ic_red_equal
        riskType.equals("LOW RISK", false) -> R.drawable.ic_red_down
        else -> R.drawable.ic_red_up
    }
}

fun getReturnLevelDrawable(returnType: String?) : Int{
    return when {
        returnType.equals("HIGH RETURN", false) -> R.drawable.ic_green_up
        returnType.equals("MEDIUM RETURN", false) -> R.drawable.ic_green_equal
        returnType.equals("LOW RETURN", false) -> R.drawable.ic_green_down
        else -> R.drawable.ic_green_up
    }
}

fun getRiskLevel(riskType: String?) : String{
    return when {
        riskType.equals("HIGH RISK", false) -> "High Risk"
        riskType.equals("MEDIUM RISK", false) -> "Medium Risk"
        riskType.equals("LOW RISK", false) -> "Low Risk"
        else -> ""
    }
}

fun getReturnLevel(returnType: String?) : String{
    return when {
        returnType.equals("HIGH RETURN", false) -> "High Return"
        returnType.equals("MEDIUM RETURN", false) -> "Medium Return"
        returnType.equals("LOW RETURN", false) -> "Low Return"
        else -> ""
    }
}

fun FragmentActivity?.onInvestmentStrategies(item : HomeData.Data.Category.SecondLevelCategory){
    if (!item.isGoal) {
        if (item.isThematic) {
            val bundle = Bundle().apply {
                putString(CATEGORYNAME, item.categoryName)
            }
            this?.startFragment(InvestmentStrategiesFragment.newInstance(bundle), R.id.frmContainer)
            EventBus.getDefault().postSticky(item)
        } else {
            val thirdLevelCategory = item.thirdLevelCategory
            if (thirdLevelCategory.isNotEmpty()) {
                if (thirdLevelCategory[0].categoryName.isNullOrEmpty()) {
                    if (!item.categoryDesctiption.isNullOrEmpty()) {
                        val bundle = Bundle().apply {
                            putString(CATEGORYNAME, item.sectionName)
                            putBoolean(ISSINGLEINVESTMENT, true)
                        }
                        this?.startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                        EventBus.getDefault().postSticky(item)
                    } else {
                        this?.investmentStragiesDialog(item.thirdLevelCategory[0]) { thirdLevelCategoryItem, amountLumpsum, amountSIP ->
                            investmentRecommendation(thirdLevelCategoryItem.id, amountSIP, amountLumpsum, 0).observe(this,
                                    android.arch.lifecycle.Observer { response ->
                                        val bundle = Bundle().apply {
                                            putInt("sip", amountSIP)
                                            putInt("lumpsump", amountLumpsum)
                                            putInt("isFrom", 2)
                                        }
                                        startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                        EventBus.getDefault().postSticky(item)
                                        EventBus.getDefault().postSticky(item.thirdLevelCategory[0])
                                        EventBus.getDefault().postSticky(response?.data)
                                    })
                        }
                    }
                } else {
                    val bundle = Bundle().apply {
                        putString(CATEGORYNAME, item.sectionName)
                        putBoolean(ISSINGLEINVESTMENT, false)
                    }
                    this?.startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                    EventBus.getDefault().postSticky(item)
                }
            } else {

                this?.simpleAlert(getString(R.string.alert_third_level_category))
            }
        }
    } else {
        this?.startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.redirectTo}") }), R.id.frmContainer)
    }
}