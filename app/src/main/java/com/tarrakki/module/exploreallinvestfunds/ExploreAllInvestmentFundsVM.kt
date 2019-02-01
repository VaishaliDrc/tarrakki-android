package com.tarrakki.module.exploreallinvestfunds

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.HomeData
import com.tarrakki.api.model.parseTo
import com.tarrakki.module.home.HomeSection
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class ExploreAllInvestmentFundsVM : FragmentViewModel() {
    var homeSections = ArrayList<WidgetsViewModel>()

    fun getHomeData(isRefreshing: Boolean = false): MutableLiveData<HomeData> {
        val homeData = MutableLiveData<HomeData>()
        if (!isRefreshing)
            EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getHomeData(),
                apiNames = WebserviceBuilder.ApiNames.getHomeData,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                val data = o.data?.parseTo<HomeData>()
                                data?.let { it ->
                                    data.data.cartCount?.let {
                                        App.INSTANCE.cartCount.value = it
                                    }
                                    homeSections.clear()
                                    it.data.category.forEach { item ->
                                        for (secondlevel in item.secondLevelCategory){
                                            secondlevel.sectionName = item.categoryName
                                        }
                                        homeSections.add(
                                                HomeSection(
                                                        item.categoryName,
                                                        it.data.toWadgesArray(item.secondLevelCategory)
                                                ).apply { category = item })
                                    }
                                    homeSections.add(
                                            HomeSection(
                                                    "Set a Goal",
                                                    it.data.getGoals()
                                            ))
                                    homeData.value = it
                                }
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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