package com.tarrakki.module.investmentstrategies

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import android.text.SpannableStringBuilder
import android.view.View
import com.tarrakki.BR
import com.tarrakki.api.model.HomeData
import org.supportcompact.FragmentViewModel
import java.io.Serializable

class SelectInvestmentStrategyVM : FragmentViewModel() {

    val txtnoteToInvestors = ObservableField("")
    val noteToInvestors = ObservableField(true)

    val secondlevel = ObservableField<HomeData.Data.Category.SecondLevelCategory>()
    val thirdlevel = ObservableField<HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory>()

    val isSingleInvestment = ObservableField(true)
    val isThematicInvestment = ObservableField(false)
    val singleInvestmentImg = ObservableField("")
    val singleInvestmentCategoryName = ObservableField("")
    val singleInvestmentCategoryShortDes = ObservableField("")
    val singleInvestmentCategoryDesc = ObservableField("")
    val singleInvestmentReturntype = ObservableField("")
    val singleInvestmentRiskType = ObservableField("")
    val singleInvestmentReturntypeVisible = ObservableField(View.GONE)
    val singleInvestmentRiskTypeVisible = ObservableField(View.GONE)
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