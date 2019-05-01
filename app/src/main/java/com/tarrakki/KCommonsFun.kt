package com.tarrakki

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TableRow
import com.tarrakki.api.ApiClient
import com.tarrakki.api.model.HomeData
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.home.ISSINGLEINVESTMENT
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.investmentstrategies.SelectInvestmentStrategyFragment
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.portfolio.fragments.DirectInvestmentFragment
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.row_table_layout_content.view.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.ktx.clearUserData
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toCurrency

fun getRiskLevelVisibility(riskType: String?): Int {
    return when {
        riskType.equals("HIGH RISK", false) -> View.VISIBLE
        riskType.equals("MEDIUM RISK", false) -> View.VISIBLE
        riskType.equals("LOW RISK", false) -> View.VISIBLE
        else -> View.GONE
    }
}

fun getReturnLevelVisibility(returnType: String?): Int {
    return when {
        returnType.equals("HIGH RETURN", false) -> View.VISIBLE
        returnType.equals("MEDIUM RETURN", false) -> View.VISIBLE
        returnType.equals("LOW RETURN", false) -> View.VISIBLE
        else -> View.GONE
    }
}

fun getRiskLevelDrawable(riskType: String?): Int {
    return when {
        riskType.equals("HIGH RISK", false) -> R.drawable.ic_red_up
        riskType.equals("MEDIUM RISK", false) -> R.drawable.ic_red_equal
        riskType.equals("LOW RISK", false) -> R.drawable.ic_red_down
        else -> R.drawable.ic_red_up
    }
}

fun getReturnLevelDrawable(returnType: String?): Int {
    return when {
        returnType.equals("HIGH RETURN", false) -> R.drawable.ic_green_up
        returnType.equals("MEDIUM RETURN", false) -> R.drawable.ic_green_equal
        returnType.equals("LOW RETURN", false) -> R.drawable.ic_green_down
        else -> R.drawable.ic_green_up
    }
}

fun getRiskLevel(riskType: String?): String {
    return when {
        riskType.equals("HIGH RISK", false) -> "High Risk"
        riskType.equals("MEDIUM RISK", false) -> "Medium Risk"
        riskType.equals("LOW RISK", false) -> "Low Risk"
        else -> ""
    }
}

fun getReturnLevel(returnType: String?): String {
    return when {
        returnType.equals("HIGH RETURN", false) -> "High Return"
        returnType.equals("MEDIUM RETURN", false) -> "Medium Return"
        returnType.equals("LOW RETURN", false) -> "Low Return"
        else -> ""
    }
}

fun FragmentActivity?.onInvestmentStrategies(item: HomeData.Data.Category.SecondLevelCategory) {
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
                                            putString("sip", amountSIP.toString())
                                            putString("lumpsump", amountLumpsum.toString())
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
                        putString(CATEGORYNAME, item.categoryName)
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

fun Context.getUCropOptions(): UCrop.Options {
    val options = UCrop.Options()
    options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
    options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent))
    return options
}

fun Context.getCustomUCropOptions(): com.tarrakki.ucrop.UCrop.Options {
    val options = com.tarrakki.ucrop.UCrop.Options()
    options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
    options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent))
    return options
}

fun Context?.onLogout() {
    this?.let {
        it.clearUserData()
        ApiClient.clear()
        startActivity(Intent(it, LoginActivity::class.java))
        LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(ACTION_FINISH_ALL_TASK))
    }
}

fun Context.tableRow(): TableRow {
    val tableRow = TableRow(this)
    tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
    tableRow.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE or LinearLayout.SHOW_DIVIDER_BEGINNING or LinearLayout.SHOW_DIVIDER_END
    tableRow.dividerDrawable = getDrawable(android.R.drawable.divider_horizontal_bright)
    tableRow.setBackgroundResource(R.color.white)
    return tableRow
}

fun Context.tableRowContent(text: String, textColor: Int? = null): View? {
    val view = LayoutInflater.from(this).inflate(R.layout.row_table_layout_content, null, false)
    view.tvTableRowContent.text = text
    if (textColor != null) {
        view.tvTableRowContent.setTextColor(textColor)
    }
    return view
}

fun Context.tableRowContentWithDrawable(text: String = "", drawable: Drawable? = null): View? {
    val view = LayoutInflater.from(this).inflate(R.layout.row_table_layout_content, null, false)
    view.tvTableRowContent.text = text
    if (drawable != null) {
        view.tvTableRowContent.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
    }
    return view
}

fun getPortfolioCalculatedIntro(): MutableList<DirectInvestmentFragment.InvestmentPortfolioIntro> {
    val options: MutableList<DirectInvestmentFragment.InvestmentPortfolioIntro> = mutableListOf()
    options.add(DirectInvestmentFragment.InvestmentPortfolioIntro(
            "How are returns calculated?",
            "Investments of less than one year reflect absolute returns.\n" +
                    "Investments of one year and above reflect XIRR returns (annualised returns)."))
    options.add(DirectInvestmentFragment.InvestmentPortfolioIntro(
            "When does my portfolio get updated?",
            "Updates that you make to your portfolio today will only be visible here, on your Portfolio Screen, after 9:30 am tomorrow. To check the status of your recent transactions, please navigate to the Transactions screen."))
    return options
}

fun alertStopPortfolio(folio: String, date: String): String {
    return "Your SIP with Folio No. $folio and Start Date $date, has been stopped successfully."
}

fun alertRedeemPortfolio(amount: String): String {
    return "Your redemption of $amount was successful."
}

fun alertSIPMin(amount: String): String {
    return "The SIP amount must be greater than or equal to $amount."
}

fun alertLumpsumMin(amount: String): String {
    return "The lumpsum amount must be greater than or equal to $amount."
}

fun alertBankMandateMinLimit(): String {
    return "Please enter an amount that is greater than or equal to ${1000.0.toCurrency()}."
}