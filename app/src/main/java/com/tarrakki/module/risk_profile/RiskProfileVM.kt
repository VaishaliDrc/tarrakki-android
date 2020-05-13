package com.tarrakki.module.risk_profile

import androidx.lifecycle.MutableLiveData
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class RiskProfileVM : FragmentViewModel() {
    val data = arrayListOf<WidgetsViewModel>()
    val riskProfile = MutableLiveData<ApiResponse>()
}

data class RiskProfile(val name: String, val date: String, val imgUrl: String) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_risk_profile
    }
}

data class RiskObservation(val observation: String) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_risk_observation
    }
}

data class RiskSpeedometer(val riskProfile: String) : WidgetsViewModel {

    override fun layoutId(): Int {
        return R.layout.row_speedometer_risk_profile
    }

    val riskProfileLevel: Float
        get() = when {
            "Aggressive".equals("$riskProfile", true) -> 90f
            "Moderately Aggressive".equals("$riskProfile", true) -> 70f
            "Balanced".equals("$riskProfile", true) -> 50f
            "Moderately Conservative".equals("$riskProfile", true) -> 30f
            "Conservative".equals("$riskProfile", true) -> 10f
            else -> 0f
        }
}