package com.tarrakki.module.transactions

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel

class LoadMore : WidgetsViewModel, BaseObservable() {

    @get:Bindable
    var loadMore = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.loadMore)
        }

    override fun layoutId(): Int {
        return R.layout.row_load_more
    }
}