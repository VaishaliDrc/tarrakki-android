package com.tarrakki.module.home

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.HomeData
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.subscribeToSingle
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

class HomeVM : FragmentViewModel() {

    val whayTarrakki = ObservableField(true)
    var homeSections = ArrayList<WidgetsViewModel>()
    var portfolioVisibility = ObservableField(View.VISIBLE)
    var portfolioDetails = ObservableField<HomeData.Data.PortfolioDetails>()
    var isAskedForSecurityLock = false
    var isShowingSecurityDialog = false
    val redirectToInvestmentStratergy = MutableLiveData<String>()

    fun getHomeData(isRefreshing: Boolean = false): MutableLiveData<HomeData> {
        val homeData = MutableLiveData<HomeData>()
        if (!isRefreshing) {
            showProgress()
            /*Handler().postDelayed({

            }, 500)*/
        }

        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getHomeData(),
                apiNames = WebserviceBuilder.ApiNames.getHomeData,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        //EventBus.getDefault().post(DISMISS_PROGRESS)
                        dismissProgress()
                        /*Handler().postDelayed({
                            dismissProgress()
                        }, 500)*/
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                val data = o.data?.parseTo<HomeData>()
                                data?.data?.readyToInvest?.let { it1 -> App.INSTANCE.setReadyToInvest(it1) }
                                data?.data?.isKycVerified?.let { it1 -> App.INSTANCE.setKYClVarified(it1) }
                                data?.data?.completeRegistration?.let { it1 -> App.INSTANCE.setCompletedRegistration(it1) }

                                App.INSTANCE.homeData = data
                                data?.let {
                                    portfolioDetails.set(data.data.portfolioDetails)
                                    data.data.cartCount?.let {
                                        App.INSTANCE.cartCount.value = it
                                    }
                                    homeSections.clear()
                                    it.data.category.forEach { item ->
                                        for (secondlevel in item.secondLevelCategory) {
                                            secondlevel.sectionName = item.categoryName
                                        }
                                        if (!it.data.toWadgesArray(item.secondLevelCategory).isNullOrEmpty()) {
                                            homeSections.add(
                                                    HomeSection(
                                                            item.categoryName,
                                                            it.data.toWadgesArray(item.secondLevelCategory)
                                                    ).apply { category = item })
                                        }
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
                        /*Handler().postDelayed({
                            dismissProgress()
                        }, 500)*/
                        dismissProgress()
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return homeData
    }
}

data class HomeSection(var title: String, var homeItems: ArrayList<WidgetsViewModel>?) : WidgetsViewModel {

    var category: HomeData.Data.Category? = null

    override fun layoutId(): Int {
        return R.layout.row_section_investment_item
    }
}