package com.tarrakki.module.redeem

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.api.model.UserPortfolioResponse
import org.supportcompact.FragmentViewModel

class RedeemConfirmVM : FragmentViewModel() {

    val goalBasedRedeemFund = MutableLiveData<UserPortfolioResponse.Data.GoalBasedInvestment.Fund>()
    val directRedeemFund = MutableLiveData<UserPortfolioResponse.Data.DirectInvestment>()
    val tarrakkiZyaadaRedeemFund = MutableLiveData<UserPortfolioResponse.Data.TarrakkiZyaadaInvestment>()
}