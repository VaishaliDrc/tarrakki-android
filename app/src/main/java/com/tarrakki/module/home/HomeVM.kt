package com.tarrakki.module.home

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.HomeData
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

class HomeVM : FragmentViewModel() {

    val whayTarrakki = ObservableField(true)
    var homeSections = ArrayList<WidgetsViewModel>()
    var portfolioVisibility = ObservableField(View.GONE)

    /*init {
        homeSections.add(
                HomeSection(
                        "Investment Strategies",
                        arrayListOf(
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 10+ years or longer"
                                ),
                                HomeItem(
                                        "Long Term Investments",
                                        "Diversified equity founds, 5+ years or longer"
                                ),
                                HomeItem(
                                        "Short Long Term Investments",
                                        "Diversified equity founds, 3+ years or longer"
                                ),
                                HomeItem(
                                        "Very Long Term Investments",
                                        "Diversified equity founds, 2+ years or longer"
                                )
                        ))
        )
        homeSections.add(
                HomeSection(
                        "Set a Goal",
                        arrayListOf(
                                Goal("Wealth creation", R.drawable.wealth_creation),
                                Goal("Holiday", R.drawable.holiday),
                                Goal("Electronic Gadget", R.drawable.electronic_gadget),
                                Goal("Automobile", R.drawable.automobile),
                                Goal("Own a Home", R.drawable.own_a_home)
                        )
                ))
    }*/

    fun getHomeData(): MutableLiveData<HomeData> {
        val homeData = MutableLiveData<HomeData>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getHomeData(),
                apiNames = WebserviceBuilder.ApiNames.getHomeData,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status.code == 1) {
                                val data = o.data?.parseTo<HomeData>()
                                data?.let { it ->
                                    it.data.category.forEach { item ->
                                        homeSections.add(
                                                HomeSection(
                                                        item.categoryName,
                                                        it.data.toWadgesArray(item.secondLevelCategory)
                                                ))
                                    }
                                    homeSections.add(
                                            HomeSection(
                                                    "Set a Goal",
                                                    it.data.getGoals()
                                            ))
                                    homeData.value = it
                                }
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
        return homeData
    }
}

data class HomeSection(var title: String, var homeItems: ArrayList<WidgetsViewModel>?) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_section_investment_item
    }
}

/*
data class HomeItem(var title: String, var description: String = "", @DrawableRes var imgUrl: Int = R.drawable.very_long_investments) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_investment_list_item
    }
}*/
