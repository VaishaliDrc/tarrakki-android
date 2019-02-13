package com.tarrakki.module.confirmorder

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.tarrakki.R
import com.tarrakki.module.bankaccount.SingleButton
import org.supportcompact.BR
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class ConfirmOrderVM : FragmentViewModel() {

    val orders = arrayListOf<WidgetsViewModel>()
    val orderTotal = OrderTotal()

    init {
        orders.add(Order(
                name = "SBI Banking and Financial Services Growth Direct Plan",
                type = "DEBT-BONDS",
                sipAmount = 10000.0,
                lumpsumAmount = 1000.0,
                startDate = "30/03/2019",
                isFirstInstallmentSIP = true
        ))
        orderTotal.total = 11000.0
        orderTotal.bank = "ICICI Bank"
        orders.add(orderTotal)
        orders.add(SingleButton(R.string.place_order))
    }
}

class OrderTotal : WidgetsViewModel {

    var total: Double = 0.0
    var bank: String = ""

    override fun layoutId(): Int {
        return R.layout.row_order_total
    }
}

data class Order(var name: String) : BaseObservable(), WidgetsViewModel {

    override fun layoutId(): Int {
        return R.layout.row_confirm_order
    }

    var type: String = ""
    var sipAmount: Double = 0.0
    var lumpsumAmount: Double = 0.0
    var startDate: String = ""
    @get:Bindable
    var isFirstInstallmentSIP: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstInstallmentSIP)
        }

    constructor(name: String, type: String, sipAmount: Double, lumpsumAmount: Double, startDate: String, isFirstInstallmentSIP: Boolean) : this(name) {
        this.name = name
        this.type = type
        this.sipAmount = sipAmount
        this.lumpsumAmount = lumpsumAmount
        this.startDate = startDate
        this.isFirstInstallmentSIP = isFirstInstallmentSIP
    }

}