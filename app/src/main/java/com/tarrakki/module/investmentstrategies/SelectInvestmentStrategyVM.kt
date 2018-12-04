package com.tarrakki.module.investmentstrategies

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.graphics.Color
import android.support.annotation.DrawableRes
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.FragmentViewModel

class SelectInvestmentStrategyVM : FragmentViewModel() {

    val noteToInvestors = ObservableField(true)
    val investmentOptions = arrayListOf<InvestmentOption>()

    init {
        investmentOptions.add(InvestmentOption(
                title = "Aggressive",
                imgRes = R.drawable.very_long_investments,
                riskLevel = "High Risk",
                riskLevelIMGRes = R.drawable.very_long_investments,
                returnOnRiskLevel = "High Return",
                returnOnRiskLevelIMGRes = R.drawable.very_long_investments,
                sortDescription = "An aggressive investment strategy involves allocating more money to direct equity, and less money to bonds.",
                descriptions = SpannableStringBuilder().apply {
                    append(SpannableString("You have a high risk tolerance").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    append("\n\n")
                    append(SpannableString("You are willing to accept significant ups and downs or fluctuations in the value of your portfolio").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    append("\n\n")
                    append(SpannableString("You want relatively higher returns that outpace inflation by a huge margin").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                }
        ).apply {
            hasNext = true
            hasPrevious = false
        })
        investmentOptions.add(InvestmentOption(
                title = "Moderate",
                imgRes = R.drawable.very_long_investments,
                riskLevel = "Medium Risk",
                riskLevelIMGRes = R.drawable.very_long_investments,
                returnOnRiskLevel = "Medium Return",
                returnOnRiskLevelIMGRes = R.drawable.very_long_investments,
                sortDescription = "An aggressive investment strategy involves allocating equal money to equity and bonds.",
                descriptions = SpannableStringBuilder().apply {
                    append(SpannableString("You have medium risk tolerance").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    append("\n\n")
                    append(SpannableString("You are willing to accept some ups and downs or moderate fluctuations in the value of your portfolio").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    append("\n\n")
                    append(SpannableString("You want returns on investment that are likely to outpace inflation by a notable margin").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                }
        ))
        investmentOptions.add(InvestmentOption(
                title = "Conservative",
                imgRes = R.drawable.very_long_investments,
                riskLevel = "Low Risk",
                riskLevelIMGRes = R.drawable.very_long_investments,
                returnOnRiskLevel = "Low Return",
                returnOnRiskLevelIMGRes = R.drawable.very_long_investments,
                sortDescription = "This investment strategy involves allocating most of the money to bonds and liquid assets. This strategy works well if you are a risk averse investor.",
                descriptions = SpannableStringBuilder().apply {
                    append(SpannableString("You have a low risk tolerance").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    append("\n\n")
                    append(SpannableString("You are unwilling to accept extreme ups and downs or fluctuations in the value of your portfolio").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    append("\n\n")
                    append(SpannableString("You want returns that match the rate of inflation or marginally outpace it").apply {
                        setSpan(BulletSpan(20, Color.GREEN), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                }
        ).apply {
            hasNext = false
            hasPrevious = true
        })
    }
}

data class InvestmentOption(
        var title: String,
        @DrawableRes
        var imgRes: Int,
        var riskLevel: String,
        @DrawableRes
        var riskLevelIMGRes: Int,
        var returnOnRiskLevel: String,
        @DrawableRes
        var returnOnRiskLevelIMGRes: Int,
        var sortDescription: String,
        var descriptions: SpannableStringBuilder
) : BaseObservable() {
    @get:Bindable
    var hasNext: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasNext)
        }
    @get:Bindable
    var hasPrevious: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasPrevious)
        }
}