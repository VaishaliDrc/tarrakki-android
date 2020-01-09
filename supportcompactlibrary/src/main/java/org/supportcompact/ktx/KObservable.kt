package org.supportcompact.ktx

import androidx.databinding.Observable
import androidx.databinding.ObservableField

inline fun <R> ObservableField<R>.observe(crossinline callback: (R) -> Unit) {
    this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            get()?.let { callback(it) }
        }
    })
}

// observing the change of data is simple, clean and readable.
/*

viewModel.user.observe {
    //TODO use whatever with the observed data
}

 */