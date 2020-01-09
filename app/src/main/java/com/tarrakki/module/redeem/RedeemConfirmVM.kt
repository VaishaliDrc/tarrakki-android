package com.tarrakki.module.redeem

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import android.view.View
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.module.portfolio.StopSIP
import org.supportcompact.FragmentViewModel

class RedeemConfirmVM : FragmentViewModel() {

    val goalBasedRedeemFund = MutableLiveData<UserPortfolioResponse.Data.GoalBasedInvestment.Fund>()
    val directRedeemFund = MutableLiveData<UserPortfolioResponse.Data.DirectInvestment>()
    val tarrakkiZyaadaRedeemFund = MutableLiveData<UserPortfolioResponse.Data.TarrakkiZyaadaInvestment>()
    val isRedeemReq = ObservableField<Boolean>()
    var stopSIP: StopSIP? = null
    val isInstaRedeem = ObservableField(false)
}