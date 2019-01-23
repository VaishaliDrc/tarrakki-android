package com.tarrakki.module.investmentstrategies

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import org.supportcompact.FragmentViewModel
import java.io.Serializable

class SelectInvestmentStrategyVM : FragmentViewModel() {

    val txtnoteToInvestors = ObservableField("")
    val noteToInvestors = ObservableField(true)
    val investmentOptions = arrayListOf<InvestmentOption>()

    val category = MutableLiveData<HomeData.Data.Category.SecondLevelCategory>()

    init {
        investmentOptions.add(InvestmentOption(
                title = "Aggressive",
                imgRes = R.drawable.ic_aggressive,
                riskLevel = "High Risk",
                riskLevelIMGRes = App.INSTANCE.getDrawable(R.drawable.ic_red_up),
                returnOnRiskLevel = "High Return",
                returnOnRiskLevelIMGRes = App.INSTANCE.getDrawable(R.drawable.ic_green_up),
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
                imgRes = R.drawable.ic_moderate,
                riskLevel = "Medium Risk",
                riskLevelIMGRes = App.INSTANCE.getDrawable(R.drawable.ic_red_equal),
                returnOnRiskLevel = "Medium Return",
                returnOnRiskLevelIMGRes = App.INSTANCE.getDrawable(R.drawable.ic_green_equal),
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
                imgRes = R.drawable.ic_conservative,
                riskLevel = "Low Risk",
                riskLevelIMGRes = App.INSTANCE.getDrawable(R.drawable.ic_red_down),
                returnOnRiskLevel = "Low Return",
                returnOnRiskLevelIMGRes = App.INSTANCE.getDrawable(R.drawable.ic_green_down),
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
        var riskLevelIMGRes: Drawable?,
        var returnOnRiskLevel: String,
        var returnOnRiskLevelIMGRes: Drawable?,
        var sortDescription: String,
        var descriptions: SpannableStringBuilder
) : BaseObservable(), Serializable {
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