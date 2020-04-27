package com.tarrakki.module.risk_profile

import androidx.annotation.StringRes
import com.tarrakki.R
import com.tarrakki.module.bankaccount.SingleButton
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class RiskProfileVM : FragmentViewModel() {

    val data = arrayListOf<WidgetsViewModel>()

    init {
        data.add(RiskProfile("Suresh Kumar", "as on 04 Dec 2020"))
        data.add(RiskObservation(R.string.the_house_that))
        data.add(RiskSpeedometer(5))
        data.add(SingleButton(R.string.retake_risk_assessment))
    }

}

data class RiskProfile(val name: String, val date: String) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_risk_profile
    }
}

data class RiskObservation(@StringRes val observation: Int) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_risk_observation
    }
}

data class RiskSpeedometer(val value: Int) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_speedometer_risk_profile
    }
}