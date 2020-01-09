package com.tarrakki.module.transactions

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.e

class LoadMore : BaseObservable(), WidgetsViewModel {

    @get:Bindable
    var isLoading = false
        set(value) {
            field = value
            e("Notify1=$field")
            notifyPropertyChanged(BR.loading)
        }

    override fun layoutId(): Int {
        return R.layout.row_load_more
    }
}