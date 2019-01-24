package com.tarrakki.module.investmentstrategies

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import org.supportcompact.FragmentViewModel

class InvestmentStrategiesVM : FragmentViewModel() {

    val secondaryCategoriesList = MutableLiveData<List<HomeData.Data.Category.SecondLevelCategory>>()
    val secondaryCategories = MutableLiveData<HomeData.Data.Category.SecondLevelCategory>()
}
