package com.tarrakki.module.invest

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.graphics.Color
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import java.io.Serializable

class InvestVM : FragmentViewModel() {

    val funds = arrayListOf<Fund>()
    var fundTypes = arrayListOf<FundType>()
    var fundReturns = arrayListOf<FundType>()
    var arrRiskLevel = arrayListOf<RiskLevel>()
    val filter = ObservableField<Boolean>(true)

    init {

        fundTypes.add(FundType("Tarrakki Recommended", true))
        fundTypes.add(FundType("All"))
        fundTypes.add(FundType("NFO"))

        fundReturns.add(FundType("1Y"))
        fundReturns.add(FundType("3Y", true))
        fundReturns.add(FundType("5Y"))
        fundReturns.add(FundType("AUM"))

        funds.add(Fund(
                "SBI Banking and Financial Services Growth Direct Plan",
                "Sectoral/Thematic",
                0.93f,
                18.2f,
                19.4f,
                13.7f,
                5.2f,
                5.2f)
        )

        funds.add(Fund(
                "DSP Blackrock Natural Resources and New Energy Growth Direct Plan",
                "Sectoral/Thematic",
                0.26f,
                18.5f,
                4.6f,
                14.8f,
                25.7f,
                6.5f,
                true)
        )

        funds.add(Fund(
                "SBI Banking and Financial Services Growth Direct Plan",
                "Sectoral/Thematic",
                0.93f,
                18.2f,
                19.4f,
                13.7f,
                5.2f,
                5.2f)
        )

        val riskLevel = App.INSTANCE.resources.getStringArray(R.array.risk_level)
        val riskLevelColor = App.INSTANCE.resources.getStringArray(R.array.risk_level_color)
        riskLevel.forEachIndexed { index, value ->
            arrRiskLevel.add(RiskLevel(value, Color.parseColor(riskLevelColor[index]), index == 0))
        }
    }

}

data class Fund(
        var name: String,
        var description: String,
        var currentReturn: Float,
        var returnSinceLaunch: Float,
        var returnInYear: Float,
        var volatility: Float,
        var threeYearOfFundReturn: Float,
        var threeYearOfFDReturn: Float,
        var hasNegativeReturn: Boolean = false,
        var temp: String = "39.550",
        var fundType: String = "Equity",
        var whatNew: String = "Folio"
) : BaseObservable(), Serializable {
    @get:Bindable
    var hasOneTimeAmount: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasOneTimeAmount)
        }
    @get:Bindable
    var date: String = "07 Sep 2018"
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }
}

data class FundType(
        var name: String,
        var _isSelected: Boolean = false
) : BaseObservable() {
    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}

data class RiskLevel(
        var name: String,
        var color: Int,
        var _isSelected: Boolean = false
) : BaseObservable() {
    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}