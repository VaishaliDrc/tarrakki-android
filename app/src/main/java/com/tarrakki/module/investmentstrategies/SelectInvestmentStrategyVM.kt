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

    val category = MutableLiveData<HomeData.Data.Category.SecondLevelCategory>()
    val secondlevel = ObservableField<HomeData.Data.Category.SecondLevelCategory>()

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