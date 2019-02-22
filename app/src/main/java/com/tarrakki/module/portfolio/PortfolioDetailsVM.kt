package com.tarrakki.module.portfolio

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.BR
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.getUserId
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class PortfolioDetailsVM : FragmentViewModel() {

    val investment = MutableLiveData<Investment>()
    val goalBasedInvestment = MutableLiveData<UserPortfolioResponse.Data.GoalBasedInvestment>()
    val goalInvestment = ObservableField<UserPortfolioResponse.Data.GoalBasedInvestment>()

}