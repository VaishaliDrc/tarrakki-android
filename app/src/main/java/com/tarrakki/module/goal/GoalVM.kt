package com.tarrakki.module.goal

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.DrawableRes
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.parseTo
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.io.Serializable

class GoalVM : FragmentViewModel() {

    var goals = ArrayList<Goal>()
    val goalList = MutableLiveData<com.tarrakki.api.model.Goal>()

    init {
        goals.add(Goal("Wealth creation", R.drawable.wealth_creation))
        goals.add(Goal("Holiday", R.drawable.holiday))
        goals.add(Goal("Electronic Gadget", R.drawable.electronic_gadget))
        goals.add(Goal("Automobile", R.drawable.automobile))
        goals.add(Goal("Own a Home", R.drawable.own_a_home))
        goals.add(Goal("Emergency Fund", R.drawable.emergency_fund))
        goals.add(Goal("Children's Education", R.drawable.childrens_education))
        goals.add(Goal("Wedding", R.drawable.wedding))
    }

    fun getGoals(): MutableLiveData<com.tarrakki.api.model.Goal> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getGoals(),
                apiNames = WebserviceBuilder.ApiNames.getGoals,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status.code == 1) {
                                val data = o.data?.parseTo<com.tarrakki.api.model.Goal>()
                                goalList.value = data
                            } else {
                                EventBus.getDefault().post(ShowError(o.status.message))
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return goalList
    }
}


data class Goal(var title: String, @DrawableRes var imgUrl: Int) : BaseObservable(), WidgetsViewModel, Serializable {

    @Bindable
    var investmentAmount: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.investmentAmount)
        }

    @Bindable
    var investmentDuration: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.investmentDuration)
        }

    override fun layoutId(): Int {
        return R.layout.row_goal_home_list_item
    }
}
